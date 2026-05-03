package com.example.sijar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.sijar.api.utils.SessionManager
import com.example.sijar.ui.theme.*
import com.example.sijar.ui.theme.presentation.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SIJARTheme {
                SIJARApp()
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector,
) {
    HOME("Beranda", Icons.Filled.Home, Icons.Outlined.Home),
    BARANG("Barang", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    PINJAM("Pinjam", Icons.Filled.Add, Icons.Outlined.Add),
    RIWAYAT("Riwayat", Icons.Filled.History, Icons.Outlined.History),
    PROFILE("Profil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun SIJARApp() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    // var isLoggedIn by rememberSaveable { mutableStateOf(sessionManager.isLoggedIn()) }
    var isLoggedIn by rememberSaveable { mutableStateOf(true) }

    // nanti hapus kalo login udah bisa
    LaunchedEffect(Unit) {
        if (sessionManager.getToken() == null) {
            sessionManager.saveToken("dummy_token")
        }
    }
    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp)
            ) {
                when (currentDestination) {
                    AppDestinations.HOME -> DashboardScreen()
                    AppDestinations.BARANG -> BarangScreen(
                        onItemClick = { currentDestination = AppDestinations.PINJAM }
                    )
                    AppDestinations.PINJAM -> PinjamBarang()
                    AppDestinations.RIWAYAT -> RiwayatScreen()
                    AppDestinations.PROFILE -> ProfileScreen(onLogout = {
                        sessionManager.clearSession()
                        isLoggedIn = false
                    })
                }
            }

            FloatingNavBar(
                destinations = AppDestinations.entries.toList(),
                currentDestination = currentDestination,
                onDestinationSelected = { currentDestination = it },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}