package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FamilyGroupEntity
import com.example.data.model.GroupInviteEntity
import com.example.ui.theme.SafeTalkPrimary

@Composable
fun GroupApprovalDialog(
    group: FamilyGroupEntity? = null,
    invite: GroupInviteEntity? = null,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = SafeTalkPrimary
                    )
                    Text(
                        text = "Aprovação de Grupo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val title = group?.groupName ?: invite?.inviteeName ?: "Grupo"
                val subtitle = if (group != null) {
                    "${group.creatorName} criou um grupo para revisão dos pais."
                } else {
                    "Este convite precisa de aprovação dos responsáveis."
                }

                Text(title, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(subtitle, color = Color.Gray, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Fechar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Recusar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = SafeTalkPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Aprovar")
                    }
                }
            }
        }
    }
}
