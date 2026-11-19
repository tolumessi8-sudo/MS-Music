package com.msgames.music.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msgames.music.R
import com.msgames.music.data.Stats
import com.msgames.music.data.Track

@Composable
fun HomeScreen(
  stats: Stats,
  featured: List<Track>,
  onPlay: (Track, List<Track>) -> Unit,
  onPlayRandom: () -> Unit,
  onOpenSearch: () -> Unit,
  onOpenLibrary: () -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier.fillMaxSize().statusBarsPadding(),
    contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 120.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    item { HeaderRow() }
    item { SearchPill(onOpenSearch) }
    item { HeroCard(onPlayRandom) }
    item { StatGrid(stats) }
    item { WeekCard(stats) }
    item { GenresCard(stats) }
    item { RecentCard(stats.recents, onPlay, onOpenLibrary) }
    item { DailyMixCard(featured, onPlay) }
    item { DiscoverBanner(onOpenSearch) }
  }
}

@Composable
private fun HeaderRow() {
  Row(
    Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text("Dashboard", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Clay.ink.copy(alpha = 0.75f))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      ClayIconButton(Icons.Rounded.Notifications, {}, size = 40.dp, contentDescription = "Notifications")
      ClayIconButton(Icons.Rounded.Person, {}, size = 40.dp, tint = Color.White, container = Clay.teal)
    }
  }
}

@Composable
private fun SearchPill(onOpenSearch: () -> Unit) {
  ClayCard(Modifier.fillMaxWidth(), color = Clay.frost, corner = 30.dp, elevation = 6.dp, onClick = onOpenSearch) {
    Row(
      Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Icon(Icons.Rounded.Search, null, tint = Clay.inkSoft)
      Text("Search for songs, artists...", color = Clay.inkSoft, fontSize = 14.sp, maxLines = 1)
    }
  }
}

@Composable
private fun HeroCard(onPlayRandom: () -> Unit) {
  ClayCard(Modifier.fillMaxWidth(), color = Clay.navy, corner = 30.dp, highlight = 0.1f, elevation = 12.dp) {
    Box(
      Modifier.matchParentSize()
        .background(Brush.linearGradient(listOf(Color(0xFF55688A), Color(0xFF2E405E))))
    )
    Row(
      Modifier.padding(start = 10.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Image(painterResource(R.drawable.mascot), null, Modifier.size(104.dp))
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          "Time for your next track!",
          color = Color.White,
          fontSize = 19.sp,
          fontWeight = FontWeight.ExtraBold,
          maxLines = 3,
        )
        ClayButton("Play Something", onPlayRandom, icon = Icons.Rounded.PlayArrow)
      }
    }
  }
}

@Composable
private fun StatCard(
  color: Color,
  icon: ImageVector,
  label: String,
  value: String,
  sub: String,
  modifier: Modifier = Modifier,
) {
  ClayCard(modifier, color = color, corner = 26.dp, elevation = 8.dp) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      ClayIconButton(icon, {}, size = 40.dp, container = Color.White.copy(alpha = 0.55f), tint = Clay.navy)
      Spacer(Modifier.height(8.dp))
      Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Clay.ink)
      Text(value, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Clay.ink)
      Text(sub, fontSize = 12.sp, color = Clay.inkSoft)
    }
  }
}

private fun hours(seconds: Long): String =
  String.format(java.util.Locale.US, "%.1f", seconds / 3600.0)

@Composable
private fun StatGrid(stats: Stats) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
      StatCard(
        Clay.mint, Icons.Rounded.MusicNote, "Songs Played", "${stats.songsPlayed}",
        "+${stats.songsThisWeek} this week", Modifier.weight(1f),
      )
      StatCard(
        Clay.ice, Icons.Rounded.Favorite, "Favorites", "${stats.favorites.size}",
        "saved songs", Modifier.weight(1f),
      )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
      StatCard(
        Clay.frost, Icons.Rounded.Schedule, "Hours Listened", hours(stats.secondsTotal),
        "+${hours(stats.secondsThisWeek)} this week", Modifier.weight(1f),
      )
      StatCard(
        Clay.lavender, Icons.Rounded.LocalFireDepartment, "Current Streak", "${stats.streak}",
        if (stats.streak == 1) "day in a row" else "days in a row", Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun WeekCard(stats: Stats) {
  ClayCard(Modifier.fillMaxWidth(), color = Clay.frost, corner = 28.dp) {
    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        SectionTitle("Listening Overview")
        Chip("Last 7 days")
      }
      WeekBars(stats.week)
    }
  }
}

@Composable
private fun GenresCard(stats: Stats) {
  ClayCard(Modifier.fillMaxWidth(), color = Clay.frost, corner = 28.dp) {
    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
      SectionTitle("Top Genres")
      GenreDonut(stats.genres)
    }
  }
}

@Composable
private fun RecentCard(recents: List<Track>, onPlay: (Track, List<Track>) -> Unit, onOpenLibrary: () -> Unit) {
  ClayCard(Modifier.fillMaxWidth(), color = Clay.frost, corner = 28.dp) {
    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        SectionTitle("Recently Played")
        Chip("See All", onClick = onOpenLibrary)
      }
      if (recents.isEmpty()) {
        Text("Nothing yet. Hit Play Something!", fontSize = 14.sp, color = Clay.inkSoft)
      } else {
        recents.take(3).forEach { t ->
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CoverArt(t, 48.dp, corner = 14.dp)
            Column(Modifier.weight(1f)) {
              Text(t.title, fontWeight = FontWeight.Bold, color = Clay.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
              Text(t.artist, fontSize = 13.sp, color = Clay.inkSoft, maxLines = 1)
            }
            ClayIconButton(Icons.Rounded.PlayArrow, { onPlay(t, recents) }, size = 38.dp, tint = Color.White, container = Clay.teal)
          }
        }
      }
    }
  }
}

@Composable
private fun DailyMixCard(featured: List<Track>, onPlay: (Track, List<Track>) -> Unit) {
  ClayCard(Modifier.fillMaxWidth().height(170.dp), color = Clay.navy, corner = 28.dp, highlight = 0.05f) {
    Box(
      Modifier.fillMaxSize()
        .background(Brush.linearGradient(listOf(Color(0xFF4A6FA5), Color(0xFF8B6BB0))))
    )
    Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
      Text("Daily Mix", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
      Text("Chill Vibes", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
      Text("${featured.size} songs", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
    }
    ClayIconButton(
      Icons.Rounded.PlayArrow,
      {
        val list = featured.shuffled()
        if (list.isNotEmpty()) onPlay(list.first(), list)
      },
      modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
      size = 54.dp,
      tint = Color.White,
      container = Clay.teal,
    )
  }
}

@Composable
private fun DiscoverBanner(onExplore: () -> Unit) {
  ClayCard(Modifier.fillMaxWidth(), color = Clay.frost, corner = 28.dp) {
    Row(
      Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(Modifier.size(46.dp).clip(RoundedCornerShape(50)).background(Clay.ice), contentAlignment = Alignment.Center) {
        Icon(Icons.Rounded.AutoAwesome, null, tint = Clay.teal)
      }
      Column(Modifier.weight(1f)) {
        Text("Discover new music", fontWeight = FontWeight.Bold, color = Clay.ink)
        Text("Made just for you", fontSize = 13.sp, color = Clay.inkSoft)
      }
      ClayButton("Explore", onExplore)
    }
  }
}
