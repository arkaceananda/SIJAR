package com.example.sijar.ui.theme.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sijar.api.model.data.Peminjaman
import com.example.sijar.api.utils.UiState
import com.example.sijar.viewModel.DashboardViewModel
import com.example.sijar.ui.theme.GreenSoft
import com.example.sijar.ui.theme.YellowSoft

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val uiState by viewModel.dashboardState.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Success -> {
            val data = state.data
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 18.dp,
                                bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(label = "Dipinjam", count = data.totalDipinjam, modifier = Modifier.weight(1f))
                        StatCard(label = "Selesai", count = data.totalSelesai, modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Text(
                        "Peminjaman Terbaru", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(data.peminjamanTerbaru) { pinjam ->
                    PeminjamanCard(peminjaman = pinjam)
                }
            }
        }

        is UiState.Error -> {
            val errorMessage = state.message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun StatCard(label: String, count: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(count.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PeminjamanCard(peminjaman: Peminjaman) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                peminjaman.item?.namaItem ?: "Barang Tidak Diketahui", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Kode: ${peminjaman.item?.kodeUnit ?: peminjaman.kodeUnit ?: "-"}", 
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Keperluan: ${peminjaman.keperluan ?: "-"}", 
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ISO FORMAT
                val displayDate = peminjaman.createdAt?.substringBefore("T") ?: "-"
                Text(
                    displayDate, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Badge(
                    text = peminjaman.statusTujuan ?: "Pending",
                    color = if (peminjaman.statusTujuan == "Approved") GreenSoft else YellowSoft
                )
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text, 
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
            color = color, 
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
