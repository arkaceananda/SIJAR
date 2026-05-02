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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sijar.api.utils.UiState
import com.example.sijar.viewModel.BarangViewModel
import com.example.sijar.ui.theme.GreenSoft
import com.example.sijar.ui.theme.YellowSoft

@Composable
fun BarangScreen(viewModel: BarangViewModel = viewModel()) {
//    val uiState by viewModel.barangState.collectAsState()
//
//    when (uiState) {
//        is UiState.Loading -> {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator()
//            }
//        }
//
//        is UiState.Success -> {
//            val barangList = (uiState as UiState.Success).data
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                contentPadding = PaddingValues(bottom = 16.dp)
//            ) {
//                item {
//                    Text(
//                        "Barang yang Bisa Dipinjam",
//                        style = MaterialTheme.typography.headlineSmall,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.onBackground,
//                        modifier = Modifier.padding(top = 18.dp, bottom = 16.dp)
//                    )
//                }
//
//                items(barangList) { barang ->
//                    BarangCard(barang = barang)
//                }
//            }
//        }
//
//        is UiState.Error -> {
//            val message = (uiState as UiState.Error).message
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("Error: $message", color = MaterialTheme.colorScheme.error)
//            }
//        }
//    }
//}
//
//@Composable
//fun BarangCard(barang: BarangItem) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column {
//            AsyncImage(
//                model = "https://your-server.com/storage/barang/${barang.fotoBarang}",
//                contentDescription = barang.namaItem,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(180.dp),
//                contentScale = ContentScale.Crop
//            )
//
//            Column(modifier = Modifier.padding(16.dp)) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.Top
//                ) {
//                    Column(modifier = Modifier.weight(1f)) {
//                        Text(
//                            barang.namaItem,
//                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                        Text(
//                            barang.kategoriJurusan.namaKategori,
//                            style = MaterialTheme.typography.labelSmall,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//
//                    // Status badge
//                    Surface(
//                        color = when (barang.statusItem.lowercase()) {
//                            "tersedia" -> GreenSoft
//                            "dipinjam" -> YellowSoft
//                            else -> MaterialTheme.colorScheme.error
//                        }.copy(alpha = 0.2f),
//                        shape = RoundedCornerShape(4.dp)
//                    ) {
//                        Text(
//                            barang.statusItem.uppercase(),
//                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//                            style = MaterialTheme.typography.labelSmall,
//                            color = when (barang.statusItem.lowercase()) {
//                                "tersedia" -> GreenSoft
//                                "dipinjam" -> YellowSoft
//                                else -> MaterialTheme.colorScheme.error
//                            },
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                Button(
//                    onClick = { /* TODO: Navigate ke form peminjaman */ },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary
//                    ),
//                    enabled = barang.statusItem.lowercase() == "tersedia"
//                ) {
//                    Text("Pinjam Sekarang", fontWeight = FontWeight.Bold)
//                }
//            }
//        }
//    }
}
