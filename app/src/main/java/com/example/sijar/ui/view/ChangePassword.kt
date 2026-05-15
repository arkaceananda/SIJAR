package com.example.sijar.ui.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import com.example.sijar.R
import com.example.sijar.api.utils.UiState
import com.example.sijar.ui.helper.LoadingDots
import com.example.sijar.ui.helper.ModernCard
import com.example.sijar.ui.helper.RowDivider
import com.example.sijar.ui.helper.SectionLabel
import com.example.sijar.ui.theme.*
import com.example.sijar.ui.helper.asString
import com.example.sijar.viewModel.ProfileViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePassword(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var isVisible by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isCurrentVisible by remember { mutableStateOf(false) }
    var isNewVisible by remember { mutableStateOf(false) }
    var isConfirmVisible by remember { mutableStateOf(false) }

    val changePasswordState = viewModel.changePasswordState
    val isLoading = changePasswordState is UiState.Loading
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val resolvedErrorMessage = (changePasswordState as? UiState.Error)?.asString()

    val passwordMismatch = confirmPassword.isNotEmpty() && newPassword != confirmPassword
    val passwordMatch = confirmPassword.isNotEmpty() && newPassword == confirmPassword
    val confirmStatusColor = when {
        passwordMismatch -> MaterialTheme.colorScheme.error
        passwordMatch -> GreenSoft
        else -> BlueLighter
    }
    val confirmStatusIcon = when {
        passwordMismatch -> Icons.Outlined.Cancel
        passwordMatch -> Icons.Outlined.CheckCircle
        else -> null
    }

    val errorOccurred = stringResource(R.string.error_occured)
    val passwordErrorEmpty = stringResource(R.string.change_password_error_empty)
    val passwordErrorMismatch = stringResource(R.string.change_password_error_mismatch)
    val passwordHint = stringResource(R.string.change_password_hint_character)

    LaunchedEffect(changePasswordState) {
        when (changePasswordState) {
            is UiState.Success -> {
                focusManager.clearFocus()
                keyboardController?.hide()
                snackbarHostState.showSnackbar(
                    message = changePasswordState.data.message,
                    duration = SnackbarDuration.Short
                )
                currentPassword = ""; newPassword = ""; confirmPassword = ""
                viewModel.resetChangePasswordState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = resolvedErrorMessage ?: errorOccurred,
                    duration = SnackbarDuration.Long
                )
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = BlueDarker,
                    contentColor = White,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        },
        containerColor = Sky
    ) { paddingValues ->

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    WaveHeader(onBack = onBack)

                    Spacer(modifier = Modifier.height(24.dp))

                    /* Info box */
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = BlueLighter
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Info,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = stringResource(R.string.change_password_hint_character),
                                fontSize = 12.sp,
                                color = BlueDark,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    /* Form */
                    SectionLabel(stringResource(R.string.change_password_title))

                    ModernCard {
                        PasswordFieldRow(
                            label = stringResource(R.string.change_password_label_now),
                            placeholder = stringResource(R.string.change_password_description),
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            isVisible = isCurrentVisible,
                            onToggleVisibility = { isCurrentVisible = !isCurrentVisible },
                            imeAction = ImeAction.Next,
                            isEnabled = !isLoading
                        )
                        RowDivider()
                        PasswordFieldRow(
                            label = stringResource(R.string.change_password_hint_new),
                            placeholder = stringResource(R.string.change_password_hint_character),
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            isVisible = isNewVisible,
                            onToggleVisibility = { isNewVisible = !isNewVisible },
                            imeAction = ImeAction.Next,
                            isEnabled = !isLoading
                        )
                        RowDivider()
                        PasswordFieldRow(
                            label = stringResource(R.string.change_password_label_confirm),
                            placeholder = stringResource(R.string.change_password_hint_confirm),
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            isVisible = isConfirmVisible,
                            onToggleVisibility = { isConfirmVisible = !isConfirmVisible },
                            imeAction = ImeAction.Done,
                            isEnabled = !isLoading,
                            statusColor = confirmStatusColor,
                            trailingStatus = confirmStatusIcon
                        )
                    }

                    /* Error mismatch label */
                    AnimatedVisibility(
                        visible = passwordMismatch,
                        enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                        exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
                    ) {
                        Text(
                            text = stringResource(R.string.change_password_error_mismatch),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 20.dp, top = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    /* Save Button */
                    Button(
                        onClick = {
                            when {
                                currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() ->
                                    scope.launch { snackbarHostState.showSnackbar(passwordErrorEmpty) }
                                newPassword != confirmPassword ->
                                    scope.launch { snackbarHostState.showSnackbar(passwordErrorMismatch) }
                                newPassword.length < 8 ->
                                    scope.launch { snackbarHostState.showSnackbar(passwordHint) }
                                else ->
                                    viewModel.changePassword(currentPassword, newPassword, confirmPassword)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluePrimary,
                            disabledContainerColor = BlueLight
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            LoadingDots()
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LockReset,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = stringResource(R.string.action_change_password),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    /* Cancel Button */
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.action_cancel),
                            color = TextMuted,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun WaveHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            /* Wave path */
            .drawBehind {
                val waveHeight = 36.dp.toPx()
                val w = size.width
                val h = size.height

                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, h - waveHeight)
                    /* Soft bezier curve */
                    cubicTo(
                        w * 0.25f, h + waveHeight * 0.6f,
                        w * 0.75f, h - waveHeight * 1.8f,
                        w, h - waveHeight * 0.2f
                    )
                    lineTo(w, 0f)
                    close()
                }

                drawPath(path = path, color = BlueDark)
            }
            .statusBarsPadding()
            .padding(bottom = 44.dp)
    ) {
        /* Back Button */
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = stringResource(R.string.action_back),
                tint = White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Hero Content
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.change_password_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Text(
                text = stringResource(R.string.change_password_make_sure),
                fontSize = 13.sp,
                color = BlueLight.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/* Password Field Row */
@Composable
fun PasswordFieldRow(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    imeAction: ImeAction = ImeAction.Next,
    isEnabled: Boolean = true,
    statusColor: Color = BlueLighter,
    trailingStatus: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
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
                Icons.Outlined.Lock,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 8.dp)
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(placeholder, color = TextMuted.copy(alpha = 0.5f), fontSize = 14.sp)
                },
                singleLine = true,
                enabled = isEnabled,
                visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = imeAction
                ),
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        trailingStatus?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        IconButton(
                            onClick = onToggleVisibility,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isVisible) Icons.Outlined.Visibility
                                else Icons.Outlined.VisibilityOff,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    cursorColor = BluePrimary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}