package com.sekolah.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sekolah.mobile.data.repository.AuthRepository
import com.sekolah.mobile.ui.screens.home.HomeScreen
import com.sekolah.mobile.ui.screens.login.LoginScreen
import com.sekolah.mobile.ui.theme.SekolahMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = AuthRepository(applicationContext)
        val startDestination = if (authRepository.hasActiveSession()) "home" else "login"

        setContent {
            SekolahMobileTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(startDestination = startDestination)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}

