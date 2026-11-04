package com.example

import com.example.data.analyzer.MediaAnalyzerEngine
import com.example.data.model.PlatformType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPlatformDetection() {
        assertEquals(
            PlatformType.YOUTUBE,
            MediaAnalyzerEngine.detectPlatform("https://www.youtube.com/watch?v=aqz-KE-bpKQ")
        )
        assertEquals(
            PlatformType.YOUTUBE,
            MediaAnalyzerEngine.detectPlatform("https://youtu.be/aqz-KE-bpKQ")
        )
        assertEquals(
            PlatformType.TIKTOK,
            MediaAnalyzerEngine.detectPlatform("https://www.tiktok.com/@creator/video/7234567890")
        )
        assertEquals(
            PlatformType.INSTAGRAM,
            MediaAnalyzerEngine.detectPlatform("https://www.instagram.com/reel/Cx123456789/")
        )
        assertEquals(
            PlatformType.FACEBOOK,
            MediaAnalyzerEngine.detectPlatform("https://www.facebook.com/watch/?v=123456789")
        )
        assertEquals(
            PlatformType.GENERIC,
            MediaAnalyzerEngine.detectPlatform("https://example.com/media/sample_video.mp4")
        )
    }

    @Test
    fun testUrlValidation() {
        assertTrue(MediaAnalyzerEngine.isValidUrl("https://youtube.com/watch?v=123"))
        assertFalse(MediaAnalyzerEngine.isValidUrl("ftp://invalid.com"))
        assertFalse(MediaAnalyzerEngine.isValidUrl("not-a-url"))
        assertFalse(MediaAnalyzerEngine.isValidUrl(""))
    }
}
