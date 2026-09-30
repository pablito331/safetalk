package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Add
import com.example.data.model.FamilyTaskEntity
import com.example.ui.theme.FamilyAccentAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import android.content.Intent
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import java.io.File
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ContactEntity
import com.example.ui.theme.FamilyAlertRed
import com.example.ui.theme.FamilyBlue
import com.example.ui.theme.FamilySecondary
import com.example.ui.theme.FamilySuccessGreen
import com.example.ui.theme.WhatsAppChatBackground
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppIncomingBubble
import com.example.ui.theme.WhatsAppOutgoingBubble
import com.example.ui.viewmodel.AppRole
import com.example.ui.viewmodel.FamilySafeViewModel

/** 0:07 / 1:23 — rótulo do áudio no balão. */
private fun formatDurationLabel(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val minutes = safe / 60
    val seconds = safe % 60
    return "%d:%02d".format(minutes, seconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    contact: ContactEntity,
    viewModel: FamilySafeViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.currentChatMessages.collectAsState()
    val isRecording by viewModel.isRecordingAudio.collectAsState()
    val recordingSeconds by viewModel.recordingSeconds.collectAsState()
    val playingAudioId by viewModel.playingAudioMessageId.collectAsState()
    val audioProgress by viewModel.audioPlayProgress.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val isParentReview by viewModel.isParentReviewModeActive.collectAsState()
    val profile by viewModel.childProfile.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val pendingPhotoUri by viewModel.pendingPhotoUri.collectAsState()
    val showMicPermissionDialog by viewModel.showMicPermissionDialog.collectAsState()

    val isParentAuditing = currentRole == AppRole.PARENT || isParentReview

    var textInput by remember { mutableStateOf("") }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var showTaskPicker by remember { mutableStateOf(false) }
    var exportReportNotice by remember { mutableStateOf<String?>(null) }
    var showMicRationale by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Escolhe foto da galeria; a permissão é concedida pelo sistema
    // (Photo Picker) e a confirmação de envio acontece num dialog.
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.pendingPhotoUri.value = uri
    }

    // Dispara direto para Configurações quando o usuário marcou "não perguntar".
    val micSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { }

    Column(
        modifier = modifier
            .fillMaxSize()
            // Empurra o conteúdo para cima quando o teclado abre, mantendo os
            // botões de navegação do Android (voltar/home/recents) acessíveis.
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .background(WhatsAppChatBackground)
    ) {
        // WhatsApp style Top Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(contact.avatarColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.name.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = contact.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isParentAuditing) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Auditoria Parental",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Modo Auditoria Parental",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (contact.isApprovedByParent) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Protegido",
                                    tint = FamilySuccessGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (contact.isOnline) "Online • Criptografado E2EE" else "Criptografado E2EE",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Pendente",
                                    tint = FamilyAlertRed,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Aguardando Aprovação dos Pais",
                                    color = Color(0xFFFFCC80),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = { viewModel.closeChat() },
                    modifier = Modifier.testTag("chat_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { viewModel.showEncryptionInfoDialog.value = true },
                    modifier = Modifier.testTag("chat_e2ee_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Criptografia E2EE",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (isParentAuditing) Color(0xFF0F3E35) else WhatsAppDarkTeal
            )
        )

        // Parental Audit Control Panel
        if (isParentAuditing) {
            Surface(
                color = Color(0xFFE0F2FE),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF0369A1),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Supervisão Parental Ativa (E2EE)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF0369A1)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (!contact.isApprovedByParent) {
                                Surface(
                                    color = FamilySuccessGreen,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.clickable { viewModel.approveContact(contact) }
                                ) {
                                    Text(
                                        text = "Aprovar Contato",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    color = FamilyAlertRed,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.clickable { viewModel.blockContact(contact) }
                                ) {
                                    Text(
                                        text = "Bloquear",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFF0284C7),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable {
                                    exportReportNotice = "Relatório de auditoria gerado com sucesso para este contato!"
                                }
                            ) {
                                Text(
                                    text = "Relatório",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    exportReportNotice?.let { msg ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "✓ $msg", color = Color(0xFF0369A1), fontSize = 11.sp)
                    }
                }
            }
        }

        // Friend Mode Opportune Invitation Prompt
        if (currentRole == AppRole.FRIEND_SIMPLIFIED) {
            Surface(
                color = Color(0xFFFEF3C7),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💌",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Peça para seus pais assumirem o controle da sua família!",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                    Surface(
                        color = Color(0xFFD97706),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { viewModel.showInviteParentsDialog.value = true }
                    ) {
                        Text(
                            text = "Convidar Pais",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Safety Status Banner if pending
        if (!contact.isApprovedByParent && !isParentAuditing) {
            Surface(
                color = Color(0xFFFFF3CD),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF856404),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔒 Contato pendente. Apenas os pais podem liberar novas conversas com amigos.",
                        color = Color(0xFF856404),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Messages List or Privacy Placeholder
        if (isParentAuditing && profile?.monitoringEnabled == false) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔒", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Monitoramento de Mensagens Desativado",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Você optou por não monitorar o conteúdo diário das conversas nas configurações de 'Privacidade'. Você ainda pode aprovar ou bloquear este contato no painel acima.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                reverseLayout = false
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { viewModel.showEncryptionInfoDialog.value = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = FamilySuccessGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🔒 SafeTalk E2EE • Salvo no Celular (Sem Nuvem)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1E293B),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                itemsIndexed(messages, key = { _, msg -> msg.id }) { index, msg ->
                    // A lista não rola sob a top bar: o primeiro balão ganha
                    // espaço equivalente ao inset da status bar + respiro.
                    Box(
                        modifier = Modifier.padding(
                            top = if (index == 0) {
                                WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp
                            } else 0.dp
                        )
                    ) {
                        ChatMessageBubble(
                            message = msg,
                            isPlayingAudio = playingAudioId == msg.id,
                            audioProgress = if (playingAudioId == msg.id) audioProgress else 0f,
                            onTogglePlayAudio = {
                                viewModel.togglePlayAudio(msg.id, msg.mediaDurationSeconds, msg.mediaUri)
                            },
                            onMediaClick = {
                                viewModel.previewMediaMessage.value = msg
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }


        // Voice Recording Banner if active
        AnimatedVisibility(visible = isRecording) {
            val transition = rememberInfiniteTransition(label = "recPulse")
            val alpha by transition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "recAlpha"
            )

            Surface(
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(FamilyAlertRed.copy(alpha = alpha))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gravando áudio... ${formatDurationLabel(recordingSeconds)}",
                            color = FamilyAlertRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { viewModel.cancelRecordingVoice() },
                            modifier = Modifier.testTag("cancel_voice_recording")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancelar",
                                tint = Color.Gray
                            )
                        }

                        IconButton(
                            onClick = { viewModel.finishAndSendVoice() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(WhatsAppDarkTeal)
                                .testTag("finish_send_voice_recording")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Enviar Áudio",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // WhatsApp Style Bottom Input Bar
        if (!isRecording) {
            Surface(
                color = Color(0xFFF0F2F5),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment button (Photos, Videos, Audio)
                    IconButton(
                        onClick = { showAttachmentMenu = true },
                        modifier = Modifier.testTag("chat_attachment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Anexar Arquivo",
                            tint = Color(0xFF54656F)
                        )
                    }

                    // Input Text
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Mensagem segura...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    if (textInput.isNotBlank()) {
                        // Send text button
                        IconButton(
                            onClick = {
                                viewModel.sendTextMessage(textInput)
                                textInput = ""
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(WhatsAppDarkTeal)
                                .testTag("chat_send_text_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        // Voice Record Button
                        IconButton(
                            onClick = {
                                val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                                        PackageManager.PERMISSION_GRANTED
                                } else true
                                if (granted) viewModel.startRecordingVoice() else showMicRationale = true
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(WhatsAppDarkTeal)
                                .testTag("chat_mic_record_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Gravar Áudio",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmação de envio da foto escolhida na galeria
    pendingPhotoUri?.let { uri ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelPendingPhoto() },
            title = { Text("Enviar esta foto?") },
            text = {
                Column {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A foto será reduzida e enviada com segurança pelo SafeTalk.",
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmSendPendingPhoto() }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelPendingPhoto() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Pedir permissão de microfone (primeira gravação)
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.startRecordingVoice()
    }

    if (showMicRationale || showMicPermissionDialog) {
        val permanentlyDenied = context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_DENIED &&
            !(context as Activity).shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
        AlertDialog(
            onDismissRequest = {
                showMicRationale = false
                viewModel.showMicPermissionDialog.value = false
            },
            title = { Text("Permitir microfone?") },
            text = {
                Text(
                    if (permanentlyDenied) {
                        "Para gravar mensagens de voz, libere o microfone do SafeTalk nas configurações do aparelho."
                    } else {
                        "Para gravar mensagens de voz, o SafeTalk precisa de acesso ao microfone do aparelho."
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showMicRationale = false
                    viewModel.showMicPermissionDialog.value = false
                    if (permanentlyDenied) {
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                        micSettingsLauncher.launch(intent)
                    } else {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }) {
                    Text("Permitir")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showMicRationale = false
                    viewModel.showMicPermissionDialog.value = false
                }) {
                    Text("Agora não")
                }
            }
        )
    }

    // Attachment Modal Sheet
    if (showAttachmentMenu) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showAttachmentMenu = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Enviar Mídia Segura",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Send Photo Option
                    AttachmentOptionItem(
                        icon = Icons.Default.Image,
                        label = "Foto",
                        color = Color(0xFFAC44CF),
                        tag = "attach_photo_button",
                        onClick = {
                            showAttachmentMenu = false
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    // Send Video Option
                    AttachmentOptionItem(
                        icon = Icons.Default.Videocam,
                        label = "Vídeo",
                        color = Color(0xFFE91E63),
                        tag = "attach_video_button",
                        onClick = {
                            viewModel.sendVideoMessage("🎬 Vídeo compartilhado (0:24)")
                            showAttachmentMenu = false
                        }
                    )

                    // Send Audio Option
                    AttachmentOptionItem(
                        icon = Icons.Default.Mic,
                        label = "Áudio",
                        color = Color(0xFFF59E0B),
                        tag = "attach_audio_button",
                        onClick = {
                            showAttachmentMenu = false
                            val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                                    PackageManager.PERMISSION_GRANTED
                            } else true
                            if (granted) viewModel.startRecordingVoice() else showMicRationale = true
                        }
                    )

                    // Send Task Option
                    AttachmentOptionItem(
                        icon = Icons.Default.TaskAlt,
                        label = "Missão",
                        color = Color(0xFF0F766E),
                        tag = "attach_task_button",
                        onClick = {
                            showAttachmentMenu = false
                            showTaskPicker = true
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Task Picker Modal Sheet
    if (showTaskPicker) {
        val taskSheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showTaskPicker = false },
            sheetState = taskSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Enviar Missão no Chat",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Selecione uma tarefa para cobrar ou realizar",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Surface(
                        color = Color(0xFF0F766E),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable {
                            showTaskPicker = false
                            viewModel.showCreateTaskDialog.value = true
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nova", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (tasks.isEmpty()) {
                    Text(
                        text = "Nenhuma tarefa cadastrada no momento.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tasks, key = { it.id }) { t ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.sendTaskMessage(t)
                                        showTaskPicker = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = t.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            text = "Recompensa: R$ ${"%.2f".format(t.rewardAmount)} • Prazo: ${t.dueDate}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                        if (t.penaltyAmount > 0) {
                                            Text(
                                                text = "⚠️ Multa se atrasar: -R$ ${"%.2f".format(t.penaltyAmount)}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FamilyAlertRed
                                            )
                                        }
                                        if (t.requiresPhotoEvidence) {
                                            Text(
                                                text = "📸 Exige foto da câmera ao vivo",
                                                fontSize = 10.sp,
                                                color = Color(0xFF0284C7)
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Enviar",
                                        tint = Color(0xFF0F766E),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AttachmentOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessageEntity,
    isPlayingAudio: Boolean,
    audioProgress: Float,
    onTogglePlayAudio: () -> Unit,
    onMediaClick: () -> Unit
) {
    if (message.sender == "SYSTEM") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D))
            ) {
                Text(
                    text = message.text,
                    fontSize = 11.sp,
                    color = Color(0xFF78350F),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
        return
    }

    val isMe = message.sender == "ME" || message.sender == "PARENT"
    val bubbleColor = if (isMe) WhatsAppOutgoingBubble else WhatsAppIncomingBubble

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (isMe) 12.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 12.dp
            ),
            shadowElevation = 1.dp,
            // Em telas grandes o balão não estica: máx 82% OU 420dp, o que for menor.
            modifier = Modifier.fillMaxWidth(0.82f).widthIn(max = 420.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                if (message.isFlaggedByFilter) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = FamilyAlertRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "⚠️ Sinalizado pelo Filtro Familiar",
                                color = FamilyAlertRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                when (message.mediaType) {
                    "AUDIO" -> {
                        // WhatsApp Style Audio Bubble
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            IconButton(
                                onClick = onTogglePlayAudio,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(FamilySecondary)
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = "Reproduzir áudio",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                LinearProgressIndicator(
                                    progress = { if (isPlayingAudio) audioProgress else 0.4f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = FamilySecondary,
                                    trackColor = Color(0xFFCBD5E1),
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = formatDurationLabel(message.mediaDurationSeconds),
                                        fontSize = 11.sp,
                                        color = Color(0xFF667781)
                                    )
                                    Text(
                                        text = "Mensagem de Voz",
                                        fontSize = 10.sp,
                                        color = FamilySecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    "IMAGE" -> {
                        // Photo Bubble: arquivo local (enviado ou recebido via base64)
                        val localFile = remember(message.id, message.mediaUri) { File(message.mediaUri) }
                        if (message.mediaUri.isNotBlank() && localFile.exists()) {
                            val painter = rememberAsyncImagePainter(
                                model = coil.request.ImageRequest.Builder(LocalContext.current)
                                    .data(localFile)
                                    .size(coil.size.Size.ORIGINAL)
                                    .build()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 260.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE2E8F0))
                                    .clickable(onClick = onMediaClick),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painter,
                                    contentDescription = "Foto",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE2E8F0))
                                    .clickable(onClick = onMediaClick),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Foto",
                                        tint = FamilySecondary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Toque para ver a foto",
                                        fontSize = 12.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                        if (message.text.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = message.text, fontSize = 14.sp, color = Color(0xFF111B21))
                        }
                    }

                    "VIDEO" -> {
                        // Video Bubble
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF334155))
                                .clickable(onClick = onMediaClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Assistir Vídeo",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Vídeo • 0:${message.mediaDurationSeconds.toString().padStart(2, '0')}",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        if (message.text.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = message.text, fontSize = 14.sp, color = Color(0xFF111B21))
                        }
                    }

                    "TASK" -> {
                        // Task Card Bubble
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.TaskAlt,
                                            contentDescription = null,
                                            tint = FamilySuccessGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "MISSÃO COMPARTILHADA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF166534)
                                        )
                                    }
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "SafeTalk Chores",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF166534),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = message.text,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E293B),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    else -> {
                        // Regular Text
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = Color(0xFF111B21)
                        )
                    }
                }

                // Time and Double Checks
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.formattedTime,
                        fontSize = 10.sp,
                        color = Color(0xFF667781)
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Lido",
                            tint = Color(0xFF53BDEB),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
