package com.example.sijar.ui.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sijar.R
import com.example.sijar.api.model.data.Peminjaman
import com.example.sijar.api.utils.UiState
import com.example.sijar.ui.theme.*
import com.example.sijar.ui.utils.asString
import com.example.sijar.viewModel.PeminjamanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(
    peminjamanViewModel: PeminjamanViewModel = viewModel()
) {
    val listState = peminjamanViewModel.listState
    val isRefreshing = peminjamanViewModel.isRefreshing
    var isVisible by remember { mutableStateOf(false) }

    // Tab: 0 = Dipinjam, 1 = Selesai
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { isVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Sky)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { peminjamanViewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {

                    // ── Header ───────────────────────────────────────
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BlueDark)
                                .statusBarsPadding()
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.nav_history),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    }

                    // ── Tab row ──────────────────────────────────────
                    item {
                        RiwayatTabRow(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }

                    // ── Content ──────────────────────────────────────
                    when (listState) {
                        is UiState.Loading -> {
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            items(4) { RiwayatCardSkeleton() }
                        }

                        is UiState.Success -> {
                            // Filter berdasarkan tab yang aktif
                            val displayList = if (selectedTab == 0) {
                                peminjamanViewModel.peminjamanActive
                            } else {
                                peminjamanViewModel.peminjamanSelesai
                            }

                            item { Spacer(modifier = Modifier.height(16.dp)) }

                            if (displayList.isEmpty()) {
                                item {
                                    RiwayatEmptyState(
                                        // Pesan berbeda tergantung tab aktif
                                        message = if (selectedTab == 0)
                                            stringResource(R.string.riwayat_empty_active)
                                        else
                                            stringResource(R.string.riwayat_empty_finished)
                                    )
                                }
                            } else {
                                items(
                                    items = displayList,
                                    key = { it.id }
                                ) { peminjaman ->
                                    RiwayatCard(peminjaman = peminjaman)
                                }
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
                                        text = stringResource(R.string.error_to_load_data),
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextMain,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = listState.asString(),
                                        color = TextMuted,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(
                                            top = 4.dp,
                                            bottom = 16.dp
                                        )
                                    )
                                    Button(
                                        onClick = { peminjamanViewModel.refresh() },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = BluePrimary
                                        )
                                    ) {
                                        Text(
                                            text = stringResource(R.string.action_try_again),
                                            color = White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

// ── Tab row ──────────────────────────────────────────────────────────────

@Composable
private fun RiwayatTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(White),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        RiwayatTab(
            label = stringResource(R.string.status_borrowed),
            icon = Icons.Outlined.Schedule,
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f)
        )
        RiwayatTab(
            label = stringResource(R.string.status_finished),
            icon = Icons.Outlined.CheckCircle,
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RiwayatTab(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) BluePrimary else White,
        animationSpec = tween(200),
        label = "tab_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) White else TextMuted,
        animationSpec = tween(200),
        label = "tab_content"
    )

    Surface(
        onClick = onClick,
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}

// ── Card ──────────────────────────────────────────────────────────────────

@Composable
private fun RiwayatCard(peminjaman: Peminjaman) {
    // Tentukan warna badge berdasarkan status_tujuan (approved/pending/rejected)
    val (badgeColor, badgeText) = when (peminjaman.statusTujuan?.lowercase()) {
        "approved" -> GreenSoft to stringResource(R.string.status_approved)
        "rejected" -> MaterialTheme.colorScheme.error to stringResource(R.string.status_rejected)
        else -> YellowSoft to stringResource(R.string.status_pending)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Baris atas: nama barang + badge status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = peminjaman.item?.namaItem
                            ?: stringResource(R.string.item_label_unknown_item),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Text(
                        text = stringResource(
                            R.string.item_label_code,
                            peminjaman.item?.kodeUnit ?: peminjaman.kodeUnit ?: "-"
                        ),
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = BlueLighter,
                thickness = 0.5.dp
            )

            // Baris bawah: keperluan + tanggal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.item_label_purpose,
                        peminjaman.keperluan ?: "-"
                    ),
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    // Tampilkan tanggal selesai jika ada, fallback ke tanggal dibuat
                    text = (peminjaman.updatedAt ?: peminjaman.createdAt)
                        ?.substringBefore("T") ?: "-",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
    }
}

// ── Skeleton ──────────────────────────────────────────────────────────────

@Composable
private fun RiwayatCardSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(width = 160.dp, height = 15.dp)
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 90.dp, height = 12.dp)
                            .shimmerEffect()
                    )
                }
                Box(
                    modifier = Modifier
                        .size(width = 70.dp, height = 24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerEffect()
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = BlueLighter,
                thickness = 0.5.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 130.dp, height = 12.dp)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .size(width = 70.dp, height = 12.dp)
                        .shimmerEffect()
                )
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────

@Composable
private fun RiwayatEmptyState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = BlueLighter,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.search_no_results_title),
            fontWeight = FontWeight.SemiBold,
            color = TextMain,
            fontSize = 15.sp
        )
        Text(
            text = message,
            color = TextMuted,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}