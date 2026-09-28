package com.example.data.supabase

import android.util.Log
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChildProfileEntity
import com.example.data.model.ContactEntity

class SupabaseSyncService : SupabaseSyncContract {

    private val client by lazy {
        SupabaseRealtimeHttp.createClient(SupabaseConfig.URL)
    }

    private val authHeader = "Bearer ${SupabaseConfig.ANON_KEY}"
    private val apiKey = SupabaseConfig.ANON_KEY

    override suspend fun syncProfile(profile: ChildProfileEntity) {
        if (!SupabaseConfig.IS_CONFIGURED) return
        try {
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
            client.insertProfile(apiKey, authHeader, payload)
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Falha segura ao sincronizar perfil (offline): ${e.message}")
        }
    }

    override suspend fun syncContacts(contacts: List<ContactEntity>) {
        if (!SupabaseConfig.IS_CONFIGURED) return
        // Operação silenciosa e segura
    }

    override suspend fun sendMessage(message: ChatMessageEntity) {
        if (!SupabaseConfig.IS_CONFIGURED) return
        try {
            val payload = mapOf(
                "contact_id" to message.contactId,
                "sender" to message.sender,
                "text" to message.text,
                "media_type" to message.mediaType,
                "media_uri" to message.mediaUri,
                "formatted_time" to message.formattedTime,
                "timestamp" to message.timestamp
            )
            client.insertMessage(apiKey, authHeader, payload)
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Falha segura ao enviar mensagem via Supabase (offline): ${e.message}")
        }
    }

    override suspend fun fetchPendingMessagesForUser(userId: String): List<ChatMessageEntity> {
        if (!SupabaseConfig.IS_CONFIGURED) return emptyList()
        return try {
            val rawMessages = client.fetchMessages(apiKey, authHeader)
            rawMessages.mapNotNull { map ->
                val id = (map["id"] as? Number)?.toLong() ?: return@mapNotNull null
                val contactId = (map["contact_id"] as? Number)?.toLong() ?: 1L
                val sender = map["sender"] as? String ?: "CONTACT"
                val text = map["text"] as? String ?: ""
                val mediaType = map["media_type"] as? String ?: "TEXT"
                val mediaUri = map["media_uri"] as? String
                val formattedTime = map["formatted_time"] as? String ?: "12:00"
                val timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()

                ChatMessageEntity(
                    id = id,
                    contactId = contactId,
                    sender = sender,
                    text = text,
                    mediaType = mediaType,
                    mediaUri = mediaUri ?: "",
                    formattedTime = formattedTime,
                    timestamp = timestamp
                )
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Falha segura ao buscar mensagens remotas (offline): ${e.message}")
            emptyList()
        }
    }

    override suspend fun markMessageDelivered(messageId: Long) {
        if (!SupabaseConfig.IS_CONFIGURED) return
        try {
            client.markDelivered(
                apiKey = apiKey,
                authHeader = authHeader,
                messageId = messageId.toString(),
                payload = mapOf("is_delivered" to true)
            )
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Falha ao marcar entrega: ${e.message}")
        }
    }

    override suspend fun backupHistoryToDrive(userId: String) {
        // Reservado para backup local/drive
    }
}
