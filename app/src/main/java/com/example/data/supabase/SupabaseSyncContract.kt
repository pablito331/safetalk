package com.example.data.supabase

import com.example.data.model.ChatMessageEntity
import com.example.data.model.ContactEntity
import com.example.data.model.ChildProfileEntity

interface SupabaseSyncContract {
    suspend fun syncProfile(profile: ChildProfileEntity)
    suspend fun syncContacts(contacts: List<ContactEntity>)
    suspend fun sendMessage(message: ChatMessageEntity)
    suspend fun fetchPendingMessagesForUser(userId: String): List<ChatMessageEntity>
    suspend fun markMessageDelivered(messageId: Long)
    suspend fun backupHistoryToDrive(userId: String)
}
