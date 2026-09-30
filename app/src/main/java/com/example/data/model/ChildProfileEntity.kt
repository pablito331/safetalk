package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_profile")
data class ChildProfileEntity(
    @PrimaryKey val id: Long = 1,
    val name: String = "",
    val age: Int = 0,
    val userRole: String = "",
    val familyRole: String = "",
    val loginIdentifier: String = "",
    val profilePhotoUri: String? = null,
    val profileStatus: String = "ATIVO",
    val familyCode: String = "",
    val balance: Double = 0.0,
    val monthlyPoints: Int = 0,
    val monthlyTasksCompleted: Int = 0,
    val monthlyTasksTarget: Int = 0,
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isInSafeZone: Boolean = false,
    val safeZoneName: String = "",
    val batteryPercent: Int = 0,
    val isSilentModeActive: Boolean = false,
    val isUrgentAlarmRinging: Boolean = false,
    val lastLocationUpdate: String = "",
    val parentPin: String = "",
    val monitoringEnabled: Boolean = false,
    val funnyFilterEnabled: Boolean = false,
    val spouseName: String = "",
    val spouseContact: String = "",
    val isSpouseLinked: Boolean = false,
    val isAutonomousChild: Boolean = false,
    // Papel da tela salvo (PARENT/CHILD/FRIEND_SIMPLIFIED): reabrir o app volta
    // para a mesma UI em vez de depender da idade ou de um default.
    val persistedRole: String = ""
)

