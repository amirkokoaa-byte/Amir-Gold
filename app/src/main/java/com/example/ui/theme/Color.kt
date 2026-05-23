package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val Slate950 = Color(0xFF020617)
val Slate50 = Color(0xFFF8FAFC)
val Slate400 = Color(0xFF94A3B8)

val Amber500 = Color(0xFFF59E0B)
val Indigo500 = Color(0xFF6366F1)
val Emerald500 = Color(0xFF10B981)
val Rose400 = Color(0xFFFB7185)

val GlassSurface = Color.White.copy(alpha = 0.05f)
val GlassBorder = Color.White.copy(alpha = 0.1f)

// Keep old names mapped to new colors to avoid breaking some screens
val DarkBackground = Slate950
val CardBackground = GlassSurface
val TextPrimary = Slate50
val TextSecondary = Slate400

val NeonGold = Amber500
val NeonCyan = Indigo500
val NeonRed = Rose400
val NeonGreen = Emerald500
val NeonGoldBright = Color(0xFFFDE68A)
