package echo.music.iad1tya.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

enum class AppIconType(val value: Int) {
  DEFAULT(0),
  MIDNIGHT(1),
  AQUA(2),
  SUNSET(3)
}

object IconUtils {
  private const val PKG = "echo.music.iad1tya"

  fun setIcon(context: Context, iconType: AppIconType) {
    val pm = context.packageManager
    val targets =
      listOf(
        AppIconType.DEFAULT to "MainActivityAlias",
        AppIconType.MIDNIGHT to "MainActivityMidnight",
        AppIconType.AQUA to "MainActivityAqua",
        AppIconType.SUNSET to "MainActivitySunset"
      )
    // Enable the chosen alias first so the app is never left without a launcher entry.
    val ordered = targets.sortedByDescending { it.first == iconType }
    for ((type, cls) in ordered) {
      pm.setComponentEnabledSetting(
        ComponentName(context, "$PKG.$cls"),
        if (type == iconType) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
      )
    }
  }
}
