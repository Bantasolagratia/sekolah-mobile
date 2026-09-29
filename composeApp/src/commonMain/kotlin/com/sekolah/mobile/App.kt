package com.sekolah.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sekolah.mobile.data.model.UserProfileResponse
import com.sekolah.mobile.data.repository.AuthRepository
import com.sekolah.mobile.data.repository.GuruRepository
import com.sekolah.mobile.ui.screens.guru.GuruModuleScreen
import com.sekolah.mobile.ui.screens.home.HomeScreen
import com.sekolah.mobile.ui.screens.login.LoginScreen
import com.sekolah.mobile.ui.screens.profile.ProfileScreen
import com.sekolah.mobile.ui.theme.LightBackground
import com.sekolah.mobile.ui.theme.PrimaryTeal
import com.sekolah.mobile.ui.theme.SekolahMobileTheme

enum class ScreenState {
    LOGIN,
    MAIN
}

enum class NavigationTab(val label: String, val icon: ImageVector) {
    HOME("Beranda", Icons.Default.Home),
    GURU("Guru", Icons.Default.Groups),
    PROFILE("Profil", Icons.Default.Person)
}

@Composable
fun App() {
    val authRepository = remember { AuthRepository() }
    val guruRepository = remember { GuruRepository(authRepository = authRepository) }

    var screenState by remember {
        mutableStateOf(if (authRepository.hasActiveSession()) ScreenState.MAIN else ScreenState.LOGIN)
    }
    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    var currentProfile by remember { mutableStateOf(authRepository.getCachedProfile()) }

    SekolahMobileTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = LightBackground
        ) {
            when (screenState) {
                ScreenState.LOGIN -> {
                    LoginScreen(
                        authRepository = authRepository,
                        onLoginSuccess = {
                            currentProfile = authRepository.getCachedProfile()
                            currentTab = NavigationTab.HOME
                            screenState = ScreenState.MAIN
                        }
                    )
                }

                ScreenState.MAIN -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val isTabletOrWide = maxWidth >= 600.dp

                        if (isTabletOrWide) {
                            // Tablet & iPad Responsive Layout: NavigationRail on the left
                            Row(modifier = Modifier.fillMaxSize()) {
                                NavigationRail(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = PrimaryTeal
                                ) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    NavigationTab.entries.forEach { tab ->
                                        NavigationRailItem(
                                            selected = currentTab == tab,
                                            onClick = { currentTab = tab },
                                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                                            label = { Text(tab.label) },
                                            colors = NavigationRailItemDefaults.colors(
                                                selectedIconColor = PrimaryTeal,
                                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                            )
                                        )
                                    }
                                }

                                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                    MainContent(
                                        tab = currentTab,
                                        authRepository = authRepository,
                                        guruRepository = guruRepository,
                                        profile = currentProfile,
                                        onNavigateToGuru = { currentTab = NavigationTab.GURU },
                                        onLogout = {
                                            currentProfile = null
                                            screenState = ScreenState.LOGIN
                                        }
                                    )
                                }
                            }
                        } else {
                            // Phone Layout: Bottom Navigation Bar
                            Scaffold(
                                bottomBar = {
                                    NavigationBar(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        tonalElevation = 4.dp
                                    ) {
                                        NavigationTab.entries.forEach { tab ->
                                            NavigationBarItem(
                                                selected = currentTab == tab,
                                                onClick = { currentTab = tab },
                                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                                label = { Text(tab.label) },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = PrimaryTeal,
                                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                                )
                                            )
                                        }
                                    }
                                }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    MainContent(
                                        tab = currentTab,
                                        authRepository = authRepository,
                                        guruRepository = guruRepository,
                                        profile = currentProfile,
                                        onNavigateToGuru = { currentTab = NavigationTab.GURU },
                                        onLogout = {
                                            currentProfile = null
                                            screenState = ScreenState.LOGIN
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    tab: NavigationTab,
    authRepository: AuthRepository,
    guruRepository: GuruRepository,
    profile: UserProfileResponse?,
    onNavigateToGuru: () -> Unit,
    onLogout: () -> Unit
) {
    when (tab) {
        NavigationTab.HOME -> {
            HomeScreen(
                profile = profile,
                onNavigateToGuru = onNavigateToGuru
            )
        }
        NavigationTab.GURU -> {
            GuruModuleScreen(
                guruRepository = guruRepository,
                onNavigateBack = null
            )
        }
        NavigationTab.PROFILE -> {
            ProfileScreen(
                authRepository = authRepository,
                profile = profile,
                onLogout = onLogout
            )
        }
    }
}

