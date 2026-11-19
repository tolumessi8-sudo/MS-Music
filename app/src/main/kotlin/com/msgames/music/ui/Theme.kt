package com.msgames.music.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Soft "clay / frosted" palette. */
object Clay {
  val bgTop = Color(0xFFC6D4DF)
  val bgBottom = Color(0xFF9FB2C2)
  val navy = Color(0xFF2B4264)
  val navyDark = Color(0xFF1C2D49)
  val ink = Color(0xFF1E2E45)
  val inkSoft = Color(0xFF52657D)
  val teal = Color(0xFF4FA8A8)
  val tealLight = Color(0xFF6CC3C0)
  val tealDark = Color(0xFF357F86)
  val mint = Color(0xFFB4E4DC)
  val ice = Color(0xFFE2EDF6)
  val frost = Color(0xFFEDF3F8)
  val lavender = Color(0xFFB9B8E0)
  val panel = Color(0xFFD5E0E9)
}

@Composable
fun MSMusicTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme =
      lightColorScheme(
        primary = Clay.teal,
        onPrimary = Color.White,
        secondary = Clay.navy,
        background = Clay.bgTop,
        surface = Clay.panel,
        onSurface = Clay.ink,
        onBackground = Clay.ink,
      ),
    content = content,
  )
}
