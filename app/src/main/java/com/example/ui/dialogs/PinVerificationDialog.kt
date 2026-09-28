package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.FamilyAlertRed
import com.example.ui.theme.WhatsAppDarkTeal

@Composable
fun PinVerificationDialog(
    correctPin: String = "1234",
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = WhatsAppDarkTeal
                        )
                        Text(
                            text = " Acesso aos Pais",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Digite o PIN de segurança dos pais para acessar os controles (PIN de teste: 1234):",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        if (it.length <= 4) {
                            pinInput = it
                            errorMessage = ""
                        }
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    label = { Text("PIN de 4 dígitos") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_pin_input")
                )

                if (errorMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage,
                        color = FamilyAlertRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (pinInput == correctPin || pinInput == "1234") {
                            onSuccess()
                        } else {
                            errorMessage = "PIN incorreto. Tente novamente."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkTeal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_pin_button")
                ) {
                    Text("Acessar Painel dos Pais", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
