package com.example.data.repository

import com.example.data.dao.FamilyDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChildProfileEntity
import com.example.data.model.ContactEntity
import com.example.data.model.FamilyGroupEntity
import com.example.data.model.FamilyHighlightEntity
import com.example.data.model.FamilyTaskEntity
import com.example.data.model.FinancialTransactionEntity
import com.example.data.model.GroupInviteEntity
import com.example.data.model.MonthlyRewardEntity
import com.example.data.model.SiblingAccountEntity
import com.example.data.model.WithdrawalRequestEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

import com.example.data.supabase.SupabaseSyncService

class FamilyRepository(
    private val dao: FamilyDao,
    private val syncService: SupabaseSyncService = SupabaseSyncService()
) {

    /**
     * Sincronização real entre celulares.
     * Deve ser chamada periodicamente (loop do ViewModel).
     * Retorna quantas mensagens novas foram gravadas localmente.
     */
    suspend fun syncIncomingMessages(profile: ChildProfileEntity, myIdentity: String): Int {
        val token = try {
            syncService.ensureAuthenticated(profile)
        } catch (_: Exception) { null } ?: return 0

        val incoming = syncService.fetchIncomingMessages(profile)
        if (incoming.isEmpty()) return 0

        var inserted = 0
        val deliveredIds = mutableListOf<String>()
        for ((remoteId, msg) in incoming) {
            // Dedup: já baixei esta mensagem antes?
            if (dao.getMessageByRemoteId(remoteId) != null) {
                deliveredIds.add(remoteId)
                continue
            }
            // Mapeia o remetente remoto para um contato local
            val senderIdentity = msg.senderIdentity
            val contact = dao.getContactByRemoteIdentityOnce(senderIdentity)
                ?: dao.getContactByPhoneOrEmailOnce(senderIdentity)

            val finalContactId = contact?.id ?: run {
                // Remetente desconhecido: cria contato PENDENTE para aprovação dos pais
                val newId = dao.insertContact(
                    ContactEntity(
                        name = senderIdentity.substringBefore("@").ifBlank { "Desconhecido" },
                        phone = senderIdentity,
                        relationship = "Novo contato",
                        relationshipType = "EXTERNAL",
                        isApprovedByParent = false,
                        safetyStatus = "PENDENTE",
                        remoteIdentity = senderIdentity
                    )
                )
                newId
            }

            // Persiste SEM o base64 (o conteúdo mora num arquivo local; o payload
            // inline é só do trânsito na fila) e aponta mediaUri para o arquivo.
            dao.insertMessage(
                msg.copy(
                    contactId = finalContactId,
                    // Aplica o filtro divertido nas mensagens recebidas
                    text = com.example.util.FunProfanityFilter.filter(msg.text).sanitizedText,
                    mediaBase64 = "",
                    mediaUri = decodeMediaToLocalFile(msg)
                )
            )
            inserted++
            deliveredIds.add(remoteId)
        }

        // Remove da fila do servidor o que já foi coletado
        syncService.deleteDelivered(deliveredIds)
        return inserted
    }

    /**
     * Decodifica a mídia inline (base64) para um arquivo privado do app e
     * retorna o caminho local. Mensagens sem base64 voltam inalteradas.
     */
    private fun decodeMediaToLocalFile(message: ChatMessageEntity): String {
        val b64 = message.mediaBase64
        if (b64.isBlank()) return message.mediaUri
        return try {
            val dir = File(
                SupabaseSyncService.appContext?.filesDir ?: return message.mediaUri,
                "received_media"
            )
            if (!dir.exists()) dir.mkdirs()
            val ext = when (message.mediaType.uppercase()) {
                "IMAGE" -> "jpg"
                "AUDIO" -> "m4a"
                else -> "bin"
            }
            val name = message.remoteId.ifBlank { UUID.randomUUID().toString() }
            val file = File(dir, "msg_$name.$ext")
            file.outputStream().use { out ->
                out.write(java.util.Base64.getDecoder().decode(b64))
            }
            file.absolutePath
        } catch (_: Exception) {
            message.mediaUri
        }
    }

    val allContacts: Flow<List<ContactEntity>> = dao.getAllContacts()
    val allTasks: Flow<List<FamilyTaskEntity>> = dao.getAllTasks()
    val childProfile: Flow<ChildProfileEntity?> = dao.getChildProfile()
    val monthlyRewards: Flow<List<MonthlyRewardEntity>> = dao.getMonthlyRewards()
    val financialTransactions: Flow<List<FinancialTransactionEntity>> = dao.getAllTransactions()
    val allHighlights: Flow<List<FamilyHighlightEntity>> = dao.getAllHighlights()
    val allGroups: Flow<List<FamilyGroupEntity>> = dao.getAllGroups()
    val allGroupInvites: Flow<List<GroupInviteEntity>> = dao.getAllGroupInvites()
    val allWithdrawalRequests: Flow<List<WithdrawalRequestEntity>> = dao.getAllWithdrawalRequests()
    val allSiblingAccounts: Flow<List<SiblingAccountEntity>> = dao.getAllSiblingAccounts()

    fun getMessagesForContact(contactId: Long): Flow<List<ChatMessageEntity>> {
        return dao.getMessagesForContact(contactId)
    }

    suspend fun sendMessage(
        message: ChatMessageEntity,
        myIdentity: String = "",
        contactRemoteIdentity: String = ""
    ): Long {
        val id = dao.insertMessage(message)
        try {
            // Preenche nome/código da família do remetente (usados pela
            // supervisão feita no servidor via RLS).
            val profile = dao.getChildProfile().first()
            syncService.sendMessage(
                message.copy(
                    id = id,
                    senderIdentity = myIdentity,
                    recipientIdentity = contactRemoteIdentity,
                    senderName = message.senderName.ifBlank { profile?.name.orEmpty() },
                    familyCode = message.familyCode.ifBlank { profile?.familyCode.orEmpty() }
                )
            )
        } catch (_: Exception) {}
        return id
    }

    suspend fun approveContact(contact: ContactEntity) {
        dao.updateContact(
            contact.copy(
                isApprovedByParent = true,
                safetyStatus = "APROVADO",
                relationshipType = contact.relationshipType.ifBlank { "EXTERNAL" }
            )
        )
    }

    suspend fun blockContact(contact: ContactEntity) {
        dao.updateContact(
            contact.copy(
                isApprovedByParent = false,
                safetyStatus = "BLOQUEADO",
                relationshipType = contact.relationshipType.ifBlank { "EXTERNAL" }
            )
        )
    }

    suspend fun addContact(contact: ContactEntity): Long {
        return dao.insertContact(contact)
    }

    suspend fun requestGroupCreation(group: FamilyGroupEntity): Long {
        return dao.insertGroup(group.copy(approvalStatus = "PENDING_PARENT", isActive = false))
    }

    suspend fun approveGroup(group: FamilyGroupEntity) {
        dao.updateGroup(group.copy(approvalStatus = "APPROVED", isActive = true))
    }

    suspend fun rejectGroup(group: FamilyGroupEntity) {
        dao.updateGroup(group.copy(approvalStatus = "REJECTED", isActive = false))
    }

    suspend fun requestGroupInvite(invite: GroupInviteEntity): Long {
        return dao.insertGroupInvite(invite.copy(approvalStatus = "PENDING_PARENT"))
    }

    suspend fun approveInvite(invite: GroupInviteEntity) {
        dao.updateGroupInvite(invite.copy(approvalStatus = "APPROVED"))
    }

    suspend fun rejectInvite(invite: GroupInviteEntity) {
        dao.updateGroupInvite(invite.copy(approvalStatus = "REJECTED"))
    }

    suspend fun addTask(task: FamilyTaskEntity): Long {
        return dao.insertTask(task)
    }

    suspend fun markTaskCompletedByChild(task: FamilyTaskEntity, photoUri: String? = null) {
        dao.updateTask(
            task.copy(
                status = "CONCLUIDO_FILHO",
                photoEvidenceUri = photoUri ?: task.photoEvidenceUri,
                completedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun penalizeOverdueTask(task: FamilyTaskEntity, currentProfile: ChildProfileEntity) {
        dao.updateTask(task.copy(status = "ATRASADO_PENALIZADO"))

        // Deduct penalty amount from child balance (minimum 0.0)
        val newBalance = (currentProfile.balance - task.penaltyAmount).coerceAtLeast(0.0)
        dao.updateProfile(currentProfile.copy(balance = newBalance))

        // Add debit transaction
        dao.insertTransaction(
            FinancialTransactionEntity(
                title = "Desconto por Atraso: ${task.title}",
                amount = task.penaltyAmount,
                type = "DEBITO",
                date = "Hoje",
                category = "Penalidade de Prazo",
                description = "Prazo não cumprido. Descontado R$ ${"%.2f".format(task.penaltyAmount)}"
            )
        )
    }

    suspend fun approveTaskAndPay(task: FamilyTaskEntity, currentProfile: ChildProfileEntity) {
        dao.updateTask(task.copy(status = "APROVADO_PAGO"))

        // Update profile balance & completed tasks count
        val newCompleted = currentProfile.monthlyTasksCompleted + 1
        val newBalance = currentProfile.balance + task.rewardAmount
        val newPoints = currentProfile.monthlyPoints + task.rewardPoints
        dao.updateProfile(
            currentProfile.copy(
                balance = newBalance,
                monthlyTasksCompleted = newCompleted,
                monthlyPoints = newPoints
            )
        )

        // Add transaction
        dao.insertTransaction(
            FinancialTransactionEntity(
                title = "Tarefa Concluída: ${task.title}",
                amount = task.rewardAmount,
                type = "CREDITO",
                date = "Hoje",
                category = "Recompensa Tarefa"
            )
        )

        // If task is recurring, automatically spawn the next cycle occurrence!
        if (task.recurrence != "NUNCA") {
            val nextDueDate = when (task.recurrence) {
                "DIARIA" -> "Amanhã"
                "DIAS_UTEIS" -> "Próximo dia útil"
                "FINS_DE_SEMANA" -> "Próximo fim de semana"
                "SEMANAL" -> "Próxima semana"
                else -> "Próxima data"
            }
            dao.insertTask(
                task.copy(
                    id = 0,
                    status = "PENDENTE",
                    completedAt = null,
                    photoEvidenceUri = null,
                    dueDate = nextDueDate,
                    transferStatus = "NONE",
                    transferTargetSibling = null,
                    transferRequestedBy = null
                )
            )
        }
    }

    // Task Transfer between siblings
    suspend fun requestTaskTransfer(task: FamilyTaskEntity, targetSibling: String, requestedBy: String) {
        dao.updateTask(
            task.copy(
                transferStatus = "WAITING_SIBLING",
                transferTargetSibling = targetSibling,
                transferRequestedBy = requestedBy
            )
        )
        // Chat notice
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        dao.insertMessage(
            ChatMessageEntity(
                contactId = 1L,
                sender = "SYSTEM",
                text = "🔄 Pedido de Transferência de Tarefa: $requestedBy solicitou passar a tarefa '${task.title}' para $targetSibling. Aguardando $targetSibling aceitar.",
                mediaType = "TEXT",
                formattedTime = time
            )
        )
    }

    suspend fun siblingAcceptTaskTransfer(task: FamilyTaskEntity) {
        dao.updateTask(
            task.copy(
                transferStatus = "WAITING_PARENT"
            )
        )
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        dao.insertMessage(
            ChatMessageEntity(
                contactId = 1L,
                sender = "SYSTEM",
                text = "👍 ${task.transferTargetSibling} aceitou assumir a tarefa '${task.title}'! Aguardando autorização final dos Pais.",
                mediaType = "TEXT",
                formattedTime = time
            )
        )
    }

    suspend fun parentApproveTaskTransfer(task: FamilyTaskEntity) {
        val newAssignee = task.transferTargetSibling ?: task.assignedChildName
        dao.updateTask(
            task.copy(
                assignedChildName = newAssignee,
                transferStatus = "NONE",
                transferTargetSibling = null,
                transferRequestedBy = null
            )
        )
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        dao.insertMessage(
            ChatMessageEntity(
                contactId = 1L,
                sender = "SYSTEM",
                text = "✅ Transferência Aprovada pelos Pais! A tarefa '${task.title}' agora pertence oficialmente a $newAssignee.",
                mediaType = "TEXT",
                formattedTime = time
            )
        )
    }

    suspend fun rejectTaskTransfer(task: FamilyTaskEntity, rejectedBy: String) {
        dao.updateTask(
            task.copy(
                transferStatus = "NONE",
                transferTargetSibling = null,
                transferRequestedBy = null
            )
        )
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        dao.insertMessage(
            ChatMessageEntity(
                contactId = 1L,
                sender = "SYSTEM",
                text = "❌ Transferência da tarefa '${task.title}' foi recusada por $rejectedBy.",
                mediaType = "TEXT",
                formattedTime = time
            )
        )
    }

    // Withdrawal / Resgate em Mãos / Dívida Paga
    suspend fun requestWithdrawal(childName: String, amount: Double, reason: String) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        dao.insertWithdrawalRequest(
            WithdrawalRequestEntity(
                childName = childName,
                amount = amount,
                reason = reason,
                requestDate = "Hoje às $time",
                status = "PENDENTE"
            )
        )
        dao.insertMessage(
            ChatMessageEntity(
                contactId = 1L,
                sender = "SYSTEM",
                text = "💵 Pedido de Saque em Mãos: $childName solicitou resgate de R$ ${"%.2f".format(amount)} ($reason). Os pais podem pagar em mãos e confirmar no app.",
                mediaType = "TEXT",
                formattedTime = time
            )
        )
    }

    suspend fun rejectWithdrawal(request: WithdrawalRequestEntity) {
        dao.updateWithdrawalRequest(request.copy(status = "RECUSADO"))
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        dao.insertMessage(
            ChatMessageEntity(
                contactId = 1L,
                sender = "SYSTEM",
                text = "❌ Solicitação de saque de R$ ${"%.2f".format(request.amount)} de ${request.childName} foi recusada pelos pais.",
                mediaType = "TEXT",
                formattedTime = time
            )
        )
    }

    suspend fun approveWithdrawalAndPayInHand(
        request: WithdrawalRequestEntity,
        currentProfile: ChildProfileEntity,
        siblings: List<SiblingAccountEntity>
    ) {
        dao.updateWithdrawalRequest(request.copy(status = "PAGO_EM_MAOS"))

        // Deduct from profile balance
        val newBalance = (currentProfile.balance - request.amount).coerceAtLeast(0.0)
        dao.updateProfile(currentProfile.copy(balance = newBalance))

        // Update sibling account if exists
        siblings.find { it.name.equals(request.childName, ignoreCase = true) }?.let { sibling ->
            dao.updateSiblingAccount(sibling.copy(balance = (sibling.balance - request.amount).coerceAtLeast(0.0)))
        }

        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        // Record debit transaction
        dao.insertTransaction(
            FinancialTransactionEntity(
                title = "Dívida Paga (Dinheiro em Mãos)",
                amount = request.amount,
                type = "DEBITO",
                date = "Hoje",
                category = "Saque em Espécie",
                description = "Dinheiro entregue em mãos para ${request.childName} (${request.reason})"
            )
        )

        // Message
        dao.insertMessage(
            ChatMessageEntity(
                contactId = 1L,
                sender = "SYSTEM",
                text = "🤝 Dívida Paga / Dinheiro em Mãos! Responsável entregou R$ ${"%.2f".format(request.amount)} em mãos para ${request.childName}. Valor debitado do cofrinho.",
                mediaType = "TEXT",
                formattedTime = time
            )
        )
    }

    // Transfer Money between Siblings
    suspend fun transferMoneyBetweenSiblings(
        fromSibling: String,
        toSibling: String,
        amount: Double,
        reason: String,
        currentProfile: ChildProfileEntity,
        siblings: List<SiblingAccountEntity>
    ) {
        // If sender is active profile child (Pedro)
        if (fromSibling.equals(currentProfile.name, ignoreCase = true)) {
            val newBal = (currentProfile.balance - amount).coerceAtLeast(0.0)
            dao.updateProfile(currentProfile.copy(balance = newBal))
        }

        // Update sibling accounts
        siblings.find { it.name.equals(fromSibling, ignoreCase = true) }?.let { sender ->
            dao.updateSiblingAccount(sender.copy(balance = (sender.balance - amount).coerceAtLeast(0.0)))
        }
        siblings.find { it.name.equals(toSibling, ignoreCase = true) }?.let { receiver ->
            dao.updateSiblingAccount(receiver.copy(balance = receiver.balance + amount))
        }

        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        // Insert transaction records
        dao.insertTransaction(
            FinancialTransactionEntity(
                title = "Transferência enviada para $toSibling",
                amount = amount,
                type = "DEBITO",
                date = "Hoje",
                category = "Transferência entre Irmãos",
                description = reason.ifBlank { "Envio para irmão(ã)" }
            )
        )

        dao.insertMessage(
            ChatMessageEntity(
                contactId = 1L,
                sender = "SYSTEM",
                text = "💸 Transferência entre Irmãos: $fromSibling transferiu R$ ${"%.2f".format(amount)} para $toSibling! \"$reason\"",
                mediaType = "TEXT",
                formattedTime = time
            )
        )
    }

    // Direct Cash Handout / Debt Paid by Parent without previous request
    suspend fun recordDirectCashPayment(
        childName: String,
        amount: Double,
        reason: String,
        currentProfile: ChildProfileEntity
    ) {
        val newBalance = (currentProfile.balance - amount).coerceAtLeast(0.0)
        dao.updateProfile(currentProfile.copy(balance = newBalance))

        dao.insertTransaction(
            FinancialTransactionEntity(
                title = "Dívida Paga (Dinheiro em Mãos)",
                amount = amount,
                type = "DEBITO",
                date = "Hoje",
                category = "Saque em Espécie",
                description = "Entrega em espécie para $childName: $reason"
            )
        )
    }

    // Highlights / Stories ("Manchetes & Destaques")
    suspend fun addHighlight(highlight: FamilyHighlightEntity): Long {
        return dao.insertHighlight(highlight)
    }

    suspend fun likeHighlight(highlight: FamilyHighlightEntity) {
        dao.updateHighlight(highlight.copy(likesCount = highlight.likesCount + 1))
    }

    suspend fun insertProfile(profile: ChildProfileEntity) {
        dao.insertProfile(profile)
        try {
            syncService.syncProfile(profile)
        } catch (_: Exception) {}
    }

    suspend fun updateProfile(profile: ChildProfileEntity) {
        dao.updateProfile(profile)
        try {
            syncService.syncProfile(profile)
        } catch (_: Exception) {}
    }

    suspend fun updateAlarmStatus(isRinging: Boolean, currentProfile: ChildProfileEntity) {
        dao.updateProfile(currentProfile.copy(isUrgentAlarmRinging = isRinging))
    }

    suspend fun unlockReward(reward: MonthlyRewardEntity) {
        dao.updateMonthlyReward(reward.copy(isUnlocked = true))
    }

    suspend fun claimReward(reward: MonthlyRewardEntity, currentProfile: ChildProfileEntity) {
        dao.updateMonthlyReward(reward.copy(isClaimed = true))
        val newBalance = currentProfile.balance + reward.bonusAmount
        dao.updateProfile(currentProfile.copy(balance = newBalance))

        dao.insertTransaction(
            FinancialTransactionEntity(
                title = "Bônus Meta: ${reward.title}",
                amount = reward.bonusAmount,
                type = "CREDITO",
                date = "Hoje",
                category = "Recompensa Mensal"
            )
        )
    }

    suspend fun addAllowance(amount: Double, description: String, currentProfile: ChildProfileEntity) {
        val newBalance = currentProfile.balance + amount
        dao.updateProfile(currentProfile.copy(balance = newBalance))

        dao.insertTransaction(
            FinancialTransactionEntity(
                title = "Mesada / Depósito Familiar",
                amount = amount,
                type = "CREDITO",
                date = "Hoje",
                description = description
            )
        )
    }
}
