# MS Music

A glassmorphism-styled Android music player by **MSGames**, with synced lyrics, offline playback and a floating glass navigation bar.

## Download

Get the latest APK from the [Releases](../../releases) page (pick the newest `test-…` release and download `MS-Music-test.apk`). It is an optimized arm64 build for modern phones.

## Credits

**MS Music is a modified fork of [Echo Music](https://github.com/EchoMusicApp/Echo-Music) by Aditya (iad1tya).** Echo Music is itself built on top of [SimpMusic](https://github.com/maxrave-dev/SimpMusic). Full credit for the original app, architecture and most of the code goes to them.

MS Music also stands on the shoulders of these projects:

| Project | What it contributed |
| :-- | :-- |
| [Echo Music](https://github.com/EchoMusicApp/Echo-Music) | The original app this fork is based on (GPL-3.0) |
| [SimpMusic](https://github.com/maxrave-dev/SimpMusic) | Foundation of Echo Music; lyrics implementation reference |
| [Metrolist](https://github.com/MetrolistGroup/Metrolist) & [Vivi Music](https://github.com/vivizzz007/vivi-music) | Foundational inspiration and architecture reference |
| [ArchiveTune](https://github.com/koiverse/ArchiveTune) | Material You UI inspiration |
| [Better Lyrics](https://better-lyrics.boidu.dev/) | Lyrics enhancement and synchronization |
| [Music Recognizer](https://github.com/aleksey-saenko/MusicRecognizer) | Audio recognition |
| [BravePipe](https://github.com/bravepipeproject/BravePipe) | Decryption handling and backup playback engine |

A credit row linking to Echo Music is also in the app under **Settings → About**.

## License and source code

MS Music is free software under the **GNU General Public License v3.0**, the same license as Echo Music. The complete modified source code is on the [`source`](../../tree/source) branch. [`CHANGES.md`](../../blob/source/CHANGES.md) there lists everything that was changed from Echo Music.

## How the build works

This repository holds the MS Music changes (`MS-Music-overlay.zip`) and a GitHub Actions workflow. When you run **Actions → Build MS Music APK**, it downloads Echo Music at a pinned commit, applies the changes, publishes the full source to the `source` branch, builds the APK and attaches it to a new release.

## Disclaimer

MS Music is an unofficial, independent project. It is not affiliated with, endorsed by, or sponsored by YouTube, Google or any music service, and it hosts no audio or video content. The software is provided "as is", without warranty of any kind.
