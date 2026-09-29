package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.example.data.model.WithdrawalRequestEntity
import com.example.ui.components.FamilyHighlightsBar
import com.example.ui.dialogs.CreateHighlightDialog
import com.example.ui.dialogs.HighlightStoryViewerDialog
import com.example.ui.dialogs.TaskCameraCaptureDialog
import com.example.ui.theme.FamilyAccentAmber
import com.example.ui.theme.FamilyAlertRed
import com.example.ui.theme.FamilySuccessGreen
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.viewmodel.ChildTab
import com.example.ui.viewmodel.FamilySafeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildModeScreen(
    viewModel: FamilySafeViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.childTab.collectAsState()
    val childProfile by viewModel.childProfile.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val rewards by viewModel.monthlyRewards.collectAsState()
    val highlights by viewModel.highlights.collectAsState()
    val selectedHighlight by viewModel.selectedHighlight.collectAsState()
    val showCreateHighlight by viewModel.showCreateHighlightDialog.collectAsState()
    val taskToCompleteWithCamera by viewModel.taskToCompleteWithCamera.collectAsState()
    val withdrawalRequests by viewModel.withdrawalRequests.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    val profile = childProfile ?: ChildProfileEntity()
    val approvedContacts = contacts.filter { it.isApprovedByParent }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F5142))
                        ) {
                            if (!profile.profilePhotoUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = Uri.parse(profile.profilePhotoUri),
                                    contentDescription = "Foto de ${profile.name}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Iniciais do nome real da criança — sem avatar mockado.
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = profile.name.trim().take(2).uppercase().ifBlank { "ST" },
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SafeTalk Kids",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Olá, ${profile.name}! • Modo Seguro Ativo",
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
                    selected = currentTab == ChildTab.CHATS,
                    onClick = { viewModel.setChildTab(ChildTab.CHATS) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Conversas") },
                    label = { Text("Conversas", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppDarkTeal,
                        indicatorColor = Color(0xFFD1F2EB)
                    ),
                    modifier = Modifier.testTag("child_tab_chats")
                )
                NavigationBarItem(
                    selected = currentTab == ChildTab.SOS_CHECKIN,
                    onClick = { viewModel.setChildTab(ChildTab.SOS_CHECKIN) },
                    icon = { Icon(Icons.Default.Security, contentDescription = "Supervisão") },
                    label = { Text("Supervisão", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppDarkTeal,
                        indicatorColor = Color(0xFFD1F2EB)
                    ),
                    modifier = Modifier.testTag("child_tab_sos")
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
                ChildTab.CHATS -> {
                    ChildChatsView(
                        contacts = approvedContacts,
                        highlights = highlights,
                        viewModel = viewModel,
                        currentUserName = profile.name,
                        onRequestFriendClick = { viewModel.showInviteParentsDialog.value = true },
                        onOpenChat = { viewModel.openChat(it) }
                    )
                }
                ChildTab.MINHAS_TAREFAS -> {
                    MinhasTarefasView(
                        tasks = tasks,
                        profile = profile,
                        onMarkDone = { task ->
                            if (task.requiresPhotoEvidence) {
                                viewModel.startPhotoCaptureForTask(task)
                            } else {
                                viewModel.markTaskDoneByChild(task)
                            }
                        },
                        onTransferTask = { task ->
                            viewModel.taskToTransferToSibling.value = task
                        },
                        onAcceptTaskTransfer = { task ->
                            viewModel.acceptTaskTransfer(task)
                        },
                        onRejectTaskTransfer = { task ->
                            viewModel.rejectTaskTransfer(task)
                        }
                    )
                }
                ChildTab.COFRINHO_METAS -> {
                    ChildPiggyBankView(
                        profile = profile,
                        rewards = rewards,
                        withdrawalRequests = withdrawalRequests,
                        transactions = transactions,
                        onRequestWithdrawalClick = { viewModel.showRequestWithdrawalDialog.value = true },
                        onTransferMoneyClick = { viewModel.showTransferMoneyDialog.value = true }
                    )
                }
                ChildTab.SOS_CHECKIN -> {
                    ChildSosAndLocationView(
                        profile = profile,
                        onSendCheckIn = { viewModel.sendSafetyCheckIn() },
                        onDeparted = { viewModel.sendDepartureNotice() },
                        onSos = { viewModel.triggerEmergencySos() }
                    )
                }
            }
        }
    }

    // Dialog: Create Highlight / Story by Child
    if (showCreateHighlight) {
        CreateHighlightDialog(
            currentUserName = profile.name,
            currentUserRole = "CHILD",
            onDismiss = { viewModel.showCreateHighlightDialog.value = false },
            onPublish = { title, text, emoji, hasPhoto, audience ->
                viewModel.addHighlight(
                    title = title,
                    textContent = text,
                    authorName = profile.name,
                    authorRole = "CHILD",
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

    // Dialog: Live Camera Capture for Task Evidence
    taskToCompleteWithCamera?.let { task ->
        TaskCameraCaptureDialog(
            task = task,
            onDismiss = { viewModel.cancelPhotoCapture() },
            onConfirmWithPhoto = { photoUri ->
                viewModel.completeTaskWithPhotoEvidence(task, photoUri)
            }
        )
    }
}

@Composable
fun ChildChatsView(
    contacts: List<ContactEntity>,
    highlights: List<FamilyHighlightEntity>,
    viewModel: FamilySafeViewModel,
    currentUserName: String,
    onRequestFriendClick: () -> Unit,
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

        // Safe Zone Status Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = FamilySuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Seus pais protegem suas conversas e o filtro engraçado substitui palavras feias por termos divertidos!",
                        fontSize = 12.sp,
                        color = Color(0xFF166534),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section Title & Add Friend Request
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Minhas Conversas Autorizadas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = onRequestFriendClick,
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("child_ask_friend_btn")
                ) {
                    Text("+ Pedir Amigo", fontSize = 11.sp)
                }
            }
        }

        if (contacts.isEmpty()) {
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
                            text = "Nenhum contato autorizado ainda. Peça aos seus pais para adicionarem sua família!",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        } else {
            items(contacts, key = { it.id }) { contact ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenChat(contact.id) }
                        .testTag("child_contact_item_${contact.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
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
                                    .size(46.dp)
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
                            ) {
                                // Avatares são sempre as iniciais do nome real — sem imagens mockadas.
                                run {
                                    val initials = contact.name.trim().take(2).uppercase()
                                    Text(
                                        text = initials,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = contact.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${contact.relationship}${if (contact.phone.isBlank()) "" else " • ${contact.phone}"}",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    maxLines = 1
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = contact.lastSeen,
                                fontSize = 11.sp,
                                color = Color.Gray
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
}

@Composable
fun MinhasTarefasView(
    tasks: List<FamilyTaskEntity>,
    profile: ChildProfileEntity,
    onMarkDone: (FamilyTaskEntity) -> Unit,
    onTransferTask: (FamilyTaskEntity) -> Unit = {},
    onAcceptTaskTransfer: (FamilyTaskEntity) -> Unit = {},
    onRejectTaskTransfer: (FamilyTaskEntity) -> Unit = {}
) {
    var showOnlyMine by remember { mutableStateOf(true) }

    val myTasks = tasks.filter {
        it.assignedChildName.equals(profile.name, ignoreCase = true) ||
                it.assignedChildName == "Todos os Filhos" ||
                it.assignedChildName.equals("Todos", ignoreCase = true)
    }

    // Tasks transferred to this child waiting for this child's approval
    val incomingTaskTransfers = tasks.filter {
        it.transferTargetSibling.equals(profile.name, ignoreCase = true) &&
                it.transferStatus == "WAITING_SIBLING"
    }

    val displayedTasks = if (showOnlyMine) myTasks else tasks

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner: Missões para ganhar mesada
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WhatsAppDarkTeal)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = FamilyAccentAmber,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Missões para Ganhar Mesada",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Cumpra suas tarefas diárias, clique em 'Concluir' e seus pais irão liberar o valor no seu cofre!",
                            fontSize = 12.sp,
                            color = Color(0xFFD1F2EB)
                        )
                    }
                }
            }
        }

        // INCOMING TASK TRANSFER REQUESTS FROM SIBLINGS
        if (incomingTaskTransfers.isNotEmpty()) {
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
                        text = "Irmão quer repassar missão para você (${incomingTaskTransfers.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1D4ED8)
                    )
                }
            }

            items(incomingTaskTransfers, key = { it.id }) { task ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF93C5FD)),
                    modifier = Modifier.fillMaxWidth().testTag("incoming_task_transfer_card_${task.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🤝 ${task.transferRequestedBy} quer repassar a missão:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "'${task.title}' (+R$ ${"%.2f".format(task.rewardAmount)})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = FamilySuccessGreen
                        )
                        Text(
                            text = "Se você aceitar, seus pais ainda precisarão aprovar a troca!",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
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
                                onClick = { onAcceptTaskTransfer(task) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_accept_sibling_task_${task.id}")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Aceitar Missão", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Filter tabs: Minhas Tarefas vs Todas da Família
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (showOnlyMine) WhatsAppDarkTeal else Color(0xFFF1F5F9),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showOnlyMine = true }
                        .testTag("child_filter_my_tasks")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (showOnlyMine) Color.White else Color(0xFF475569),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Minhas (${myTasks.size})",
                            fontSize = 13.sp,
                            fontWeight = if (showOnlyMine) FontWeight.Bold else FontWeight.Medium,
                            color = if (showOnlyMine) Color.White else Color(0xFF475569)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (!showOnlyMine) WhatsAppDarkTeal else Color(0xFFF1F5F9),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showOnlyMine = false }
                        .testTag("child_filter_all_tasks")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = if (!showOnlyMine) Color.White else Color(0xFF475569),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Família (${tasks.size})",
                            fontSize = 13.sp,
                            fontWeight = if (!showOnlyMine) FontWeight.Bold else FontWeight.Medium,
                            color = if (!showOnlyMine) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }
        }

        if (displayedTasks.isEmpty()) {
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
                            text = "Nenhuma tarefa pendente para você no momento! 🎉",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(displayedTasks, key = { it.id }) { task ->
                val isMine = task.assignedChildName.equals(profile.name, ignoreCase = true)
                val isAll = task.assignedChildName == "Todos os Filhos" || task.assignedChildName.equals("Todos", ignoreCase = true)

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

                                Surface(
                                    color = when {
                                        isMine -> Color(0xFFDCFCE7)
                                        isAll -> Color(0xFFEFF6FF)
                                        else -> Color(0xFFF1F5F9)
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        when {
                                            isMine -> Color(0xFF86EFAC)
                                            isAll -> Color(0xFFBFDBFE)
                                            else -> Color(0xFFCBD5E1)
                                        }
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isAll) Icons.Default.Groups else Icons.Default.Person,
                                            contentDescription = null,
                                            tint = when {
                                                isMine -> Color(0xFF15803D)
                                                isAll -> Color(0xFF1D4ED8)
                                                else -> Color(0xFF475569)
                                            },
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = when {
                                                isMine -> "Sua Missão (${profile.name})"
                                                isAll -> "Toda a Família"
                                                else -> "Para ${task.assignedChildName}"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                isMine -> Color(0xFF15803D)
                                                isAll -> Color(0xFF1D4ED8)
                                                else -> Color(0xFF475569)
                                            }
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "R$ ${"%.2f".format(task.rewardAmount)} (+${task.rewardPoints} pts)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
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

                        // Penalty, Recurrence & Photo Evidence Badges
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
                                    "DIARIA" -> "🔄 Diária"
                                    "DIAS_UTEIS" -> "📅 Dias Úteis"
                                    "FINS_DE_SEMANA" -> "🏖️ Fins de Semana"
                                    "SEMANAL" -> "🗓️ Semanal"
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
                                        text = "📸 Exige Foto ao Vivo",
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
                                        text = "⚠️ Perde R$ ${"%.2f".format(task.penaltyAmount)} se atrasar",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FamilyAlertRed,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (task.transferStatus == "WAITING_SIBLING") {
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "⏳ Repassada para ${task.transferTargetSibling} (Aguardando aceite)",
                                        fontSize = 10.sp,
                                        color = Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else if (task.transferStatus == "WAITING_PARENT") {
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "⏳ Troca aceita por ${task.transferTargetSibling} • Aguardando Pais",
                                        fontSize = 10.sp,
                                        color = Color(0xFF1D4ED8),
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
                                text = "Prazo: ${task.dueDate}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )

                            when (task.status) {
                                "PENDENTE" -> {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (isMine && task.transferStatus == "NONE") {
                                            OutlinedButton(
                                                onClick = { onTransferTask(task) },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.testTag("transfer_task_to_sibling_${task.id}")
                                            ) {
                                                Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Repassar", fontSize = 11.sp)
                                            }
                                        }

                                        Button(
                                            onClick = { onMarkDone(task) },
                                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("kid_done_task_${task.id}")
                                        ) {
                                            Icon(
                                                if (task.requiresPhotoEvidence) Icons.Default.CameraAlt else Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                if (task.requiresPhotoEvidence) "📸 Foto & Concluir" else "Fiz Essa Tarefa!",
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                                "CONCLUIDO_FILHO" -> {
                                    Surface(
                                        color = Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (task.photoEvidenceUri != null) "📸 Foto enviada • Aguardando Pais ⏳" else "Aguardando Aprovação dos Pais ⏳",
                                            color = Color(0xFF92400E),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                "ATRASADO_PENALIZADO" -> {
                                    Surface(
                                        color = Color(0xFFFEE2E2),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, FamilyAlertRed)
                                    ) {
                                        Text(
                                            text = "⚠️ Prazo Atrasado • Desconto de R$ ${"%.2f".format(task.penaltyAmount)}",
                                            color = FamilyAlertRed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                "APROVADO_PAGO" -> {
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Aprovada e Paga ✓",
                                            color = Color(0xFF166534),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChildPiggyBankView(
    profile: ChildProfileEntity,
    rewards: List<MonthlyRewardEntity>,
    withdrawalRequests: List<WithdrawalRequestEntity> = emptyList(),
    transactions: List<FinancialTransactionEntity> = emptyList(),
    onRequestWithdrawalClick: () -> Unit,
    onTransferMoneyClick: () -> Unit
) {
    val completionPct = if (profile.monthlyTasksTarget > 0) {
        ((profile.monthlyTasksCompleted.toFloat() / profile.monthlyTasksTarget) * 100).toInt()
    } else 0

    val myWithdrawals = withdrawalRequests.filter { it.childName.equals(profile.name, ignoreCase = true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cofrinho Saldo Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhatsAppDarkTeal)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Meu Cofrinho de Mesada",
                        color = Color(0xFFD1F2EB),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "R$ ${"%.2f".format(profile.balance)}",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total de Pontos de Bom Comportamento: ${profile.monthlyPoints} pts",
                        color = FamilyAccentAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Action Buttons: Solicitar Resgate em Mãos & Transferir R$ para Irmão
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRequestWithdrawalClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("kid_request_withdrawal_button")
                ) {
                    Icon(Icons.Default.Savings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("💸 Pedir Saque em Mãos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onTransferMoneyClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("kid_transfer_money_button")
                ) {
                    Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🤝 Pix / Transferir p/ Irmão", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Pedidos de Saque / Dívida Paga em Andamento
        if (myWithdrawals.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Solicitações de Resgate em Mãos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        myWithdrawals.forEach { req ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("R$ ${"%.2f".format(req.amount)} (${req.reason})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(req.requestDate, fontSize = 10.sp, color = Color.Gray)
                                }
                                Surface(
                                    color = when (req.status) {
                                        "PAID" -> Color(0xFFDCFCE7)
                                        "REJECTED" -> Color(0xFFFEE2E2)
                                        else -> Color(0xFFFEF3C7)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = when (req.status) {
                                            "PAID" -> "✓ Dívida Paga em Mãos"
                                            "REJECTED" -> "Recusado"
                                            else -> "⏳ Aguardando Pai dar em mãos"
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (req.status) {
                                            "PAID" -> Color(0xFF15803D)
                                            "REJECTED" -> FamilyAlertRed
                                            else -> Color(0xFF92400E)
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Monthly goal progress
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
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Minha Meta do Mês",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Text(
                            text = "$completionPct%",
                            color = Color(0xFF34D399),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

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
                    rewards.forEach { reward ->
                        val isAchieved = completionPct >= reward.requiredPercentage
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isAchieved) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isAchieved) Color(0xFF34D399) else Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = reward.title,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = if (reward.isClaimed) "Recebido ✓" else "+ R$ ${"%.2f".format(reward.bonusAmount)}",
                                color = if (isAchieved) Color(0xFF34D399) else Color.LightGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChildSosAndLocationView(
    profile: ChildProfileEntity,
    onSendCheckIn: () -> Unit,
    onDeparted: () -> Unit,
    onSos: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Safe Zone & Parent Monitoring Status Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = FamilySuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Celular dos Pais Conectado & Monitorando",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF166534)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Local atual: ${profile.locationName}",
                    fontSize = 13.sp,
                    color = Color(0xFF166534)
                )
                Text(
                    text = "Sinal de Rede: Estável • Bateria: ${profile.batteryPercent}% • Status: ${profile.lastLocationUpdate}",
                    fontSize = 11.sp,
                    color = Color(0xFF166534).copy(alpha = 0.85f)
                )
            }
        }

        // Departure & Arrival Actions
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Avisar os Pais em Tempo Real",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sempre que sair ou chegar de um lugar, toque abaixo:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDeparted,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kid_depart_button")
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🚀 Avisar que Saí / Em Trânsito", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onSendCheckIn,
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kid_checkin_button")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📍 Cheguei Bem! (Check-in Seguro)")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Big Emergency SOS Button ("Me Ajuda Aí")
        Button(
            onClick = onSos,
            colors = ButtonDefaults.buttonColors(containerColor = FamilyAlertRed),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("kid_sos_button")
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🚨 ME AJUDA AÍ! (SOS ALARME PAIS)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
