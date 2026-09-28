package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContactEntity
import com.example.ui.theme.FamilySuccessGreen
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.viewmodel.AppRole
import com.example.ui.viewmodel.FamilySafeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendSimplifiedScreen(
    viewModel: FamilySafeViewModel,
    modifier: Modifier = Modifier
) {
    val contacts by viewModel.contacts.collectAsState()
    val friendGuestName by viewModel.friendGuestName.collectAsState()
    val isParentLinked by viewModel.isParentLinked.collectAsState()

    // Friend in simplified mode sees contacts they can chat with (family contacts + friend)
    val availableContacts = contacts.filter { it.isApprovedByParent }

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
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "M",
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
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
                                text = "$friendGuestName • Modo Simplificado",
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Timely Invitation Banner for Parents
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("parent_invitation_banner_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(FamilySuccessGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FamilyRestroom,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Conecte seus Pais ao SafeTalk",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )

                                Text(
                                    text = "Uso simplificado ativo sem conta parental",
                                    fontSize = 11.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Seus pais ainda não criaram uma conta? Você pode continuar conversando normalmente com as amigas! " +
                                    "Quando quiserem, envie um convite oportuno para que seus pais assumam o controle da sua família.",
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.showInviteParentsDialog.value = true },
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_invite_parents_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enviar Convite aos Pais", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Security & Encryption Status Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { viewModel.showEncryptionInfoDialog.value = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = FamilySuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔒 Criptografia de Ponta a Ponta Ativa",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Mensagens de texto, áudios, fotos e vídeos protegidos.",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = "Saber mais",
                            fontSize = 11.sp,
                            color = WhatsAppDarkTeal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Section: Conversas Amigáveis
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Conversas com Amigos (${availableContacts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedButton(
                        onClick = { viewModel.showAddNewContactDialog.value = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("friend_request_new_contact_button")
                    ) {
                        Text("+ Adicionar Amiga", fontSize = 11.sp, color = WhatsAppDarkTeal)
                    }
                }
            }

            items(availableContacts, key = { it.id }) { contact ->
                FriendContactItem(
                    contact = contact,
                    onClick = { viewModel.openChat(contact.id) }
                )
            }
        }
    }
}

@Composable
fun FriendContactItem(
    contact: ContactEntity,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("friend_contact_card_${contact.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
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
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = contact.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = contact.lastSeen,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = FamilySuccessGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${contact.relationship} • Criptografado E2EE",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
