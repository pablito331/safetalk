package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_highlights")
data class FamilyHighlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorRole: String = "CHILD", // "CHILD", "PARENT", "FRIEND"
    val avatarDrawableResName: String = "avatar_pedro",
    val title: String,
    val textContent: String,
    val audience: String = "FAMILY", // "FAMILY", "ALL_CONTACTS"
    val mediaType: String = "TEXT", // "TEXT", "CAMERA_PHOTO"
    val photoBitmapBase64: String? = null,
    val timeAgo: String = "Agora mesmo",
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val moodEmoji: String = "🌟",
    val isViewed: Boolean = false
)
