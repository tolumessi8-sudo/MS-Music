package com.msgames.music

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.msgames.music.data.DemoSource
import com.msgames.music.data.MusicSource
import com.msgames.music.data.Stats
import com.msgames.music.data.StatsStore
import com.msgames.music.data.Track
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(app: Application) : AndroidViewModel(app) {

  // To switch music source later, only this line changes.
  private val source: MusicSource = DemoSource()
  private val store = StatsStore(app)

  val player: ExoPlayer =
    ExoPlayer.Builder(app)
      .setAudioAttributes(
        AudioAttributes.Builder()
          .setUsage(C.USAGE_MEDIA)
          .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
          .build(),
        true,
      )
      .setHandleAudioBecomingNoisy(true)
      .build()

  private val _stats = MutableStateFlow(store.snapshot())
  val stats: StateFlow<Stats> = _stats.asStateFlow()

  private val _current = MutableStateFlow<Track?>(null)
  val current: StateFlow<Track?> = _current.asStateFlow()

  private val _isPlaying = MutableStateFlow(false)
  val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _loading = MutableStateFlow(false)
  val loading: StateFlow<Boolean> = _loading.asStateFlow()

  private val _position = MutableStateFlow(0L)
  val position: StateFlow<Long> = _position.asStateFlow()

  private val _duration = MutableStateFlow(0L)
  val duration: StateFlow<Long> = _duration.asStateFlow()

  private val _error = MutableStateFlow<String?>(null)
  val error: StateFlow<String?> = _error.asStateFlow()

  private val _featured = MutableStateFlow<List<Track>>(emptyList())
  val featured: StateFlow<List<Track>> = _featured.asStateFlow()

  private val _results = MutableStateFlow<List<Track>>(emptyList())
  val results: StateFlow<List<Track>> = _results.asStateFlow()

  private var queue: List<Track> = emptyList()
  private var index = 0
  private var playJob: Job? = null
  private var listenedMs = 0L

  init {
    player.addListener(
      object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
          _isPlaying.value = isPlaying
          if (!isPlaying) flushListening()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
          if (playbackState == Player.STATE_READY) {
            _loading.value = false
            _duration.value = player.duration.coerceAtLeast(0L)
          }
          if (playbackState == Player.STATE_ENDED) next()
        }

        override fun onPlayerError(error: PlaybackException) {
          _loading.value = false
          _error.value = "Playback failed (${error.errorCodeName})"
        }
      }
    )

    viewModelScope.launch { _featured.value = runCatching { source.featured() }.getOrDefault(emptyList()) }

    // Progress + listening-time ticker.
    viewModelScope.launch {
      while (true) {
        delay(500)
        if (player.isPlaying) {
          _position.value = player.currentPosition
          _duration.value = player.duration.coerceAtLeast(0L)
          listenedMs += 500
          if (listenedMs >= 5000) flushListening()
        }
      }
    }
  }

  private fun flushListening() {
    val secs = listenedMs / 1000
    if (secs > 0) {
      store.addListening(secs)
      listenedMs -= secs * 1000
      _stats.value = store.snapshot()
    }
  }

  fun play(track: Track, list: List<Track> = listOf(track)) {
    queue = list.ifEmpty { listOf(track) }
    index = queue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
    startCurrent()
  }

  fun playRandom() {
    val list = _featured.value.shuffled()
    if (list.isNotEmpty()) play(list.first(), list)
  }

  private fun startCurrent() {
    val t = queue[index]
    playJob?.cancel()
    playJob =
      viewModelScope.launch {
        _current.value = t
        _loading.value = true
        _error.value = null
        _position.value = 0L
        _duration.value = 0L
        try {
          val url = source.resolveStream(t)
          player.setMediaItem(MediaItem.fromUri(url))
          player.prepare()
          player.play()
          store.recordPlayStart(t)
          _stats.value = store.snapshot()
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          _loading.value = false
          _error.value = "Couldn't load \"${t.title}\": ${e.message ?: "unknown error"}"
        }
      }
  }

  fun togglePlay() {
    if (_current.value == null) return
    if (player.isPlaying) player.pause() else player.play()
  }

  fun next() {
    if (queue.isEmpty()) return
    index = (index + 1) % queue.size
    startCurrent()
  }

  fun previous() {
    if (queue.isEmpty()) return
    if (player.currentPosition > 3000) {
      player.seekTo(0)
      return
    }
    index = (index - 1 + queue.size) % queue.size
    startCurrent()
  }

  fun seekTo(ms: Long) {
    player.seekTo(ms)
    _position.value = ms
  }

  fun search(query: String) {
    viewModelScope.launch {
      _results.value = runCatching { source.search(query) }.getOrDefault(emptyList())
    }
  }

  fun toggleFavorite(track: Track) {
    store.toggleFavorite(track)
    _stats.value = store.snapshot()
  }

  fun setName(name: String) {
    store.setName(name)
    _stats.value = store.snapshot()
  }

  fun resetStats() {
    store.reset()
    _stats.value = store.snapshot()
  }

  fun clearError() {
    _error.value = null
  }

  val sourceName: String
    get() = source.name

  override fun onCleared() {
    flushListening()
    player.release()
    super.onCleared()
  }
}
