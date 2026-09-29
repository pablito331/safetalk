package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FamilyAlertRed
import com.example.ui.theme.WhatsAppDarkTeal

@Composable
fun AddNewContactDialog(
    isParent: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, relationship: String, isApproved: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf(if (isParent) "Amigo(a) da Escola" else "Amiguinha(o)") }
    var autoApprove by remember { mutableStateOf(isParent) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("add_new_contact_dialog"),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(WhatsAppDarkTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isParent) "Adicionar Contato Permitido" else "Pedir Adição de Amigo",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isParent) "Gerenciamento de contatos seguros" else "Seus pais receberão o pedido",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isParent)
                        "Cadastre um novo amigo ou familiar para conversar de forma segura e criptografada com seu filho:"
                    else
                        "Digite o nome e (opcionalmente) o e-mail ou telefone do seu amigo(a). Seus pais poderão analisar e aprovar a conversa:",
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Nome do Contato") },
                    placeholder = { Text("Ex: Mariana (Balé)") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = WhatsAppDarkTeal)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_contact_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; errorMessage = null },
                    label = { Text("E-mail ou Telefone (opcional)") },
                    placeholder = { Text("ex: amiga@escola.com") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = WhatsAppDarkTeal)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_contact_phone_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Grau / Onde se conhecem") },
                    placeholder = { Text("Ex: Amiga do condomínio, Prima, Colega de sala") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_contact_relationship_input")
                )

                if (isParent) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = autoApprove,
                            onCheckedChange = { autoApprove = it },
                            colors = CheckboxDefaults.colors(checkedColor = WhatsAppDarkTeal)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Aprovar imediatamente para conversas",
                            fontSize = 12.sp,
                            color = Color(0xFF334155)
                        )
                    }
                }

                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = err, color = FamilyAlertRed, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Por favor, preencha o nome do contato."
                    } else {
                        // E-mail/telefone é opcional: a identidade principal é o e-mail,
                        // e nenhuma criança precisa de chip para usar o SafeTalk.
                        onConfirm(name.trim(), phone.trim(), relationship.trim().ifBlank { "Amigo" }, if (isParent) autoApprove else false)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_new_contact_button")
            ) {
                Text(if (isParent) "Salvar Contato" else "Enviar Pedido aos Pais")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}
