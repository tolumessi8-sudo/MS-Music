package com.msgames.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msgames.music.R
import com.msgames.music.data.Track
import com.msgames.music.ui.Clay
import com.msgames.music.ui.ClayCard
import com.msgames.music.ui.ClayIconButton
import com.msgames.music.ui.CoverArt
import com.msgames.music.ui.HomeScreen
import com.msgames.music.ui.LibraryScreen
import com.msgames.music.ui.MSMusicTheme
import com.msgames.music.ui.PlayerScreen
import com.msgames.music.ui.SearchScreen
import com.msgames.music.ui.SettingsScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent { MSMusicTheme { App() } }
  }
}

private data class NavItem(val label: String, val icon: ImageVector)

private val navItems =
  listOf(
    NavItem("Home", Icons.Rounded.Home),
    NavItem("Search", Icons.Rounded.Search),
    NavItem("Music", Icons.Rounded.LibraryMusic),
    NavItem("Settings", Icons.Rounded.Settings),
  )

@Composable
private fun App(vm: AppViewModel = viewModel()) {
  val stats by vm.stats.collectAsState()
  val current by vm.current.collectAsState()
  val isPlaying by vm.isPlaying.collectAsState()
  val loading by vm.loading.collectAsState()
  val position by vm.position.collectAsState()
  val duration by vm.duration.collectAsState()
  val error by vm.error.collectAsState()
  val featured by vm.featured.collectAsState()
  val results by vm.results.collectAsState()

  var tab by rememberSaveable { mutableIntStateOf(0) }
  var playerOpen by rememberSaveable { mutableStateOf(false) }

  BackHandler(enabled = playerOpen) { playerOpen = false }
  BackHandler(enabled = !playerOpen && tab != 0) { tab = 0 }

  Box(
    Modifier.fillMaxSize()
      .background(Brush.verticalGradient(listOf(Clay.bgTop, Clay.bgBottom)))
  ) {
    Row(Modifier.fillMaxSize()) {
      SideRail(selected = tab, onSelect = { tab = it })

      Box(Modifier.weight(1f).fillMaxHeight()) {
        when (tab) {
          0 ->
            HomeScreen(
              stats = stats,
              featured = featured,
              onPlay = { t, list -> vm.play(t, list) },
              onPlayRandom = { vm.playRandom() },
              onOpenSearch = { tab = 1 },
              onOpenLibrary = { tab = 2 },
            )
          1 ->
            SearchScreen(
              results = results,
              featured = featured,
              onSearch = { vm.search(it) },
              onPlay = { t, list -> vm.play(t, list) },
            )
          2 ->
            LibraryScreen(
              favorites = stats.favorites,
              recents = stats.recents,
              onPlay = { t, list -> vm.play(t, list) },
            )
          else -> SettingsScreen(sourceName = vm.sourceName, onReset = { vm.resetStats() })
        }

        current?.let { t ->
          MiniPlayer(
            track = t,
            isPlaying = isPlaying,
            loading = loading,
            onOpen = { playerOpen = true },
            onToggle = { vm.togglePlay() },
            modifier =
              Modifier.align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 6.dp, end = 14.dp, bottom = 12.dp),
          )
        }

        error?.let { message ->
          ClayCard(
            Modifier.align(Alignment.TopCenter)
              .statusBarsPadding()
              .padding(start = 6.dp, end = 14.dp, top = 10.dp)
              .fillMaxWidth(),
            color = Color(0xFFF3D6D6),
            corner = 20.dp,
            onClick = { vm.clearError() },
          ) {
            Text(message, Modifier.padding(14.dp), color = Color(0xFF7A2E2E), fontSize = 14.sp)
          }
        }
      }
    }

    AnimatedVisibility(
      visible = playerOpen && current != null,
      enter = slideInVertically { it } + fadeIn(),
      exit = slideOutVertically { it } + fadeOut(),
    ) {
      current?.let { t ->
        PlayerScreen(
          track = t,
          isPlaying = isPlaying,
          loading = loading,
          position = position,
          duration = duration,
          isFavorite = stats.favorites.any { it.id == t.id },
          onClose = { playerOpen = false },
          onToggle = { vm.togglePlay() },
          onNext = { vm.next() },
          onPrevious = { vm.previous() },
          onSeek = { vm.seekTo(it) },
          onFavorite = { vm.toggleFavorite(t) },
        )
      }
    }
  }
}

@Composable
private fun MiniPlayer(
  track: Track,
  isPlaying: Boolean,
  loading: Boolean,
  onOpen: () -> Unit,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ClayCard(
    modifier.fillMaxWidth().height(66.dp),
    color = Clay.navy,
    corner = 26.dp,
    highlight = 0.12f,
    onClick = onOpen,
  ) {
    Row(
      Modifier.fillMaxSize().padding(horizontal = 9.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      CoverArt(track, 48.dp, corner = 16.dp)
      Column(Modifier.weight(1f)) {
        Text(
          track.title,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(track.artist, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, maxLines = 1)
      }
      if (loading) {
        CircularProgressIndicator(Modifier.size(28.dp), color = Color.White, strokeWidth = 3.dp)
      } else {
        ClayIconButton(
          if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
          onToggle,
          size = 44.dp,
          tint = Color.White,
          container = Clay.teal,
        )
      }
    }
  }
}

@Composable
private fun SideRail(selected: Int, onSelect: (Int) -> Unit) {
  ClayCard(
    Modifier.statusBarsPadding()
      .navigationBarsPadding()
      .padding(start = 10.dp, top = 10.dp, bottom = 12.dp, end = 4.dp)
      .width(66.dp)
      .fillMaxHeight(),
    color = Clay.navy,
    corner = 30.dp,
    highlight = 0.1f,
    elevation = 12.dp,
  ) {
    Column(
      Modifier.fillMaxSize().padding(vertical = 14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Image(
        painterResource(R.drawable.mascot),
        contentDescription = null,
        modifier = Modifier.size(46.dp).clip(CircleShape).background(Clay.ice),
      )
      Spacer(Modifier.height(20.dp))
      navItems.forEachIndexed { i, item ->
        if (item.label == "Settings") Spacer(Modifier.weight(1f))
        RailItem(item, i == selected) { onSelect(i) }
        Spacer(Modifier.height(8.dp))
      }
    }
  }
}

@Composable
private fun RailItem(item: NavItem, on: Boolean, onClick: () -> Unit) {
  Column(
    Modifier.width(54.dp)
      .clip(RoundedCornerShape(18.dp))
      .then(
        if (on) Modifier.background(Brush.verticalGradient(listOf(Color.White, Clay.ice)))
        else Modifier
      )
      .clickable(onClick = onClick)
      .padding(vertical = 9.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(3.dp),
  ) {
    Icon(
      item.icon,
      contentDescription = item.label,
      tint = if (on) Clay.navy else Color.White.copy(alpha = 0.75f),
      modifier = Modifier.size(24.dp),
    )
    Text(
      item.label,
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      color = if (on) Clay.navy else Color.White.copy(alpha = 0.75f),
      maxLines = 1,
    )
  }
}
