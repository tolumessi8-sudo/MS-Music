# Changes from Echo Music

This notice is provided to satisfy section 5(a) of the GNU GPL v3.

**MS Music** is a modified version of **Echo Music** (https://github.com/EchoMusicApp/Echo-Music),
copyright its respective authors, licensed under GPL-3.0.

- Based on upstream commit: `c69c4081140579b7efcc99eda936f4e4a66948ee` (2026-11-16)
- Modified by: MSGames
- Modification date: 2026-11-16

## Modifications

### Branding
- App display name changed from "Echo Music" to "MS Music" (`app_name.xml` and all locale `strings.xml` files)
- `applicationId` changed from `echo.music.iad1tya` to `com.msgames.msmusic` (the Kotlin/Java package and `namespace` are unchanged)
- `shortcuts.xml` target package updated to the new application ID
- New launcher icons (all densities) and adaptive icon background color
- README rewritten; upstream README preserved in credits form

### Look and feel
- Default theme seed color changed to violet (`0xFF7C5CFF`), palette style set to Vibrant, and wallpaper-based dynamic color disabled by default
- Liquid Glass enabled by default (vibrancy 1.4, blur 12, surface opacity 0.3)
- Floating navigation bar enabled by default
- Gradient color glow drawn behind the app so glass surfaces have content to blur

### Upstream-specific services
- In-app updater, update settings and commit viewer now point to this project's GitHub repository
- Removed upstream's `google-services.json` (Firebase analytics/crash reporting is disabled unless you add your own)
- Removed upstream's `FUNDING.yml` and maintainer-only GitHub workflows
- Removed the first-launch welcome dialog (it carried the original developer's social and donation links)
- Replaced the original developer's social/donation rows in Settings → About with a single credit row linking to Echo Music (GPL-3.0); the full thanks list is in the repository README
- Performance: lighter defaults (static gradient player background instead of Live Mesh, no chromatic-aberration/depth shader passes, no glass on the full-screen player, blur radius 8) and the background glow is cached instead of rebuilt every frame; all of these can be changed in Settings → Appearance
- Added a new GitHub Actions workflow that builds the APK

All other code remains as in the original project.
