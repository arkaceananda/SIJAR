package com.example.sijar.ui.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sijar.R
import com.example.sijar.api.model.data.Item
import com.example.sijar.api.model.data.WaktuPeminjaman
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.ImagePickerHelper
import com.example.sijar.api.utils.UiState
import com.example.sijar.ui.helper.HapticHelper
import com.example.sijar.ui.helper.LoadingDots
import com.example.sijar.ui.helper.ModernCard
import com.example.sijar.ui.helper.RowDivider
import com.example.sijar.ui.helper.SectionLabel
import com.example.sijar.ui.helper.ShakeEffect
import com.example.sijar.ui.theme.*
import com.example.sijar.ui.helper.asString
import com.example.sijar.viewModel.PeminjamanViewModel
import com.example.sijar.viewModel.WaktuViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinjamBarang(
    selectedItem: Item?,
    onSuccess: () -> Unit,
    peminjamanViewModel: PeminjamanViewModel = viewModel(),
    waktuViewModel: WaktuViewModel = viewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current
    val submitState = peminjamanViewModel.submitState
    val waktuState = waktuViewModel.waktuState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }

    // Resolve di composable context
    val resolvedErrorMessage = (submitState as? UiState.Error)?.asString()
    val isLoading = submitState is UiState.Loading
    val hasValidation = submitState is UiState.Error &&
            (submitState as UiState.Error).type is ErrorType.BadRequest

    LaunchedEffect(Unit) { isVisible = true }

    LaunchedEffect(selectedItem) {
        selectedItem?.id?.let { peminjamanViewModel.onItemSelected(it) }
    }

    LaunchedEffect(submitState) {
        when (submitState) {
            is UiState.Success -> {
                HapticHelper.performClick(view)
                peminjamanViewModel.resetForm()
                onSuccess()
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
                peminjamanViewModel.resetSubmitState()
            }
            else -> {}
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = ImagePickerHelper.uriToFile(context, it)
            if (file != null) {
                peminjamanViewModel.onBuktiFotoSelected(file)
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.error_image_pick_failed)
                    )
                }
            }
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
    ) { innerPadding ->

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
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                PinjamBarangHeader()

                Spacer(modifier = Modifier.height(20.dp))

                SectionLabel(stringResource(R.string.form_label_selected_item))
                selectedItem?.let { item ->
                    SelectedItemCard(item = item)
                } ?: run {
                    ModernCard {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.catalog_no_items_found),
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionLabel(stringResource(R.string.form_section_detail))
                ModernCard {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BlueLighter),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.form_label_purpose),
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                OutlinedTextField(
                                    value = peminjamanViewModel.keperluan,
                                    onValueChange = {
                                        peminjamanViewModel.onKeperluanChange(it)
                                    },
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_hint_purpose),
                                            color = TextMuted.copy(alpha = 0.6f),
                                            fontSize = 14.sp
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                        cursorColor = BluePrimary,
                                        focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
                                    )
                                )
                            }
                        }
                    }

                    RowDivider()

                    // Kode unit
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BlueLighter),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.QrCode,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.form_label_unit_code),
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                OutlinedTextField(
                                    value = peminjamanViewModel.kodeUnit,
                                    onValueChange = {
                                        peminjamanViewModel.onKodeUnitChange(it)
                                    },
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_hint_unit_code),
                                            color = TextMuted.copy(alpha = 0.6f),
                                            fontSize = 14.sp
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                        cursorColor = BluePrimary,
                                        focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionLabel(stringResource(R.string.form_label_time))

                when (waktuState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = BluePrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    is UiState.Success -> {
                        LaunchedEffect(waktuState.data) {
                            peminjamanViewModel.setCachedWaktu(waktuState.data)
                        }

                        WaktuRangeSelector(
                            waktuList = waktuState.data,
                            selectedJamMulai = peminjamanViewModel.selectedJamMulai,
                            selectedJamSelesai = peminjamanViewModel.selectedJamSelesai,
                            onJamMulaiSelected = { peminjamanViewModel.onJamMulaiSelected(it) },
                            onJamSelesaiSelected = { peminjamanViewModel.onJamSelesaiSelected(it) }
                        )
                    }
                    is UiState.Error -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
                            )
                        ) {
                            Text(
                                text = waktuState.asString(),
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Upload bukti ──
                SectionLabel(stringResource(R.string.form_label_proof))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ShakeEffect(trigger = hasValidation) {
                        BuktiFotoSelector(
                            selectedFile = peminjamanViewModel.selectedBuktiFoto,
                            onPickImage = { imagePickerLauncher.launch("image/*") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { peminjamanViewModel.submitPeminjaman(context) },
                    enabled = !isLoading && selectedItem != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        disabledContainerColor = BlueLighter
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
                                Icons.AutoMirrored.Outlined.Send,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.action_submit_borrow),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun PinjamBarangHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val waveHeight = 40.dp.toPx()
                val w = size.width
                val h = size.height
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, h - waveHeight)
                    cubicTo(
                        w * 0.3f, h + waveHeight * 0.5f,
                        w * 0.7f, h - waveHeight * 1.6f,
                        w, h - waveHeight * 0.1f
                    )
                    lineTo(w, 0f)
                    close()
                }
                drawPath(path = path, color = BlueDark)
            }
            .statusBarsPadding()
            .padding(bottom = 52.dp)
    ) {
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
                    imageVector = Icons.AutoMirrored.Outlined.Send,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.nav_borrow),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Text(
                text = stringResource(R.string.form_hint),
                fontSize = 13.sp,
                color = BlueLight.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/* Selected item card */
@Composable
private fun SelectedItemCard(item: Item) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BlueLighter)
            ) {
                if (!item.fotoBarang.isNullOrBlank()) {
                    AsyncImage(
                        model = "${ApiClient.BASE_URL}storage/barang/${item.fotoBarang}",
                        contentDescription = item.namaItem,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.namaItem ?: "-",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextMain
                )
                Text(
                    text = item.kodeUnit ?: "-",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (!item.jenisItem.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier.padding(top = 6.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = BlueLighter
                    ) {
                        Text(
                            text = item.jenisItem,
                            fontSize = 11.sp,
                            color = BluePrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(
                                horizontal = 8.dp, vertical = 3.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

/* Time Selector */
@Composable
fun WaktuRangeSelector(
    waktuList: List<WaktuPeminjaman>,
    selectedJamMulai: WaktuPeminjaman?,
    selectedJamSelesai: WaktuPeminjaman?,
    onJamMulaiSelected: (WaktuPeminjaman) -> Unit,
    onJamSelesaiSelected: (WaktuPeminjaman) -> Unit,
) {
    val waktuSelesaiOptions = remember(selectedJamMulai, waktuList) {
        if (selectedJamMulai == null) waktuList
        else waktuList.filter { it.jamKe >= selectedJamMulai.jamKe }
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnimatedVisibility(
            visible = selectedJamMulai != null && selectedJamSelesai != null,
            enter = fadeIn(tween(200)) + expandVertically(),
            exit = fadeOut(tween(200)) + shrinkVertically()
        ) {
            if (selectedJamMulai != null && selectedJamSelesai != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BluePrimary.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Jam ke-${selectedJamMulai.jamKe} " +
                                    "(${selectedJamMulai.startTime}) " +
                                    "→ Jam ke-${selectedJamSelesai.jamKe} " +
                                    "(${selectedJamSelesai.endTime})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BluePrimary
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WaktuDropdown(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.form_label_jam_mulai),
                placeholder = stringResource(R.string.form_hint_jam_mulai),
                waktuList = waktuList,
                selectedWaktu = selectedJamMulai,
                onSelected = onJamMulaiSelected
            )

            WaktuDropdown(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.form_label_jam_selesai),
                placeholder = stringResource(R.string.form_hint_jam_selesai),
                waktuList = waktuSelesaiOptions,
                selectedWaktu = selectedJamSelesai,
                onSelected = onJamSelesaiSelected,
                enabled = selectedJamMulai != null
            )
        }
    }
}

@Composable
private fun WaktuDropdown(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String,
    waktuList: List<WaktuPeminjaman>,
    selectedWaktu: WaktuPeminjaman?,
    onSelected: (WaktuPeminjaman) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val view = LocalView.current

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) TextMuted else TextMuted.copy(alpha = 0.4f),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Trigger button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = enabled) {
                    HapticHelper.performClick(view)
                    expanded = true
                },
            shape = RoundedCornerShape(12.dp),
            color = if (enabled) White else White.copy(alpha = 0.5f),
            border = BorderStroke(
                width = if (selectedWaktu != null) 1.5.dp else 1.dp,
                color = when {
                    !enabled -> BlueLighter.copy(alpha = 0.4f)
                    selectedWaktu != null -> BluePrimary
                    else -> BlueLighter
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (selectedWaktu != null) {
                        Text(
                            text = "Jam ke-${selectedWaktu.jamKe}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BluePrimary
                        )
                        Text(
                            text = if (label.contains("Mulai", ignoreCase = true))
                                selectedWaktu.startTime
                            else
                                selectedWaktu.endTime,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    } else {
                        Text(
                            text = placeholder,
                            fontSize = 13.sp,
                            color = if (enabled) TextMuted else TextMuted.copy(alpha = 0.4f)
                        )
                    }
                }

                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    animationSpec = tween(200),
                    label = "chevron_rotation"
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (enabled) TextMuted else TextMuted.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = rotation }
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(White)
                .widthIn(min = 160.dp)
        ) {
            waktuList.forEach { waktu ->
                val isSelected = selectedWaktu?.jamKe == waktu.jamKe

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (isSelected) BluePrimary
                                        else androidx.compose.ui.graphics.Color.Transparent
                                    )
                            )
                            Column {
                                Text(
                                    text = "Jam ke-${waktu.jamKe}",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold
                                    else FontWeight.Normal,
                                    color = if (isSelected) BluePrimary else TextMain
                                )
                                Text(
                                    text = "${waktu.startTime} – ${waktu.endTime}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    },
                    onClick = {
                        HapticHelper.performClick(view)
                        onSelected(waktu)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (isSelected) BluePrimary.copy(alpha = 0.06f)
                        else androidx.compose.ui.graphics.Color.Transparent
                    )
                )
            }
        }
    }
}

/* Bukti Item Selector */
@Composable
private fun BuktiFotoSelector(
    selectedFile: java.io.File?,
    onPickImage: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (selectedFile != null) 200.dp else 120.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.5.dp,
                color = if (selectedFile != null) BluePrimary else BlueLighter,
                shape = RoundedCornerShape(14.dp)
            )
            .background(if (selectedFile != null) White else Sky)
            .clickable { onPickImage() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = selectedFile != null,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            selectedFile?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Bukti foto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp))
                )
            }
        }

        AnimatedVisibility(
            visible = selectedFile == null,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BlueLighter),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddPhotoAlternate,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.form_action_pick_photo),
                    fontSize = 13.sp,
                    color = BluePrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "JPG, PNG • Maks. 2MB",
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}