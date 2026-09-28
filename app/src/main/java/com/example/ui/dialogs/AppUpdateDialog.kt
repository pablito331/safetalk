package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SafeTalkPrimary
import com.example.util.AppUpdateInfo

@Composable
fun AppUpdateDialog(
    updateInfo: AppUpdateInfo,
    isDownloading: Boolean,
    downloadProgress: Float,
    onDismiss: () -> Unit,
    onConfirmUpdate: () -> Unit
) {
    Dialog(onDismissRequest = { if (!isDownloading) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SafeTalkPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDownloading) Icons.Default.Download else Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = SafeTalkPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Nova Atualização Disponível!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Versão v${updateInfo.latestVersion} (Atual: v${updateInfo.currentVersion})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SafeTalkPrimary
                )

                if (updateInfo.releaseNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = updateInfo.releaseNotes,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isDownloading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SafeTalkPrimary,
                            trackColor = SafeTalkPrimary.copy(alpha = 0.2f),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Baixando atualização: ${(downloadProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Depois", color = Color(0xFF64748B))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onConfirmUpdate,
                            colors = ButtonDefaults.buttonColors(containerColor = SafeTalkPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Atualizar Agora", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
