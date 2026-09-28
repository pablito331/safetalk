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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FamilyTaskEntity
import com.example.ui.theme.SafeTalkPrimary
import com.example.ui.theme.SafeTalkSuccess

@Composable
fun TransferTaskDialog(
    task: FamilyTaskEntity,
    currentChildName: String = "Pedro",
    availableSiblings: List<String> = listOf("Mariana", "Lucas"),
    onDismiss: () -> Unit,
    onConfirmTransferRequest: (targetSibling: String) -> Unit
) {
    val eligibleSiblings = availableSiblings.filter { !it.equals(currentChildName, ignoreCase = true) }
    var selectedSibling by remember { mutableStateOf(eligibleSiblings.firstOrNull() ?: "Mariana") }

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
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Repassar Missão",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Transferência entre irmãos",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Task Summary Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = task.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        if (task.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = task.description,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Recompensa: +R$ ${"%.2f".format(task.rewardAmount)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SafeTalkSuccess
                            )
                            Text(
                                text = "Prazo: ${task.dueDate}",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Para qual irmão(ã) deseja repassar?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    eligibleSiblings.forEach { sibName ->
                        val isSelected = sibName == selectedSibling
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFD97706) else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSibling = sibName }
                                .testTag("select_task_sibling_$sibName")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color(0xFF64748B),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = sibName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color.White else Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Workflow Rule Explanatory Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "📋 Fluxo de Aprovação Obrigatório:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1º Passo: $selectedSibling precisa aceitar assumir a missão.\n2º Passo: Os Pais precisam autorizar a troca no painel familiar.",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onConfirmTransferRequest(selectedSibling) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_task_transfer_request_button")
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Solicitar Troca para $selectedSibling",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
