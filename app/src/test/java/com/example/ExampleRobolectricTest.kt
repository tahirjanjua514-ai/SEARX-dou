package com.example

import com.example.data.local.DownloadEntity
import com.example.data.model.DownloadStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun testDownloadEntityProgressCalculation() {
        val download = DownloadEntity(
            id = 1,
            url = "https://example.com/video.mp4",
            title = "Test Video",
            thumbnailUrl = "",
            platform = "YOUTUBE",
            mediaType = "VIDEO",
            format = "MP4",
            quality = "1080p",
            filePath = "/storage/video.mp4",
            fileSize = 100_000_000L,
            downloadedBytes = 45_000_000L,
            status = DownloadStatus.DOWNLOADING.name,
            speedBps = 6_800_000L
        )

        assertEquals(45, download.progressPercentage)
        // (100MB - 45MB) / 6.8MB/s = 55MB / 6.8MB/s = 8 seconds remaining
        assertTrue(download.remainingSeconds in 7L..9L)
    }

    @Test
    fun testCompletedDownloadStats() {
        val download = DownloadEntity(
            id = 2,
            url = "https://example.com/audio.mp3",
            title = "Test Audio",
            thumbnailUrl = "",
            platform = "TIKTOK",
            mediaType = "AUDIO",
            format = "MP3",
            quality = "320 kbps",
            filePath = "/storage/audio.mp3",
            fileSize = 8_000_000L,
            downloadedBytes = 8_000_000L,
            status = DownloadStatus.COMPLETED.name,
            speedBps = 0L
        )

        assertEquals(100, download.progressPercentage)
        assertEquals(0L, download.remainingSeconds)
    }
}
