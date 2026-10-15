package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = CleanWhite,
    primaryContainer = ForestGreenSurface,
    onPrimaryContainer = ForestGreenDark,
    
    secondary = WarmGold,
    onSecondary = CharcoalText,
    secondaryContainer = SoftGoldContainer,
    onSecondaryContainer = WarmGoldDark,
    
    tertiary = EmeraldAccent,
    onTertiary = CleanWhite,
    
    background = OffWhiteBackground,
    onBackground = CharcoalText,
    
    surface = CleanWhite,
    onSurface = CharcoalText,
    surfaceVariant = ForestGreenSurface,
    onSurfaceVariant = SlateMuted,
    
    outline = BorderLight,
    outlineVariant = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = WarmGold,
    onPrimary = CharcoalText,
    primaryContainer = ForestGreenDark,
    onPrimaryContainer = WarmGoldLight,
    
    secondary = WarmGoldLight,
    onSecondary = CharcoalText,
    secondaryContainer = ForestGreenLight,
    onSecondaryContainer = CleanWhite,
    
    tertiary = EmeraldAccent,
    onTertiary = CleanWhite,
    
    background = ForestGreenDark,
    onBackground = CleanWhite,
    
    surface = Color(0xFF071F1A),
    onSurface = CleanWhite,
    surfaceVariant = Color(0xFF0E2E27),
    onSurfaceVariant = Color(0xFFB0C4B8),
    
    outline = Color(0xFF1E423A)
)

@Composable
fun RoyalSpecsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We prioritize the elegant Forest Green + Warm Gold theme over Android 12 dynamic colors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ForestGreenPrimary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
