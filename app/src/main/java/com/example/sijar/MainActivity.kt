package com.example.sijar

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
import com.example.sijar.ui.view.BarangScreen
import com.example.sijar.ui.view.ChangePassword
import com.example.sijar.ui.view.DashboardScreen
import com.example.sijar.ui.view.EditProfile
import com.example.sijar.ui.view.LoginScreen
import com.example.sijar.ui.view.PinjamBarang
import com.example.sijar.ui.view.ProfileScreen
import com.example.sijar.ui.view.RiwayatScreen
import com.example.sijar.ui.theme.*

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
    HOME(R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    BARANG(R.string.nav_item, Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    PINJAM(R.string.nav_borrow, Icons.Filled.Add, Icons.Outlined.Add),
    RIWAYAT(R.string.nav_history, Icons.Filled.History, Icons.Outlined.History),
    PROFILE(R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun SIJARApp() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var isLoggedIn by rememberSaveable { mutableStateOf(sessionManager.isLoggedIn()) }
    var showChangePassword by rememberSaveable { mutableStateOf(false) }
    var showEditProfile by rememberSaveable { mutableStateOf(false) }
    var selectedItemForPeminjaman by remember { mutableStateOf<com.example.sijar.api.model.data.Item?>(null) }

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    } else {
        if (showChangePassword) {
            ChangePassword(onBack = { showChangePassword = false })
        } else if (showEditProfile) {
            EditProfile(onBack = { showEditProfile = false })
        }
        else {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 90.dp)
                ) {
                    when (currentDestination) {
                        AppDestinations.HOME -> DashboardScreen()
                        AppDestinations.BARANG -> BarangScreen(
                            onItemClick = { item ->
                                selectedItemForPeminjaman = item
                                currentDestination = AppDestinations.PINJAM
                            }
                        )
                        AppDestinations.PINJAM -> PinjamBarang(
//                            selectedItem = selectedItemForPeminjaman,
//                            onSuccess = { currentDestination = AppDestinations.HOME }
                        )
                        AppDestinations.RIWAYAT -> RiwayatScreen()
                        AppDestinations.PROFILE -> ProfileScreen(
                            onLogoutSuccess = {
                                sessionManager.clearSession()
                                isLoggedIn = false
                            },
                            onChangePassword = { showChangePassword = true },
                            onEditProfile = { showEditProfile = true }
                        )
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
}