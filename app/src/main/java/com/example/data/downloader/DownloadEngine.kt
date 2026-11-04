package com.example.data.downloader

import android.content.Context
import android.os.Environment
import com.example.data.local.DownloadEntity
import com.example.data.model.DownloadFormat
import com.example.data.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class DownloadEngine(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val activeJobTokens = ConcurrentHashMap<Long, Boolean>()

    fun cancel(downloadId: Long) {
        activeJobTokens[downloadId] = false
    }

    fun isCancelled(downloadId: Long): Boolean {
        return activeJobTokens[downloadId] == false
    }

    fun getDownloadDirectory(): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: File(context.filesDir, "downloads")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    suspend fun executeDownload(
        download: DownloadEntity,
        directUrl: String,
        onProgress: suspend (downloaded: Long, total: Long, speedBps: Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        activeJobTokens[download.id] = true

        val downloadDir = getDownloadDirectory()
        val safeName = sanitizeFilename("${download.title.take(50)}_${download.id}.${download.format.lowercase()}")
        val targetFile = File(downloadDir, safeName)

        val existingBytes = if (targetFile.exists()) targetFile.length() else 0L

        try {
            val requestBuilder = Request.Builder()
                .url(directUrl)
                .header("User-Agent", "Mozilla/5.0 (Android; Mobile; SEARX AI Downloader)")

            if (existingBytes > 0) {
                requestBuilder.header("Range", "bytes=$existingBytes-")
            }

            val response = client.newCall(requestBuilder.build()).execute()
            if (!response.isSuccessful && response.code != 206) {
                // If range request was not acceptable (416), delete file and start over
                if (response.code == 416) {
                    targetFile.delete()
                    return@withContext executeDownload(download.copy(downloadedBytes = 0), directUrl, onProgress)
                }
                return@withContext Result.failure(Exception("HTTP Error ${response.code}: ${response.message}"))
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Empty response body"))
            val contentLength = body.contentLength()
            val totalBytes = if (contentLength > 0) {
                if (response.code == 206) existingBytes + contentLength else contentLength
            } else {
                if (download.fileSize > 0) download.fileSize else 20_000_000L
            }

            val randomAccessFile = RandomAccessFile(targetFile, "rw")
            if (response.code == 206) {
                randomAccessFile.seek(existingBytes)
            } else {
                randomAccessFile.setLength(0)
                randomAccessFile.seek(0)
            }

            val buffer = ByteArray(16 * 1024)
            var currentDownloaded = if (response.code == 206) existingBytes else 0L

            var lastReportTime = System.currentTimeMillis()
            var bytesSinceLastReport = 0L
            var currentSpeedBps = 0L

            val inputStream = body.byteStream()
            inputStream.use { input ->
                randomAccessFile.use { output ->
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        if (activeJobTokens[download.id] == false) {
                            // Paused or cancelled
                            return@withContext Result.failure(InterruptedException("Download paused or cancelled"))
                        }

                        output.write(buffer, 0, bytesRead)
                        currentDownloaded += bytesRead
                        bytesSinceLastReport += bytesRead

                        val now = System.currentTimeMillis()
                        val elapsed = now - lastReportTime
                        if (elapsed >= 300) { // report every ~300ms
                            currentSpeedBps = (bytesSinceLastReport * 1000) / elapsed
                            onProgress(currentDownloaded, totalBytes, currentSpeedBps)
                            lastReportTime = now
                            bytesSinceLastReport = 0L
                        }
                    }
                }
            }

            // Final progress update
            onProgress(currentDownloaded, totalBytes, 0L)

            if (!targetFile.exists() || targetFile.length() == 0L) {
                return@withContext Result.failure(Exception("File was not saved correctly."))
            }

            Result.success(targetFile)
        } catch (e: InterruptedException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            activeJobTokens.remove(download.id)
        }
    }

    private fun sanitizeFilename(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|\\s+]"), "_")
    }
}
