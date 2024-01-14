package com.example.steamnetworth.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = SteamDarkColors.background,
    surface = SteamDarkColors.background,
    primary = SteamDarkColors.textPrimary,
    secondary = SteamDarkColors.textSecondary
)

@Composable
fun SteamNetWorthTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}