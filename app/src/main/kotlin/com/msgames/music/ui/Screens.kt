package com.msgames.music.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msgames.music.data.Track
import kotlinx.coroutines.delay

@Composable
private fun fieldColors() =
  OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Clay.teal,
    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
    focusedContainerColor = Clay.frost,
    unfocusedContainerColor = Clay.frost.copy(alpha = 0.85f),
    focusedTextColor = Clay.ink,
    unfocusedTextColor = Clay.ink,
  )

@Composable
private fun ScreenTitle(text: String) {
  Text(text, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Clay.ink.copy(alpha = 0.75f))
}

@Composable
fun SearchScreen(
  results: List<Track>,
  featured: List<Track>,
  onSearch: (String) -> Unit,
  onPlay: (Track, List<Track>) -> Unit,
  modifier: Modifier = Modifier,
) {
  var query by rememberSaveable { mutableStateOf("") }
  LaunchedEffect(query) {
    delay(350)
    onSearch(query)
  }
  val list = if (query.isBlank()) featured else results

  Column(modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp, vertical = 12.dp)) {
    ScreenTitle("Search")
    Spacer(Modifier.height(14.dp))
    OutlinedTextField(
      value = query,
      onValueChange = { query = it },
      modifier = Modifier.fillMaxWidth(),
      placeholder = { Text("Search for songs, artists…") },
      leadingIcon = { Icon(Icons.Rounded.Search, null) },
      singleLine = true,
      shape = RoundedCornerShape(50),
      colors = fieldColors(),
    )
    Spacer(Modifier.height(14.dp))
    SectionTitle(if (query.isBlank()) "Featured" else "Results")
    Spacer(Modifier.height(10.dp))
    LazyColumn(
      contentPadding = PaddingValues(bottom = 120.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      if (list.isEmpty()) {
        item { Text("Nothing found.", color = Clay.inkSoft) }
      }
      items(list, key = { it.id }) { t -> TrackRow(t, onClick = { onPlay(t, list) }) }
    }
  }
}

@Composable
fun LibraryScreen(
  favorites: List<Track>,
  recents: List<Track>,
  onPlay: (Track, List<Track>) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier.fillMaxSize().statusBarsPadding(),
    contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 120.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    item { ScreenTitle("Library") }
    item { SectionTitle("Favorites") }
    if (favorites.isEmpty()) {
      item { Text("Tap the heart in the player to save songs here.", color = Clay.inkSoft, fontSize = 14.sp) }
    }
    items(favorites, key = { "f" + it.id }) { t -> TrackRow(t, onClick = { onPlay(t, favorites) }) }
    item { Spacer(Modifier.height(8.dp)) }
    item { SectionTitle("Recently played") }
    if (recents.isEmpty()) {
      item { Text("Songs you play will show up here.", color = Clay.inkSoft, fontSize = 14.sp) }
    }
    items(recents, key = { "r" + it.id }) { t -> TrackRow(t, onClick = { onPlay(t, recents) }) }
  }
}

@Composable
fun SettingsScreen(
  sourceName: String,
  onReset: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(start = 8.dp, end = 14.dp, top = 12.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    ScreenTitle("Settings")

    ClayCard(Modifier.fillMaxWidth(), color = Clay.frost) {
      Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionTitle("Music source")
        Text(sourceName, color = Clay.ink)
        Text("More sources will be added next.", fontSize = 13.sp, color = Clay.inkSoft)
      }
    }

    ClayCard(Modifier.fillMaxWidth(), color = Clay.frost) {
      Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle("Data")
        Text("Clears your stats and history.", fontSize = 13.sp, color = Clay.inkSoft)
        ClayButton("Reset stats", onReset)
      }
    }

    ClayCard(Modifier.fillMaxWidth(), color = Clay.frost) {
      Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionTitle("About")
        Text("MS Music 0.1.0", color = Clay.ink, fontWeight = FontWeight.SemiBold)
        Text("Made by MSGames", fontSize = 13.sp, color = Clay.inkSoft)
      }
    }

    ClayCard(Modifier.fillMaxWidth(), color = Clay.frost) {
      Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Credits")
        CreditLine("Design and development", "MSGames")
        CreditLine("Interface style", "Inspired by a clay / frosted-glass dashboard concept")
        CreditLine("Demo music", "SoundHelix (soundhelix.com)")
        CreditLine("Built with", "Kotlin, Jetpack Compose, AndroidX Media3, OkHttp, Kotlin Coroutines (Apache License 2.0)")
      }
    }
  }
}

@Composable
private fun CreditLine(title: String, body: String) {
  Column {
    Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Clay.ink)
    Text(body, fontSize = 13.sp, color = Clay.inkSoft)
  }
}
