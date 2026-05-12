package com.example.sijar.ui.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sijar.R
import com.example.sijar.api.model.data.Item
import com.example.sijar.api.model.data.JurusanFilter
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.UiState
import com.example.sijar.ui.helper.HapticHelper
import com.example.sijar.ui.theme.*
import com.example.sijar.ui.helper.asString
import com.example.sijar.viewModel.BarangViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarangScreen(
    viewModel: BarangViewModel = koinViewModel(),
    onItemClick: (Item) -> Unit
) {
    val uiState = viewModel.barangState
    val barangList = viewModel.barangList
    val totalItems = viewModel.totalItemsFound
    val listState = rememberLazyListState()
    val searchQuery = viewModel.searchQuery
    val selectedJurusan = viewModel.selectedJurusan
    val isRefreshing = viewModel.isRefreshing

    var isVisible by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 4
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && uiState !is UiState.Loading) {
            viewModel.fetchBarang(isRefresh = false)
        }
    }

    LaunchedEffect(Unit) { isVisible = true }

    val itemCountText = when (totalItems) {
        0 -> stringResource(R.string.catalog_no_items_found)
        1 -> "1 ${stringResource(R.string.catalog_item_found)}"
        else -> "$totalItems ${stringResource(R.string.catalog_items_found)}"
    }

    val daftarJurusan = listOf(
        JurusanFilter(stringResource(R.string.category_all), null, BluePrimary),
        JurusanFilter(stringResource(R.string.category_pplg), 1, ColorPPLG),
        JurusanFilter(stringResource(R.string.category_lk), 2, ColorLK),
        JurusanFilter(stringResource(R.string.category_tjkt), 3, ColorTJKT),
        JurusanFilter(stringResource(R.string.category_dkv), 4, ColorDKV),
        JurusanFilter(stringResource(R.string.category_ps), 5, ColorPS)
    )

    val view = LocalView.current

    selectedItem?.let { item ->
        ModalBottomSheet(
            onDismissRequest = { selectedItem = null },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null,
            scrimColor = BlueDarker.copy(alpha = 0.4f)
        ) {
            BarangDetailSheet(
                item = item,
                onPinjam = { onItemClick(item) },
                onDismiss = { selectedItem = null }
            )
        }
    }

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
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                HapticHelper.performClick(view)
                viewModel.refresh()
            }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Sky),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {

                /* Header & search */
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(White)
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp, bottom = 12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.catalog_title_goods),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMain
                        )
                        Text(
                            text = stringResource(R.string.catalog_label_select_item),
                            fontSize = 13.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                        )
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    stringResource(R.string.catalog_hint_search),
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Search,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
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

                /* Filter chips */
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(White)
                            .padding(
                                start = 20.dp, end = 20.dp,
                                top = 8.dp, bottom = 14.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(daftarJurusan) { jurusan ->
                            val isSelected = selectedJurusan == jurusan.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) jurusan.activeColor else Sky
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) jurusan.activeColor
                                        else BlueLighter,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        HapticHelper.performClick(view)
                                        viewModel.onJurusanSelected(jurusan.id)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = jurusan.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold
                                    else FontWeight.Normal,
                                    color = if (isSelected) White else TextMuted
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = BlueLighter, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                /* Item Count Text */
                item {
                    if (uiState is UiState.Success || barangList.isNotEmpty()) {
                        Text(
                            text = itemCountText,
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(
                                horizontal = 20.dp,
                                vertical = 4.dp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (barangList.isEmpty() && uiState is UiState.Loading) {
                    items(3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            repeat(2) {
                                BarangGridSkeleton(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                } else if (barangList.isEmpty() && uiState is UiState.Success) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 56.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(R.string.search_no_results_title),
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (searchQuery.isNotEmpty())
                                    stringResource(R.string.search_no_results_description)
                                else
                                    stringResource(R.string.search_no_items_in_category),
                                color = TextMuted,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(
                                    top = 8.dp,
                                    start = 32.dp,
                                    end = 32.dp
                                )
                            )
                        }
                    }
                } else {
                    val rows = barangList.chunked(2)
                    items(
                        items = rows,
                        key = { it.first().id }
                    ) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { barang ->
                                BarangGridCard(
                                    barang = barang,
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedItem = barang }
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (uiState is UiState.Loading && barangList.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = BluePrimary,
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }
                }

                // Handling Error
                if (uiState is UiState.Error && barangList.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 56.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(R.string.error_to_load_data),
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain,
                                fontSize = 15.sp
                            )
                            Text(
                                uiState.asString(),
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(
                                    top = 4.dp,
                                    bottom = 16.dp
                                )
                            )
                            Button(
                                onClick = { viewModel.refresh() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BluePrimary
                                )
                            ) {
                                Text(
                                    stringResource(R.string.action_try_again),
                                    color = White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BarangGridCard(
    barang: Item,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val status = barang.statusItem?.lowercase() ?: ""
    val tersedia = status == "tersedia"
    val badgeColor = when (status) {
        "tersedia" -> GreenSoft
        "dipinjam" -> YellowSoft
        "rusak" -> Color(0xFFE53935)
        else -> Color.Gray
    }

    Card(
        modifier = modifier
            .height(260.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            /* Image */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = "${ApiClient.BASE_URL}storage/encrypted/${barang.fotoBarang}",
                    contentDescription = barang.namaItem,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp
                        )),
                    contentScale = ContentScale.Crop
                )

                /* Badge status */
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.92f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = status.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            /* Item's Info */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = barang.namaItem
                            ?: stringResource(R.string.item_label_name_not_available),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = barang.kategoriJurusan?.namaKategori
                            ?: stringResource(R.string.category_general),
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                /* Borrow Button */
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = tersedia,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        disabledContainerColor = BlueLighter
                    )
                ) {
                    Text(
                        text = if (tersedia)
                            stringResource(R.string.action_borrow_now)
                        else
                            stringResource(R.string.status_unavailable),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (tersedia) White else TextMuted
                    )
                }
            }
        }
    }
}

/* Item's Bottom Sheet */
@Composable
fun BarangDetailSheet(
    item: Item,
    onPinjam: () -> Unit,
    onDismiss: () -> Unit
) {
    val status = item.statusItem?.lowercase() ?: ""
    val tersedia = status == "tersedia"
    val badgeColor = when (status) {
        "tersedia" -> GreenSoft
        "dipinjam" -> YellowSoft
        "rusak" -> Color(0xFFE53935)
        else -> Color.Gray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        /* Image */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            AsyncImage(
                model = "${ApiClient.BASE_URL}storage/encrypted/${item.fotoBarang}",
                contentDescription = item.namaItem,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(
                        topStart = 24.dp, topEnd = 24.dp
                    )),
                contentScale = ContentScale.Crop
            )

            /* Bottom Gradient Overlay */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.35f)
                            )
                        )
                    )
            )

            /* Badge status */
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor.copy(alpha = 0.92f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = status.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    letterSpacing = 0.5.sp
                )
            }

            /* Drag handle */
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(White.copy(alpha = 0.6f))
            )
        }

        /* White Content Below the Picture */
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = White,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                /* Name + Category */
                Text(
                    text = item.namaItem
                        ?: stringResource(R.string.item_label_name_not_available),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextMain
                )
                Text(
                    text = item.kategoriJurusan?.namaKategori
                        ?: stringResource(R.string.category_general),
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BlueLighter, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))

                /* Detail info rows */
                SheetInfoRow(
                    icon = Icons.Outlined.QrCode,
                    label = stringResource(R.string.item_label_code),
                    value = item.kodeUnit ?: "-"
                )
                Spacer(modifier = Modifier.height(10.dp))
                SheetInfoRow(
                    icon = Icons.Outlined.Category,
                    label = "Jurusan",
                    value = item.kategoriJurusan?.namaKategori
                        ?: stringResource(R.string.category_general)
                )
                Spacer(modifier = Modifier.height(10.dp))
                SheetInfoRow(
                    icon = Icons.Outlined.Info,
                    label = "Status",
                    value = status.replaceFirstChar { it.uppercase() },
                    valueColor = badgeColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                /* Action Button */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    /* Close Button */
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, BlueLighter
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.action_close),
                            color = TextMuted,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    /* Borrow Button */
                    Button(
                        onClick = {
                            onDismiss()
                            onPinjam()
                        },
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = tersedia,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluePrimary,
                            disabledContainerColor = BlueLighter
                        )
                    ) {
                        Text(
                            text = if (tersedia)
                                stringResource(R.string.action_borrow_now)
                            else
                                stringResource(R.string.status_unavailable),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = if (tersedia) White else TextMuted
                        )
                    }
                }
            }
        }
    }
}

/* Information inside the bottom sheet */
@Composable
fun SheetInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = TextMain
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BlueLighter),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(16.dp)
            )
        }
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = valueColor
            )
        }
    }
}

@Composable
fun BarangGridSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(260.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .shimmerEffect()
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(13.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(11.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
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
