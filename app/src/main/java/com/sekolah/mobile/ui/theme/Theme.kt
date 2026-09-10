package com.sekolah.mobile.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = SlateSurface,
    primaryContainer = PrimaryTealContainer,
    onPrimaryContainer = OnPrimaryTealContainer,
    secondary = SecondaryNavy,
    onSecondary = SlateSurface,
    background = SlateBackground,
    surface = SlateSurface,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurface = SlateTextPrimary,
    onSurfaceVariant = SlateTextSecondary,
    error = ErrorRed,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

@Composable
fun SekolahMobileTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = PrimaryTeal.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

