package com.example.sijar.ui.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sijar.R
import com.example.sijar.api.model.data.Item
import com.example.sijar.api.model.data.WaktuPeminjaman
import com.example.sijar.api.utils.SessionManager
import com.example.sijar.api.utils.UiState
import com.example.sijar.ui.theme.BluePrimary
import com.example.sijar.ui.theme.Sky
import com.example.sijar.ui.theme.TextMain
import com.example.sijar.ui.theme.TextMuted
import com.example.sijar.ui.theme.White
import com.example.sijar.ui.utils.asString
import com.example.sijar.viewModel.PeminjamanViewModel

@Composable
fun PinjamBarang(
//    selectedItem: Item? = null,
//    onSuccess: (() -> Unit)? = null,
//    viewModel: PeminjamanViewModel = viewModel()
) {
//    val createState = viewModel.createPeminjamanState
//    val context = LocalContext.current
//    val sessionManager = remember { SessionManager.getInstance(context) }
//
//    var isVisible by remember { mutableStateOf(false) }
//    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//    var showSuccessDialog by remember { mutableStateOf(false) }
//
//    LaunchedEffect(selectedItem) {
//        if (selectedItem != null) {
//            viewModel.setSelectedItem(selectedItem)
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchWaktuPembelajaran()
//    }
//
//    val imagePicker = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { selectedImageUri = it }
//    }
//
//    LaunchedEffect(Unit) { isVisible = true }
//
//    LaunchedEffect(createState) {
//        if (createState is UiState.Success) {
//            showSuccessDialog = true
//        }
//    }
//
//    if (showSuccessDialog) {
//        AlertDialog(
//            onDismissRequest = { showSuccessDialog = false },
//            title = { Text(stringResource(R.string.status_approved)) },
//            text = { Text("Peminjaman berhasil dibuat dan menunggu approval dari admin") },
//            confirmButton = {
//                TextButton(onClick = {
//                    showSuccessDialog = false
//                    viewModel.resetForm()
//                    onSuccess?.invoke()
//                }) {
//                    Text("OK")
//                }
//            }
//        )
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Sky)
//    ) {
//        when (createState) {
//            is UiState.Loading -> {
//                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator(color = BluePrimary)
//                }
//            }
//
//            else -> {
//                AnimatedVisibility(
//                    visible = isVisible,
//                    enter = fadeIn() + slideInVertically(
//                        initialOffsetY = { it },
//                        animationSpec = spring(
//                            dampingRatio = Spring.DampingRatioLowBouncy,
//                            stiffness = Spring.StiffnessMediumLow
//                        )
//                    )
//                ) {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(bottom = 20.dp),
//                        verticalArrangement = Arrangement.spacedBy(0.dp)
//                    ) {
//                        /* HEADER */
//                        item {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .background(BluePrimary)
//                                    .statusBarsPadding()
//                                    .padding(16.dp),
//                                contentAlignment = Alignment.CenterStart
//                            ) {
//                                Text(
//                                    text = "Formulir Peminjaman",
//                                    fontSize = 18.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = White
//                                )
//                            }
//                        }
//
//                        item { Spacer(modifier = Modifier.height(20.dp)) }
//
//                        /* SELECTED ITEM INFO */
//                        item {
//                            if (viewModel.selectedItem != null) {
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(horizontal = 16.dp)
//                                ) {
//                                    Card(
//                                        modifier = Modifier.fillMaxWidth(),
//                                        colors = CardDefaults.cardColors(containerColor = White),
//                                        shape = RoundedCornerShape(12.dp)
//                                    ) {
//                                        Row(
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .padding(12.dp),
//                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
//                                            verticalAlignment = Alignment.CenterVertically
//                                        ) {
//                                            AsyncImage(
//                                                model = "https://via.placeholder.com/60",
//                                                contentDescription = null,
//                                                modifier = Modifier
//                                                    .size(60.dp)
//                                                    .clip(RoundedCornerShape(8.dp)),
//                                                contentScale = ContentScale.Crop
//                                            )
//                                            Column(modifier = Modifier.weight(1f)) {
//                                                Text(
//                                                    text = viewModel.selectedItem?.namaItem ?: "Unknown",
//                                                    fontWeight = FontWeight.SemiBold,
//                                                    color = TextMain
//                                                )
//                                                Text(
//                                                    text = "Kode: ${viewModel.selectedItem?.kodeUnit ?: "-"}",
//                                                    fontSize = 12.sp,
//                                                    color = TextMuted
//                                                )
//                                            }
//                                            Icon(
//                                                Icons.Outlined.Close,
//                                                contentDescription = null,
//                                                modifier = Modifier
//                                                    .clickable { viewModel.setSelectedItem(null) }
//                                            )
//                                        }
//                                    }
//                                }
//                                Spacer(modifier = Modifier.height(16.dp))
//                            }
//                        }
//
//                        /* FORM */
//                        item {
//                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
//                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
//                                    /* KEPERLUAN */
//                                    Column {
//                                        Text(
//                                            text = "Keperluan Peminjaman",
//                                            fontSize = 12.sp,
//                                            color = TextMuted,
//                                            fontWeight = FontWeight.Medium
//                                        )
//                                        Spacer(modifier = Modifier.height(8.dp))
//                                        TextField(
//                                            value = viewModel.keperluan,
//                                            onValueChange = { viewModel.setKeperluan(it) },
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .height(100.dp),
//                                            placeholder = { Text("Jelaskan keperluan peminjaman...") },
//                                            shape = RoundedCornerShape(10.dp),
//                                            colors = TextFieldDefaults.colors(
//                                                focusedContainerColor = White,
//                                                unfocusedContainerColor = White,
//                                                focusedIndicatorColor = Color.Transparent,
//                                                unfocusedIndicatorColor = Color.Transparent
//                                            ),
//                                            singleLine = false
//                                        )
//                                    }
//
//                                    /* WAKTU PEMBELAJARAN */
//                                    Column {
//                                        Text(
//                                            text = "Waktu Pembelajaran",
//                                            fontSize = 12.sp,
//                                            color = TextMuted,
//                                            fontWeight = FontWeight.Medium
//                                        )
//                                        Spacer(modifier = Modifier.height(8.dp))
//
//                                        when (val waktuState = viewModel.waktuAvailableState) {
//                                            is UiState.Loading -> {
//                                                Box(
//                                                    modifier = Modifier
//                                                        .fillMaxWidth()
//                                                        .height(40.dp)
//                                                        .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
//                                                    contentAlignment = Alignment.Center
//                                                ) {
//                                                    CircularProgressIndicator(
//                                                        modifier = Modifier.size(20.dp),
//                                                        strokeWidth = 2.dp,
//                                                        color = BluePrimary
//                                                    )
//                                                }
//                                            }
//                                            is UiState.Success -> {
//                                                val availableWaktu = waktuState.data
//                                                if (availableWaktu.isEmpty()) {
//                                                    Text(
//                                                        text = "Tidak ada waktu pembelajaran tersedia",
//                                                        fontSize = 13.sp,
//                                                        color = TextMuted,
//                                                        modifier = Modifier
//                                                            .fillMaxWidth()
//                                                            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
//                                                            .padding(12.dp)
//                                                    )
//                                                } else {
//                                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                                                        availableWaktu.forEach { waktu ->
//                                                            val isSelected = viewModel.selectedWaktu.contains(waktu)
//                                                            Row(
//                                                                modifier = Modifier
//                                                                    .fillMaxWidth()
//                                                                    .background(
//                                                                        if (isSelected) BluePrimary.copy(alpha = 0.1f)
//                                                                        else Color.LightGray.copy(alpha = 0.1f),
//                                                                        RoundedCornerShape(8.dp)
//                                                                    )
//                                                                    .border(
//                                                                        1.dp,
//                                                                        if (isSelected) BluePrimary else Color.Transparent,
//                                                                        RoundedCornerShape(8.dp)
//                                                                    )
//                                                                    .clickable {
//                                                                        val currentList = viewModel.selectedWaktu.toMutableList()
//                                                                        if (isSelected) {
//                                                                            currentList.remove(waktu)
//                                                                        } else {
//                                                                            currentList.add(waktu)
//                                                                        }
//                                                                        viewModel.setSelectedWaktu(currentList)
//                                                                    }
//                                                                    .padding(12.dp),
//                                                                horizontalArrangement = Arrangement.SpaceBetween,
//                                                                verticalAlignment = Alignment.CenterVertically
//                                                            ) {
//                                                                Column {
//                                                                    Text(
//                                                                        text = "Jam ${waktu.jamKe}",
//                                                                        fontSize = 13.sp,
//                                                                        fontWeight = FontWeight.SemiBold,
//                                                                        color = TextMain
//                                                                    )
//                                                                    Text(
//                                                                        text = "${waktu.startTime} - ${waktu.endTime}",
//                                                                        fontSize = 11.sp,
//                                                                        color = TextMuted
//                                                                    )
//                                                                }
//                                                                Checkbox(
//                                                                    checked = isSelected,
//                                                                    onCheckedChange = {
//                                                                        val currentList = viewModel.selectedWaktu.toMutableList()
//                                                                        if (it) {
//                                                                            currentList.add(waktu)
//                                                                        } else {
//                                                                            currentList.remove(waktu)
//                                                                        }
//                                                                        viewModel.setSelectedWaktu(currentList)
//                                                                    },
//                                                                    colors = CheckboxDefaults.colors(
//                                                                        checkedColor = BluePrimary
//                                                                    )
//                                                                )
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                            is UiState.Error -> {
//                                                Text(
//                                                    text = "Gagal memuat waktu pembelajaran",
//                                                    fontSize = 13.sp,
//                                                    color = MaterialTheme.colorScheme.error,
//                                                    modifier = Modifier
//                                                        .fillMaxWidth()
//                                                        .background(
//                                                            MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
//                                                            RoundedCornerShape(10.dp)
//                                                        )
//                                                        .padding(12.dp)
//                                                )
//                                            }
//                                            else -> {}
//                                        }
//
//                                        if (viewModel.selectedWaktu.isNotEmpty()) {
//                                            Spacer(modifier = Modifier.height(12.dp))
//                                            Text(
//                                                text = "${viewModel.selectedWaktu.size} waktu dipilih",
//                                                fontSize = 12.sp,
//                                                color = BluePrimary,
//                                                fontWeight = FontWeight.SemiBold
//                                            )
//                                        }
//                                    }
//
//                                    /* BUKTI FOTO */
//                                    Column {
//                                        Text(
//                                            text = "Bukti Peminjaman (Foto)",
//                                            fontSize = 12.sp,
//                                            color = TextMuted,
//                                            fontWeight = FontWeight.Medium
//                                        )
//                                        Spacer(modifier = Modifier.height(8.dp))
//
//                                        if (selectedImageUri != null) {
//                                            Box(
//                                                modifier = Modifier
//                                                    .fillMaxWidth()
//                                                    .height(200.dp)
//                                                    .clip(RoundedCornerShape(10.dp))
//                                                    .background(Color.LightGray.copy(alpha = 0.2f))
//                                            ) {
//                                                AsyncImage(
//                                                    model = selectedImageUri,
//                                                    contentDescription = null,
//                                                    modifier = Modifier.fillMaxSize(),
//                                                    contentScale = ContentScale.Crop
//                                                )
//                                                Icon(
//                                                    Icons.Outlined.Close,
//                                                    contentDescription = null,
//                                                    modifier = Modifier
//                                                        .align(Alignment.TopEnd)
//                                                        .padding(8.dp)
//                                                        .clickable { selectedImageUri = null }
//                                                        .background(Color.White, RoundedCornerShape(50))
//                                                        .padding(4.dp)
//                                                        .size(20.dp)
//                                                )
//                                            }
//                                        } else {
//                                            Button(
//                                                onClick = { imagePicker.launch("image/*") },
//                                                modifier = Modifier
//                                                    .fillMaxWidth()
//                                                    .height(120.dp),
//                                                colors = ButtonDefaults.buttonColors(
//                                                    containerColor = Color.White
//                                                ),
//                                                shape = RoundedCornerShape(10.dp),
//                                                border = BorderStroke(2.dp, BluePrimary)
//                                            ) {
//                                                Column(
//                                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                                    verticalArrangement = Arrangement.Center
//                                                ) {
//                                                    Icon(
//                                                        Icons.Filled.Upload,
//                                                        contentDescription = null,
//                                                        tint = BluePrimary,
//                                                        modifier = Modifier.size(32.dp)
//                                                    )
//                                                    Spacer(modifier = Modifier.height(4.dp))
//                                                    Text(
//                                                        "Pilih Foto",
//                                                        color = BluePrimary,
//                                                        fontWeight = FontWeight.SemiBold
//                                                    )
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    Spacer(modifier = Modifier.height(16.dp))
//
//                                    /* ERROR MESSAGE */
//                                    if (createState is UiState.Error) {
//                                        Text(
//                                            text = "Error: ${(createState as UiState.Error).asString()}",
//                                            color = MaterialTheme.colorScheme.error,
//                                            fontSize = 12.sp,
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .background(
//                                                    MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
//                                                    RoundedCornerShape(8.dp)
//                                                )
//                                                .padding(12.dp)
//                                        )
//                                        Spacer(modifier = Modifier.height(8.dp))
//                                    }
//
//                                    /* SUBMIT BUTTON */
//                                    Button(
//                                        onClick = {
//                                            if (selectedImageUri != null && viewModel.selectedItem != null) {
//                                                val contentResolver = context.contentResolver
//                                                val fileName = "bukti_peminjaman_${System.currentTimeMillis()}.jpg"
//                                                val file = java.io.File(context.cacheDir, fileName)
//
//                                                try {
//                                                    contentResolver.openInputStream(selectedImageUri!!)?.use { inputStream ->
//                                                        java.io.FileOutputStream(file).use { outputStream ->
//                                                            inputStream.copyTo(outputStream)
//                                                        }
//                                                    }
//                                                    viewModel.createPeminjaman(file, sessionManager)
//                                                } catch (e: Exception) {
//                                                    e.printStackTrace()
//                                                }
//                                            }
//                                        },
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .height(48.dp),
//                                        enabled = viewModel.selectedItem != null &&
//                                                  viewModel.keperluan.isNotBlank() &&
//                                                  viewModel.selectedWaktu.isNotEmpty() &&
//                                                  selectedImageUri != null,
//                                        colors = ButtonDefaults.buttonColors(
//                                            containerColor = BluePrimary,
//                                            disabledContainerColor = BluePrimary.copy(alpha = 0.5f)
//                                        ),
//                                        shape = RoundedCornerShape(10.dp)
//                                    ) {
//                                        Text(
//                                            "Kirim Peminjaman",
//                                            color = White,
//                                            fontWeight = FontWeight.SemiBold
//                                        )
//                                    }
//                                }
//                            }
//                        }
//
//                        item { Spacer(modifier = Modifier.height(32.dp)) }
//                    }
//                }
//            }
//        }
//    }
}

