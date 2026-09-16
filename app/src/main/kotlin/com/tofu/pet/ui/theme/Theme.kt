package com.tofu.pet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TofuColorScheme = lightColorScheme(
    primary = Color(0xFFF5E6C8),
    primaryContainer = Color(0xFFE8D4A8),
    secondary = Color(0xFF5C3A1E),
    secondaryContainer = Color(0xFF5C3A1E),
    tertiary = Color(0xFFF4A0B0),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    error = Color(0xFFB00020),
)

@Composable
fun TofuPetTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TofuColorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
