package com.example.sijar.ui.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sijar.R
import com.example.sijar.api.utils.UiState
import com.example.sijar.api.utils.uriToFile
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
fun EditProfile(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val profileState = viewModel.profileState
    val updateState = viewModel.updateProfileState
    val isLoading = updateState is UiState.Loading

    var isVisible by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var kode by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    // State for photo
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var removePhotoFlag by remember { mutableStateOf(false) }
    var currentPhotoUrl by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val updateErrorMessage = (updateState as? UiState.Error)?.asString()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
            removePhotoFlag = false
        }
    }

    val profileUpdated = stringResource(R.string.profile_successfully_updated)
    val errorOccurred = stringResource(R.string.error_occurred)
    val profileNameCantEmpty = stringResource(R.string.profile_name_cannot_be_empty)
    val codeCantEmpty = stringResource(R.string.profile_name_cannot_be_empty)

    LaunchedEffect(profileState) {
        if (profileState is UiState.Success) {
            val user = profileState.data
            fullName = user.name
            kode = user.kode
            phone = user.telepon ?: ""
            currentPhotoUrl = user.profile
            isVisible = true
        }
    }

    LaunchedEffect(updateState) {
        when (updateState) {
            is UiState.Success -> {
                focusManager.clearFocus()
                keyboardController?.hide()
                snackbarHostState.showSnackbar(
                    message = profileUpdated,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetUpdateProfileState()
                onBack() // Back after success
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = updateErrorMessage ?: errorOccurred,
                    duration = SnackbarDuration.Long
                )
            }
            else -> {}
        }
    }

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (profileState) {
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                }

                is UiState.Success -> {
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
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {

                            // ── Wave header ──
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawBehind {
                                        val waveHeight = 36.dp.toPx()
                                        val w = size.width
                                        val h = size.height
                                        val path = Path().apply {
                                            moveTo(0f, 0f)
                                            lineTo(0f, h - waveHeight)
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
                                    .padding(bottom = 20.dp)
                            ) {
                                // Back button
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(start = 8.dp, top = 8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.ArrowBackIosNew,
                                        contentDescription = null,
                                        tint = White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(top = 16.dp, bottom = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Profile Image Picker UI
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape)
                                            .background(White.copy(alpha = 0.1f))
                                            .border(2.dp, White.copy(alpha = 0.5f), CircleShape)
                                            .clickable { imagePicker.launch("image/*") },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val photoModel = when {
                                            selectedPhotoUri != null -> selectedPhotoUri
                                            removePhotoFlag -> null
                                            else -> currentPhotoUrl
                                        }

                                        if (photoModel != null) {
                                            AsyncImage(
                                                model = photoModel,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Text(
                                                text = fullName.take(2).uppercase(),
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = White
                                            )
                                        }

                                        // Overlay Camera Icon
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Filled.CameraAlt,
                                                contentDescription = null,
                                                tint = White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    if (currentPhotoUrl != null || selectedPhotoUri != null) {
                                        TextButton(
                                            onClick = {
                                                selectedPhotoUri = null
                                                removePhotoFlag = true
                                            }
                                        ) {
                                            Icon(Icons.Outlined.Delete, contentDescription = null, tint = White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text(stringResource(R.string.action_delete_picture), color = White.copy(alpha = 0.8f), fontSize = 13.sp)
                                        }
                                    }

                                    Text(
                                        text = stringResource(R.string.action_edit_profile),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            SectionLabel(stringResource(R.string.profile_label_full_name)
                                .split(" ").first() + " Info")

                            ModernCard {
                                EditFieldRow(
                                    icon = Icons.Outlined.Person,
                                    label = stringResource(R.string.profile_label_full_name),
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                    isEnabled = !isLoading
                                )
                                RowDivider()
                                // Class Code
                                EditFieldRow(
                                    icon = Icons.Outlined.Class,
                                    label = stringResource(R.string.profile_label_code),
                                    value = kode,
                                    onValueChange = { kode = it },
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                    isEnabled = !isLoading
                                )
                                RowDivider()
                                // Telepon
                                EditFieldRow(
                                    icon = Icons.Outlined.Phone,
                                    label = stringResource(R.string.profile_label_telephone),
                                    value = phone,
                                    onValueChange = { phone = it },
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Done,
                                    isEnabled = !isLoading
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Action Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Cancel
                                OutlinedButton(
                                    onClick = onBack,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp, BlueLighter
                                    ),
                                    enabled = !isLoading
                                ) {
                                    Text(
                                        text = stringResource(R.string.action_cancel),
                                        color = TextMuted,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }

                                // Save
                                Button(
                                    onClick = {
                                        when {
                                            fullName.isBlank() -> scope.launch {
                                                snackbarHostState.showSnackbar(profileNameCantEmpty)
                                            }
                                            kode.isBlank() -> scope.launch {
                                                snackbarHostState.showSnackbar(codeCantEmpty)
                                            }
                                            else -> {
                                                val photoFile = selectedPhotoUri?.let { uriToFile(context, it) }
                                                viewModel.updateProfile(
                                                    fullName, kode, phone, removePhotoFlag, photoFile
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(2f)
                                        .height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    enabled = !isLoading,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BluePrimary,
                                        disabledContainerColor = BlueLight
                                    )
                                ) {
                                    if (isLoading) {
                                        LoadingDots()
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Save,
                                                contentDescription = null,
                                                tint = White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = stringResource(R.string.action_save),
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 15.sp,
                                                color = White
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            stringResource(R.string.profile_error_load),
                            fontWeight = FontWeight.SemiBold,
                            color = TextMain
                        )
                        Text(
                            profileState.asString(),
                            color = TextMuted,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BluePrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(stringResource(R.string.action_back), color = White)
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun EditFieldRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isEnabled: Boolean = true
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
                imageVector = icon,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Label + field
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
                singleLine = true,
                enabled = isEnabled,
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction
                ),
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