package com.msgames.music.data

/** A song as shown in the UI. [hue] (0..360) tints the generated cover art. */
data class Track(
  val id: String,
  val title: String,
  val artist: String,
  val genre: String = "Other",
  val hue: Float = 200f,
)

/**
 * Where music comes from. The UI and player only talk to this interface, so a new source
 * (for example a YouTube one) can be added without touching any screen.
 */
interface MusicSource {
  val name: String

  suspend fun featured(): List<Track>

  suspend fun search(query: String): List<Track>

  /** Returns a direct, playable audio URL for [track]. Called right before playback. */
  suspend fun resolveStream(track: Track): String
}

/** Free demo tracks (SoundHelix) so the whole app can be tested before a real source is added. */
class DemoSource : MusicSource {
  override val name = "Demo (SoundHelix test tracks)"

  private data class Entry(val track: Track, val url: String)

  private val entries =
    listOf(
        Triple("Sunset Drive", "Lo-fi", 210f),
        Triple("Golden Hour", "Pop", 35f),
        Triple("Coffee Shop", "Lo-fi", 25f),
        Triple("Night Bus", "Hip Hop", 255f),
        Triple("Neon Rain", "Pop", 300f),
        Triple("Paper Planes", "Rock", 10f),
        Triple("Slow Tide", "Lo-fi", 190f),
        Triple("City Lights", "Hip Hop", 275f),
        Triple("Wild Fields", "Rock", 120f),
        Triple("Blue Room", "Other", 225f),
      )
      .mapIndexed { i, (title, genre, hue) ->
        Entry(
          Track("demo${i + 1}", title, "SoundHelix", genre, hue),
          "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-${i + 1}.mp3",
        )
      }

  override suspend fun featured(): List<Track> = entries.map { it.track }

  override suspend fun search(query: String): List<Track> {
    val q = query.trim().lowercase()
    if (q.isEmpty()) return emptyList()
    return entries
      .map { it.track }
      .filter {
        it.title.lowercase().contains(q) ||
          it.artist.lowercase().contains(q) ||
          it.genre.lowercase().contains(q)
      }
  }

  override suspend fun resolveStream(track: Track): String =
    entries.firstOrNull { it.track.id == track.id }?.url
      ?: throw IllegalArgumentException("Unknown track")
}
