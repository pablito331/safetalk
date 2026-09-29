package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ChildProfileEntity
import com.example.ui.dialogs.AddFundsDialog
import com.example.ui.dialogs.AddNewContactDialog
import com.example.ui.dialogs.ApproveContactDialog
import com.example.ui.dialogs.CreateTaskDialog
import com.example.ui.dialogs.DirectDebtPaidDialog
import com.example.ui.dialogs.EmergencyRingDialog
import com.example.ui.dialogs.EncryptionInfoDialog
import com.example.ui.dialogs.InviteParentsDialog
import com.example.ui.dialogs.MediaPreviewDialog
import com.example.ui.dialogs.PinVerificationDialog
import com.example.ui.dialogs.ProfileEditorDialog
import com.example.ui.dialogs.RequestWithdrawalDialog
import com.example.ui.dialogs.TransferMoneyDialog
import com.example.ui.dialogs.TransferTaskDialog
import com.example.ui.dialogs.UserRegistrationDialog
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChildModeScreen
import com.example.ui.screens.FriendSimplifiedScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SafeTalkAccent
import com.example.ui.viewmodel.AppRole
import com.example.ui.viewmodel.FamilySafeViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.platform.LocalContext
import com.example.ui.dialogs.AppUpdateDialog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: FamilySafeViewModel = viewModel()
                FamilySafeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FamilySafeApp(viewModel: FamilySafeViewModel) {
    val context = LocalContext.current
    val currentRole by viewModel.currentRole.collectAsState()
    val selectedContactId by viewModel.selectedContactId.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val childProfile by viewModel.childProfile.collectAsState()

    val showCreateTask by viewModel.showCreateTaskDialog.collectAsState()
    val pendingContact by viewModel.pendingContactToReview.collectAsState()
    val showPin by viewModel.showPinDialog.collectAsState()
    val showAddFunds by viewModel.showAddFundsDialog.collectAsState()
    val previewMedia by viewModel.previewMediaMessage.collectAsState()
    val showInviteParents by viewModel.showInviteParentsDialog.collectAsState()
    val showAddNewContact by viewModel.showAddNewContactDialog.collectAsState()
    val showEncryptionInfo by viewModel.showEncryptionInfoDialog.collectAsState()
    val showRegistration by viewModel.showRegistrationDialog.collectAsState()
    val showProfileEditor by viewModel.showProfileEditorDialog.collectAsState()
    val funFilterToast by viewModel.funFilterToastMessage.collectAsState()

    // Auto-Update States
    val availableUpdate by viewModel.availableUpdate.collectAsState()
    val isDownloadingUpdate by viewModel.isDownloadingUpdate.collectAsState()
    val updateDownloadProgress by viewModel.updateDownloadProgress.collectAsState()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsState()

    // New Sibling & Withdrawal Dialog States
    val showRequestWithdrawal by viewModel.showRequestWithdrawalDialog.collectAsState()
    val showTransferMoney by viewModel.showTransferMoneyDialog.collectAsState()
    val taskToTransfer by viewModel.taskToTransferToSibling.collectAsState()
    val showDirectDebtPaid by viewModel.showDirectDebtPaidDialog.collectAsState()
    val siblingAccounts by viewModel.siblingAccounts.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val withdrawalFeedback by viewModel.withdrawalFeedbackMessage.collectAsState()

    val profile = childProfile

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_root_surface"),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (profile == null) {
                if (showRegistration) {
                    UserRegistrationDialog(
                        initialName = "",
                        initialAge = 0,
                        initialLogin = "",
                        initialSpouseName = "",
                        initialSpouseContact = "",
                        onDismiss = { viewModel.showRegistrationDialog.value = false },
                        onSave = { name, age, login, isEmail, pin, spouseName, spouseContact, familyCode, isAutonomous ->
                            viewModel.registerUser(
                                name = name,
                                age = age,
                                loginIdentifier = login,
                                isEmail = isEmail,
                                pin = pin,
                                spouseName = spouseName,
                                spouseContact = spouseContact,
                                familyCode = familyCode,
                                isAutonomousChild = isAutonomous
                            )
                        }
                    )
                }
                return@Surface
            }

            if (selectedContactId != null) {
                // WhatsApp Chat Detail Screen
                val activeContact = contacts.find { it.id == selectedContactId }
                if (activeContact != null) {
                    ChatDetailScreen(
                        contact = activeContact,
                        viewModel = viewModel
                    )
                }
            } else {
                // Role-based main screens
                AnimatedContent(
                    targetState = currentRole,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "RoleSwitchAnimation"
                ) { role ->
                    when (role) {
                        AppRole.PARENT -> ParentDashboardScreen(viewModel = viewModel)
                        AppRole.CHILD -> ChildModeScreen(viewModel = viewModel)
                        AppRole.FRIEND_SIMPLIFIED -> FriendSimplifiedScreen(viewModel = viewModel)
                    }
                }
            }

            // Dialogs
            if (profile.isUrgentAlarmRinging) {
                EmergencyRingDialog(
                    childName = profile.name,
                    onDismissAndStop = { viewModel.stopEmergencyAlarm() }
                )
            }

            if (showCreateTask) {
                val childNames = siblingAccounts.map { it.name } + profile.name
                CreateTaskDialog(
                    availableChildren = childNames.distinct(),
                    onDismiss = { viewModel.showCreateTaskDialog.value = false },
                    onConfirm = { title, desc, reward, pts, cat, due, assignedChild, penaltyAmount, requiresPhotoEvidence, recurrence ->
                        viewModel.createNewTask(title, desc, reward, pts, cat, due, assignedChild, penaltyAmount, requiresPhotoEvidence, recurrence)
                    }
                )
            }

            // Sibling & Withdrawal Dialogs
            if (showRequestWithdrawal) {
                val hasPending = viewModel.hasPendingTasksForChild(profile.name)
                val pendingCount = tasks.count { it.assignedChildName.equals(profile.name, true) && it.status == "PENDENTE" }
                RequestWithdrawalDialog(
                    currentBalance = profile.balance,
                    hasPendingTasks = hasPending,
                    pendingTasksCount = pendingCount,
                    onDismiss = { viewModel.showRequestWithdrawalDialog.value = false },
                    onConfirm = { amount, reason ->
                        viewModel.requestWithdrawal(amount, reason)
                    }
                )
            }

            if (showTransferMoney) {
                TransferMoneyDialog(
                    senderName = profile.name,
                    senderBalance = profile.balance,
                    availableSiblings = siblingAccounts,
                    onDismiss = { viewModel.showTransferMoneyDialog.value = false },
                    onConfirmTransfer = { toSibling, amount, reason ->
                        viewModel.transferMoneyToSibling(profile.name, toSibling, amount, reason)
                    }
                )
            }

            taskToTransfer?.let { task ->
                TransferTaskDialog(
                    task = task,
                    currentChildName = profile.name,
                    availableSiblings = siblingAccounts.map { it.name }.filter { !it.equals(profile.name, true) },
                    onDismiss = { viewModel.taskToTransferToSibling.value = null },
                    onConfirmTransferRequest = { targetSibling ->
                        viewModel.requestTaskTransfer(task, targetSibling)
                    }
                )
            }

            if (showDirectDebtPaid) {
                val childNames = siblingAccounts.map { it.name } + profile.name
                DirectDebtPaidDialog(
                    children = childNames.distinct(),
                    currentChildBalance = profile.balance,
                    onDismiss = { viewModel.showDirectDebtPaidDialog.value = false },
                    onConfirmDebtPaid = { childName, amount, reason ->
                        viewModel.directDebtPaid(childName, amount, reason)
                    }
                )
            }

            pendingContact?.let { contact ->
                ApproveContactDialog(
                    contact = contact,
                    onDismiss = { viewModel.pendingContactToReview.value = null },
                    onApprove = { viewModel.approveContact(contact) },
                    onBlock = { viewModel.blockContact(contact) }
                )
            }

            if (showPin) {
                PinVerificationDialog(
                    correctPin = profile.parentPin,
                    onDismiss = { viewModel.showPinDialog.value = false },
                    onSuccess = {
                        viewModel.showPinDialog.value = false
                        viewModel.switchRole(AppRole.PARENT)
                    }
                )
            }

            if (showAddFunds) {
                AddFundsDialog(
                    onDismiss = { viewModel.showAddFundsDialog.value = false },
                    onConfirm = { amount, desc ->
                        viewModel.addAllowance(amount, desc)
                    }
                )
            }

            previewMedia?.let { msg ->
                MediaPreviewDialog(
                    message = msg,
                    onDismiss = { viewModel.previewMediaMessage.value = null }
                )
            }

            if (showInviteParents) {
                InviteParentsDialog(
                    familyCode = profile.familyCode.ifEmpty { "" },
                    childName = profile.name,
                    onDismiss = { viewModel.showInviteParentsDialog.value = false },
                    onTakeOverControl = { parentName, pin ->
                        viewModel.linkParentToFriendAccount(parentName, pin)
                    }
                )
            }

            if (showAddNewContact) {
                AddNewContactDialog(
                    isParent = (currentRole == AppRole.PARENT),
                    onDismiss = { viewModel.showAddNewContactDialog.value = false },
                    onConfirm = { name, phone, relationship, isApproved ->
                        viewModel.addNewContact(name, phone, relationship, isApproved)
                    }
                )
            }

            if (showEncryptionInfo) {
                EncryptionInfoDialog(
                    onDismiss = { viewModel.showEncryptionInfoDialog.value = false }
                )
            }

            if (showRegistration) {
                UserRegistrationDialog(
                    initialName = profile.name,
                    initialAge = profile.age,
                    initialLogin = profile.loginIdentifier,
                    initialSpouseName = profile.spouseName,
                    initialSpouseContact = profile.spouseContact,
                    onDismiss = { viewModel.showRegistrationDialog.value = false },
                    onSave = { name, age, login, isEmail, pin, spouseName, spouseContact, familyCode, isAutonomous ->
                        viewModel.registerUser(
                            name = name,
                            age = age,
                            loginIdentifier = login,
                            isEmail = isEmail,
                            pin = pin,
                            spouseName = spouseName,
                            spouseContact = spouseContact,
                            familyCode = familyCode,
                            isAutonomousChild = isAutonomous
                        )
                    }
                )
            }

            if (showProfileEditor) {
                ProfileEditorDialog(
                    initialName = profile.name,
                    initialRole = profile.familyRole,
                    initialStatus = profile.profileStatus,
                    initialPhotoUri = profile.profilePhotoUri,
                    isParentOnlyRole = currentRole == AppRole.PARENT,
                    onDismiss = { viewModel.showProfileEditorDialog.value = false },
                    onSave = { name, role, status, photoUri ->
                        viewModel.updateProfileData(
                            name = name,
                            role = role,
                            status = status,
                            photoUri = photoUri
                        )
                    }
                )
            }

            // SafeTalk Funny Filter Toast Banner
            AnimatedVisibility(
                visible = funFilterToast != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 75.dp, start = 16.dp, end = 16.dp)
            ) {
                funFilterToast?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SafeTalkAccent),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.funFilterToastMessage.value = null },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Fechar",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Withdrawal & Financial Feedback Toast Banner
            AnimatedVisibility(
                visible = withdrawalFeedback != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 85.dp, start = 16.dp, end = 16.dp)
            ) {
                withdrawalFeedback?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.dismissWithdrawalFeedback() },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Fechar",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (showUpdateDialog && availableUpdate != null) {
                AppUpdateDialog(
                    updateInfo = availableUpdate!!,
                    isDownloading = isDownloadingUpdate,
                    downloadProgress = updateDownloadProgress,
                    onDismiss = { viewModel.showUpdateDialog.value = false },
                    onConfirmUpdate = { viewModel.downloadAndApplyUpdate(context) }
                )
            }

            Text(
                text = "Versão ${BuildConfig.VERSION_NAME}",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                color = Color(0xFF8A8A8A),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
