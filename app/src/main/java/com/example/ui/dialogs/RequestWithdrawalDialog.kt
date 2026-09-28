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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Warning
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
import com.example.ui.theme.FamilyAlertRed
import com.example.ui.theme.SafeTalkPrimary
import com.example.ui.theme.SafeTalkSuccess

@Composable
fun RequestWithdrawalDialog(
    currentBalance: Double,
    hasPendingTasks: Boolean,
    pendingTasksCount: Int = 0,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, reason: String) -> Unit
) {
    var amountText by remember { mutableStateOf(if (currentBalance >= 20.0) "20.00" else "%.2f".format(currentBalance.coerceAtLeast(5.0))) }
    var reasonText by remember { mutableStateOf("Comprar lanche na cantina") }

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
                                .background(SafeTalkSuccess.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Payments,
                                contentDescription = null,
                                tint = SafeTalkSuccess,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Resgatar em Dinheiro",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Saldo: R$ ${"%.2f".format(currentBalance)}",
                                fontSize = 12.sp,
                                color = SafeTalkSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Condition Check: Pending Tasks Restriction
                if (hasPendingTasks) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, FamilyAlertRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = FamilyAlertRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Resgate Bloqueado!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = FamilyAlertRed
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Você possui $pendingTasksCount missão(ões) pendente(s) hoje. Para solicitar dinheiro em mãos com seus pais, cumpra todas as suas tarefas primeiro!",
                                fontSize = 12.sp,
                                color = Color(0xFF991B1B),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = SafeTalkPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dismiss_blocked_withdrawal_button")
                    ) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ir para Minhas Missões", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Allowed to withdraw
                    Text(
                        text = "Valores rápidos:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(5.0, 10.0, 20.0, currentBalance)
                        presets.forEach { preset ->
                            if (preset > 0 && preset <= currentBalance) {
                                val isSelected = amountText.toDoubleOrNull() == preset
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) SafeTalkSuccess else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { amountText = "%.2f".format(preset) }
                                ) {
                                    Text(
                                        text = if (preset == currentBalance) "Tudo" else "R$ ${preset.toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF334155),
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Valor do Saque (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdrawal_amount_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("Motivo / Onde vai usar?") },
                        placeholder = { Text("Ex: Lanche na escola, figurinhas...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdrawal_reason_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Explanation card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 Ao solicitar, seu pai ou mãe receberá o pedido. Eles entregarão a nota/moedas em mãos e confirmarão no app, descontando do seu cofrinho.",
                            fontSize = 11.sp,
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val enteredAmount = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val isAmountValid = enteredAmount > 0.0 && enteredAmount <= currentBalance

                    Button(
                        onClick = {
                            if (isAmountValid) {
                                onConfirm(enteredAmount, reasonText)
                            }
                        },
                        enabled = isAmountValid,
                        colors = ButtonDefaults.buttonColors(containerColor = SafeTalkSuccess),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_withdrawal_button")
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Solicitar R$ ${"%.2f".format(enteredAmount)} aos Pais",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
