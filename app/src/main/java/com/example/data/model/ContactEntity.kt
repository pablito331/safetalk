package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val relationship: String,
    val relationshipType: String = "EXTERNAL", // "FAMILY" or "EXTERNAL"
    val isApprovedByParent: Boolean,
    val safetyStatus: String, // "APROVADO", "PENDENTE", "BLOQUEADO"
    val isOnline: Boolean = false,
    val lastSeen: String = "Online",
    val unreadCount: Int = 0,
    val avatarColor: Long = 0xFF008069,
    // Identidade de rede deste contato (e-mail/@usuário no SafeTalk) —
    // é o endereço usado para entregar mensagens entre celulares.
    val remoteIdentity: String = ""
)
