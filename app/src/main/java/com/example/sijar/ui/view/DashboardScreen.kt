package com.example.sijar.ui.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sijar.R
import com.example.sijar.api.model.data.DashboardData
import com.example.sijar.api.model.data.Peminjaman
import com.example.sijar.api.utils.UiState
import com.example.sijar.api.utils.greetingDay
import com.example.sijar.api.utils.greetingTime
import com.example.sijar.ui.helper.AnimatedCounter
import com.example.sijar.ui.helper.HapticHelper
import com.example.sijar.ui.helper.PulsingBadge
import com.example.sijar.ui.helper.SectionLabel
import com.example.sijar.ui.theme.*
import com.example.sijar.ui.helper.asString
import com.example.sijar.viewModel.DashboardViewModel
import com.example.sijar.viewModel.PeminjamanViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = koinViewModel(),
    peminjamanViewModel: PeminjamanViewModel = koinViewModel()
) {
    val uiState = dashboardViewModel.dashboardState
    val view = LocalView.current
    val isRefreshing = dashboardViewModel.isRefreshing
    val listState = rememberLazyListState()
    val peminjamanListState = peminjamanViewModel.listState
    var isVisible by remember { mutableStateOf(false) }
    val previousListState = remember { mutableStateOf<UiState<*>>(UiState.Idle) }

    LaunchedEffect(Unit) { isVisible = true }

    LaunchedEffect(peminjamanListState) {
        if (previousListState.value is UiState.Loading &&
            peminjamanListState is UiState.Success
        ) { listState.animateScrollToItem(0) }
        previousListState.value = peminjamanListState
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Sky)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                HapticHelper.performClick(view)
                dashboardViewModel.refresh()
                        },
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
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {

                    item {
                        DashboardHeader(
                            userName = dashboardViewModel.userName,
                            uiState = uiState
                        )
                    }

                    when (uiState) {
                        is UiState.Loading -> {
                            item { Spacer(modifier = Modifier.height(24.dp)) }
                            items(3) { DashboardCardSkeleton() }
                        }

                        is UiState.Success -> {
                            val data: DashboardData = uiState.data

                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                SectionLabel(stringResource(R.string.catalog_title_latest_loan))
                            }

                            when(peminjamanListState) {
                                is UiState.Loading -> {
                                    items(3) { DashboardCardSkeleton() }
                                }
                                is UiState.Success -> {
                                    val active = peminjamanViewModel.peminjamanActive
                                    if (active.isEmpty()) item { EmptyPeminjamanPlaceholder() }
                                    else {
                                        items(items = active, key = { it.id } ) { pinjam ->
                                            PeminjamanCard(peminjaman = pinjam)
                                        }
                                    }
                                }
                                is UiState.Error -> {
                                    item {
                                        Text(
                                            text =  peminjamanListState.asString(),
                                            color = TextMuted,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                                else -> {}
                            }

                            if (data.peminjamanTerbaru.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 48.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.search_no_results_title),
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextMain,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = stringResource(R.string.catalog_no_items_found),
                                            color = TextMuted,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            } else {
                                items(
                                    items = data.peminjamanTerbaru,
                                    key = { it.id }
                                ) { pinjam ->
                                    PeminjamanCard(peminjaman = pinjam)
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
                                        text = uiState.asString(),
                                        color = TextMuted,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                    )
                                    Button(
                                        onClick = { dashboardViewModel.refresh() },
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

@Composable
fun DashboardHeader(
    userName: String,
    uiState: UiState<DashboardData>
) {
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
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 24.dp,
                bottom = 52.dp
            )
    ) {
        Column {
            Text(
                text = greetingTime(),
                fontSize = 13.sp,
                color = BlueLight.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium
            )

            Text(
                text = stringResource(greetingDay(), userName),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
            )

            /* Stat cards */
            when (uiState) {
                is UiState.Success -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardStatCard(
                            label = stringResource(R.string.status_borrowed),
                            count = uiState.data.totalDipinjam,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardStatCard(
                            label = stringResource(R.string.status_finished),
                            count = uiState.data.totalSelesai,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is UiState.Loading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(2) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .shimmerEffect()
                            )
                        }
                    }
                }
                else -> {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    label: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = White.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedCounter(
                count = count,
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = White
                )
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = White.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun PeminjamanCard(peminjaman: Peminjaman) {
    val isApproved = peminjaman.statusTujuan ==
            stringResource(R.string.status_approved)
    val statusText = peminjaman.statusTujuan
        ?: stringResource(R.string.status_pending)
    val badgeColor = if (isApproved) GreenSoft else YellowSoft

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
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

                if (peminjaman.statusTujuan?.lowercase() == "pending") {
                    PulsingBadge(text = statusText, color = badgeColor)
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = statusText,
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
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = BlueLighter,
                thickness = 0.5.dp
            )
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
                    text = peminjaman.createdAt?.substringBefore("T") ?: "-",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun DashboardCardSkeleton() {
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

@Composable
fun EmptyPeminjamanPlaceholder(
    message: String = stringResource(R.string.catalog_no_items_found)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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