package com.example.sijar.ui.view

import android.view.View
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sijar.R
import com.example.sijar.api.utils.ApiClient.BASE_URL
import com.example.sijar.api.utils.UiState
import com.example.sijar.ui.helper.HapticHelper
import com.example.sijar.ui.helper.ModernCard
import com.example.sijar.ui.helper.RowDivider
import com.example.sijar.ui.helper.SectionLabel
import com.example.sijar.ui.theme.*
import com.example.sijar.ui.helper.asString
import com.example.sijar.viewModel.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onLogoutSuccess: () -> Unit,
    onChangePassword: () -> Unit,
    onEditProfile: (() -> Unit)? = null,
    onLanguageChanged: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel()
) {
    val profileState = viewModel.profileState
    val language = viewModel.language
    val isNotifEnabled = viewModel.isNotifEnabled
    var isVisible by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val view = LocalView.current

    LaunchedEffect(Unit) { isVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Sky)
    ) {
        when (profileState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            }

            is UiState.Success -> {
                val user = profileState.data
                val profilePhotoUrl = if (!user.profile.isNullOrBlank()) {
                    "${BASE_URL}storage/avatars/${user.profile}"
                } else null

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(
                        initialOffsetY = { it / 6 },
                        animationSpec = tween(500)
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        /* HEADER CARD */
                        item {
                            ProfileHeader(
                                name = user.name,
                                kode = user.kode,
                                photoUrl = profilePhotoUrl
                            )
                        }

                        item { Spacer(modifier = Modifier.height(20.dp)) }

                        /* PERSONAL INFORMATION */
                        item {
                            SectionLabel(stringResource(R.string.profile_title_personal_info))
                            ModernCard {
                                InfoRow(
                                    icon = Icons.Outlined.Person,
                                    label = stringResource(R.string.profile_label_full_name),
                                    value = user.name
                                )
                                RowDivider()
                                InfoRow(
                                    icon = Icons.Outlined.Class,
                                    label = stringResource(R.string.profile_label_code),
                                    value = user.kode
                                )
                                RowDivider()
                                InfoRow(
                                    icon = Icons.Outlined.Phone,
                                    label = stringResource(R.string.profile_label_telephone),
                                    value = user.telepon ?: "-"
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }

                        /* SETTING */
                        item {
                            SectionLabel(stringResource(R.string.settings_title))
                            ModernCard {
                                LanguageRow(
                                    currentLanguage = language,
                                    onLanguageChange = { viewModel.changeLanguage(it, onLanguageChanged) }
                                )
                                RowDivider()
                                NotifRow(
                                    isEnabled = isNotifEnabled,
                                    onToggle = { viewModel.toggleNotification(it) }
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }

                        /* AKUN */
                        item {
                            SectionLabel(stringResource(R.string.profile_label_account))
                            ModernCard {
                                ActionRow(
                                    icon = Icons.Outlined.Edit,
                                    label = stringResource(R.string.action_edit_profile),
                                    iconTint = BluePrimary,
                                    onClick = { onEditProfile?.invoke() }
                                )
                                RowDivider()
                                ActionRow(
                                    icon = Icons.Outlined.Lock,
                                    label = stringResource(R.string.action_change_password),
                                    iconTint = BluePrimary,
                                    onClick = { onChangePassword() }
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }

                        /* LOGOUT */
                        item {
                            ModernCard {
                                ActionRow(
                                    icon = Icons.AutoMirrored.Outlined.Logout,
                                    label = stringResource(R.string.action_logout),
                                    iconTint = MaterialTheme.colorScheme.error,
                                    labelColor = MaterialTheme.colorScheme.error,
                                    showChevron = false,
                                    onClick = {
                                        showLogoutDialog = true
                                    }
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                    if (showLogoutDialog) {
                        AlertDialog(
                            onDismissRequest = { showLogoutDialog = false },
                            title = { Text(
                                stringResource(R.string.dialog_title_logout),
                                fontWeight = FontWeight.Bold) },
                            text = { Text(stringResource(R.string.dialog_msg_logout)) },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showLogoutDialog = false
                                        HapticHelper.performLongPress(view = view)
                                        viewModel.logout(onLogoutSuccess)
                                    }
                                ) {
                                    Text(
                                        stringResource(R.string.action_exit),
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showLogoutDialog = false }
                                ) {
                                    Text(stringResource(R.string.action_cancel), color = TextMain)
                                }
                            },
                            containerColor = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            is UiState.Error -> {
                val errorMessage = profileState.asString()
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.profile_error_load),
                            fontWeight = FontWeight.SemiBold,
                            color = TextMain
                        )
                        Text(
                            text = errorMessage,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )
                        Button(
                            onClick = { viewModel.loadProfile() },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(stringResource(R.string.action_try_again), color = White)
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    kode: String,
    photoUrl: String?
) {
    val hasPhoto = !photoUrl.isNullOrBlank()

    /* Header visual */
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlueDark)
            .statusBarsPadding()
            .padding(top = 32.dp, bottom = 36.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            /* AVATAR */
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (hasPhoto) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = stringResource(R.string.profile_pic_title),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BluePrimary, CircleShape)
                            .border(3.dp, BlueLight.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        /* INITIAL NAME */
                        Text(
                            text = name.take(2).uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Text(
                text = kode,
                fontSize = 13.sp,
                color = BlueLight.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BlueLighter),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextMuted)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextMain)
        }
    }
}

@Composable
fun ActionRow(
    icon: ImageVector,
    label: String,
    iconTint: Color = BluePrimary,
    labelColor: Color = TextMain,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = labelColor,
            modifier = Modifier.weight(1f)
        )
        if (showChevron) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun NotifRow(isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BlueLighter),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Notifications,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.settings_label_notification), fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextMain)
            Text(
                if (isEnabled) stringResource(R.string.status_active) else stringResource(R.string.status_inactive),
                fontSize = 11.sp,
                color = if (isEnabled) GreenSoft else TextMuted
            )
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = BluePrimary,
                uncheckedThumbColor = White,
                uncheckedTrackColor = TextMuted.copy(alpha = 0.3f)
            )
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageRow(currentLanguage: String, onLanguageChange: (String) -> Unit) {
    var showSheet by remember { mutableStateOf(false) }

    val languages = listOf(
        Triple("in", "Indonesia", "🇮🇩"),
        Triple("en", "English", "🇺🇸"),
        Triple("system", stringResource(R.string.settings_label_system_default), "⚙️")
    )

    val currentLabel = languages.find { it.first == currentLanguage }?.second ?: stringResource(R.string.settings_label_system_default)
    val currentFlag  = languages.find { it.first == currentLanguage }?.third ?: "⚙️"

    /* BOTTOM SHEET SELECT LANGUAGE */
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            containerColor = Color.Transparent,
            dragHandle = null,
            shape = RoundedCornerShape(0.dp),
            scrimColor = BlueDarker.copy(alpha = 0.35f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.action_select_language),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 4.dp)
                )

                /* SELECT LANGUAGE */
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = White.copy(alpha = 0.97f),
                    shadowElevation = 0.dp
                ) {
                    Column {
                        languages.forEachIndexed { index, (code, name, flag) ->
                            val isSelected = currentLanguage == code

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(
                                        when (index) {
                                            0 -> RoundedCornerShape(
                                                topStart = 20.dp,
                                                topEnd = 20.dp
                                            )

                                            languages.lastIndex -> RoundedCornerShape(
                                                bottomStart = 20.dp,
                                                bottomEnd = 20.dp
                                            )

                                            else -> RoundedCornerShape(0.dp)
                                        }
                                    )
                                    .background(
                                        if (isSelected) BlueLighter.copy(alpha = 0.6f)
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        onLanguageChange(code)
                                        showSheet = false
                                    }
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                /* EMOJI FLAG */
                                Text(
                                    text = flag,
                                    fontSize = 22.sp,
                                    modifier = Modifier.size(32.dp)
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                /* LABEL NAME */
                                Text(
                                    text = name,
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) BluePrimary else TextMain,
                                    modifier = Modifier.weight(1f)
                                )

                                /* CHECKMARK */
                                AnimatedVisibility(
                                    visible = isSelected,
                                    enter = fadeIn(tween(150)) + scaleIn(tween(150)),
                                    exit = fadeOut(tween(100)) + scaleOut(tween(100))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            /* DIVIDER EACH ITEM */
                            if (index < languages.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 66.dp, end = 20.dp),
                                    thickness = 0.5.dp,
                                    color = BlueLighter
                                )
                            }
                        }
                    }
                }

                /* CANCEL BUTTON */
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = White.copy(alpha = 0.97f),
                    shadowElevation = 0.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showSheet = false }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.action_cancel),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BluePrimary
                        )
                    }
                }
            }
        }
    }

    /* ROW TRIGGER BOTTOM SHEET */
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showSheet = true }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BlueLighter),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Language,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.settings_label_language), fontSize = 11.sp, color = TextMuted)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(currentFlag, fontSize = 14.sp)
                Text(currentLabel, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextMain)
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}