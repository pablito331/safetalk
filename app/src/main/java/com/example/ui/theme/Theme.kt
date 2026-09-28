package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WhatsAppTeal,
    onPrimary = Color.Black,
    primaryContainer = FamilyOnPrimaryContainer,
    onPrimaryContainer = FamilyPrimaryContainer,
    secondary = WhatsAppLightGreen,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF134E4A),
    onSecondaryContainer = Color(0xFFCCFBF1),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569),
    error = FamilyAlertRed,
    errorContainer = Color(0xFF991B1B)
)

private val LightColorScheme = lightColorScheme(
    primary = FamilyPrimary,
    onPrimary = FamilyOnPrimary,
    primaryContainer = FamilyPrimaryContainer,
    onPrimaryContainer = FamilyOnPrimaryContainer,
    secondary = FamilySecondary,
    onSecondary = Color.White,
    secondaryContainer = FamilySecondaryContainer,
    onSecondaryContainer = FamilyOnSecondaryContainer,
    background = NeutralLightBg,
    surface = NeutralSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = NeutralSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = NeutralOutline,
    error = FamilyAlertRed,
    errorContainer = FamilyAlertRedContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep cohesive family/whatsapp green theme
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
