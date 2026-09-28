package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_tasks")
data class FamilyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val rewardAmount: Double = 5.00,
    val rewardPoints: Int = 50,
    val penaltyAmount: Double = 2.00, // Desconto caso não cumpra no prazo
    val dueDate: String = "Hoje",
    val category: String = "Casa", // "Casa", "Estudos", "Pets", "Higiene"
    val status: String = "PENDENTE", // "PENDENTE", "CONCLUIDO_FILHO", "APROVADO_PAGO", "ATRASADO_PENALIZADO"
    val requiresPhotoEvidence: Boolean = false, // Exigir foto da câmera ao vivo como comprovação
    val photoEvidenceUri: String? = null,
    val completedAt: Long? = null,
    val assignedChildName: String = "", // nome real do filho ou "Todos os Filhos"
    val recurrence: String = "NUNCA", // "NUNCA", "DIARIA", "DIAS_UTEIS", "FINS_DE_SEMANA", "SEMANAL"
    val transferStatus: String = "NONE", // "NONE", "WAITING_SIBLING", "WAITING_PARENT", "TRANSFERRED", "REJECTED"
    val transferTargetSibling: String? = null, // Irmão que receberá a tarefa
    val transferRequestedBy: String? = null // Irmão que solicitou transferir
)
