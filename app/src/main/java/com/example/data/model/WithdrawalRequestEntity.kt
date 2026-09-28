package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "withdrawal_requests")
data class WithdrawalRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childName: String = "Pedro",
    val amount: Double,
    val reason: String = "Retirada em mãos / Lanche",
    val requestDate: String = "Hoje",
    val status: String = "PENDENTE", // "PENDENTE", "PAGO_EM_MAOS", "RECUSADO"
    val timestamp: Long = System.currentTimeMillis()
)
