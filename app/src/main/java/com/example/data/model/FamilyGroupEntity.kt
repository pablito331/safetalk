package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_groups")
data class FamilyGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupName: String,
    val description: String = "",
    val creatorName: String,
    val creatorRole: String = "CHILD",
    val createdByParent: Boolean = false,
    val approvalStatus: String = "APPROVED",
    val memberCount: Int = 1,
    val isActive: Boolean = approvalStatus.equals("APPROVED", ignoreCase = true),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "group_invites")
data class GroupInviteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val inviteeName: String,
    val invitedBy: String,
    val requiresParentApproval: Boolean = true,
    val approvalStatus: String = "PENDING_PARENT",
    val createdAt: Long = System.currentTimeMillis()
)
