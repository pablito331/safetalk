package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_transactions")
data class FinancialTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // "CREDITO", "DEBITO"
    val date: String,
    val category: String = "Geral",
    val description: String = ""
)
