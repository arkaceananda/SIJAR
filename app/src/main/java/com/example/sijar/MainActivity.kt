package com.example.sijar

import FloatingNavBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    val labelResId: Int,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector,
) {
    HOME(R.string.menu_home, Icons.Filled.Home, Icons.Outlined.Home),
    BARANG(R.string.menu_barang, Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    PINJAM(R.string.menu_pinjam, Icons.Filled.Add, Icons.Outlined.Add),
    RIWAYAT(R.string.menu_riwayat, Icons.Filled.History, Icons.Outlined.History),
    PROFILE(R.string.menu_profile, Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun SIJARApp() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var isLoggedIn by rememberSaveable { mutableStateOf(sessionManager.isLoggedIn()) }

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