package com.example.sijar.ui.theme.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sijar.api.model.data.Item
import com.example.sijar.api.model.data.JurusanFilter
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.UiState
import com.example.sijar.ui.theme.*
import com.example.sijar.viewModel.BarangViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarangScreen(
    viewModel: BarangViewModel = viewModel(),
    onItemClick: (Int) -> Unit
) {
    val uiState by viewModel.barangState.collectAsState()
    val filteredList by viewModel.filteredBarang.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedJurusan by viewModel.selectedJurusan.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val daftarJurusan = listOf(
        JurusanFilter("Semua", null, BluePrimary),
        JurusanFilter("PPLG", 1, ColorPPLG),
        JurusanFilter("LK", 2, ColorLK),
        JurusanFilter("TJKT", 3, ColorTJKT),
        JurusanFilter("DKV", 4, ColorDKV),
        JurusanFilter("PS", 5, ColorPS)
    )

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Sky),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Katalog Barang",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMain
                    )
                    Text(
                        text = "Pilih barang yang ingin dipinjam",
                        fontSize = 13.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                    )

                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Cari nama barang...", color = TextMuted, fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Search,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BlueLighter,
                            cursorColor = BluePrimary,
                            focusedContainerColor = White,
                            unfocusedContainerColor = Sky
                        )
                    )
                }
            }

            // Filter chips
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .padding(start = 20.dp, end = 20.dp, bottom = 16.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daftarJurusan) { jurusan ->
                        val isSelected = selectedJurusan == jurusan.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) jurusan.activeColor else Sky)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) jurusan.activeColor else BlueLighter,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.onJurusanSelected(jurusan.id) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = jurusan.label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) White else TextMuted
                            )
                        }
                    }
                }
                Divider(color = BlueLighter, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Main Content
            when (uiState) {
                is UiState.Loading -> {
                    item {
                        Text(
                            text = "Memuat barang...",
                            fontSize = 13.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                    items(4) {
                        BarangCardSkeleton()
                    }
                }

                is UiState.Success -> {
                    item {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${filteredList.size} barang ditemukan",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(filteredList) { barang ->
                        BarangCard(
                            barang = barang,
                            onClick = { onItemClick(barang.id) }
                        )
                    }
                }

                is UiState.Error -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Gagal memuat data",
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain,
                                fontSize = 16.sp
                            )
                            Text(
                                text = (uiState as UiState.Error).message,
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )
                            Button(
                                onClick = { viewModel.refresh() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                            ) {
                                Text("Coba Lagi", color = White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BarangCard(barang: Item, onClick: () -> Unit) {
    val status = barang.statusItem?.lowercase() ?: "unknown"
    val tersedia = status == "tersedia"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .then(if (tersedia) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Foto barang
            Box {
                AsyncImage(
                    model = "${ApiClient.BASE_URL}storage/barang/${barang.fotoBarang}",
                    contentDescription = barang.namaItem,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Badge status
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (status) {
                                "tersedia" -> GreenSoft.copy(alpha = 0.92f)
                                "dipinjam" -> YellowSoft.copy(alpha = 0.92f)
                                else -> Color(0xFFE53935).copy(alpha = 0.92f)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = status.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Info barang
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = barang.namaItem ?: "Nama tidak tersedia",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Text(
                            text = barang.kategoriJurusan?.namaKategori ?: "Umum",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = BlueLighter, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    enabled = tersedia,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        disabledContainerColor = BlueLighter
                    )
                ) {
                    Text(
                        text = if (tersedia) "Pinjam Sekarang" else "Tidak Tersedia",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (tersedia) White else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun BarangCardSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .shimmerEffect()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Box(modifier = Modifier.size(width = 160.dp, height = 16.dp).shimmerEffect())
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.size(width = 90.dp, height = 12.dp).shimmerEffect())
                Spacer(modifier = Modifier.height(20.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .shimmerEffect()
                )
            }
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    background(
        Brush.linearGradient(
            colors = listOf(
                BlueLighter.copy(alpha = 0.5f),
                Sky,
                BlueLighter.copy(alpha = 0.5f),
            ),
            start = Offset.Zero,
            end = Offset(x = translateAnim, y = translateAnim)
        )
    )
}