package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.ui.theme.FamilyAlertRed
import com.example.ui.theme.FamilySecondary
import com.example.ui.theme.SafeTalkPrimary
import com.example.ui.theme.WhatsAppDarkTeal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
    availableChildren: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        desc: String,
        rewardAmount: Double,
        rewardPoints: Int,
        category: String,
        dueDate: String,
        assignedChildName: String,
        penaltyAmount: Double,
        requiresPhotoEvidence: Boolean,
        recurrence: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rewardAmountText by remember { mutableStateOf("5.00") }
    var penaltyAmountText by remember { mutableStateOf("2.00") }
    var rewardPointsText by remember { mutableStateOf("50") }
    var selectedCategory by remember { mutableStateOf("Casa") }
    var selectedChild by remember { mutableStateOf(availableChildren.firstOrNull() ?: "Todos os Filhos") }
    var customChildName by remember { mutableStateOf("") }
    var isCustomChildSelected by remember { mutableStateOf(false) }
    var requiresPhotoEvidence by remember { mutableStateOf(true) }
    var selectedRecurrence by remember { mutableStateOf("NUNCA") }

    // Date & Time Picker states
    val currentCalendar = remember { Calendar.getInstance() }
    var selectedDateMillis by remember { mutableLongStateOf(currentCalendar.timeInMillis) }
    var selectedHour by remember { mutableIntStateOf(19) }
    var selectedMinute by remember { mutableIntStateOf(0) }

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    // Formatted date string for preview and database
    val formattedDueDate by remember {
        derivedStateOf {
            val cal = Calendar.getInstance()
            cal.timeInMillis = selectedDateMillis
            cal.set(Calendar.HOUR_OF_DAY, selectedHour)
            cal.set(Calendar.MINUTE, selectedMinute)

            val todayCal = Calendar.getInstance()
            val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

            val timeStr = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)

            val dateStr = when {
                cal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                        cal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR) -> "Hoje"
                cal.get(Calendar.YEAR) == tomorrowCal.get(Calendar.YEAR) &&
                        cal.get(Calendar.DAY_OF_YEAR) == tomorrowCal.get(Calendar.DAY_OF_YEAR) -> "Amanhã"
                else -> {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    sdf.format(Date(selectedDateMillis))
                }
            }

            "$dateStr às $timeStr"
        }
    }

    val categories = listOf("Casa", "Estudos", "Pets", "Higiene")
    val recurrenceOptions = listOf(
        "NUNCA" to "Única (Não repete)",
        "DIARIA" to "🔄 Todos os Dias",
        "DIAS_UTEIS" to "📅 Dias Úteis (Seg-Sex)",
        "FINS_DE_SEMANA" to "🏖️ Fins de Semana",
        "SEMANAL" to "🗓️ Semanalmente"
    )

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
                    Column {
                        Text(
                            text = "Nova Tarefa Familiar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Defina missão, prazo exato e recompensas",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selection of child
                Text(
                    text = "Atribuir para qual filho(a)?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SafeTalkPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableChildren.forEach { childName ->
                        val isSelected = !isCustomChildSelected && selectedChild == childName
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SafeTalkPrimary else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .clickable {
                                    isCustomChildSelected = false
                                    selectedChild = childName
                                }
                                .testTag("assign_child_chip_$childName")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                val icon = if (childName == "Todos os Filhos") Icons.Default.Groups else Icons.Default.Person
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = childName,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else Color(0xFF1E293B),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    // "+ Outro" chip
                    val isOtherSelected = isCustomChildSelected
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isOtherSelected) SafeTalkPrimary else Color(0xFFF8FAFC),
                        modifier = Modifier
                            .clickable { isCustomChildSelected = true }
                            .testTag("assign_child_chip_custom")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = if (isOtherSelected) Color.White else Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Outro Filho",
                                fontSize = 12.sp,
                                color = if (isOtherSelected) Color.White else Color(0xFF475569),
                                fontWeight = if (isOtherSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                if (isCustomChildSelected) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customChildName,
                        onValueChange = { customChildName = it },
                        label = { Text("Nome do Filho(a) responsável") },
                        placeholder = { Text("Ex: Sofia, Gabriel...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_child_name_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nome da Tarefa (ex: Ler 20 min, Arrumar Cama)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_task_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Instruções ou Detalhes") },
                    placeholder = { Text("Ex: Guardar brinquedos e dobrar cobertor") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- PRAZO INTERATIVO: DATA E HORA EXATAS ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EditCalendar,
                                    contentDescription = null,
                                    tint = WhatsAppDarkTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Prazo da Tarefa (Dia e Hora)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WhatsAppDarkTeal
                                )
                            }

                            // Current selected preview badge
                            Surface(
                                color = WhatsAppDarkTeal,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = formattedDueDate,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interactive Day and Time Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Date Picker Button
                            OutlinedButton(
                                onClick = { showDatePickerDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pick_task_date_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF1E293B)
                                )
                            ) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = WhatsAppDarkTeal
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val displayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDateMillis))
                                Text(
                                    text = displayDate,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Time Picker Button
                            OutlinedButton(
                                onClick = { showTimePickerDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pick_task_time_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF1E293B)
                                )
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = WhatsAppDarkTeal
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val displayTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                                Text(
                                    text = displayTime,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Presets
                        Text(
                            text = "Atalhos rápidos:",
                            fontSize = 11.sp,
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val presets = listOf(
                                "Hoje 18h" to {
                                    selectedDateMillis = System.currentTimeMillis()
                                    selectedHour = 18
                                    selectedMinute = 0
                                },
                                "Hoje 20h" to {
                                    selectedDateMillis = System.currentTimeMillis()
                                    selectedHour = 20
                                    selectedMinute = 0
                                },
                                "Amanhã 12h" to {
                                    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                                    selectedDateMillis = cal.timeInMillis
                                    selectedHour = 12
                                    selectedMinute = 0
                                },
                                "Amanhã 19h" to {
                                    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                                    selectedDateMillis = cal.timeInMillis
                                    selectedHour = 19
                                    selectedMinute = 0
                                },
                                "Fim de Semana" to {
                                    val cal = Calendar.getInstance()
                                    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                                        cal.add(Calendar.DAY_OF_YEAR, 1)
                                    }
                                    selectedDateMillis = cal.timeInMillis
                                    selectedHour = 18
                                    selectedMinute = 0
                                }
                            )

                            presets.forEach { (label, action) ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFDCFCE7),
                                    modifier = Modifier.clickable { action() }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rewards & Penalties
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = rewardAmountText,
                        onValueChange = { rewardAmountText = it },
                        label = { Text("Recompensa (R$)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_task_reward_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = penaltyAmountText,
                        onValueChange = { penaltyAmountText = it },
                        label = { Text("Desconto s/ Prazo (R$)") },
                        supportingText = { Text("Penalidade se atrasar", fontSize = 10.sp, color = FamilyAlertRed) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_task_penalty_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = rewardPointsText,
                    onValueChange = { rewardPointsText = it },
                    label = { Text("Pontos da Missão") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Camera Photo Evidence Switch
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = SafeTalkPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Exigir Foto da Câmera",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Abre a câmera ao vivo (bloqueia fotos falsas da galeria)",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Switch(
                            checked = requiresPhotoEvidence,
                            onCheckedChange = { requiresPhotoEvidence = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SafeTalkPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Recurrence Selector
                Text(
                    text = "Repetição da Tarefa (Recorrente):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SafeTalkPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recurrenceOptions.forEach { (code, label) ->
                        val isSelected = code == selectedRecurrence
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) SafeTalkPrimary else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .clickable { selectedRecurrence = code }
                                .testTag("recurrence_chip_$code")
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else Color(0xFF334155),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Categoria:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = cat == selectedCategory
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SafeTalkPrimary else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                val finalChildTarget = if (isCustomChildSelected) {
                    customChildName.ifBlank { "Filho" }
                } else {
                    selectedChild
                }

                Button(
                    onClick = {
                        val amount = rewardAmountText.toDoubleOrNull() ?: 5.0
                        val penalty = penaltyAmountText.toDoubleOrNull() ?: 2.0
                        val points = rewardPointsText.toIntOrNull() ?: 50
                        onConfirm(
                            title,
                            description,
                            amount,
                            points,
                            selectedCategory,
                            formattedDueDate,
                            finalChildTarget,
                            penalty,
                            requiresPhotoEvidence,
                            selectedRecurrence
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SafeTalkPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_create_task_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Atribuir para $finalChildTarget ($formattedDueDate)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    // --- DIALOG: Material 3 Date Picker ---
    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            // Adjust for UTC offset so the local date matches
                            val timeZone = TimeZone.getDefault()
                            val offset = timeZone.getOffset(it)
                            selectedDateMillis = it - offset
                        }
                        showDatePickerDialog = false
                    }
                ) {
                    Text("Confirmar Data", fontWeight = FontWeight.Bold, color = SafeTalkPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- DIALOG: Material 3 Time Picker ---
    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            is24Hour = true
        )
        Dialog(onDismissRequest = { showTimePickerDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = SafeTalkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Definir Horário Limite",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TimePicker(state = timePickerState)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePickerDialog = false }) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                selectedHour = timePickerState.hour
                                selectedMinute = timePickerState.minute
                                showTimePickerDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SafeTalkPrimary)
                        ) {
                            Text("Confirmar Hora")
                        }
                    }
                }
            }
        }
    }
}
