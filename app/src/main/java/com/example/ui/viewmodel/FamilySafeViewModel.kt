package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChildProfileEntity
import com.example.data.model.ContactEntity
import com.example.data.model.FamilyGroupEntity
import com.example.data.model.FamilyHighlightEntity
import com.example.data.model.FamilyTaskEntity
import com.example.data.model.GroupInviteEntity
import com.example.data.model.FinancialTransactionEntity
import com.example.data.model.MonthlyRewardEntity
import com.example.data.model.SiblingAccountEntity
import com.example.data.model.WithdrawalRequestEntity
import com.example.data.repository.FamilyRepository
import com.example.util.AudioAlertManager
import com.example.util.FunProfanityFilter
import com.example.util.OnboardingRules
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.util.AppUpdateInfo
import com.example.util.GitHubUpdateManager
import android.content.Context

enum class AppRole {
    PARENT,
    CHILD,
    FRIEND_SIMPLIFIED
}

enum class ParentTab {
    CONVERSAS,
    TAREFAS_COFRINHO,
    PRIVACIDADE_SUPERVISAO,
    LOCALIZACAO_ALARME
}


enum class ChildTab {
    CHATS,
    MINHAS_TAREFAS,
    COFRINHO_METAS,
    SOS_CHECKIN
}

class FamilySafeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = FamilyRepository(database.familyDao())
    private val audioAlertManager = AudioAlertManager(application)

    // Current Mode & Navigation
    private val _currentRole = MutableStateFlow(AppRole.PARENT)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _parentTab = MutableStateFlow(ParentTab.CONVERSAS)
    val parentTab: StateFlow<ParentTab> = _parentTab.asStateFlow()

    private val _childTab = MutableStateFlow(ChildTab.CHATS)
    val childTab: StateFlow<ChildTab> = _childTab.asStateFlow()

    private val _selectedContactId = MutableStateFlow<Long?>(null)
    val selectedContactId: StateFlow<Long?> = _selectedContactId.asStateFlow()

    // Auto-Update States
    val availableUpdate = MutableStateFlow<AppUpdateInfo?>(null)
    val isDownloadingUpdate = MutableStateFlow(false)
    val updateDownloadProgress = MutableStateFlow(0f)
    val showUpdateDialog = MutableStateFlow(false)

    // Data from Room
    val contacts: StateFlow<List<ContactEntity>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<FamilyTaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val childProfile: StateFlow<ChildProfileEntity?> = repository.childProfile
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    val showRegistrationDialog = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            childProfile.collect { profile ->
                showRegistrationDialog.value = profile == null
            }
        }
        checkForAppUpdates()
    }

    fun checkForAppUpdates() {
        viewModelScope.launch {
            val info = GitHubUpdateManager.checkForUpdates()
            if (info.hasUpdate) {
                availableUpdate.value = info
                showUpdateDialog.value = true
            }
        }
    }

    fun downloadAndApplyUpdate(context: Context) {
        val update = availableUpdate.value ?: return
        val url = update.downloadUrl ?: return
        if (isDownloadingUpdate.value) return

        viewModelScope.launch {
            isDownloadingUpdate.value = true
            updateDownloadProgress.value = 0f
            val file = GitHubUpdateManager.downloadApk(
                context = context,
                apkUrl = url,
                onProgress = { progress ->
                    updateDownloadProgress.value = progress
                }
            )
            isDownloadingUpdate.value = false
            if (file != null && file.exists()) {
                showUpdateDialog.value = false
                GitHubUpdateManager.triggerApkInstallation(context, file)
            }
        }
    }

    val monthlyRewards: StateFlow<List<MonthlyRewardEntity>> = repository.monthlyRewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<FinancialTransactionEntity>> = repository.financialTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val highlights: StateFlow<List<FamilyHighlightEntity>> = repository.allHighlights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val familyGroups: StateFlow<List<FamilyGroupEntity>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groupInvites: StateFlow<List<GroupInviteEntity>> = repository.allGroupInvites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val withdrawalRequests: StateFlow<List<WithdrawalRequestEntity>> = repository.allWithdrawalRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val siblingAccounts: StateFlow<List<SiblingAccountEntity>> = repository.allSiblingAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentChatMessages: StateFlow<List<ChatMessageEntity>> = _selectedContactId
        .flatMapLatest { id ->
            if (id != null) repository.getMessagesForContact(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Audio Voice Recording & Playing State
    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()
    private var recordTimerJob: Job? = null

    private val _playingAudioMessageId = MutableStateFlow<Long?>(null)
    val playingAudioMessageId: StateFlow<Long?> = _playingAudioMessageId.asStateFlow()

    private val _audioPlayProgress = MutableStateFlow(0f)
    val audioPlayProgress: StateFlow<Float> = _audioPlayProgress.asStateFlow()
    private var audioPlayJob: Job? = null

    // Dialog and Modal states
    val showCreateTaskDialog = MutableStateFlow(false)
    val showCreateHighlightDialog = MutableStateFlow(false)
    val selectedHighlight = MutableStateFlow<FamilyHighlightEntity?>(null)
    val taskToCompleteWithCamera = MutableStateFlow<FamilyTaskEntity?>(null)
    val pendingContactToReview = MutableStateFlow<ContactEntity?>(null)
    val showPinDialog = MutableStateFlow(false)
    val showAddFundsDialog = MutableStateFlow(false)
    val previewMediaMessage = MutableStateFlow<ChatMessageEntity?>(null)

    // New Sibling & Withdrawal Dialog States
    val showRequestWithdrawalDialog = MutableStateFlow(false)
    val showTransferMoneyDialog = MutableStateFlow(false)
    val taskToTransferToSibling = MutableStateFlow<FamilyTaskEntity?>(null)
    val showDirectDebtPaidDialog = MutableStateFlow(false)
    val withdrawalFeedbackMessage = MutableStateFlow<String?>(null)

    // Simplified Friend Mode and Parent Takeover States
    val friendGuestName = MutableStateFlow("Mariana (Amiguinha)")
    val isParentLinked = MutableStateFlow(false)
    val familyInviteCode: String
        get() = childProfile.value?.familyCode?.ifBlank { "" } ?: ""
    val showInviteParentsDialog = MutableStateFlow(false)
    val showAddNewContactDialog = MutableStateFlow(false)
    val showEncryptionInfoDialog = MutableStateFlow(false)
    val showProfileEditorDialog = MutableStateFlow(false)
    val familyJoinMessage = MutableStateFlow<String?>(null)
    val funFilterToastMessage = MutableStateFlow<String?>(null)
    val isParentReviewModeActive = MutableStateFlow(false)
    val parentContactFilter = MutableStateFlow("TODOS")
    val taskChildFilter = MutableStateFlow("Todos")

    fun joinFamilyWithCode(childName: String, code: String): Boolean {
        val current = childProfile.value ?: return false
        val normalizedCode = OnboardingRules.normalizeFamilyCode(code)
        if (normalizedCode.isBlank()) return false

        val expectedCode = current.familyCode.ifBlank { "" }
        if (!OnboardingRules.matchesFamilyCode(normalizedCode, expectedCode)) {
            familyJoinMessage.value = "Código inválido. Peça ao responsável para compartilhar o código correto."
            return false
        }

        val hasExistingFamilyLink = current.familyCode.isNotBlank()
        if (!hasExistingFamilyLink) {
            familyJoinMessage.value = "Seu perfil ainda não foi vinculado à família. Crie o perfil do responsável primeiro."
            return false
        }

        viewModelScope.launch {
            repository.updateProfile(
                current.copy(
                    familyCode = expectedCode,
                    monitoringEnabled = false,
                    funnyFilterEnabled = true,
                    profileStatus = "ATIVO"
                )
            )
            familyJoinMessage.value = "Você entrou na família com sucesso. Aguardando sincronização dos responsáveis."
            _currentRole.value = AppRole.CHILD
        }
        return true
    }


    // Role switching
    fun switchRole(role: AppRole) {
        _currentRole.value = role
        if (role != AppRole.PARENT) {
            isParentReviewModeActive.value = false
        }
    }

    fun setParentTab(tab: ParentTab) {
        _parentTab.value = tab
    }

    fun setChildTab(tab: ChildTab) {
        _childTab.value = tab
    }

    fun openChat(contactId: Long) {
        _selectedContactId.value = contactId
        isParentReviewModeActive.value = false
    }

    fun openChatForParentReview(contactId: Long) {
        _selectedContactId.value = contactId
        isParentReviewModeActive.value = true
    }

    fun closeChat() {
        _selectedContactId.value = null
        isParentReviewModeActive.value = false
        stopAudioPlayback()
    }

    // Friend / Parent takeover flow
    fun linkParentToFriendAccount(parentName: String, pin: String) {
        viewModelScope.launch {
            isParentLinked.value = true
            showInviteParentsDialog.value = false
            val current = childProfile.value ?: ChildProfileEntity()
            repository.updateProfile(
                current.copy(
                    parentPin = pin,
                    name = friendGuestName.value.replace(" (Amiguinha)", "")
                )
            )
            // Send system message in family chat
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            repository.sendMessage(
                ChatMessageEntity(
                    contactId = 1L,
                    sender = "SYSTEM",
                    text = "🛡️ Conta assumida pelo responsável $parentName! Modo Proteção Familiar e Supervisão ativados com sucesso.",
                    mediaType = "TEXT",
                    formattedTime = time
                )
            )
            // Switch to Parent Dashboard directly to welcome parent
            _currentRole.value = AppRole.PARENT
        }
    }

    // Add and Manage Contacts
    fun addNewContact(name: String, phone: String, relationship: String, isApproved: Boolean) {
        viewModelScope.launch {
            val newContact = ContactEntity(
                name = name,
                phone = phone,
                relationship = relationship,
                isApprovedByParent = isApproved,
                safetyStatus = if (isApproved) "APROVADO" else "PENDENTE",
                isOnline = true,
                lastSeen = "Online",
                avatarColor = 0xFF0284C7
            )
            val id = repository.addContact(newContact)
            showAddNewContactDialog.value = false

            // If added by child as pending, send notification in chat
            if (!isApproved) {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                repository.sendMessage(
                    ChatMessageEntity(
                        contactId = 1L,
                        sender = "SYSTEM",
                        text = "🔔 Novo pedido de amizade: Seu filho pediu para conversar com $name ($phone). Você pode autorizar no painel de supervisão.",
                        mediaType = "TEXT",
                        formattedTime = time
                    )
                )
            }
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            // Unapprove/Block or delete
            repository.blockContact(contact)
            if (_selectedContactId.value == contact.id) {
                closeChat()
            }
        }
    }

    // Chat Actions
    fun sendTextMessage(text: String) {
        val contactId = _selectedContactId.value ?: return
        if (text.isBlank()) return

        val contact = contacts.value.find { it.id == contactId }
        val isFamilyConversation = contact != null && contact.relationshipType.equals("FAMILY", true)
        val isExternalConversation = contact != null && contact.relationshipType.equals("EXTERNAL", true)
        val isBlockedConversation = contact != null && contact.safetyStatus.equals("BLOQUEADO", true)

        if (isBlockedConversation || (isExternalConversation && !contact.isApprovedByParent)) {
            funFilterToastMessage.value = "🛡️ Mensagem bloqueada: esse contato não está autorizado para conversar com seu filho."
            return
        }

        viewModelScope.launch {
            val profile = childProfile.value
            val isFunnyFilterOn = profile?.funnyFilterEnabled ?: true
            val sanitizedText: String
            if (isFunnyFilterOn) {
                val result = FunProfanityFilter.filter(text)
                sanitizedText = result.sanitizedText
                if (result.wasFiltered) {
                    val replacedSummary = result.replacedItems.firstOrNull()?.second ?: "💩"
                    funFilterToastMessage.value = "✨ SafeTalk Divertido: Palavrão trocado por risada! ($replacedSummary)"
                }
            } else {
                sanitizedText = text.trim()
            }

            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            repository.sendMessage(
                ChatMessageEntity(
                    contactId = contactId,
                    sender = if (_currentRole.value == AppRole.PARENT) "PARENT" else "ME",
                    text = sanitizedText,
                    mediaType = "TEXT",
                    formattedTime = time
                )
            )

            if (!isFamilyConversation && contact != null && profile != null && profile.userRole == "CHILD") {
                repository.sendMessage(
                    ChatMessageEntity(
                        contactId = 1L,
                        sender = "SYSTEM",
                        text = "👨‍👩‍👧 Supervisão ativa: ${profile.name} enviou uma mensagem para ${contact.name}.",
                        mediaType = "TEXT",
                        formattedTime = time
                    )
                )
            }
        }
    }


    fun sendPhotoMessage(caption: String = "Foto compartilhada") {
        val contactId = _selectedContactId.value ?: return
        viewModelScope.launch {
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            repository.sendMessage(
                ChatMessageEntity(
                    contactId = contactId,
                    sender = if (_currentRole.value == AppRole.PARENT) "PARENT" else "ME",
                    text = caption,
                    mediaType = "IMAGE",
                    mediaUri = "photo_${System.currentTimeMillis()}",
                    formattedTime = time
                )
            )
        }
    }

    fun sendVideoMessage(caption: String = "Vídeo escolar (0:24)") {
        val contactId = _selectedContactId.value ?: return
        viewModelScope.launch {
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            repository.sendMessage(
                ChatMessageEntity(
                    contactId = contactId,
                    sender = if (_currentRole.value == AppRole.PARENT) "PARENT" else "ME",
                    text = caption,
                    mediaType = "VIDEO",
                    mediaUri = "video_${System.currentTimeMillis()}",
                    mediaDurationSeconds = 24,
                    formattedTime = time
                )
            )
        }
    }

    fun sendTaskMessage(task: FamilyTaskEntity) {
        val contactId = _selectedContactId.value ?: return
        viewModelScope.launch {
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val penaltyText = if (task.penaltyAmount > 0) "\n⚠️ Multa se atrasar: -R$ ${"%.2f".format(task.penaltyAmount)}" else ""
            val photoText = if (task.requiresPhotoEvidence) "\n📸 Exige foto ao vivo da câmera" else ""
            val msgText = "📋 [Tarefa] ${task.title}\n💰 Recompensa: R$ ${"%.2f".format(task.rewardAmount)} (+${task.rewardPoints} pts)$penaltyText$photoText\n⏳ Prazo: ${task.dueDate}"
            repository.sendMessage(
                ChatMessageEntity(
                    contactId = contactId,
                    sender = if (_currentRole.value == AppRole.PARENT) "PARENT" else "ME",
                    text = msgText,
                    mediaType = "TASK",
                    mediaUri = "${task.id}",
                    formattedTime = time
                )
            )
        }
    }

    fun startRecordingVoice() {
        _isRecordingAudio.value = true
        _recordingSeconds.value = 0
        recordTimerJob?.cancel()
        recordTimerJob = viewModelScope.launch {
            while (_isRecordingAudio.value) {
                delay(1000)
                _recordingSeconds.value += 1
            }
        }
    }

    fun cancelRecordingVoice() {
        _isRecordingAudio.value = false
        recordTimerJob?.cancel()
        _recordingSeconds.value = 0
    }

    fun finishAndSendVoice() {
        val contactId = _selectedContactId.value ?: return
        val duration = if (_recordingSeconds.value > 0) _recordingSeconds.value else 4
        _isRecordingAudio.value = false
        recordTimerJob?.cancel()
        _recordingSeconds.value = 0

        viewModelScope.launch {
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            repository.sendMessage(
                ChatMessageEntity(
                    contactId = contactId,
                    sender = if (_currentRole.value == AppRole.PARENT) "PARENT" else "ME",
                    text = "Mensagem de voz (${duration}s)",
                    mediaType = "AUDIO",
                    mediaUri = "voice_${System.currentTimeMillis()}",
                    mediaDurationSeconds = duration,
                    formattedTime = time
                )
            )
        }
    }

    fun togglePlayAudio(messageId: Long, durationSeconds: Int) {
        if (_playingAudioMessageId.value == messageId) {
            stopAudioPlayback()
        } else {
            stopAudioPlayback()
            _playingAudioMessageId.value = messageId
            _audioPlayProgress.value = 0f
            val totalSteps = (durationSeconds.coerceAtLeast(3)) * 10
            audioPlayJob = viewModelScope.launch {
                for (step in 1..totalSteps) {
                    delay(100)
                    _audioPlayProgress.value = step.toFloat() / totalSteps
                }
                stopAudioPlayback()
            }
        }
    }

    fun stopAudioPlayback() {
        audioPlayJob?.cancel()
        _playingAudioMessageId.value = null
        _audioPlayProgress.value = 0f
    }

    // Parental Controls: Approve or Block Contact
    fun approveContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.approveContact(contact)
            pendingContactToReview.value = null
        }
    }

    fun blockContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.blockContact(contact)
            pendingContactToReview.value = null
        }
    }

    // Tasks & Chores Management
    fun createNewTask(
        title: String,
        description: String,
        rewardAmount: Double,
        rewardPoints: Int,
        category: String,
        dueDate: String,
        assignedChildName: String = "",
        penaltyAmount: Double = 2.00,
        requiresPhotoEvidence: Boolean = false,
        recurrence: String = "NUNCA"
    ) {
        viewModelScope.launch {
            repository.addTask(
                FamilyTaskEntity(
                    title = title.ifBlank { "Nova Tarefa" },
                    description = description,
                    rewardAmount = rewardAmount,
                    rewardPoints = rewardPoints,
                    penaltyAmount = penaltyAmount,
                    dueDate = dueDate.ifBlank { "Hoje" },
                    category = category,
                    status = "PENDENTE",
                    assignedChildName = assignedChildName.ifBlank { childProfile.value?.name ?: "Meu filho" },
                    requiresPhotoEvidence = requiresPhotoEvidence,
                    recurrence = recurrence
                )
            )
            showCreateTaskDialog.value = false
        }
    }

    fun markTaskDoneByChild(task: FamilyTaskEntity, photoUri: String? = null) {
        viewModelScope.launch {
            repository.markTaskCompletedByChild(task, photoUri)
        }
    }

    fun penalizeOverdueTask(task: FamilyTaskEntity) {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            repository.penalizeOverdueTask(task, current)
        }
    }

    fun approveTaskAndPayParent(task: FamilyTaskEntity) {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            repository.approveTaskAndPay(task, current)
            checkRewardMilestones()
        }
    }

    // Check if child has any pending tasks blocking withdrawal
    fun hasPendingTasksForChild(childName: String = "Pedro"): Boolean {
        val currentTasks = tasks.value
        return currentTasks.any { task ->
            task.status == "PENDENTE" && (task.assignedChildName.equals(childName, ignoreCase = true) || task.assignedChildName.equals("Todos os Filhos", ignoreCase = true) || task.assignedChildName.equals("Todos", ignoreCase = true))
        }
    }

    // Request Withdrawal ("Solicitar Saque em Mãos")
    fun requestWithdrawal(amount: Double, reason: String) {
        val profile = childProfile.value ?: return
        if (hasPendingTasksForChild(profile.name)) {
            withdrawalFeedbackMessage.value = "⚠️ Bloqueado: Você possui missões pendentes! Conclua todas as tarefas antes de solicitar resgate do cofre."
            return
        }
        if (amount <= 0 || amount > profile.balance) {
            withdrawalFeedbackMessage.value = "⚠️ Valor inválido! O valor deve ser maior que zero e menor ou igual ao seu saldo de R$ ${"%.2f".format(profile.balance)}."
            return
        }

        viewModelScope.launch {
            repository.requestWithdrawal(profile.name, amount, reason.ifBlank { "Saque em mãos para lanche/gastos" })
            showRequestWithdrawalDialog.value = false
            withdrawalFeedbackMessage.value = "✅ Solicitação de saque de R$ ${"%.2f".format(amount)} enviada para os pais!"
        }
    }

    // Parent Approves Withdrawal and Hands Cash ("Dívida Paga")
    fun approveWithdrawal(request: WithdrawalRequestEntity) {
        val current = childProfile.value ?: return
        val siblings = siblingAccounts.value
        viewModelScope.launch {
            repository.approveWithdrawalAndPayInHand(request, current, siblings)
        }
    }

    fun rejectWithdrawal(request: WithdrawalRequestEntity) {
        viewModelScope.launch {
            repository.rejectWithdrawal(request)
        }
    }

    fun dismissWithdrawalFeedback() {
        withdrawalFeedbackMessage.value = null
    }

    // Direct Cash Handout / Debt Paid by Parent without prior request
    fun recordDirectCashDebtPaid(childName: String, amount: Double, reason: String) {
        val current = childProfile.value ?: return
        if (amount <= 0 || amount > current.balance) return
        viewModelScope.launch {
            repository.recordDirectCashPayment(childName, amount, reason, current)
            showDirectDebtPaidDialog.value = false
        }
    }

    fun directDebtPaid(childName: String, amount: Double, reason: String) {
        recordDirectCashDebtPaid(childName, amount, reason)
    }

    // Transfer money between siblings
    fun transferMoneyBetweenSiblings(fromSibling: String, toSibling: String, amount: Double, reason: String) {
        val profile = childProfile.value ?: return
        val siblings = siblingAccounts.value

        val senderBalance = if (fromSibling.equals(profile.name, ignoreCase = true)) {
            profile.balance
        } else {
            siblings.find { it.name.equals(fromSibling, ignoreCase = true) }?.balance ?: 0.0
        }

        if (amount <= 0 || amount > senderBalance) {
            withdrawalFeedbackMessage.value = "⚠️ Saldo insuficiente para transferir R$ ${"%.2f".format(amount)}."
            return
        }

        viewModelScope.launch {
            repository.transferMoneyBetweenSiblings(fromSibling, toSibling, amount, reason, profile, siblings)
            showTransferMoneyDialog.value = false
            withdrawalFeedbackMessage.value = "✅ R$ ${"%.2f".format(amount)} transferidos com sucesso para $toSibling!"
        }
    }

    fun transferMoneyToSibling(fromSibling: String, toSibling: String, amount: Double, reason: String) {
        transferMoneyBetweenSiblings(fromSibling, toSibling, amount, reason)
    }

    // Task Transfer between siblings workflow
    fun requestTaskTransfer(task: FamilyTaskEntity, targetSibling: String) {
        val currentChild = childProfile.value?.name ?: "Pedro"
        viewModelScope.launch {
            repository.requestTaskTransfer(task, targetSibling, currentChild)
            taskToTransferToSibling.value = null
        }
    }

    fun siblingAcceptTaskTransfer(task: FamilyTaskEntity) {
        viewModelScope.launch {
            repository.siblingAcceptTaskTransfer(task)
        }
    }

    fun acceptTaskTransfer(task: FamilyTaskEntity) {
        siblingAcceptTaskTransfer(task)
    }

    fun parentApproveTaskTransfer(task: FamilyTaskEntity) {
        viewModelScope.launch {
            repository.parentApproveTaskTransfer(task)
        }
    }

    fun approveTaskTransfer(task: FamilyTaskEntity) {
        parentApproveTaskTransfer(task)
    }

    fun rejectTaskTransfer(task: FamilyTaskEntity, rejectedBy: String = "PARENTS") {
        viewModelScope.launch {
            repository.rejectTaskTransfer(task, rejectedBy)
        }
    }

    fun startPhotoCaptureForTask(task: FamilyTaskEntity) {
        taskToCompleteWithCamera.value = task
    }

    fun cancelPhotoCapture() {
        taskToCompleteWithCamera.value = null
    }

    fun completeTaskWithPhotoEvidence(task: FamilyTaskEntity, photoUri: String) {
        markTaskDoneByChild(task, photoUri)
        taskToCompleteWithCamera.value = null
    }

    fun sendSafetyCheckIn() {
        childCheckIn()
    }

    fun sendDepartureNotice() {
        childDepartedLocation()
    }

    fun triggerEmergencySos() {
        childSignalLostOrHelpNeeded()
    }

    // Highlights / Stories ("Manchetes & Destaques da Família")
    fun requestGroupCreation(groupName: String, description: String = "", creatorName: String = "Pedro", creatorRole: String = "CHILD") {
        if (groupName.isBlank()) return
        viewModelScope.launch {
            repository.requestGroupCreation(
                FamilyGroupEntity(
                    groupName = groupName,
                    description = description,
                    creatorName = creatorName,
                    creatorRole = creatorRole,
                    createdByParent = false,
                    approvalStatus = "PENDING_PARENT",
                    memberCount = 1,
                    isActive = false
                )
            )
        }
    }

    fun approveGroup(group: FamilyGroupEntity) {
        viewModelScope.launch {
            repository.approveGroup(group)
        }
    }

    fun rejectGroup(group: FamilyGroupEntity) {
        viewModelScope.launch {
            repository.rejectGroup(group)
        }
    }

    fun requestGroupInvite(groupId: Long, inviteeName: String, invitedBy: String, requiresParentApproval: Boolean = true) {
        if (inviteeName.isBlank()) return
        viewModelScope.launch {
            repository.requestGroupInvite(
                GroupInviteEntity(
                    groupId = groupId,
                    inviteeName = inviteeName,
                    invitedBy = invitedBy,
                    requiresParentApproval = requiresParentApproval,
                    approvalStatus = if (requiresParentApproval) "PENDING_PARENT" else "APPROVED"
                )
            )
        }
    }

    fun approveInvite(invite: GroupInviteEntity) {
        viewModelScope.launch {
            repository.approveInvite(invite)
        }
    }

    fun rejectInvite(invite: GroupInviteEntity) {
        viewModelScope.launch {
            repository.rejectInvite(invite)
        }
    }

    fun addHighlight(
        title: String,
        textContent: String,
        authorName: String,
        authorRole: String = "CHILD",
        moodEmoji: String = "🌟",
        audience: String = "FAMILY",
        photoBase64: String? = null
    ) {
        viewModelScope.launch {
            val avatarRes = when {
                authorName.contains("Mãe", ignoreCase = true) || authorName.contains("Juliana", ignoreCase = true) -> "avatar_mae"
                authorName.contains("Mariana", ignoreCase = true) -> "avatar_mariana"
                else -> "avatar_pedro"
            }
            repository.addHighlight(
                FamilyHighlightEntity(
                    authorName = authorName,
                    authorRole = authorRole,
                    avatarDrawableResName = avatarRes,
                    title = title,
                    textContent = textContent,
                    audience = audience,
                    mediaType = if (photoBase64 != null) "CAMERA_PHOTO" else "TEXT",
                    photoBitmapBase64 = photoBase64,
                    timeAgo = "Agora mesmo",
                    moodEmoji = moodEmoji
                )
            )
            showCreateHighlightDialog.value = false
        }
    }

    fun likeHighlight(highlight: FamilyHighlightEntity) {
        viewModelScope.launch {
            repository.likeHighlight(highlight)
        }
    }

    private fun checkRewardMilestones() {
        val current = childProfile.value ?: return
        val currentPct = if (current.monthlyTasksTarget > 0) {
            ((current.monthlyTasksCompleted.toFloat() / current.monthlyTasksTarget) * 100).toInt()
        } else 0

        monthlyRewards.value.forEach { reward ->
            if (!reward.isUnlocked && currentPct >= reward.requiredPercentage) {
                viewModelScope.launch {
                    repository.unlockReward(reward)
                }
            }
        }
    }

    fun claimMonthlyReward(reward: MonthlyRewardEntity) {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            repository.claimReward(reward, current)
        }
    }

    // Financial / Allowance Management
    fun addAllowance(amount: Double, description: String) {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            repository.addAllowance(amount, description, current)
            showAddFundsDialog.value = false
        }
    }

    // Emergency Sound Alarm ("Tocar mesmo no silencioso")
    fun triggerLoudEmergencyAlarm() {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            repository.updateAlarmStatus(true, current)
            audioAlertManager.startEmergencyAlarm()
        }
    }

    fun stopEmergencyAlarm() {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            repository.updateAlarmStatus(false, current)
            audioAlertManager.stopEmergencyAlarm()
        }
    }

    // Child Check-in
    fun childCheckIn(statusNote: String = "Cheguei bem ao destino!") {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            repository.updateProfile(
                current.copy(
                    lastLocationUpdate = "Chegou bem ($time)",
                    isInSafeZone = true
                )
            )
            // Also notify parents in family chat
            repository.sendMessage(
                ChatMessageEntity(
                    contactId = 1L, // Mom
                    sender = "ME",
                    text = "📍 Cheguei Bem! Check-in de segurança confirmado às $time.",
                    mediaType = "TEXT",
                    formattedTime = time
                )
            )
        }
    }

    fun childDepartedLocation() {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            repository.updateProfile(
                current.copy(
                    lastLocationUpdate = "Saiu do local / Em trânsito ($time)",
                    isInSafeZone = false
                )
            )
            repository.sendMessage(
                ChatMessageEntity(
                    contactId = 1L,
                    sender = "ME",
                    text = "🚀 Avisando aos Pais: Estou saindo do local atual às $time. Rota iniciada com monitoramento ativo!",
                    mediaType = "TEXT",
                    formattedTime = time
                )
            )
        }
    }

    fun childSignalLostOrHelpNeeded() {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            repository.updateProfile(
                current.copy(
                    lastLocationUpdate = "Sinal instável / Pedido de Ajuda ($time)",
                    isUrgentAlarmRinging = true
                )
            )
            audioAlertManager.startEmergencyAlarm()
            repository.sendMessage(
                ChatMessageEntity(
                    contactId = 1L,
                    sender = "ME",
                    text = "🚨 ME AJUDA AÍ! Alerta de sinal fraco / pedido de ajuda acionado às $time. Celular dos pais monitorando em tempo real!",
                    mediaType = "TEXT",
                    formattedTime = time
                )
            )
        }
    }

    fun refreshLocation() {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            repository.updateProfile(
                current.copy(
                    lastLocationUpdate = "Atualizado às $time",
                    batteryPercent = (current.batteryPercent - 1).coerceAtLeast(5)
                )
            )
        }
    }

    fun toggleMonitoringEnabled(enabled: Boolean) {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(monitoringEnabled = enabled))
        }
    }

    fun toggleFunnyFilterEnabled(enabled: Boolean) {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(funnyFilterEnabled = enabled))
        }
    }

    fun linkSpouse(spouseName: String, spouseContact: String) {
        val current = childProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(
                current.copy(
                    spouseName = spouseName,
                    spouseContact = spouseContact,
                    isSpouseLinked = true
                )
            )
        }
    }

    fun updateProfileData(
        name: String,
        role: String,
        status: String,
        photoUri: String? = null
    ) {
        val current = childProfile.value ?: ChildProfileEntity()
        viewModelScope.launch {
            repository.updateProfile(
                current.copy(
                    name = name.ifBlank { current.name },
                    familyRole = role.ifBlank { current.familyRole },
                    profileStatus = status.ifBlank { current.profileStatus },
                    profilePhotoUri = photoUri ?: current.profilePhotoUri
                )
            )
            showProfileEditorDialog.value = false
        }
    }

    fun registerUser(
        name: String,
        age: Int,
        loginIdentifier: String,
        isEmail: Boolean,
        pin: String,
        spouseName: String,
        spouseContact: String,
        familyCode: String,
        isAutonomousChild: Boolean
    ) {
        val current = childProfile.value ?: ChildProfileEntity()
        val isAdult = age >= 18
        val requiresCode = OnboardingRules.requiresFamilyCode(age, isAutonomousChild)
        val normalizedCode = OnboardingRules.normalizeFamilyCode(familyCode)

        if (requiresCode && normalizedCode.isBlank()) {
            return
        }

        viewModelScope.launch {
            val resolvedFamilyCode = if (isAdult) {
                OnboardingRules.generateFamilyCode()
            } else {
                normalizedCode
            }

            val updated = current.copy(
                id = 1L,
                name = name.ifBlank { current.name },
                age = age,
                userRole = if (isAdult) "PARENT" else "CHILD",
                familyRole = if (isAdult) "PAI" else "FILHO",
                loginIdentifier = loginIdentifier,
                profileStatus = "ATIVO",
                familyCode = resolvedFamilyCode,
                parentPin = if (pin.isNotBlank()) pin else current.parentPin,
                spouseName = spouseName.ifEmpty { current.spouseName },
                spouseContact = spouseContact.ifEmpty { current.spouseContact },
                isSpouseLinked = isAdult && spouseName.isNotBlank(),
                isAutonomousChild = isAutonomousChild && !isAdult,
                monitoringEnabled = isAdult,
                funnyFilterEnabled = !isAdult
            )

            repository.insertProfile(updated)

            if (isAdult) {
                _currentRole.value = AppRole.PARENT
            } else if (isAutonomousChild) {
                _currentRole.value = AppRole.FRIEND_SIMPLIFIED
            } else {
                _currentRole.value = AppRole.CHILD
            }
            showRegistrationDialog.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioAlertManager.stopEmergencyAlarm()
        stopAudioPlayback()
    }
}

