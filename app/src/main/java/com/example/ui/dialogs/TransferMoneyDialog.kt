package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.SiblingAccountEntity
import com.example.ui.theme.SafeTalkAccent
import com.example.ui.theme.SafeTalkPrimary

@Composable
fun TransferMoneyDialog(
    senderName: String = "Pedro",
    senderBalance: Double,
    availableSiblings: List<SiblingAccountEntity>,
    onDismiss: () -> Unit,
    onConfirmTransfer: (toSibling: String, amount: Double, reason: String) -> Unit
) {
    val targetSiblings = availableSiblings.filter { !it.name.equals(senderName, ignoreCase = true) }
    var selectedSibling by remember { mutableStateOf(targetSiblings.firstOrNull()?.name ?: "Mariana") }
    var amountText by remember { mutableStateOf("5.00") }
    var reasonText by remember { mutableStateOf("Ajuda no dever de casa") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = Color(0xFF4338CA),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Transferir para Irmão(ã)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Seu Saldo: R$ ${"%.2f".format(senderBalance)}",
                                fontSize = 12.sp,
                                color = SafeTalkPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Select Recipient Sibling
                Text(
                    text = "Transferir para quem?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    targetSiblings.forEach { sib ->
                        val isSelected = sib.name == selectedSibling
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF4338CA) else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSibling = sib.name }
                                .testTag("select_sibling_${sib.name}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = sib.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color.White else Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Saldo: R$ ${"%.2f".format(sib.balance)}",
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color(0xFFC7D2FE) else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Values
                Text(
                    text = "Valores sugeridos:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(2.0, 5.0, 10.0, 15.0).forEach { preset ->
                        if (preset <= senderBalance) {
                            val isSelected = amountText.toDoubleOrNull() == preset
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF4338CA) else Color(0xFFF8FAFC),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { amountText = "%.2f".format(preset) }
                            ) {
                                Text(
                                    text = "R$ ${preset.toInt()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF334155),
                                    modifier = Modifier.padding(vertical = 7.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Valor a Transferir (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transfer_amount_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Motivo / Recado carinhoso") },
                    placeholder = { Text("Ex: Valeu pela ajuda, lanche...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transfer_reason_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Info banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🤝", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "O valor sairá do seu cofre e entrará imediatamente no cofre do(a) $selectedSibling, com comprovante visível para os pais.",
                            fontSize = 11.sp,
                            color = Color(0xFF166534),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                val enteredAmount = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0
                val isValid = enteredAmount > 0.0 && enteredAmount <= senderBalance && selectedSibling.isNotBlank()

                Button(
                    onClick = {
                        if (isValid) {
                            onConfirmTransfer(selectedSibling, enteredAmount, reasonText)
                        }
                    },
                    enabled = isValid,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_sibling_transfer_button")
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enviar R$ ${"%.2f".format(enteredAmount)} para $selectedSibling",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
