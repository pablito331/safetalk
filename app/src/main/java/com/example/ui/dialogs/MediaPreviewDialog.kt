package com.example.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.ChatMessageEntity
import com.example.ui.theme.FamilySecondary
import kotlinx.coroutines.delay

@Composable
fun MediaPreviewDialog(
    message: ChatMessageEntity,
    onDismiss: () -> Unit
) {
    var isVideoPlaying by remember { mutableStateOf(true) }
    var videoProgress by remember { mutableFloatStateOf(0.1f) }

    LaunchedEffect(isVideoPlaying) {
        while (isVideoPlaying) {
            delay(200)
            videoProgress += 0.02f
            if (videoProgress >= 1f) {
                videoProgress = 0f
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = Color.Black,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (message.mediaType == "VIDEO") "Reprodutor de Vídeo" else "Visualizador de Foto",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_media_preview")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.White)
                    }
                }

                // Media Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (message.mediaType == "VIDEO") {
                        // Simulated video stream canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(380.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_family_banner),
                                contentDescription = "Prévia de Vídeo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Play/Pause Overlay Button
                            IconButton(
                                onClick = { isVideoPlaying = !isVideoPlaying },
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(
                                    imageVector = if (isVideoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Pausar/Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    } else {
                        // Photo display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(380.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_family_banner),
                                contentDescription = "Foto Expandida",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

                // Bottom caption & controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    if (message.mediaType == "VIDEO") {
                        LinearProgressIndicator(
                            progress = { videoProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = FamilySecondary,
                            trackColor = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = message.text.ifBlank { "Arquivo de mídia segura" },
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enviado às ${message.formattedTime} • Verificado pelo Controle Parental",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
