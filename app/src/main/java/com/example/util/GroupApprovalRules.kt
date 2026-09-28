package com.example.util

import com.example.data.model.FamilyGroupEntity
import com.example.data.model.GroupInviteEntity

object GroupApprovalRules {
    fun requiresParentApproval(group: FamilyGroupEntity): Boolean {
        return !group.createdByParent && !group.approvalStatus.equals("APPROVED", ignoreCase = true)
    }

    fun shouldAllowGroupAccess(group: FamilyGroupEntity): Boolean {
        if (group.createdByParent) return true
        return group.approvalStatus.equals("APPROVED", ignoreCase = true)
    }

    fun shouldAllowInvite(invite: GroupInviteEntity): Boolean {
        if (!invite.requiresParentApproval) return true
        return invite.approvalStatus.equals("APPROVED", ignoreCase = true)
    }
}
