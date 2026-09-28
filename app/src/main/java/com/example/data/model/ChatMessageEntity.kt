package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: Long,
    val sender: String, // "ME", "CONTACT", "SYSTEM"
    val text: String = "",
    val mediaType: String = "TEXT", // "TEXT", "AUDIO", "IMAGE", "VIDEO"
    val mediaUri: String = "",
    val mediaDurationSeconds: Int = 0, // For audio/video length
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "14:32",
    val isRead: Boolean = true,
    val isFlaggedByFilter: Boolean = false
)
