# MS Music

A glassmorphism-styled Android music player by **MSGames**, with synced lyrics, offline playback and a floating glass navigation bar.

> **MS Music is a modified fork of [Echo Music](https://github.com/EchoMusicApp/Echo-Music) by Aditya (iad1tya).**
> Echo Music is itself built on top of [SimpMusic](https://github.com/maxrave-dev/SimpMusic).
> Full credit for the original app, architecture and most of the code goes to them and to the other projects listed in [Special Thanks](#special-thanks).

## License

MS Music is free software, licensed under the **GNU General Public License v3.0** (the same license as Echo Music). See [LICENSE](LICENSE).
The original copyright notices are kept in the source files. You may use, study, modify and share this code under the terms of the GPL-3.0; any distributed modified version must also be released under GPL-3.0 with its source.

## What was changed from Echo Music

See [CHANGES.md](CHANGES.md) for the full list. In short:

- Renamed and rebranded to **MS Music** (new app name, application ID `com.msgames.msmusic`, new launcher icon)
- New default look: violet/cyan palette, liquid-glass surfaces on by default, floating glass navigation bar, colored glow behind the glass
- No first-launch welcome popup; a credit row linking to Echo Music lives in **Settings → About**
- In-app update checks point to this repository instead of upstream
- Removed upstream's Firebase configuration, funding links and maintainer-only CI workflows

## Building

### With GitHub Actions (no setup needed)

1. Push this repository to GitHub.
2. Open the **Actions** tab, choose **Build MS Music APK**, and click **Run workflow**.
3. When it finishes, download the APK from the run's **Artifacts** section.

By default this produces a *debug-signed* APK that you can install on your phone. For a properly signed release APK, add these repository secrets (Settings → Secrets and variables → Actions): `KEYSTORE_BASE64`, `KEY_ALIAS`, `KEY_PASSWORD`, `STORE_PASSWORD`.

### Locally

Requirements: Android Studio, Android SDK, JDK 21. See [SETUP.md](SETUP.md) for the full steps, then:

```bash
./gradlew assembleUniversalFossDebug
```

## Special Thanks

MS Music stands on the shoulders of several excellent open-source projects. Sincere thanks to:

| Project | Description |
| :-- | :-- |
| **[Echo Music](https://github.com/EchoMusicApp/Echo-Music)** by iad1tya | The original app this fork is based on (GPL-3.0) |
| **[SimpMusic](https://github.com/maxrave-dev/SimpMusic)** | Foundation of Echo Music; lyrics implementation reference |
| **[Metrolist](https://github.com/MetrolistGroup/Metrolist)** & **[Vivi Music](https://github.com/vivizzz007/vivi-music)** | Foundational inspiration and architecture reference |
| **[ArchiveTune](https://github.com/koiverse/ArchiveTune)** | Material You UI inspiration |
| **[Better Lyrics](https://better-lyrics.boidu.dev/)** | Lyrics enhancement and synchronization |
| **[Music Recognizer](https://github.com/aleksey-saenko/MusicRecognizer)** | Audio recognition (Echo Find) |
| **[BravePipe](https://github.com/bravepipeproject/BravePipe)** | Decryption handling and backup playback engine |

## Disclaimer

MS Music is an unofficial, independent project. It is not affiliated with, endorsed by, or sponsored by YouTube, Google, or any music service. It hosts no audio or video content. Users are responsible for how they use the app and should respect the terms of service of any service it connects to, and the rights of artists and creators. Consider supporting artists through official subscriptions.

The software is provided "as is", without warranty of any kind. See the [LICENSE](LICENSE) for details.
