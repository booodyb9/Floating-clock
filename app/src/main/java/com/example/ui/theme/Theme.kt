package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = RoyalGold,
    onPrimary = ObsidianBlack,
    primaryContainer = RoyalGoldDark,
    onPrimaryContainer = RoyalGoldLight,
    secondary = GlassBlue,
    onSecondary = ObsidianBlack,
    secondaryContainer = GlassBlueDark,
    onSecondaryContainer = GlassBlueLight,
    tertiary = PlatinumSilver,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = IceWhite,
    surface = DeepCharcoal,
    onSurface = IceWhite,
    surfaceVariant = SurfaceGraphite,
    onSurfaceVariant = PlatinumSilver,
    outline = MutedSlate
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force luxury dark theme for 3D glass clock immersion
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LuxuryDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ObsidianBlack.toArgb()
                window.navigationBarColor = ObsidianBlack.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
