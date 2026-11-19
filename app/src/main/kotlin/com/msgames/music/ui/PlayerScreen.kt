package com.msgames.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msgames.music.data.Track

@Composable
fun PlayerScreen(
  track: Track,
  isPlaying: Boolean,
  loading: Boolean,
  position: Long,
  duration: Long,
  isFavorite: Boolean,
  onClose: () -> Unit,
  onToggle: () -> Unit,
  onNext: () -> Unit,
  onPrevious: () -> Unit,
  onSeek: (Long) -> Unit,
  onFavorite: () -> Unit,
) {
  var dragging by remember { mutableStateOf(false) }
  var dragValue by remember { mutableFloatStateOf(0f) }
  val max = maxOf(duration, 1L).toFloat()
  val shown = if (dragging) dragValue else position.toFloat().coerceIn(0f, max)

  Box(
    Modifier.fillMaxSize()
      .background(Brush.verticalGradient(listOf(Color.hsv(track.hue, 0.35f, 0.93f), Clay.bgBottom)))
      // swallow touches so the screen underneath is not clickable
      .pointerInput(Unit) { detectTapGestures { } }
  ) {
    Column(
      Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        ClayIconButton(Icons.Rounded.KeyboardArrowDown, onClose, contentDescription = "Close")
        Text("Now Playing", fontWeight = FontWeight.Bold, color = Clay.ink, fontSize = 16.sp)
        ClayIconButton(
          if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
          onFavorite,
          tint = if (isFavorite) Color(0xFFD9577A) else Clay.ink,
          contentDescription = "Favorite",
        )
      }

      Spacer(Modifier.weight(1f))

      ClayCard(Modifier.size(290.dp), color = Clay.frost, corner = 44.dp, elevation = 18.dp) {
        CoverArt(track, 290.dp, corner = 44.dp)
      }

      Spacer(Modifier.height(28.dp))
      Text(
        track.title,
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Clay.ink,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
      Text("${track.artist} · ${track.genre}", fontSize = 15.sp, color = Clay.inkSoft)

      Spacer(Modifier.height(20.dp))
      Slider(
        value = shown,
        onValueChange = {
          dragging = true
          dragValue = it
        },
        onValueChangeFinished = {
          onSeek(dragValue.toLong())
          dragging = false
        },
        valueRange = 0f..max,
        colors =
          SliderDefaults.colors(
            thumbColor = Clay.teal,
            activeTrackColor = Clay.teal,
            inactiveTrackColor = Color.White.copy(alpha = 0.7f),
          ),
      )
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(fmtTime(shown.toLong()), fontSize = 12.sp, color = Clay.inkSoft)
        Text(fmtTime(duration), fontSize = 12.sp, color = Clay.inkSoft)
      }

      Spacer(Modifier.height(16.dp))
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(22.dp)) {
        ClayIconButton(Icons.Rounded.SkipPrevious, onPrevious, size = 60.dp, contentDescription = "Previous")
        ClayCard(Modifier.size(84.dp), color = Clay.teal, corner = 42.dp, elevation = 14.dp, onClick = onToggle) {
          if (loading) {
            CircularProgressIndicator(
              Modifier.align(Alignment.Center).size(32.dp),
              color = Color.White,
              strokeWidth = 3.dp,
            )
          } else {
            Icon(
              if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
              contentDescription = if (isPlaying) "Pause" else "Play",
              tint = Color.White,
              modifier = Modifier.align(Alignment.Center).size(42.dp),
            )
          }
        }
        ClayIconButton(Icons.Rounded.SkipNext, onNext, size = 60.dp, contentDescription = "Next")
      }
      Spacer(Modifier.weight(1f))
    }
  }
}
