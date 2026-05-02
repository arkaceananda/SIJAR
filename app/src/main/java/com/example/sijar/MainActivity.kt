package com.example.sijar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.sijar.api.utils.SessionManager
import com.example.sijar.ui.theme.SIJARTheme
import com.example.sijar.ui.theme.presentation.BarangScreen
import com.example.sijar.ui.theme.presentation.DashboardScreen
import com.example.sijar.ui.theme.presentation.LoginScreen
import com.example.sijar.ui.theme.presentation.PinjamBarang
import com.example.sijar.ui.theme.presentation.ProfileScreen
import com.example.sijar.ui.theme.presentation.RiwayatScreen

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

@Composable
fun SIJARApp() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var isLoggedIn by rememberSaveable { mutableStateOf(sessionManager.isLoggedIn()) }

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    } else {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach { destination ->
                    item(
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = stringResource(destination.labelRes)
                            )
                        },
                        label = { Text(stringResource(destination.labelRes)) },
                        selected = destination == currentDestination,
                        onClick = { currentDestination = destination }
                    )
                }
            }
        ) {
            when (currentDestination) {
                AppDestinations.HOME -> DashboardScreen()
                AppDestinations.BARANG -> BarangScreen(
                    onItemClick = { itemId ->
                        currentDestination = AppDestinations.PINJAM
                    }
                )
                AppDestinations.PINJAM -> PinjamBarang()
                AppDestinations.RIWAYAT -> RiwayatScreen()
                AppDestinations.PROFILE -> ProfileScreen(onLogout = {
                    sessionManager.clearSession()
                    isLoggedIn = false
                })
            }
        }
    }
}

enum class AppDestinations(
    val labelRes: Int,
    val icon: ImageVector,
) {
    HOME(R.string.menu_home, Icons.Default.Home),
    BARANG(R.string.menu_barang, Icons.Default.Book),
    PINJAM(R.string.menu_pinjam, Icons.Default.Add),
    RIWAYAT(R.string.menu_riwayat, Icons.Default.History),
    PROFILE(R.string.menu_profile, Icons.Default.AccountBox)
}
