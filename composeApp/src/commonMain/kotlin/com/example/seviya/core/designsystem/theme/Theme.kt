package com.example.seviya.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ServiYaLightColors =
    lightColorScheme(
        primary = BrandBlue,
        onPrimary = White,
        secondary = BrandRed,
        onSecondary = White,
        tertiary = BrandYellow,
        onTertiary = TextPrimary,
        background = AppBackground,
        onBackground = TextPrimary,
        surface = CardSurface,
        onSurface = TextPrimary,
        surfaceVariant = SoftSurface,
        onSurfaceVariant = TextSecondary,
        outline = BorderSoft,
        outlineVariant = BorderSoftAlt,
        error = BrandRed,
        onError = White,
    )

@Composable
fun AppTheme(content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = ServiYaLightColors, content = content)
}
