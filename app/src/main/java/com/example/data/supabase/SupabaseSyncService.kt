package com.example.data.supabase

import android.content.Context
import android.util.Log
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChildProfileEntity
import com.example.data.model.ContactEntity
import java.util.UUID

/**
 * Mensageria real entre celulares via fila temporária no Supabase.
 *
 * Fluxo:
 *  1. ENVIAR: mensagem vai endereçada ao login_identifier do contato.
 *  2. RECEBER: cada ~20s o app busca mensagens destinadas a mim,
 *     grava no Room e APAGA do servidor (fila temporária).
 *  3. SUPERVISÃO: no servidor, pais da mesma família podem ler as
 *     mensagens dos filhos (RLS "pais supervisionam filhos");
 *     entre adultos da família não há auditoria.
 */
class SupabaseSyncService : SupabaseSyncContract {

    companion object {
        /** Contexto injetado pelo App (MainActivity) para acesso a preferências. */
        @Volatile
        var appContext: Context? = null
    }

    private val client by lazy {
        SupabaseRealtimeHttp.createClient(SupabaseConfig.URL)
    }

    private val apiKey get() = SupabaseConfig.ANON_KEY

    private fun authHeader(): String {
        val token = SupabaseDeviceIdentity.currentAccessToken()
        return if (!token.isNullOrBlank()) "Bearer $token" else "Bearer ${SupabaseConfig.ANON_KEY}"
    }

    private fun isConfigured(): Boolean = SupabaseConfig.IS_CONFIGURED && apiKey.isNotBlank()

    private fun myIdentity(profile: ChildProfileEntity?): String =
        profile?.loginIdentifier?.trim()?.lowercase().orEmpty()

    // ------------------------------------------------------------
    // IDENTIDADE: garante sessão autenticada deste aparelho
    // ------------------------------------------------------------
    suspend fun ensureAuthenticated(profile: ChildProfileEntity): String? {
        SupabaseDeviceIdentity.appContext = appContext
        val token = SupabaseDeviceIdentity.ensureAuthenticated(profile.loginIdentifier)
        if (token != null) {
            registerIdentity(profile, token)
        }
        return token
    }

    /** Registra/atualiza a identidade do dispositivo no servidor (role p/ supervisão). */
    private suspend fun registerIdentity(profile: ChildProfileEntity, token: String) {
        try {
            val role = if (profile.userRole.equals("PARENT", true)) "PARENT" else "CHILD"
            val payload = mapOf(
                "owner_id" to "me", // substituído pelo header Prefer: resolution=merge-digest? não — ver abaixo
                "login_identifier" to myIdentity(profile),
                "display_name" to profile.name,
                "family_code" to profile.familyCode,
                "role" to role
            )
            // owner_id real = uid do token; o servidor não confia no body.
            // Buscamos o uid do usuário logado:
            val uid = fetchAuthUid(token) ?: return
            val finalPayload = payload + ("owner_id" to uid)
            client.upsertIdentity(apiKey, "Bearer $token", finalPayload)
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Falha ao registrar identidade: ${e.message}")
        }
    }

    private suspend fun fetchAuthUid(token: String): String? = try {
        val rows = client.fetchAuthUser(apiKey, "Bearer $token")
        rows["id"]?.toString()
    } catch (_: Exception) {
        null
    }

    // ------------------------------------------------------------
    // ENVIAR
    // ------------------------------------------------------------
    override suspend fun sendMessage(message: ChatMessageEntity) {
        if (!isConfigured()) return
        try {
            val token = SupabaseDeviceIdentity.currentAccessToken() ?: return
            val payload = mapOf(
                "sender_id" to message.senderIdentity,
                "sender_name" to "",
                "recipient_id" to message.recipientIdentity,
                "family_code" to "",
                "client_msg_id" to UUID.randomUUID().toString(),
                "text" to message.text,
                "media_type" to message.mediaType,
                "media_uri" to message.mediaUri.ifBlank { null },
                "media_duration_seconds" to message.mediaDurationSeconds,
                "formatted_time" to message.formattedTime,
                "timestamp" to message.timestamp
            )
            client.insertDeviceMessage(apiKey, authHeader(), payload)
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Falha segura ao enviar mensagem (offline): ${e.message}")
        }
    }

    // ------------------------------------------------------------
    // RECEBER: busca mensagens destinadas a mim e devolve
    // (quem chama grava no Room e depois chama deleteDelivered)
    // ------------------------------------------------------------
    suspend fun fetchIncomingMessages(profile: ChildProfileEntity): List<Pair<String, ChatMessageEntity>> {
        if (!isConfigured()) return emptyList()
        val token = SupabaseDeviceIdentity.currentAccessToken() ?: return emptyList()
        val me = myIdentity(profile)
        if (me.isBlank()) return emptyList()
        return try {
            val rows = client.fetchDeviceMessages(
                apiKey = authHeader().let { "Bearer $token" },
                authHeader2 = "Bearer $token",
                recipient = "eq.$me"
            )
            rows.mapNotNull { row ->
                val remoteId = (row["id"] as? String) ?: return@mapNotNull null
                val senderId = (row["sender_id"] as? String) ?: return@mapNotNull null
                val text = (row["text"] as? String) ?: ""
                val mediaType = (row["media_type"] as? String) ?: "TEXT"
                val mediaUri = (row["media_uri"] as? String) ?: ""
                val duration = (row["media_duration_seconds"] as? Double)?.toInt() ?: 0
                val formattedTime = (row["formatted_time"] as? String) ?: ""
                val ts = (row["timestamp"] as? Double)?.toLong() ?: System.currentTimeMillis()
                remoteId to ChatMessageEntity(
                    id = 0,
                    contactId = 0, // resolvido pelo chamador via senderId
                    sender = "CONTACT",
                    text = text,
                    mediaType = mediaType,
                    mediaUri = mediaUri,
                    mediaDurationSeconds = duration,
                    timestamp = ts,
                    formattedTime = formattedTime,
                    isRead = false,
                    remoteId = remoteId,
                    senderIdentity = senderId,
                    recipientIdentity = me
                )
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Falha segura ao buscar mensagens (offline): ${e.message}")
            emptyList()
        }
    }

    /** Remove da fila as mensagens já gravadas localmente. */
    suspend fun deleteDelivered(remoteIds: List<String>) {
        if (!isConfigured() || remoteIds.isEmpty()) return
        val token = SupabaseDeviceIdentity.currentAccessToken() ?: return
        try {
            remoteIds.chunked(50).forEach { chunk ->
                client.deleteDeviceMessages(
                    apiKey = apiKey,
                    authHeader = "Bearer $token",
                    idFilter = "id=in.(${chunk.joinToString(",")})"
                )
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Falha ao apagar entregues: ${e.message}")
        }
    }

    // ------------------------------------------------------------
    // Interface legada (mantida por compatibilidade)
    // ------------------------------------------------------------
    override suspend fun syncProfile(profile: ChildProfileEntity) {
        if (!isConfigured()) return
        try {
            val token = SupabaseDeviceIdentity.currentAccessToken() ?: return
            val payload = mapOf(
                "name" to profile.name,
                "user_role" to profile.userRole,
                "family_code" to profile.familyCode,
                "login_identifier" to profile.loginIdentifier,
                "profile_status" to profile.profileStatus,
                "balance" to profile.balance,
                "battery_percent" to profile.batteryPercent,
                "last_location" to profile.lastLocationUpdate,
                "is_in_safe_zone" to profile.isInSafeZone,
                "is_alarm_ringing" to profile.isUrgentAlarmRinging
            )
            client.insertProfile(apiKey, authHeader(), payload)
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Falha segura ao sincronizar perfil (offline): ${e.message}")
        }
    }

    override suspend fun syncContacts(contacts: List<ContactEntity>) {
        // Fase 3: aprovações cruzando dispositivos.
        if (!isConfigured()) return
    }

    override suspend fun fetchPendingMessagesForUser(userId: String): List<ChatMessageEntity> =
        emptyList() // substituído por fetchIncomingMessages(profile)

    override suspend fun markMessageDelivered(messageId: Long) {
        // substituído por deleteDelivered(remoteIds)
    }

    override suspend fun backupHistoryToDrive(userId: String) {
        // Reservado para backup local/drive
    }
}
