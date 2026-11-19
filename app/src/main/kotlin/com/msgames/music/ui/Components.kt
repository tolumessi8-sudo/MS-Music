package com.msgames.music.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msgames.music.data.DayBar
import com.msgames.music.data.Track

private val shadowAmbient = Color(0x40223A55)
private val shadowSpot = Color(0x66223A55)

/** A soft, raised "clay" card: gradient fill, glossy edge, gentle shadow. */
@Composable
fun ClayCard(
  modifier: Modifier = Modifier,
  color: Color = Clay.panel,
  corner: Dp = 28.dp,
  elevation: Dp = 10.dp,
  highlight: Float = 0.4f,
  onClick: (() -> Unit)? = null,
  content: @Composable BoxScope.() -> Unit,
) {
  val shape = RoundedCornerShape(corner)
  Box(
    modifier =
      modifier
        .shadow(elevation, shape, ambientColor = shadowAmbient, spotColor = shadowSpot)
        .clip(shape)
        .background(Brush.verticalGradient(listOf(lerp(color, Color.White, highlight), color)))
        .border(
          1.dp,
          Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.05f))
          ),
          shape,
        )
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    content = content,
  )
}

@Composable
fun ClayButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
) {
  val shape = RoundedCornerShape(50)
  Row(
    modifier =
      modifier
        .shadow(8.dp, shape, ambientColor = shadowAmbient, spotColor = shadowSpot)
        .clip(shape)
        .background(Brush.verticalGradient(listOf(Clay.tealLight, Clay.tealDark)))
        .border(1.dp, Color.White.copy(alpha = 0.45f), shape)
        .clickable(onClick = onClick)
        .padding(horizontal = 20.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    if (icon != null) Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
    Text(text, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
  }
}

@Composable
fun ClayIconButton(
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  tint: Color = Clay.ink,
  container: Color = Clay.frost,
  contentDescription: String? = null,
) {
  ClayCard(
    modifier = modifier.size(size),
    color = container,
    corner = size / 2,
    elevation = 6.dp,
    onClick = onClick,
  ) {
    Icon(
      icon,
      contentDescription,
      tint = tint,
      modifier = Modifier.align(Alignment.Center).size(size * 0.5f),
    )
  }
}

/** Generated cover art, tinted by the track's hue. */
@Composable
fun CoverArt(track: Track, size: Dp, modifier: Modifier = Modifier, corner: Dp = 18.dp) {
  val shape = RoundedCornerShape(corner)
  Box(
    modifier =
      modifier
        .size(size)
        .clip(shape)
        .background(
          Brush.linearGradient(
            listOf(
              Color.hsv(track.hue, 0.45f, 0.98f),
              Color.hsv((track.hue + 50f) % 360f, 0.65f, 0.72f),
            )
          )
        )
        .border(1.dp, Color.White.copy(alpha = 0.5f), shape),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      Icons.Rounded.MusicNote,
      null,
      tint = Color.White.copy(alpha = 0.85f),
      modifier = Modifier.size(size * 0.45f),
    )
  }
}

@Composable
fun TrackRow(track: Track, onClick: () -> Unit, modifier: Modifier = Modifier) {
  ClayCard(modifier.fillMaxWidth(), color = Clay.frost, corner = 22.dp, elevation = 6.dp, onClick = onClick) {
    Row(
      Modifier.padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      CoverArt(track, 52.dp)
      Column(Modifier.weight(1f)) {
        Text(
          track.title,
          fontWeight = FontWeight.Bold,
          color = Clay.ink,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          "${track.artist} · ${track.genre}",
          fontSize = 13.sp,
          color = Clay.inkSoft,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
      ClayIconButton(Icons.Rounded.PlayArrow, onClick, size = 40.dp, tint = Color.White, container = Clay.teal)
    }
  }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
  Text(text, modifier, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Clay.ink)
}

@Composable
fun Chip(text: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
  Box(
    modifier
      .clip(RoundedCornerShape(50))
      .background(Clay.ice)
      .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(50))
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
      .padding(horizontal = 12.dp, vertical = 5.dp)
  ) {
    Text(text, fontSize = 12.sp, color = Clay.inkSoft, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
fun WeekBars(week: List<DayBar>, modifier: Modifier = Modifier) {
  val max = (week.maxOfOrNull { it.seconds } ?: 0L).coerceAtLeast(60L)
  val shades =
    listOf(Color(0xFF9FD3D6), Color(0xFF6CC3C0), Color(0xFF4FA8A8), Color(0xFF357F86), Color(0xFF5A86B8), Color(0xFF6F78B8), Color(0xFF7A6FB0))
  Row(
    modifier.fillMaxWidth().height(150.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Bottom,
  ) {
    week.forEachIndexed { i, d ->
      Column(
        Modifier.weight(1f).fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
      ) {
        val frac = d.seconds.toFloat() / max
        Box(
          Modifier.width(24.dp)
            .height(maxOf(110.dp * frac, 6.dp))
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
            .background(
              Brush.verticalGradient(
                listOf(lerp(shades[i % shades.size], Color.White, 0.35f), shades[i % shades.size])
              )
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(d.label, fontSize = 12.sp, color = Clay.inkSoft)
      }
    }
  }
}

@Composable
fun GenreDonut(genres: List<Pair<String, Int>>, modifier: Modifier = Modifier) {
  val total = genres.sumOf { it.second }
  val colors =
    listOf(Color(0xFF3F8FC9), Color(0xFF4DB8B2), Color(0xFF8E8CC6), Color(0xFFA9B4C0), Color(0xFF7CC7A4))
  Row(
    modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    Canvas(Modifier.size(120.dp)) {
      val stroke = 26.dp.toPx()
      val inset = stroke / 2
      val arcSize = Size(size.width - stroke, size.height - stroke)
      if (total == 0) {
        drawArc(
          color = Color(0x22000000),
          startAngle = 0f,
          sweepAngle = 360f,
          useCenter = false,
          topLeft = Offset(inset, inset),
          size = arcSize,
          style = Stroke(stroke),
        )
      } else {
        var start = -90f
        genres.forEachIndexed { i, g ->
          val sweep = 360f * g.second / total
          drawArc(
            color = colors[i % colors.size],
            startAngle = start,
            sweepAngle = maxOf(sweep - 2f, 1f),
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(stroke),
          )
          start += sweep
        }
      }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      if (total == 0) {
        Text("Play some songs to see your top genres.", fontSize = 13.sp, color = Clay.inkSoft)
      } else {
        genres.forEachIndexed { i, g ->
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(10.dp).clip(RoundedCornerShape(50)).background(colors[i % colors.size]))
            Text(g.first, fontSize = 13.sp, color = Clay.ink, modifier = Modifier.width(64.dp), maxLines = 1)
            Text("${g.second * 100 / total}%", fontSize = 13.sp, color = Clay.inkSoft, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}

fun fmtDuration(seconds: Long): String =
  if (seconds < 3600) "${seconds / 60}m" else "%.1fh".format(seconds / 3600.0)

fun fmtTime(ms: Long): String {
  val s = (ms / 1000).coerceAtLeast(0)
  return "%d:%02d".format(s / 60, s % 60)
}
