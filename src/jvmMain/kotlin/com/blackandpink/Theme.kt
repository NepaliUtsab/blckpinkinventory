package com.blackandpink

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

// Define custom colors
object AppColors {
    val Pink = Color(0xFFFF00FF)
    val DarkPink = Color(0xFFBB00BB)
    val Black = Color(0xFF000000)
    val White = Color(0xFFFFFFFF)
    val Gray = Color(0xFF333333)
}

// Light theme colors
val LightThemeColors = lightColors(
    primary = AppColors.Pink,
    primaryVariant = AppColors.DarkPink,
    secondary = AppColors.Black,
    background = AppColors.White,
    surface = AppColors.White
)

// Dark theme colors
val DarkThemeColors = darkColors(
    primary = AppColors.Pink,
    primaryVariant = AppColors.DarkPink,
    secondary = AppColors.White,
    background = AppColors.Black,
    surface = AppColors.Gray
)
