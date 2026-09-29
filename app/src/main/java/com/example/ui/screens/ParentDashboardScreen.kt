package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import android.net.Uri
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChildProfileEntity
import com.example.data.model.ContactEntity
import com.example.data.model.FamilyHighlightEntity
import com.example.data.model.FamilyTaskEntity
import com.example.data.model.FinancialTransactionEntity
import com.example.data.model.MonthlyRewardEntity
import com.example.data.model.SiblingAccountEntity
import com.example.data.model.WithdrawalRequestEntity
import com.example.ui.components.FamilyHighlightsBar
import com.example.ui.dialogs.CreateHighlightDialog
import com.example.ui.dialogs.HighlightStoryViewerDialog
import com.example.ui.theme.FamilyAccentAmber
import com.example.ui.theme.FamilyAlertRed
import com.example.ui.theme.FamilyAlertRedContainer
import com.example.ui.theme.FamilySuccessGreen
import com.example.ui.theme.SafeTalkPrimary
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.viewmodel.FamilySafeViewModel
import com.example.ui.viewmodel.ParentTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    viewModel: FamilySafeViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.parentTab.collectAsState()
    val childProfile by viewModel.childProfile.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val rewards by viewModel.monthlyRewards.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val highlights by viewModel.highlights.collectAsState()
    val selectedHighlight by viewModel.selectedHighlight.collectAsState()
    val showCreateHighlight by viewModel.showCreateHighlightDialog.collectAsState()
    val withdrawalRequests by viewModel.withdrawalRequests.collectAsState()
    val siblingAccounts by viewModel.siblingAccounts.collectAsState()

    val profile = childProfile ?: ChildProfileEntity()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F5142)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!profile.profilePhotoUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = Uri.parse(profile.profilePhotoUri),
                                    contentDescription = "Foto do perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Controle Parental",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Controle Familiar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Supervisionando ${profile.name} (${profile.age} anos)",
                                fontSize = 11.sp,
                                color = Color(0xFFD1F2EB)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhatsAppDarkTeal
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == ParentTab.CONVERSAS,
                    onClick = { viewModel.setParentTab(ParentTab.CONVERSAS) },
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Conversas")
                    },
                    label = { Text("Conversas", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppDarkTeal,
                        indicatorColor = Color(0xFFD1F2EB)
                    ),
                    modifier = Modifier.testTag("nav_tab_conversas")
                )
                NavigationBarItem(
                    selected = currentTab == ParentTab.PRIVACIDADE_SUPERVISAO,
                    onClick = { viewModel.setParentTab(ParentTab.PRIVACIDADE_SUPERVISAO) },
                    icon = {
                        Icon(Icons.Default.Security, contentDescription = "Supervisão")
                    },
                    label = { Text("Supervisão", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppDarkTeal,
                        indicatorColor = Color(0xFFD1F2EB)
                    ),
                    modifier = Modifier.testTag("nav_tab_privacidade")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ParentTab.CONVERSAS -> {
                    ParentConversationsView(
                        contacts = contacts,
                        highlights = highlights,
                        viewModel = viewModel,
                        currentUserName = profile.name,
                        onOpenChat = { viewModel.openChat(it) }
                    )
                }
                ParentTab.TAREFAS_COFRINHO -> {
                    ParentTasksAndRewardsView(
                        tasks = tasks,
                        rewards = rewards,
                        profile = profile,
                        withdrawalRequests = withdrawalRequests,
                        siblingAccounts = siblingAccounts,
                        transactions = transactions,
                        onApproveTask = { viewModel.approveTaskAndPayParent(it) },
                        onPenalizeTask = { viewModel.penalizeOverdueTask(it) },
                        onCreateTaskClick = { viewModel.showCreateTaskDialog.value = true },
                        onClaimReward = { viewModel.claimMonthlyReward(it) },
                        onApproveWithdrawal = { viewModel.approveWithdrawal(it) },
                        onRejectWithdrawal = { viewModel.rejectWithdrawal(it) },
                        onDirectDebtPaidClick = { viewModel.showDirectDebtPaidDialog.value = true },
                        onApproveTaskTransfer = { viewModel.approveTaskTransfer(it) },
                        onRejectTaskTransfer = { viewModel.rejectTaskTransfer(it) }
                    )
                }
                ParentTab.PRIVACIDADE_SUPERVISAO -> {
                    ParentMonitoringAndPrivacyView(
                        viewModel = viewModel,
                        contacts = contacts,
                        highlights = highlights,
                        onOpenChat = { viewModel.openChatForParentReview(it) },
                        onReviewContact = { viewModel.pendingContactToReview.value = it }
                    )
                }
                ParentTab.LOCALIZACAO_ALARME -> {
                    ParentLocationAndAlarmView(
                        profile = profile,
                        onTriggerAlarm = { viewModel.triggerLoudEmergencyAlarm() },
                        onStopAlarm = { viewModel.stopEmergencyAlarm() },
                        onRefreshLocation = { viewModel.refreshLocation() }
                    )
                }
            }
        }
    }

    // Dialog: Create Highlight / Story by Parent
    if (showCreateHighlight) {
        CreateHighlightDialog(
            currentUserName = profile.name,   // responsável logado, sem nome mockado
            currentUserRole = "PARENT",
            onDismiss = { viewModel.showCreateHighlightDialog.value = false },
            onPublish = { title, text, emoji, hasPhoto, audience ->
                viewModel.addHighlight(
                    title = title,
                    textContent = text,
                    authorName = profile.name,   // autor real, sem nome mockado
                    authorRole = "PARENT",
                    moodEmoji = emoji,
                    audience = audience,
                    photoBase64 = if (hasPhoto) "photo_story_${System.currentTimeMillis()}" else null
                )
            }
        )
    }

    // Dialog: View Highlight / Story
    selectedHighlight?.let { highlight ->
        HighlightStoryViewerDialog(
            highlight = highlight,
            onDismiss = { viewModel.selectedHighlight.value = null },
            onLike = { viewModel.likeHighlight(it) }
        )
    }
}

/**
 * ABA 1: CONVERSAS DOS PAIS COM FILHOS E FAMÍLIA
 */
@Composable
fun ParentConversationsView(
    contacts: List<ContactEntity>,
    highlights: List<FamilyHighlightEntity> = emptyList(),
    viewModel: FamilySafeViewModel,
    currentUserName: String,
    onOpenChat: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Family Highlights & Stories
        item {
            FamilyHighlightsBar(
                highlights = highlights,
                currentUserName = currentUserName,
                onAddNewHighlight = { viewModel.showCreateHighlightDialog.value = true },
                onOpenHighlight = { viewModel.selectedHighlight.value = it }
            )
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Conversas com Seus Filhos e Família",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1E293B)
                )
                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${contacts.size} contatos",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        items(contacts, key = { it.id }) { contact ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenChat(contact.id) }
                    .testTag("parent_chat_item_${contact.id}")
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        contact.name.contains("Mãe", ignoreCase = true) || contact.name.contains("Juliana", ignoreCase = true) -> Color(0xFFE11D48)
                                        contact.name.contains("Lucas", ignoreCase = true) -> Color(0xFF2563EB)
                                        contact.name.contains("Mariana", ignoreCase = true) -> Color(0xFF9333EA)
                                        contact.name.contains("Guilherme", ignoreCase = true) -> Color(0xFF0D9488)
                                        else -> Color(0xFF008069)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {                            // Avatares são sempre as iniciais do nome real — sem imagens mockadas.
                            run {
                                val initials = contact.name.trim().take(2).uppercase()
                                Text(
                                    text = initials,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = contact.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = contact.relationship,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1D4ED8),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${contact.relationship}${if (contact.phone.isBlank()) "" else " • ${contact.phone}"}",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = contact.lastSeen,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        if (contact.unreadCount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppDarkTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${contact.unreadCount}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * ABA 2: TAREFAS, RECOMPENSAS, RESGATES ("DÍVIDA PAGA") E COFRINHO
 */
@Composable
fun ParentTasksAndRewardsView(
    tasks: List<FamilyTaskEntity>,
    rewards: List<MonthlyRewardEntity>,
    profile: ChildProfileEntity,
    withdrawalRequests: List<WithdrawalRequestEntity> = emptyList(),
    siblingAccounts: List<SiblingAccountEntity> = emptyList(),
    transactions: List<FinancialTransactionEntity> = emptyList(),
    onApproveTask: (FamilyTaskEntity) -> Unit,
    onPenalizeTask: (FamilyTaskEntity) -> Unit = {},
    onCreateTaskClick: () -> Unit,
    onClaimReward: (MonthlyRewardEntity) -> Unit,
    onApproveWithdrawal: (WithdrawalRequestEntity) -> Unit = {},
    onRejectWithdrawal: (WithdrawalRequestEntity) -> Unit = {},
    onDirectDebtPaidClick: () -> Unit = {},
    onApproveTaskTransfer: (FamilyTaskEntity) -> Unit = {},
    onRejectTaskTransfer: (FamilyTaskEntity) -> Unit = {}
) {
    var selectedChildFilter by remember { mutableStateOf("Todos") }

    val pendingWithdrawals = withdrawalRequests.filter { it.status == "PENDING" }
    val pendingTaskTransfers = tasks.filter { it.transferStatus == "WAITING_PARENT" }

    val allChildrenNames = remember(tasks, profile, siblingAccounts) {
        val list = mutableListOf("Todos")
        if (profile.name.isNotBlank()) list.add(profile.name)
        siblingAccounts.forEach {
            if (it.name.isNotBlank() && !list.contains(it.name)) list.add(it.name)
        }
        if (!list.contains("Todos os Filhos")) list.add("Todos os Filhos")
        list.distinct()
    }

    val filteredTasks = if (selectedChildFilter == "Todos") {
        tasks
    } else {
        tasks.filter { it.assignedChildName.equals(selectedChildFilter, ignoreCase = true) }
    }

    val completionPct = if (profile.monthlyTasksTarget > 0) {
        ((profile.monthlyTasksCompleted.toFloat() / profile.monthlyTasksTarget) * 100).toInt()
    } else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. SOLICITAÇÕES DE RESGATE EM DINHEIRO ("DÍVIDA PAGA")
        if (pendingWithdrawals.isNotEmpty()) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = Color(0xFF0F766E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Solicitações de Resgate em Mãos (${pendingWithdrawals.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F766E)
                    )
                }
            }

            items(pendingWithdrawals, key = { it.id }) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDFA)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF5EEAD4)),
                    modifier = Modifier.fillMaxWidth().testTag("withdrawal_request_card_${req.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${req.childName} solicitou saque em dinheiro",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF134E4A)
                                )
                                Text(
                                    text = "Motivo: ${req.reason}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF0F766E)
                                )
                                Text(
                                    text = req.requestDate,
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Text(
                                text = "R$ ${"%.2f".format(req.amount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0F766E)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "💡 Entregue o dinheiro físico em mãos para a criança e confirme abaixo para debitar do cofrinho automaticamente.",
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { onRejectWithdrawal(req) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Recusar", fontSize = 11.sp, color = FamilyAlertRed)
                            }

                            Button(
                                onClick = { onApproveWithdrawal(req) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_approve_debt_paid_${req.id}")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Dar em Mãos (Dívida Paga ✓)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. TRANSFERÊNCIA DE TAREFAS ENTRE IRMÃOS (AUTORIZAÇÃO DOS PAIS)
        if (pendingTaskTransfers.isNotEmpty()) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.SyncAlt,
                        contentDescription = null,
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Transferência de Tarefas entre Irmãos (${pendingTaskTransfers.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1D4ED8)
                    )
                }
            }

            items(pendingTaskTransfers, key = { it.id }) { task ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF93C5FD)),
                    modifier = Modifier.fillMaxWidth().testTag("pending_task_transfer_card_${task.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🔄 ${task.transferRequestedBy} quer repassar tarefa para ${task.transferTargetSibling}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tarefa: ${task.title} • Recompensa: R$ ${"%.2f".format(task.rewardAmount)}",
                            fontSize = 12.sp,
                            color = Color(0xFF2563EB)
                        )
                        Text(
                            text = "O irmão (${task.transferTargetSibling}) já concordou em assumir a tarefa. Você autoriza a transferência?",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { onRejectTaskTransfer(task) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Recusar", fontSize = 11.sp, color = FamilyAlertRed)
                            }

                            Button(
                                onClick = { onApproveTaskTransfer(task) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_parent_approve_task_transfer_${task.id}")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Autorizar Troca ✓", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // 3. BOTÃO DE AÇÃO RÁPIDA: PAGAR DÍVIDA EM MÃOS (AVULSO)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "💵 Pagar Dinheiro em Mãos (Dívida Paga)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Entregou dinheiro físico para um filho? Debite do cofrinho dele aqui.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Button(
                        onClick = onDirectDebtPaidClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_open_direct_debt_paid")
                    ) {
                        Text("Pagar em Mãos", fontSize = 11.sp)
                    }
                }
            }
        }

        // 4. METAS & RECOMPENSAS MENSAIS
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F5142))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = FamilyAccentAmber,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Metas & Recompensas Mensais",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Surface(
                            color = Color(0xFF00382E),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$completionPct% cumprido",
                                color = Color(0xFF34D399),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${profile.monthlyTasksCompleted} de ${profile.monthlyTasksTarget} tarefas concluídas este mês",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { completionPct / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = FamilyAccentAmber,
                        trackColor = Color(0xFF134E4A)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Níveis de Recompensa:",
                        color = Color(0xFFD1F2EB),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    rewards.forEach { reward ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val isEarned = completionPct >= reward.requiredPercentage
                                Icon(
                                    imageVector = if (isEarned) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isEarned) FamilyAccentAmber else Color.LightGray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = reward.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "Bônus +R$ ${"%.2f".format(reward.bonusAmount)}",
                                color = FamilyAccentAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 5. LISTA DE TAREFAS & CRIADOR COM RECORRÊNCIA
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tarefas Familiares (${filteredTasks.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Atribua missões com repetição para cada filho",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Button(
                        onClick = onCreateTaskClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SafeTalkPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("create_task_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Nova Tarefa", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Child filter tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allChildrenNames.forEach { childName ->
                        val isSelected = selectedChildFilter == childName
                        val count = if (childName == "Todos") tasks.size else tasks.count { it.assignedChildName.equals(childName, ignoreCase = true) }
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) SafeTalkPrimary else Color(0xFFF1F5F9),
                            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .clickable { selectedChildFilter = childName }
                                .testTag("parent_task_filter_$childName")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = if (childName == "Todos os Filhos") Icons.Default.Groups else Icons.Default.Person
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color(0xFF64748B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$childName ($count)",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Task Items
        if (filteredTasks.isEmpty()) {
            item {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhuma tarefa atribuída a $selectedChildFilter",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            items(filteredTasks, key = { it.id }) { task ->
                TaskItemCard(
                    task = task,
                    onApprove = { onApproveTask(task) },
                    onPenalize = { onPenalizeTask(task) }
                )
            }
        }

        // 6. EXTRATO FINANCEIRO RECENTE
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Histórico Financeiro da Família",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (transactions.isEmpty()) {
                        Text("Nenhuma transação recente.", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        transactions.take(5).forEach { tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tx.description.ifBlank { tx.title }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(tx.date, fontSize = 10.sp, color = Color.Gray)
                                }
                                val isCredit = tx.type == "CREDIT"
                                Text(
                                    text = "${if (isCredit) "+" else "-"} R$ ${"%.2f".format(tx.amount)}",
                                    color = if (isCredit) FamilySuccessGreen else FamilyAlertRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: FamilyTaskEntity,
    onApprove: () -> Unit,
    onPenalize: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = task.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    val isAll = task.assignedChildName == "Todos os Filhos"
                    Surface(
                        color = if (isAll) Color(0xFFEFF6FF) else Color(0xFFECFDF5),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, if (isAll) Color(0xFFBFDBFE) else Color(0xFFA7F3D0))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (isAll) Icons.Default.Groups else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isAll) Color(0xFF1D4ED8) else Color(0xFF047857),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Para: ${task.assignedChildName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAll) Color(0xFF1D4ED8) else Color(0xFF047857)
                            )
                        }
                    }
                }

                Text(
                    text = "R$ ${"%.2f".format(task.rewardAmount)} (+${task.rewardPoints} pts)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = FamilySuccessGreen
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Recurrence, Penalty & Photo Evidence Requirement Badges
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.recurrence != "NUNCA") {
                    val recurrenceLabel = when (task.recurrence) {
                        "DIARIA" -> "🔄 Repete Diariamente"
                        "DIAS_UTEIS" -> "📅 Dias Úteis (Seg-Sex)"
                        "FINS_DE_SEMANA" -> "🏖️ Fins de Semana"
                        "SEMANAL" -> "🗓️ Toda Semana"
                        else -> "🔄 ${task.recurrence}"
                    }
                    Surface(
                        color = Color(0xFFFAF5FF),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                    ) {
                        Text(
                            text = recurrenceLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7E22CE),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (task.requiresPhotoEvidence) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFF93C5FD))
                    ) {
                        Text(
                            text = "📸 Exige Foto",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (task.penaltyAmount > 0) {
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Text(
                            text = "⚠️ Multa: -R$ ${"%.2f".format(task.penaltyAmount)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamilyAlertRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vence: ${task.dueDate}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                if (task.status == "PENDENTE") {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (task.penaltyAmount > 0) {
                            OutlinedButton(
                                onClick = onPenalize,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("penalize_task_${task.id}")
                            ) {
                                Text("Multar", fontSize = 11.sp, color = FamilyAlertRed)
                            }
                        }

                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = FamilySuccessGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("approve_task_${task.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Aprovar & Pagar", fontSize = 11.sp)
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFFE6F4EA),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FamilySuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Concluída & Paga ✓",
                                color = FamilySuccessGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * ABA 4: LOCALIZAÇÃO GPS EM TEMPO REAL E ALARME DE EMERGÊNCIA
 */
@Composable
fun ParentLocationAndAlarmView(
    profile: ChildProfileEntity,
    onTriggerAlarm: () -> Unit,
    onStopAlarm: () -> Unit,
    onRefreshLocation: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Alarme Sonoro de Emergência
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (profile.isUrgentAlarmRinging) Color(0xFFFEE2E2) else Color(0xFFFFFBEB)
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (profile.isUrgentAlarmRinging) FamilyAlertRed else FamilyAccentAmber
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (profile.isUrgentAlarmRinging) Icons.Default.VolumeUp else Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = if (profile.isUrgentAlarmRinging) FamilyAlertRed else Color(0xFFD97706),
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (profile.isUrgentAlarmRinging) "🚨 Alarme Sonoro Disparado no Celular do Filho!" else "Alarme Sonoro Forçado (Toca Mesmo Silencioso)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (profile.isUrgentAlarmRinging) FamilyAlertRed else Color(0xFF92400E)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Faça o celular do seu filho tocar no volume máximo para encontrá-lo em caso de perigo ou perda.",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (profile.isUrgentAlarmRinging) {
                        Button(
                            onClick = onStopAlarm,
                            colors = ButtonDefaults.buttonColors(containerColor = FamilyAlertRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_stop_loud_alarm")
                        ) {
                            Text("Silenciar Alarme Agora", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onTriggerAlarm,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_trigger_loud_alarm")
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tocar Alarme de Emergência", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Mapa e Localização GPS
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = WhatsAppDarkTeal,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Localização em Tempo Real",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        IconButton(onClick = onRefreshLocation) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Atualizar",
                                tint = WhatsAppDarkTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE2E8F0))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_map_safe_zone),
                            contentDescription = "Mapa da Zona Segura",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Surface(
                            color = Color.Black.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(FamilySuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Dentro da Zona Segura (Escola)",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = profile.locationName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Última sincronização: ${profile.lastLocationUpdate}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = null,
                                tint = FamilySuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Bateria: ${profile.batteryPercent}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeOff,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (profile.isSilentModeActive) "Modo Silencioso: Ativo" else "Som Normal",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
