package com.msgames.music.data

import android.content.Context
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

data class DayBar(val label: String, val seconds: Long)

/** Everything the dashboard shows. All numbers are real, saved on the phone. */
data class Stats(
  val name: String = "Friend",
  val songsPlayed: Int = 0,
  val songsThisWeek: Int = 0,
  val secondsTotal: Long = 0,
  val secondsThisWeek: Long = 0,
  val streak: Int = 0,
  val week: List<DayBar> = emptyList(),
  val genres: List<Pair<String, Int>> = emptyList(),
  val recents: List<Track> = emptyList(),
  val favorites: List<Track> = emptyList(),
)

class StatsStore(context: Context) {
  private val prefs = context.getSharedPreferences("ms_music_stats", Context.MODE_PRIVATE)

  private fun today(): Long = LocalDate.now().toEpochDay()

  fun name(): String = prefs.getString("name", null) ?: "Friend"

  fun setName(value: String) {
    prefs.edit().putString("name", value.trim().ifEmpty { "Friend" }).apply()
  }

  fun recordPlayStart(track: Track) {
    val day = today()
    val e = prefs.edit()
    e.putInt("songs_total", prefs.getInt("songs_total", 0) + 1)
    e.putInt("plays_$day", prefs.getInt("plays_$day", 0) + 1)

    val counts = JSONObject(prefs.getString("genre_counts", "{}") ?: "{}")
    counts.put(track.genre, counts.optInt(track.genre, 0) + 1)
    e.putString("genre_counts", counts.toString())

    val last = prefs.getLong("last_day", -1L)
    val streak = prefs.getInt("streak", 0)
    if (last == day - 1) e.putInt("streak", streak + 1) else if (last != day) e.putInt("streak", 1)
    e.putLong("last_day", day)

    val recents = readTracks("recents").filter { it.id != track.id }.toMutableList()
    recents.add(0, track)
    e.putString("recents", encode(recents.take(20)))
    e.apply()
  }

  fun addListening(seconds: Long) {
    if (seconds <= 0) return
    val day = today()
    prefs
      .edit()
      .putLong("sec_total", prefs.getLong("sec_total", 0L) + seconds)
      .putLong("sec_$day", prefs.getLong("sec_$day", 0L) + seconds)
      .apply()
  }

  fun toggleFavorite(track: Track) {
    val list = readTracks("favorites").toMutableList()
    val removed = list.removeAll { it.id == track.id }
    if (!removed) list.add(0, track)
    prefs.edit().putString("favorites", encode(list)).apply()
  }

  /** Clears stats and history but keeps the user's name. */
  fun reset() {
    val n = name()
    prefs.edit().clear().apply()
    setName(n)
  }

  fun snapshot(): Stats {
    val day = today()
    val week =
      (6 downTo 0).map { off ->
        val d = LocalDate.ofEpochDay(day - off)
        DayBar(
          d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
          prefs.getLong("sec_${day - off}", 0L),
        )
      }
    var songsWeek = 0
    for (i in 0..6) songsWeek += prefs.getInt("plays_${day - i}", 0)

    val last = prefs.getLong("last_day", -1L)
    val streak = if (last >= day - 1) prefs.getInt("streak", 0) else 0

    val counts = JSONObject(prefs.getString("genre_counts", "{}") ?: "{}")
    val all = mutableListOf<Pair<String, Int>>()
    val keys = counts.keys()
    while (keys.hasNext()) {
      val k = keys.next()
      all.add(k to counts.optInt(k, 0))
    }
    all.sortByDescending { it.second }
    val top = all.take(4)
    val rest = all.drop(4).sumOf { it.second }
    val genres = if (rest > 0) top + ("Other" to rest) else top

    return Stats(
      name = name(),
      songsPlayed = prefs.getInt("songs_total", 0),
      songsThisWeek = songsWeek,
      secondsTotal = prefs.getLong("sec_total", 0L),
      secondsThisWeek = week.sumOf { it.seconds },
      streak = streak,
      week = week,
      genres = genres,
      recents = readTracks("recents"),
      favorites = readTracks("favorites"),
    )
  }

  private fun readTracks(key: String): List<Track> {
    val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
    return (0 until arr.length()).map { i ->
      val o = arr.getJSONObject(i)
      Track(
        id = o.getString("id"),
        title = o.getString("title"),
        artist = o.getString("artist"),
        genre = o.optString("genre", "Other"),
        hue = o.optDouble("hue", 200.0).toFloat(),
      )
    }
  }

  private fun encode(list: List<Track>): String {
    val arr = JSONArray()
    list.forEach {
      arr.put(
        JSONObject()
          .put("id", it.id)
          .put("title", it.title)
          .put("artist", it.artist)
          .put("genre", it.genre)
          .put("hue", it.hue.toDouble())
      )
    }
    return arr.toString()
  }
}
