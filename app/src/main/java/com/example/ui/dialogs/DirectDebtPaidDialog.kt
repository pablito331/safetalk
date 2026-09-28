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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Payments
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
import com.example.ui.theme.SafeTalkPrimary
import com.example.ui.theme.SafeTalkSuccess

@Composable
fun DirectDebtPaidDialog(
    children: List<String> = emptyList(),
    currentChildBalance: Double = 0.0,
    onDismiss: () -> Unit,
    onConfirmDebtPaid: (childName: String, amount: Double, reason: String) -> Unit
) {
    var selectedChild by remember { mutableStateOf(children.firstOrNull() ?: "") }
    var amountText by remember { mutableStateOf("20.00") }
    var reasonText by remember { mutableStateOf("Dinheiro entregue em mãos para passeio") }

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
                                Icons.Default.Handshake,
                                contentDescription = null,
                                tint = SafeTalkSuccess,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Dívida Paga / Dinheiro em Mãos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Dar em espécie e debitar do cofre",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Para qual filho(a) entregou o dinheiro?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    children.forEach { child ->
                        val isSelected = child == selectedChild
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) SafeTalkSuccess else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedChild = child }
                        ) {
                            Text(
                                text = child,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF1E293B),
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Valor Entregue em Mãos (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debt_paid_amount_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Motivo / Descrição") },
                    placeholder = { Text("Ex: Lanche, transporte, compras...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debt_paid_reason_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💵 Ao confirmar, esse valor é debitado imediatamente do cofre do filho no app e registrado no extrato como dívida paga em mãos.",
                        fontSize = 11.sp,
                        color = Color(0xFF166534),
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                val enteredAmount = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0
                val isValid = enteredAmount > 0.0

                Button(
                    onClick = {
                        if (isValid) {
                            onConfirmDebtPaid(selectedChild, enteredAmount, reasonText)
                        }
                    },
                    enabled = isValid,
                    colors = ButtonDefaults.buttonColors(containerColor = SafeTalkSuccess),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_debt_paid_button")
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirmar Pagamento de R$ ${"%.2f".format(enteredAmount)}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
