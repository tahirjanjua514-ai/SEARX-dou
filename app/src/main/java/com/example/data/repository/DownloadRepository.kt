package com.example.data.repository

import com.example.data.local.DownloadDao
import com.example.data.local.DownloadEntity
import com.example.data.model.DownloadStatus
import kotlinx.coroutines.flow.Flow
import java.io.File

class DownloadRepository(private val downloadDao: DownloadDao) {

    val allDownloads: Flow<List<DownloadEntity>> = downloadDao.getAllDownloads()
    val activeDownloads: Flow<List<DownloadEntity>> = downloadDao.getActiveDownloads()
    val queuedDownloads: Flow<List<DownloadEntity>> = downloadDao.getQueuedDownloads()
    val completedDownloads: Flow<List<DownloadEntity>> = downloadDao.getCompletedDownloads()
    val totalCompletedBytes: Flow<Long?> = downloadDao.getTotalCompletedBytes()

    suspend fun insert(download: DownloadEntity): Long = downloadDao.insert(download)

    suspend fun update(download: DownloadEntity) = downloadDao.update(download)

    suspend fun getDownloadById(id: Long): DownloadEntity? = downloadDao.getDownloadById(id)

    suspend fun delete(download: DownloadEntity) {
        // Also remove local file if it exists
        if (download.filePath.isNotEmpty()) {
            val file = File(download.filePath)
            if (file.exists()) file.delete()
        }
        downloadDao.delete(download)
    }

    suspend fun deleteById(id: Long) {
        val download = downloadDao.getDownloadById(id)
        if (download != null && download.filePath.isNotEmpty()) {
            val file = File(download.filePath)
            if (file.exists()) file.delete()
        }
        downloadDao.deleteById(id)
    }

    suspend fun updateProgress(id: Long, downloadedBytes: Long, fileSize: Long, speedBps: Long, status: String) {
        downloadDao.updateProgress(id, downloadedBytes, fileSize, speedBps, status)
    }

    suspend fun updateStatus(id: Long, status: DownloadStatus) {
        downloadDao.updateStatus(id, status.name)
    }

    suspend fun markCompleted(id: Long, finalPath: String, fileSize: Long) {
        val existing = downloadDao.getDownloadById(id)
        if (existing != null) {
            downloadDao.update(
                existing.copy(
                    status = DownloadStatus.COMPLETED.name,
                    filePath = finalPath,
                    fileSize = fileSize,
                    downloadedBytes = fileSize,
                    completedAt = System.currentTimeMillis(),
                    speedBps = 0L,
                    errorMessage = null
                )
            )
        }
    }

    suspend fun markFailed(id: Long, error: String) {
        downloadDao.markFailed(id, error)
    }

    suspend fun pauseAll() {
        downloadDao.updateAllStatus(DownloadStatus.DOWNLOADING.name, DownloadStatus.PAUSED.name)
    }

    suspend fun resumeAll() {
        downloadDao.updateAllStatus(DownloadStatus.PAUSED.name, DownloadStatus.QUEUED.name)
    }

    suspend fun cancelAll() {
        downloadDao.updateAllStatus(DownloadStatus.DOWNLOADING.name, DownloadStatus.CANCELLED.name)
        downloadDao.updateAllStatus(DownloadStatus.QUEUED.name, DownloadStatus.CANCELLED.name)
    }

    suspend fun clearCompleted() {
        downloadDao.clearCompleted()
    }

    suspend fun clearQueued() {
        downloadDao.clearQueued()
    }

    suspend fun countDownloading(): Int = downloadDao.countDownloading()

    suspend fun getNextQueued(limit: Int): List<DownloadEntity> = downloadDao.getNextQueued(limit)
}
