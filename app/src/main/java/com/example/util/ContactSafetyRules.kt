package com.example.util

import com.example.data.model.ContactEntity
import com.example.data.model.FamilyHighlightEntity

object ContactSafetyRules {
    fun shouldAllowConversation(contact: ContactEntity): Boolean {
        if (contact.safetyStatus.equals("BLOQUEADO", ignoreCase = true)) return false
        if (contact.relationshipType.equals("FAMILY", ignoreCase = true)) return true
        return contact.isApprovedByParent && contact.safetyStatus.equals("APROVADO", ignoreCase = true)
    }

    fun isFamilyMember(contact: ContactEntity): Boolean {
        return contact.relationshipType.equals("FAMILY", ignoreCase = true)
    }
}

object FamilyHighlightRules {
    fun sortForDisplay(highlights: List<FamilyHighlightEntity>): List<FamilyHighlightEntity> {
        return highlights.sortedWith(compareBy<FamilyHighlightEntity> { it.isViewed }.thenByDescending { it.timestamp })
    }
}
