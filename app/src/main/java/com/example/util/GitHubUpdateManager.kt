package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

data class AppUpdateInfo(
    val hasUpdate: Boolean,
    val latestVersion: String,
    val currentVersion: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val downloadUrl: String?
)

object GitHubUpdateManager {

    // Configuração do repositório oficial do SafeTalk no GitHub
    var repoOwner: String = "pablito331"
    var repoName: String = "safetalk"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun checkForUpdates(
        owner: String = repoOwner,
        repo: String = repoName
    ): AppUpdateInfo = withContext(Dispatchers.IO) {
        val currentVersion = BuildConfig.VERSION_NAME
        try {
            val url = "https://api.github.com/repos/$owner/$repo/releases/latest"
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "SafeTalk-Android-App")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext AppUpdateInfo(
                    hasUpdate = false,
                    latestVersion = currentVersion,
                    currentVersion = currentVersion,
                    releaseTitle = "",
                    releaseNotes = "",
                    downloadUrl = null
                )
            }

            val body = response.body?.string() ?: return@withContext AppUpdateInfo(
                hasUpdate = false,
                latestVersion = currentVersion,
                currentVersion = currentVersion,
                releaseTitle = "",
                releaseNotes = "",
                downloadUrl = null
            )

            val json = JSONObject(body)
            val tagName = json.optString("tag_name", "").replace("v", "").trim()
            val title = json.optString("name", "Nova Atualização")
            val releaseNotes = json.optString("body", "Melhorias gerais e correções.")

            // Procurar APK nos assets
            val assets = json.optJSONArray("assets")
            var apkDownloadUrl: String? = null
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        apkDownloadUrl = asset.optString("browser_download_url")
                        break
                    }
                }
            }

            val isNewer = isVersionNewer(tagName, currentVersion)

            AppUpdateInfo(
                hasUpdate = isNewer && !apkDownloadUrl.isNullOrBlank(),
                latestVersion = tagName,
                currentVersion = currentVersion,
                releaseTitle = title,
                releaseNotes = releaseNotes,
                downloadUrl = apkDownloadUrl
            )
        } catch (e: Exception) {
            Log.w("GitHubUpdateManager", "Erro ao checar atualizações: ${e.message}")
            AppUpdateInfo(
                hasUpdate = false,
                latestVersion = currentVersion,
                currentVersion = currentVersion,
                releaseTitle = "",
                releaseNotes = "",
                downloadUrl = null
            )
        }
    }

    private fun isVersionNewer(remote: String, local: String): Boolean {
        if (remote.isBlank() || local.isBlank()) return false
        try {
            val remoteParts = remote.split(".").map { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            val localParts = local.split(".").map { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }

            val maxLen = maxOf(remoteParts.size, localParts.size)
            for (i in 0 until maxLen) {
                val r = remoteParts.getOrElse(i) { 0 }
                val l = localParts.getOrElse(i) { 0 }
                if (r > l) return true
                if (r < l) return false
            }
        } catch (_: Exception) {}
        return remote != local
    }

    suspend fun downloadApk(
        context: Context,
        apkUrl: String,
        onProgress: (Float) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(apkUrl)
                .header("User-Agent", "SafeTalk-Android-App")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val body = response.body ?: return@withContext null
            val contentLength = body.contentLength()

            val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir
            val destinationFile = File(downloadDir, "safetalk_update.apk")
            if (destinationFile.exists()) destinationFile.delete()

            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null

            try {
                inputStream = body.byteStream()
                outputStream = FileOutputStream(destinationFile)

                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytesRead: Long = 0

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    if (contentLength > 0) {
                        val progress = totalBytesRead.toFloat() / contentLength
                        withContext(Dispatchers.Main) {
                            onProgress(progress)
                        }
                    }
                }
                outputStream.flush()
                return@withContext destinationFile
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: Exception) {
            Log.e("GitHubUpdateManager", "Erro no download do APK: ${e.message}")
            null
        }
    }

    fun triggerApkInstallation(context: Context, apkFile: File) {
        try {
            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            context.startActivity(installIntent)
        } catch (e: Exception) {
            Log.e("GitHubUpdateManager", "Erro ao iniciar instalador: ${e.message}")
        }
    }
}
