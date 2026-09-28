package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sibling_accounts")
data class SiblingAccountEntity(
    @PrimaryKey val name: String,
    val age: Int,
    val balance: Double = 30.00,
    val relationship: String = "Irmão(a)",
    val avatarDrawableResName: String = "avatar_default"
)
