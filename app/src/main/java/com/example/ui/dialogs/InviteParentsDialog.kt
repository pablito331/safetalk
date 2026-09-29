package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FamilyAlertRed
import com.example.ui.theme.FamilySuccessGreen
import com.example.ui.theme.WhatsAppDarkTeal

@Composable
fun InviteParentsDialog(
    familyCode: String = "",
    childName: String = "",
    onDismiss: () -> Unit,
    onTakeOverControl: (parentName: String, pin: String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Enviar Convite, 1: Pais Presentes
    var copiedToClipboard by remember { mutableStateOf(false) }

    var parentNameInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var pinConfirmInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val inviteMessageText = "Oi Pai/Mãe! Estou usando o FamíliaSafe para conversar com minhas amiguinhas com segurança total. " +
            "Baixe o app e conecte-se com o código da nossa família: $familyCode para gerenciar minha conta, aprovar amigos, definir tarefas e mesada com carinho! ❤️"

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("invite_parents_dialog"),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(WhatsAppDarkTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FamilyRestroom,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Conectar com Pais",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Vincule seus responsáveis legais",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = WhatsAppDarkTeal,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("💌 Enviar Convite", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("🛡️ Assumir Agora", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                if (selectedTab == 0) {
                    // Invitation flow
                    Text(
                        text = "Seus pais ainda não criaram conta? Não tem problema! Você pode continuar conversando e enviar este convite oportuno para que eles assumam a gestão quando quiserem:",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Código Familiar:",
                                    fontSize = 11.sp,
                                    color = Color(0xFF166534),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = familyCode,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF166534)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = inviteMessageText,
                                    fontSize = 11.sp,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(inviteMessageText))
                                copiedToClipboard = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_invite_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (copiedToClipboard) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (copiedToClipboard) FamilySuccessGreen else WhatsAppDarkTeal
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (copiedToClipboard) "Copiado!" else "Copiar",
                                fontSize = 12.sp,
                                color = if (copiedToClipboard) FamilySuccessGreen else WhatsAppDarkTeal
                            )
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(inviteMessageText))
                                copiedToClipboard = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("share_whatsapp_invite_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Compartilhar", fontSize = 12.sp, color = Color.White)
                        }
                    }
                } else {
                    // Parent is present and wants to take over control
                    Text(
                        text = "Os pais estão por perto? Digite o nome do responsável e crie um PIN de 4 dígitos para assumir o controle total desta família agora:",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = parentNameInput,
                        onValueChange = { parentNameInput = it; errorMessage = null },
                        label = { Text("Nome do Pai ou Mãe") },
                        placeholder = { Text("Ex: Juliana (Mãe)") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = WhatsAppDarkTeal)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_takeover_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { ch -> ch.isDigit() }) {
                                pinInput = it
                                errorMessage = null
                            }
                        },
                        label = { Text("Criar PIN de 4 dígitos") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = WhatsAppDarkTeal)
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_takeover_pin_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pinConfirmInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { ch -> ch.isDigit() }) {
                                pinConfirmInput = it
                                errorMessage = null
                            }
                        },
                        label = { Text("Confirmar PIN de 4 dígitos") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = WhatsAppDarkTeal)
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_takeover_pin_confirm_input")
                    )

                    errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = err, color = FamilyAlertRed, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (parentNameInput.isBlank()) {
                                errorMessage = "Por favor, digite o nome do responsável."
                            } else if (pinInput.length != 4) {
                                errorMessage = "O PIN deve conter exatamente 4 números."
                            } else if (pinInput != pinConfirmInput) {
                                errorMessage = "Os PINs digitados não conferem."
                            } else {
                                onTakeOverControl(parentNameInput.trim(), pinInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_parent_takeover_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Criar Conta & Assumir Controle", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_invite_dialog_button")
            ) {
                Text("Fechar", color = Color.Gray)
            }
        }
    )
}
