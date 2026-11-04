package com.example.data.model

enum class PlatformType(val displayName: String) {
    YOUTUBE("YouTube"),
    FACEBOOK("Facebook"),
    INSTAGRAM("Instagram"),
    TIKTOK("TikTok"),
    GENERIC("Web Video")
}

enum class MediaType {
    VIDEO,
    AUDIO
}

enum class DownloadFormat(val extension: String, val mimeType: String, val isAudioOnly: Boolean) {
    MP4("mp4", "video/mp4", false),
    WEBM("webm", "video/webm", false),
    MP3("mp3", "audio/mpeg", true),
    M4A("m4a", "audio/mp4", true),
    WAV("wav", "audio/wav", true),
    OPUS("opus", "audio/opus", true)
}

enum class DownloadQuality(val label: String, val heightPixels: Int) {
    Q_360P("360p", 360),
    Q_480P("480p", 480),
    Q_720P("720p HD", 720),
    Q_1080P("1080p FHD", 1080),
    Q_1440P("1440p 2K", 1440),
    Q_2160P("2160p 4K", 2160),
    BEST("Best Available", 1080)
}

enum class AudioBitrate(val label: String, val kbps: Int) {
    B_128K("128 kbps", 128),
    B_192K("192 kbps", 192),
    B_256K("256 kbps", 256),
    B_320K("320 kbps", 320)
}

enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class MediaAnalysisResult(
    val url: String,
    val title: String,
    val author: String = "",
    val durationSeconds: Long = 0,
    val platform: PlatformType,
    val thumbnailUrl: String,
    val directMediaUrl: String,
    val availableResolutions: List<DownloadQuality> = listOf(
        DownloadQuality.Q_360P,
        DownloadQuality.Q_480P,
        DownloadQuality.Q_720P,
        DownloadQuality.Q_1080P,
        DownloadQuality.BEST
    ),
    val availableFormats: List<DownloadFormat> = listOf(
        DownloadFormat.MP4,
        DownloadFormat.WEBM,
        DownloadFormat.MP3,
        DownloadFormat.M4A,
        DownloadFormat.WAV,
        DownloadFormat.OPUS
    ),
    val audioAvailable: Boolean = true,
    val estimatedSizeBytes: Long = 18_500_000L
)

data class DownloadStatistics(
    val totalCompleted: Int = 0,
    val totalActive: Int = 0,
    val totalQueued: Int = 0,
    val totalBytesDownloaded: Long = 0L,
    val currentSpeedBps: Long = 0L
)
