package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_rewards")
data class MonthlyRewardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val requiredPercentage: Int, // e.g., 50, 75, 90%
    val bonusAmount: Double,
    val tier: String, // "BRONZE", "PRATA", "OURO"
    val isUnlocked: Boolean = false,
    val isClaimed: Boolean = false
)
