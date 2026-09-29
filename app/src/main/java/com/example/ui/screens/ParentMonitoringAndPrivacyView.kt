package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ContactEntity
import com.example.data.model.FamilyHighlightEntity
import com.example.ui.components.FamilyHighlightsBar
import com.example.ui.theme.FamilyAlertRed
import com.example.ui.theme.FamilyAlertRedContainer
import com.example.ui.theme.FamilySuccessGreen
import com.example.ui.theme.SafeTalkAccent
import com.example.ui.theme.SafeTalkDark
import com.example.ui.theme.SafeTalkPrimary
import com.example.ui.theme.SafeTalkPrimaryContainer
import com.example.ui.theme.SafeTalkSuccess
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.viewmodel.FamilySafeViewModel
import com.example.util.FunProfanityFilter
import com.example.util.OnboardingRules

@Composable
fun ParentMonitoringAndPrivacyView(
    viewModel: FamilySafeViewModel,
    contacts: List<ContactEntity> = emptyList(),
    highlights: List<FamilyHighlightEntity> = emptyList(),
    onOpenChat: (Long) -> Unit = {},
    onReviewContact: (ContactEntity) -> Unit = {}
) {
    val profile by viewModel.childProfile.collectAsState()
    val context = LocalContext.current

    val isMonitoring = profile?.monitoringEnabled ?: true
    val isFunnyFilter = profile?.funnyFilterEnabled ?: true
    // Sem fallbacks mockados: quando o dado real não existe, exibimos estado vazio honesto.
    val spouseName = profile?.spouseName ?: ""
    val spouseContact = profile?.spouseContact ?: ""
    val isSpouseLinked = profile?.isSpouseLinked ?: false
    val familyCode = profile?.familyCode ?: ""

    var selectedSubTab by remember { mutableIntStateOf(0) }
    var testInputText by remember { mutableStateOf("") }
    var copiedCode by remember { mutableStateOf(false) }
    var newMemberName by remember { mutableStateOf("") }
    var newMemberCode by remember { mutableStateOf("") }
    val familyMembers = remember(profile, contacts, spouseName) {
        buildList {
            if (!profile?.name.isNullOrBlank()) {
                add("Você • ${profile?.name}" to "Responsável")
            }
            if (!spouseName.isNullOrBlank()) {
                add("$spouseName • Cônjuge" to "Parental")
            }
            contacts.filter { it.isApprovedByParent }.forEach { contact ->
                add("${contact.name} • ${contact.relationship}" to "Aprovado")
            }
            if (newMemberName.isNotBlank()) {
                add("$newMemberName • Filho" to "Aguardando aprovação")
            }
        }
    }

    val pendingContacts = contacts.filter { !it.isApprovedByParent }
    val approvedContacts = contacts.filter { it.isApprovedByParent }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("parent_monitoring_and_privacy_view")
    ) {
        // Sub-Tab Navigation Bar: Supervisão vs Privacidade
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color(0xFFF1F5F9),
            contentColor = SafeTalkPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = SafeTalkPrimary,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedSubTab == 0) SafeTalkPrimary else Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (pendingContacts.isNotEmpty()) "Supervisão (${pendingContacts.size} ⚠️)" else "Supervisão de Filhos",
                            fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedSubTab == 0) SafeTalkPrimary else Color(0xFF64748B)
                        )
                    }
                },
                modifier = Modifier.testTag("subtab_supervisao")
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedSubTab == 1) SafeTalkPrimary else Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Privacidade & Ajustes",
                            fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedSubTab == 1) SafeTalkPrimary else Color(0xFF64748B)
                        )
                    }
                },
                modifier = Modifier.testTag("subtab_privacidade")
            )
        }

        if (selectedSubTab == 0) {
            // ================= ABA SUPERVISÃO DE CONTATOS E MENSAGENS =================
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Highlights / Stories
                item {
                    FamilyHighlightsBar(
                        highlights = highlights,
                        currentUserName = profile?.name ?: "",
                        onAddNewHighlight = { viewModel.showCreateHighlightDialog.value = true },
                        onOpenHighlight = { viewModel.selectedHighlight.value = it }
                    )
                }

                // Banner de Status da Supervisão
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMonitoring) Color(0xFFE6F4EA) else Color(0xFFEFF6FF)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isMonitoring) Icons.Default.Security else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = if (isMonitoring) FamilySuccessGreen else SafeTalkPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isMonitoring) "Supervisão Ativa • Filtro Divertido ON" else "Modo Respeito à Privacidade Ativo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isMonitoring) Color(0xFF137333) else Color(0xFF1E3A8A)
                                )
                                Text(
                                    text = if (isMonitoring)
                                        "Seu filho só conversa com amiguinhos pré-aprovados por você. Palavrões são filtrados."
                                    else
                                        "Mensagens diárias ficam ocultas respeitando a privacidade do jovem. Alertas SOS e novos contatos continuam protegidos.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Quick Action Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.showAddNewContactDialog.value = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("parent_add_contact_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Novo Contato", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.showInviteParentsDialog.value = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("parent_invite_friends_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = WhatsAppDarkTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Convidar Pais", fontSize = 12.sp, color = WhatsAppDarkTeal)
                        }
                    }
                }

                // Pending Friend Requests (Requires Parent Approval)
                if (pendingContacts.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = FamilyAlertRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Solicitações de Contato Pendentes (${pendingContacts.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = FamilyAlertRed
                            )
                        }
                    }

                    items(pendingContacts, key = { it.id }) { pending ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = FamilyAlertRedContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pending_contact_card_${pending.id}")
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
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("⚠️", fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = pending.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = "${pending.relationship}${if (pending.phone.isBlank()) "" else " • ${pending.phone}"}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF475569)
                                        )
                                        Text(
                                            text = "Aguardando sua autorização",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FamilyAlertRed
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { onReviewContact(pending) },
                                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Analisar", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Approved Contacts List
                item {
                    Text(
                        text = "Contatos Aprovados do Filho (${approvedContacts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E293B)
                    )
                }

                if (approvedContacts.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhum contato autorizado ainda.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    items(approvedContacts, key = { it.id }) { contact ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isMonitoring) {
                                        onOpenChat(contact.id)
                                    }
                                }
                                .testTag("parent_contact_card_${contact.id}")
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
                                    ) {                                        // Avatares são sempre as iniciais do nome real — sem imagens mockadas.
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
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = contact.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = Color(0xFF1E293B)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = Color(0xFFDCFCE7),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "✓ Aprovado",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF15803D),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = if (isMonitoring) "${contact.relationship}${if (contact.phone.isBlank()) "" else " • ${contact.phone}"}" else "🔒 Mensagens protegidas por privacidade",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            maxLines = 1
                                        )
                                    }
                                }

                                if (isMonitoring) {
                                    Button(
                                        onClick = { onOpenChat(contact.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Auditar", fontSize = 11.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ================= ABA CONFIGURAÇÕES DE PRIVACIDADE & AJUSTES =================
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. DECISÃO DOS PAIS: MONITORAR OU RESPEITAR PRIVACIDADE
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMonitoring) Color(0xFFF8FAFC) else Color(0xFFEFF6FF)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                color = if (isMonitoring) SafeTalkPrimary.copy(alpha = 0.4f) else Color(0xFF93C5FD),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isMonitoring) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = if (isMonitoring) SafeTalkPrimary else Color(0xFF2563EB),
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isMonitoring) "Modo Monitoramento Completo" else "Modo Respeito à Privacidade",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SafeTalkDark
                                        )
                                        Text(
                                            text = if (isMonitoring) "Você pode auditar e ler mensagens" else "Mensagens privadas para seu filho",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Switch(
                                    checked = isMonitoring,
                                    onCheckedChange = { checked ->
                                        viewModel.toggleMonitoringEnabled(checked)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = SafeTalkPrimary,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFF94A3B8)
                                    ),
                                    modifier = Modifier.testTag("switch_parent_monitoring")
                                )
                            }
                        }
                    }
                }

                // 2. FILTRO DIVERTIDO SAFETALK
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SafeTalkAccent,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Filtro Divertido SafeTalk",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SafeTalkDark
                                        )
                                        Text(
                                            text = "Substitui palavras feias por termos engraçados",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Switch(
                                    checked = isFunnyFilter,
                                    onCheckedChange = { checked ->
                                        viewModel.toggleFunnyFilterEnabled(checked)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = SafeTalkAccent,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFF94A3B8)
                                    ),
                                    modifier = Modifier.testTag("switch_funny_filter")
                                )
                            }
                        }
                    }
                }

                // 3. E2EE INFO CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showEncryptionInfoDialog.value = true }
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = FamilySuccessGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🔒 Criptografia de Ponta a Ponta (E2EE)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Histórico auditável com chave de supervisão legal.",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = "Ver Chaves",
                                fontSize = 11.sp,
                                color = WhatsAppDarkTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 4. GERENCIAR MEMBROS DA FAMÍLIA
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.FamilyRestroom,
                                    contentDescription = null,
                                    tint = SafeTalkPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Membros da Família",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SafeTalkDark
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (familyMembers.isEmpty()) {
                                Text(
                                    text = "Ainda não há membros cadastrados. Crie a família pelo primeiro cadastro e convide os filhos pelo código.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            } else {
                                familyMembers.forEach { (memberLabel, roleLabel) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(memberLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                            Text(roleLabel, fontSize = 11.sp, color = Color(0xFF64748B))
                                        }
                                        Surface(
                                            color = Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Ativo",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF475569),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Adicionar filho por código familiar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SafeTalkDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newMemberName,
                                    onValueChange = { newMemberName = it },
                                    label = { Text("Nome do filho") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SafeTalkPrimary,
                                        focusedLabelColor = SafeTalkPrimary,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        unfocusedLabelColor = Color(0xFF64748B),
                                        focusedTextColor = Color(0xFF0F172A),
                                        unfocusedTextColor = Color(0xFF0F172A)
                                    )
                                )
                                OutlinedTextField(
                                    value = newMemberCode,
                                    onValueChange = { newMemberCode = it.uppercase() },
                                    label = { Text("Código") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SafeTalkPrimary,
                                        focusedLabelColor = SafeTalkPrimary,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        unfocusedLabelColor = Color(0xFF64748B),
                                        focusedTextColor = Color(0xFF0F172A),
                                        unfocusedTextColor = Color(0xFF0F172A)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val normalizedName = newMemberName.trim()
                                    val normalizedCode = OnboardingRules.normalizeFamilyCode(newMemberCode.ifBlank { familyCode })
                                    if (normalizedName.isNotBlank() && normalizedCode.isNotBlank()) {
                                        val familyKey = OnboardingRules.normalizeFamilyCode(familyCode)
                                        val matches = OnboardingRules.matchesFamilyCode(normalizedCode, familyKey)
                                        if (matches) {
                                            newMemberName = ""
                                            newMemberCode = ""
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SafeTalkPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Adicionar membro da família")
                            }
                        }
                    }
                }

                // 5. CÔNJUGE (ESPOSA / MÃE DA FAMÍLIA) & CÓDIGO DA CASA
                item {
                    var editableSpouseName by remember(profile?.spouseName) { mutableStateOf(profile?.spouseName.orEmpty()) }
                    var editableSpouseContact by remember(profile?.spouseContact) { mutableStateOf(profile?.spouseContact.orEmpty()) }
                    var savedFeedback by remember { mutableStateOf(false) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.FamilyRestroom,
                                        contentDescription = null,
                                        tint = SafeTalkPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Esposa / Mãe da Família",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SafeTalkDark
                                        )
                                        Text(
                                            text = if (isSpouseLinked && editableSpouseName.isNotBlank()) "Vinculada aos chats da família" else "Cadastre sua esposa para supervisão conjunta",
                                            fontSize = 11.sp,
                                            color = if (isSpouseLinked && editableSpouseName.isNotBlank()) SafeTalkSuccess else Color(0xFF64748B)
                                        )
                                    }
                                }

                                if (isSpouseLinked && editableSpouseName.isNotBlank()) {
                                    Surface(
                                        color = SafeTalkSuccess.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Ativa",
                                            color = SafeTalkSuccess,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = editableSpouseName,
                                onValueChange = {
                                    editableSpouseName = it
                                    savedFeedback = false
                                },
                                label = { Text("Nome da Esposa / Mãe") },
                                placeholder = { Text("Ex: Juliana, Ana, Maria...") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = SafeTalkPrimary)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SafeTalkPrimary,
                                    focusedLabelColor = SafeTalkPrimary,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    unfocusedLabelColor = Color(0xFF64748B),
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    cursorColor = SafeTalkPrimary,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editableSpouseContact,
                                onValueChange = {
                                    editableSpouseContact = it
                                    savedFeedback = false
                                },
                                label = { Text("E-mail da Esposa (para login e supervisão)") },
                                placeholder = { Text("ex: esposa@gmail.com") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = SafeTalkPrimary)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SafeTalkPrimary,
                                    focusedLabelColor = SafeTalkPrimary,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    unfocusedLabelColor = Color(0xFF64748B),
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    cursorColor = SafeTalkPrimary,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            var authFeedbackMessage by remember { mutableStateOf<String?>(null) }

                            Button(
                                onClick = {
                                    viewModel.validateAndLinkSpouseEmail(
                                        spouseName = editableSpouseName.trim(),
                                        spouseEmail = editableSpouseContact.trim()
                                    ) { feedback ->
                                        authFeedbackMessage = feedback
                                        savedFeedback = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SafeTalkPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isSpouseLinked) "Atualizar & Validar E-mail (Supabase)" else "Vincular & Enviar Convite por E-mail",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (savedFeedback && authFeedbackMessage != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✉️ ${authFeedbackMessage}",
                                    color = SafeTalkSuccess,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Código Familiar da Casa para convite
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Código Familiar da Casa (para o celular da esposa/filhos)", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Text(familyCode, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SafeTalkPrimary)
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Family Code", familyCode))
                                        copiedCode = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (copiedCode) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copiar Código",
                                        tint = if (copiedCode) SafeTalkSuccess else SafeTalkPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. Atualizações do Aplicativo (In-App GitHub OTA)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Security,
                                        contentDescription = null,
                                        tint = SafeTalkPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Atualizações do App",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = SafeTalkDark
                                        )
                                        Text(
                                            text = "Versão atual: v${com.example.BuildConfig.VERSION_NAME}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Button(
                                    onClick = { viewModel.checkForAppUpdates() },
                                    colors = ButtonDefaults.buttonColors(containerColor = SafeTalkPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Buscar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 7. Botão de Alterar Usuário / Novo Cadastro
                item {
                    Button(
                        onClick = { viewModel.showProfileEditorDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SafeTalkPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_change_profile_registration")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar Perfil Familiar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
