package com.example.data.supabase

import android.util.Log
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Identidade de dispositivo no Supabase Auth.
 *
 * Estratégia (segura e sem atrito): o cadastro local do SafeTalk cria/loga uma
 * conta Supabase Auth com o próprio login_identifier (e-mail). A senha é gerada
 * determinísticamente por dispositivo e guardada APENAS no aparelho (SharedPreferences).
 * Com isso cada celular tem identidade própria e o RLS do Postgres garante que
 * só o destinatário leia as mensagens dele.
 */
object SupabaseDeviceIdentity {

    private const val PREFS = "safetalk_device_identity"
    private const val KEY_EMAIL = "identity_email"
    private const val KEY_PASSWORD = "identity_password"
    private const val KEY_SESSION_TOKEN = "access_token"
    private const val KEY_SESSION_REFRESH = "refresh_token"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    /** Garante sessão autenticada para o dono deste aparelho. */
    suspend fun ensureAuthenticated(loginIdentifier: String): String? =
        withContext(Dispatchers.IO) {
            if (!SupabaseConfig.IS_CONFIGURED) return@withContext null
            try {
                val email = loginIdentifier.trim().lowercase()
                if (!email.contains("@")) return@withContext null // @usuário puro ainda não loga

                val prefs = prefs()
                val savedEmail = prefs.getString(KEY_EMAIL, null)
                val savedPassword = prefs.getString(KEY_PASSWORD, null)

                // 1) já tem token válido? usa
                prefs.getString(KEY_SESSION_TOKEN, null)?.let { token ->
                    if (tokenIsValid(token)) return@withContext token
                }

                // 2) aparelho já tem credenciais salvas? login
                if (savedEmail == email && savedPassword != null) {
                    val token = passwordSignIn(email, savedPassword)
                    if (token != null) {
                        prefs.edit().putString(KEY_SESSION_TOKEN, token).apply()
                        return@withContext token
                    }
                }

                // 3) primeira vez neste aparelho: cria conta com senha aleatória local
                val password = savedPassword ?: generatePassword()
                val token = passwordSignUp(email, password)
                if (token != null) {
                    prefs.edit()
                        .putString(KEY_EMAIL, email)
                        .putString(KEY_PASSWORD, password)
                        .putString(KEY_SESSION_TOKEN, token)
                        .apply()
                    return@withContext token
                }

                // 4) signup pode falhar se e-mail já existe (ex: outro aparelho logou
                //    antes com o mesmo e-mail): tenta login — só funciona se a senha
                //    local bater, senão este aparelho não é o dono da conta.
                if (savedPassword != null) {
                    val retry = passwordSignIn(email, savedPassword)
                    if (retry != null) {
                        prefs.edit().putString(KEY_SESSION_TOKEN, retry).apply()
                        return@withContext retry
                    }
                }
                null
            } catch (e: Exception) {
                Log.w("DeviceIdentity", "Falha ao autenticar dispositivo: ${e.message}")
                null
            }
        }

    /** Token de acesso atual (ou null se não autenticado). */
    fun currentAccessToken(): String? =
        prefs().getString(KEY_SESSION_TOKEN, null)

    private fun prefs() =
        appContext!!.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE)

    @Volatile
    var appContext: android.content.Context? = null

    private fun generatePassword(): String {
        val alphabet = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return buildString {
            repeat(28) { append(alphabet.random()) }
        }
    }

    private fun tokenIsValid(token: String): Boolean = try {
        val request = Request.Builder()
            .url("${SupabaseConfig.URL}/auth/v1/user")
            .header("apikey", SupabaseConfig.ANON_KEY)
            .header("Authorization", "Bearer $token")
            .build()
        httpClient.newCall(request).execute().use { it.isSuccessful }
    } catch (_: Exception) {
        false
    }

    private fun passwordSignIn(email: String, password: String): String? = try {
        val body = JSONObject().put("email", email).put("password", password)
        val request = Request.Builder()
            .url("${SupabaseConfig.URL}/auth/v1/token?grant_type=password")
            .header("apikey", SupabaseConfig.ANON_KEY)
            .post(RequestBodyJson.of(body.toString()))
            .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@use null
            val json = JSONObject(response.body?.string() ?: return@use null)
            json.optString("access_token").takeIf { it.isNotBlank() }
        }
    } catch (_: Exception) {
        null
    }

    private fun passwordSignUp(email: String, password: String): String? = try {
        val body = JSONObject().put("email", email).put("password", password)
        val request = Request.Builder()
            .url("${SupabaseConfig.URL}/auth/v1/signup")
            .header("apikey", SupabaseConfig.ANON_KEY)
            .post(RequestBodyJson.of(body.toString()))
            .build()
        httpClient.newCall(request).execute().use { response ->
            // signup com confirmação de e-mail obrigatória retorna 200 sem sessão;
            // nesse caso o projeto precisa permitir signup sem confirmação no dashboard.
            if (!response.isSuccessful) return@use null
            val json = JSONObject(response.body?.string() ?: return@use null)
            json.optString("access_token").takeIf { it.isNotBlank() }
        }
    } catch (_: Exception) {
        null
    }
}

/** Helper mínimo para POST JSON com OkHttp. */
object RequestBodyJson {
    fun of(json: String): okhttp3.RequestBody =
        json.toRequestBody("application/json; charset=utf-8".toMediaType())
}
