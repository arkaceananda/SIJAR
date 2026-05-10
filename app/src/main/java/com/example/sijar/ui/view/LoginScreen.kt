package com.example.sijar.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sijar.R
import com.example.sijar.api.utils.SessionManager
import com.example.sijar.api.utils.UiState
import com.example.sijar.ui.helper.LoadingDots
import com.example.sijar.ui.theme.*
import com.example.sijar.ui.helper.asString
import com.example.sijar.viewModel.LoginViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, viewModel: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }
    var kodeKelas by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val loginState = viewModel.loginState
    val isLoading = loginState is UiState.Loading
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val resolvedErrorMessage = (loginState as? UiState.Error)?.asString()

    LaunchedEffect(loginState) {
        when (loginState) {
            is UiState.Success -> {
                val response = loginState.data
                val session = SessionManager.getInstance(context)
                session.saveToken(response.token)
                onLoginSuccess()
                viewModel.resetState()
            }
            is UiState.Error -> {
                resolvedErrorMessage?.let { msg ->
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = msg,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) { isVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Sky),
        contentAlignment = Alignment.Center
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) { data ->
            Snackbar(
                containerColor = if (loginState is UiState.Error)
                    MaterialTheme.colorScheme.errorContainer
                else
                    MaterialTheme.colorScheme.inverseSurface,
                contentColor = if (loginState is UiState.Error)
                    MaterialTheme.colorScheme.onErrorContainer
                else
                    MaterialTheme.colorScheme.inverseOnSurface,
                snackbarData = data
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(BluePrimary)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(600)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    AnimatedLogo()

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = context.getString(R.string.app_name),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMain
                    )
                    Text(
                        text = context.getString(R.string.sijar_desc),
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 40.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = context.getString(R.string.auth_title_login),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                            Text(
                                text = stringResource(R.string.auth_msg_use_class_code),
                                fontSize = 13.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
                            )

                            Text(
                                text = stringResource(R.string.auth_label_class_code),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMain,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = kodeKelas,
                                onValueChange = { kodeKelas = it },
                                placeholder = { Text(stringResource(R.string.auth_hint_example_class_code), color = TextMuted) },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BluePrimary,
                                    unfocusedBorderColor = BlueLighter,
                                    cursorColor = BluePrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(R.string.auth_label_password),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMain,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = { Text(stringResource(R.string.auth_hint_password), color = TextMuted) },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BluePrimary,
                                    unfocusedBorderColor = BlueLighter,
                                    cursorColor = BluePrimary
                                ),
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Button(
                                onClick = {
                                    if (kodeKelas.isNotEmpty() && password.isNotEmpty()) {
                                        viewModel.login(kodeKelas, password)
                                    } else {
                                        scope.launch {
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                            snackbarHostState.showSnackbar(context.getString(R.string.auth_msg_enter_credentials))
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BluePrimary,
                                    disabledContainerColor = BlueLight
                                ),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    LoadingDots()
                                } else {
                                    Text(
                                        text = context.getString(R.string.auth_title_login),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Footer
            Text(
                text = "© ${LocalDate.now().year} ${context.getString(R.string.app_name)}",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
fun AnimatedLogo() {
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val scale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val floatAnim = rememberInfiniteTransition(label = "float")
    val offsetY by floatAnim.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    Box(
        modifier = Modifier
            .offset(y = offsetY.dp)
            .scale(scale)
            .size(72.dp)
            .background(
                color = BluePrimary,
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SJ",
            color = White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp
        )
    }
}

@Composable
fun AnimatedDot(delayMs: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot_$delayMs")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                delayMillis = delayMs,
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_scale_$delayMs"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                delayMillis = delayMs,
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha_$delayMs"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .size(8.dp)
            .background(
                color = White.copy(alpha = alpha),
                shape = RoundedCornerShape(50)
            )
    )
}
