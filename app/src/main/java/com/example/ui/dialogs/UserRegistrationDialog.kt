package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.SafeTalkAccent
import com.example.ui.theme.SafeTalkDark
import com.example.ui.theme.SafeTalkPrimary
import com.example.ui.theme.SafeTalkPrimaryContainer
import com.example.ui.theme.SafeTalkSuccess

@Composable
fun UserRegistrationDialog(
    initialName: String = "",
    initialAge: Int = 0,
    initialLogin: String = "",
    initialSpouseName: String = "",
    initialSpouseContact: String = "",
    onDismiss: () -> Unit,
    onSave: (name: String, age: Int, login: String, isEmail: Boolean, pin: String, spouseName: String, spouseContact: String, familyCode: String, isAutonomous: Boolean, joinFamily: Boolean, desiredHierarchy: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var ageText by remember { mutableStateOf(initialAge.toString()) }
    // E-mail é a identidade principal no SafeTalk (evita exigir chip de celular para crianças).
    // Telefone continua aceito como alternativa opcional.
    var isEmailSelected by remember { mutableStateOf(initialLogin.isBlank() || initialLogin.contains("@")) }
    var loginIdentifier by remember { mutableStateOf(initialLogin) }
    var pin by remember { mutableStateOf("") }
    var spouseName by remember { mutableStateOf(initialSpouseName) }
    var spouseContact by remember { mutableStateOf(initialSpouseContact) }
    var linkSpouseChecked by remember { mutableStateOf(true) }
    var isAutonomousChild by remember { mutableStateOf(false) }
    var joinMode by remember { mutableStateOf(false) }
    var desiredRole by remember { mutableStateOf("Filha") }
    var familyCodeInput by remember { mutableStateOf("") }

    val age = ageText.toIntOrNull() ?: 0
    val isAdult = age >= 18
    // Idade é a base do papel no app: sem ela, tudo erra (perfil vazio = 0 anos = criança).
    val ageInvalid = ageText.isBlank() || age == 0 || age > 120
    // Menor só entra no app como: amigo convidado (autônomo) ou com o código
    // da família (fica pendente na aprovação dos pais). Nunca criando família.
    val minorInvalidCreate = !isAdult && !isAutonomousChild && familyCodeInput.isBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // largura total em celulares
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp) // em telas grandes não fica largo demais
                .padding(vertical = 24.dp, horizontal = 16.dp)
                .testTag("dialog_user_registration")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header com a identidade SafeTalk
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SafeTalkPrimary)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAdult) Icons.Default.Shield else Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cadastro SafeTalk",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Identificação inteligente por idade",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Nome
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome Completo / Como quer ser chamado") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SafeTalkPrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SafeTalkPrimary,
                            focusedLabelColor = SafeTalkPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            unfocusedLabelColor = Color(0xFF64748B),
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            cursorColor = SafeTalkPrimary,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tipo de Login: E-mail (padrão, sem necessidade de chip) ou Telefone (opcional)
                    Text(
                        text = "Entrar com: (e-mail recomendado — telefone é opcional)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Telefone
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isEmailSelected) SafeTalkPrimaryContainer else Color(0xFFF1F5F9))
                                .border(
                                    width = if (!isEmailSelected) 1.5.dp else 1.dp,
                                    color = if (!isEmailSelected) SafeTalkPrimary else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { isEmailSelected = false }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = if (!isEmailSelected) SafeTalkPrimary else Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Telefone",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isEmailSelected) SafeTalkPrimary else Color(0xFF64748B)
                                )
                            }
                        }

                        // E-mail
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isEmailSelected) SafeTalkPrimaryContainer else Color(0xFFF1F5F9))
                                .border(
                                    width = if (isEmailSelected) 1.5.dp else 1.dp,
                                    color = if (isEmailSelected) SafeTalkPrimary else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { isEmailSelected = true }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    tint = if (isEmailSelected) SafeTalkPrimary else Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "E-mail",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEmailSelected) SafeTalkPrimary else Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it },
                        label = { Text(if (isEmailSelected) "Endereço de E-mail" else "Número de Celular") },
                        placeholder = {
                            Text(if (isEmailSelected) "ex: pedro@escola.com" else "(11) 98888-7777")
                        },
                        leadingIcon = {
                            Icon(
                                if (isEmailSelected) Icons.Default.Email else Icons.Default.Phone,
                                contentDescription = null,
                                tint = SafeTalkPrimary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (isEmailSelected) KeyboardType.Email else KeyboardType.Phone
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_login_input"),
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

                    Spacer(modifier = Modifier.height(14.dp))

                    // Idade
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { if (it.length <= 3) ageText = it.filter { char -> char.isDigit() } },
                        label = { Text("Idade (anos) *") },
                        leadingIcon = {
                            Icon(Icons.Default.Security, contentDescription = null, tint = SafeTalkPrimary)
                        },
                        isError = ageInvalid,
                        supportingText = {
                            if (ageInvalid) Text("Informe uma idade válida (1–120)", fontSize = 11.sp)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_age_input"),
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

                    Spacer(modifier = Modifier.height(14.dp))

                    // DETECÇÃO AUTOMÁTICA
                    if (!isAdult) {
                        // CRIANÇA / ADOLESCENTE
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SafeTalkSuccess.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎉", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Perfil Criança / Jovem ($age anos)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF166534)
                                        )
                                        Text(
                                            text = "Proteção e conversas com amigos autorizados",
                                            fontSize = 11.sp,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "• Acesso seguro focado nas conversas\n• Não possui acesso a controles familiares ou finanças dos pais\n• Filtro divertido de palavrões ativado",
                                    fontSize = 12.sp,
                                    color = Color(0xFF166534),
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isAutonomousChild,
                                        onCheckedChange = { isAutonomousChild = it },
                                        colors = CheckboxDefaults.colors(checkedColor = SafeTalkSuccess)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Instalando sozinho (modo amigo convidado sem pais)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF334155)
                                    )
                                }

                                // Menor sem modo autônomo entra com o código da família
                                // (fica PENDENTE até os pais aprovarem).
                                if (!isAutonomousChild) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = familyCodeInput,
                                        onValueChange = { familyCodeInput = it.uppercase() },
                                        label = { Text("Código Familiar dos Pais (ex: FAM-7749)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = SafeTalkSuccess,
                                            focusedLabelColor = SafeTalkSuccess,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            unfocusedLabelColor = Color(0xFF64748B),
                                            focusedTextColor = Color(0xFF0F172A),
                                            unfocusedTextColor = Color(0xFF0F172A),
                                            cursorColor = SafeTalkSuccess,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        // PAI / MÃE / RESPONSÁVEL
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SafeTalkPrimaryContainer),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SafeTalkPrimary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = SafeTalkPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Perfil Responsável / Pais ($age anos)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = SafeTalkDark
                                        )
                                        Text(
                                            text = "Acesso completo de supervisão, finanças e regras",
                                            fontSize = 11.sp,
                                            color = SafeTalkPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Escolha: criar nova família OU entrar em uma existente com código
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (!joinMode) SafeTalkPrimary else Color(0xFFF1F5F9))
                                            .border(1.dp, if (!joinMode) SafeTalkPrimary else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                                            .clickable { joinMode = false }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Criar minha família", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (!joinMode) Color.White else Color(0xFF64748B))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (joinMode) SafeTalkPrimary else Color(0xFFF1F5F9))
                                            .border(1.dp, if (joinMode) SafeTalkPrimary else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                                            .clickable { joinMode = true }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Entrar com código", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (joinMode) Color.White else Color(0xFF64748B))
                                    }
                                }

                                if (joinMode) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = familyCodeInput,
                                        onValueChange = { familyCodeInput = it.uppercase() },
                                        label = { Text("Código da Família (ex: FAM-7K4Q9X2M)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Sua hierarquia (os pais confirmam depois):",
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf("Filha", "Filho", "Responsável").forEach { role ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (desiredRole == role) SafeTalkPrimary else Color(0xFFF1F5F9))
                                                    .border(1.dp, if (desiredRole == role) SafeTalkPrimary else Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                                                    .clickable { desiredRole = role }
                                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                            ) {
                                                Text(role, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (desiredRole == role) Color.White else Color(0xFF334155))
                                            }
                                        }
                                    }
                                }

                                if (!joinMode) {
                                OutlinedTextField(
                                    value = pin,
                                    onValueChange = { if (it.length <= 4) pin = it.filter { c -> c.isDigit() } },
                                    label = { Text("PIN dos Pais (4 dígitos)") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = SafeTalkPrimary)
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
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Vincular Cônjuge (Esposa / Esposo)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = linkSpouseChecked,
                                        onCheckedChange = { linkSpouseChecked = it },
                                        colors = CheckboxDefaults.colors(checkedColor = SafeTalkPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Vincular Cônjuge (Esposa / Esposo)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1E293B)
                                    )
                                }

                                if (linkSpouseChecked) {
                                    OutlinedTextField(
                                        value = spouseName,
                                        onValueChange = { spouseName = it },
                                        label = { Text("Nome do Cônjuge (ex: Mãe - Ana)") },
                                        leadingIcon = {
                                            Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = SafeTalkPrimary)
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
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = spouseContact,
                                        onValueChange = { spouseContact = it },
                                        label = { Text("Telefone / E-mail do Cônjuge") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Phone, contentDescription = null, tint = SafeTalkPrimary)
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
                                }
                                } // fim if (!joinMode)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Nota de Privacidade Local (Sem Nuvem)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📱", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Armazenamento 100% Local: As mensagens ficam salvas somente no banco interno deste celular. Nenhuma conversa sobe para servidores de nuvem.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Ações
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar", color = Color(0xFF64748B))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        val registrationInvalid = ageInvalid || minorInvalidCreate
                        Button(
                            enabled = !registrationInvalid,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAdult) SafeTalkPrimary else SafeTalkSuccess,
                                disabledContainerColor = Color(0xFFCBD5E1),
                                disabledContentColor = Color(0xFF64748B)
                            ),
                            onClick = {
                                if (registrationInvalid) return@Button
                                onSave(
                                    name.ifEmpty { if (isAdult) "Pai (responsável)" else "Meu filho" },
                                    age,
                                    loginIdentifier.ifEmpty { if (isAdult) "pai@familia.com" else "filho@familia.com" },
                                    isEmailSelected,
                                    pin.ifEmpty { "" },
                                    if (linkSpouseChecked && !joinMode) spouseName else "",
                                    if (linkSpouseChecked && !joinMode) spouseContact else "",
                                    familyCodeInput,
                                    isAutonomousChild,
                                    joinMode,
                                    desiredRole
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("reg_confirm_button")
                        ) {
                            Text(
                                text = when {
                                    minorInvalidCreate -> "Menor: informe o código da família ou marque o modo autônomo"
                                    isAdult -> "Confirmar Modo Pais"
                                    else -> "Entrar no SafeTalk"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
