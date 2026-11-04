# SEARX AI – All-in-One Media Downloader

**SEARX AI** is a professional, high-performance Android media downloading and analysis system built with Kotlin and Jetpack Compose.

## Key Features

1. **Intelligent URL Analyzer & Platform Detection**
   - Instant auto-detection for YouTube, TikTok, Instagram, Facebook, and generic web streams.
   - Live stream metadata extraction: high-resolution thumbnails, stream titles, authors, and duration.
   - Comprehensive resolution selector: 360p, 480p, 720p HD, 1080p FHD, 1440p 2K, 2160p 4K, and Best Available.
   - Audio extraction mode: MP3, M4A, WAV, and OPUS across 128 kbps, 192 kbps, 256 kbps, and 320 kbps bitrates.
   - Real-time estimated file size calculation.

2. **Advanced Multi-Download Engine**
   - OkHttp chunked streaming engine with HTTP Range resumption (`Range: bytes=OFFSET-`).
   - Unlimited queued downloads with configurable concurrency limit (1 to 10 tasks, default 3).
   - Real-time download speed calculation (MB/s or KB/s).
   - Remaining time estimates and animated progress bar.
   - Verified file storage on Android native storage with FileProvider integration.

3. **Background Downloading & Foreground Service**
   - Persistent Android notification with live percentage and speed.
   - Completion notifications.
   - Background execution compliant with Android 14+ `dataSync` service type.

4. **Persistent Local History (Room Database)**
   - All tasks (active, queued, paused, completed) persist across device reboots and app restarts.
   - One-tap Play/Open with system media player.
   - System Share Sheet integration.
   - Local file deletion with automatic storage cleanup.

5. **Modern Dark UI Design**
   - Blue and violet gradient accents (`#6366F1`, `#38BDF8`, `#8B5CF6`).
   - Clean Material 3 cards, rounded borders, and responsive layouts.
   - 5-tab bottom navigation: Home, Downloads, Queue, History, Settings.

## Building and Installing the APK

### Debug Build
```bash
gradle assembleDebug
```
The APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### Unit Tests
```bash
gradle testDebugUnitTest
```

### Installation via ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
