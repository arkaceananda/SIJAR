package com.example.sijar.ui.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sijar.R
import com.example.sijar.api.model.data.Peminjaman
import com.example.sijar.api.utils.UiState
import com.example.sijar.ui.helper.HapticHelper
import com.example.sijar.ui.helper.ModernCard
import com.example.sijar.ui.helper.RowDivider
import com.example.sijar.ui.helper.asString
import com.example.sijar.ui.theme.*
import com.example.sijar.viewModel.PeminjamanViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(
    peminjamanViewModel: PeminjamanViewModel = viewModel()
) {
    val listState = peminjamanViewModel.listState
    val isRefreshing = peminjamanViewModel.isRefreshing
    var isVisible by remember { mutableStateOf(false) }
    val view = LocalView.current

    LaunchedEffect(Unit) { isVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Sky)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                HapticHelper.performClick(view)
                peminjamanViewModel.refresh()
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
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {

                    /* Wave Header */
                    item {
                        RiwayatHeader()
                    }

                    /* Main Content */
                    when (listState) {
                        is UiState.Loading -> {
                            items(5) { RiwayatCardSkeleton() }
                        }

                        is UiState.Success -> {
                            val riwayat = peminjamanViewModel.peminjamanSelesai

                            if (riwayat.isEmpty()) {
                                item { RiwayatEmptyState() }
                            } else {
                                /* Mapping */
                                val grouped = riwayat
                                    .groupBy { peminjaman ->
                                        peminjaman.createdAt
                                            ?.substringBefore("T")
                                            ?.let {
                                                YearMonth.parse(
                                                    it.substring(0, 7),
                                                    DateTimeFormatter.ofPattern("yyyy-MM")
                                                )
                                            }
                                    }
                                    .toSortedMap(compareByDescending { it })

                                grouped.forEach { (yearMonth, peminjamanList) ->

                                    /* Month Divider */
                                    item(key = "header_$yearMonth") {
                                        MonthDivider(
                                            label = yearMonth?.format(
                                                DateTimeFormatter.ofPattern(
                                                    "MMMM yyyy",
                                                    Locale.forLanguageTag("id-ID")
                                                )
                                            ) ?: stringResource(R.string.riwayat_unknown_date)
                                        )
                                    }

                                    /* Cards*/
                                    items(
                                        items = peminjamanList,
                                        key = { it.id }
                                    ) { peminjaman ->
                                        RiwayatCard(peminjaman = peminjaman)
                                    }
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

@Composable
private fun RiwayatHeader() {
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
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.nav_history),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Text(
                text = stringResource(R.string.riwayat_header_subtitle),
                fontSize = 13.sp,
                color = BlueLight.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun MonthDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BlueLighter,
            thickness = 1.dp
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 0.5.sp
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BlueLighter,
            thickness = 1.dp
        )
    }
}

@Composable
private fun RiwayatCard(peminjaman: Peminjaman) {
    val (badgeColor, badgeText) = when (peminjaman.statusTujuan?.lowercase()) {
        "approved" -> GreenSoft to stringResource(R.string.status_finished)
        "rejected" -> MaterialTheme.colorScheme.error to stringResource(R.string.status_rejected)
        else -> YellowSoft to stringResource(R.string.status_pending)
    }

    Spacer(modifier = Modifier.height(6.dp))
    ModernCard {
        Column(modifier = Modifier.padding(16.dp)) {

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

            RowDivider()

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
private fun RiwayatCardSkeleton() {
    Spacer(modifier = Modifier.height(6.dp))
    ModernCard {
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

            RowDivider()

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
private fun RiwayatEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BlueLighter),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.riwayat_empty_title),
            fontWeight = FontWeight.SemiBold,
            color = TextMain,
            fontSize = 15.sp
        )
        Text(
            text = stringResource(R.string.riwayat_empty_desc),
            color = TextMuted,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}