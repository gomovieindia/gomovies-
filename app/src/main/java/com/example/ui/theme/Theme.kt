package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DodgerBlue,
    secondary = ElectricBlue,
    tertiary = StarYellow,
    background = BlackBg,
    surface = SurfaceDark,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    surfaceVariant = DarkGrey80,
    onSurfaceVariant = TextGray,
    outline = GrayBorder
)

private val LightColorScheme = lightColorScheme(
    primary = DodgerBlue,
    secondary = ElectricBlue,
    tertiary = StarYellow,
    background = LightBg,
    surface = LightSurface,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightTextSecondary,
    outline = LightTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme by default, but toggleable in Settings state
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
