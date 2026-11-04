package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val title: String,
    val thumbnailUrl: String,
    val platform: String,
    val mediaType: String,
    val format: String,
    val quality: String,
    val filePath: String,
    val fileSize: Long,
    val downloadedBytes: Long = 0L,
    val status: String,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val speedBps: Long = 0L
) {
    val progressPercentage: Int
        get() = if (fileSize > 0) ((downloadedBytes * 100) / fileSize).toInt().coerceIn(0, 100) else 0

    val remainingSeconds: Long
        get() {
            if (speedBps <= 0 || fileSize <= downloadedBytes) return 0L
            val remainingBytes = fileSize - downloadedBytes
            return (remainingBytes / speedBps).coerceAtLeast(0L)
        }
}
