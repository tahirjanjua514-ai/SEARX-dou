package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.SearxApplication
import com.example.data.analyzer.MediaAnalyzerEngine
import com.example.data.local.DownloadEntity
import com.example.data.model.AudioBitrate
import com.example.data.model.DownloadFormat
import com.example.data.model.DownloadQuality
import com.example.data.model.DownloadStatus
import com.example.data.model.MediaAnalysisResult
import com.example.data.model.MediaType
import com.example.data.model.PlatformType
import com.example.service.DownloadForegroundService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

enum class AppTab(val title: String) {
    HOME("Home"),
    DOWNLOADS("Downloads"),
    QUEUE("Queue"),
    HISTORY("History"),
    SETTINGS("Settings")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as SearxApplication
    private val repository = app.repository
    private val downloadEngine = app.downloadEngine

    // UI Tab State
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // URL & Analyzer State
    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()

    private val _detectedPlatform = MutableStateFlow<PlatformType?>(null)
    val detectedPlatform: StateFlow<PlatformType?> = _detectedPlatform.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisResult = MutableStateFlow<MediaAnalysisResult?>(null)
    val analysisResult: StateFlow<MediaAnalysisResult?> = _analysisResult.asStateFlow()

    private val _analysisError = MutableStateFlow<String?>(null)
    val analysisError: StateFlow<String?> = _analysisError.asStateFlow()

    // Analysis Modal/Sheet Selection State
    private val _selectedMediaType = MutableStateFlow(MediaType.VIDEO)
    val selectedMediaType: StateFlow<MediaType> = _selectedMediaType.asStateFlow()

    private val _selectedVideoQuality = MutableStateFlow(DownloadQuality.Q_1080P)
    val selectedVideoQuality: StateFlow<DownloadQuality> = _selectedVideoQuality.asStateFlow()

    private val _selectedAudioBitrate = MutableStateFlow(AudioBitrate.B_320K)
    val selectedAudioBitrate: StateFlow<AudioBitrate> = _selectedAudioBitrate.asStateFlow()

    private val _selectedFormat = MutableStateFlow(DownloadFormat.MP4)
    val selectedFormat: StateFlow<DownloadFormat> = _selectedFormat.asStateFlow()

    // Database Flows
    val allDownloads = repository.allDownloads.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeDownloads = repository.activeDownloads.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val queuedDownloads = repository.queuedDownloads.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val completedDownloads = repository.completedDownloads.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalCompletedBytes = repository.totalCompletedBytes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    // Settings State
    private val _maxConcurrentDownloads = MutableStateFlow(3)
    val maxConcurrentDownloads: StateFlow<Int> = _maxConcurrentDownloads.asStateFlow()

    private val _downloadOnWifiOnly = MutableStateFlow(false)
    val downloadOnWifiOnly: StateFlow<Boolean> = _downloadOnWifiOnly.asStateFlow()

    private val _showNotifications = MutableStateFlow(true)
    val showNotifications: StateFlow<Boolean> = _showNotifications.asStateFlow()

    private val _autoRetryFailed = MutableStateFlow(true)
    val autoRetryFailed: StateFlow<Boolean> = _autoRetryFailed.asStateFlow()

    private val _themeMode = MutableStateFlow("DARK")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // Active Running Coroutines tracking
    private val activeDownloadJobs = ConcurrentHashMap<Long, Job>()

    init {
        // Kick off queue check whenever app starts
        viewModelScope.launch {
            processQueue()
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun updateUrlInput(text: String) {
        _urlInput.value = text
        if (text.isNotBlank() && MediaAnalyzerEngine.isValidUrl(text)) {
            _detectedPlatform.value = MediaAnalyzerEngine.detectPlatform(text)
        } else {
            _detectedPlatform.value = null
        }
        _analysisError.value = null
    }

    fun pasteUrl(pastedText: String) {
        updateUrlInput(pastedText)
        if (pastedText.isNotBlank()) {
            analyzeUrl()
        }
    }

    fun analyzeUrl() {
        val url = _urlInput.value.trim()
        if (!MediaAnalyzerEngine.isValidUrl(url)) {
            _analysisError.value = "Please enter a valid HTTP/HTTPS media URL."
            return
        }

        viewModelScope.launch {
            _isAnalyzing.value = true
            _analysisError.value = null
            _analysisResult.value = null

            val result = MediaAnalyzerEngine.analyzeMedia(url)
            result.onSuccess { media ->
                _analysisResult.value = media
                _detectedPlatform.value = media.platform
                _selectedMediaType.value = MediaType.VIDEO
                _selectedFormat.value = DownloadFormat.MP4
                _selectedVideoQuality.value = media.availableResolutions.firstOrNull { it == DownloadQuality.Q_1080P }
                    ?: media.availableResolutions.firstOrNull() ?: DownloadQuality.BEST
            }.onFailure { err ->
                _analysisError.value = err.message ?: "Failed to analyze media link. Please check network and URL."
            }
            _isAnalyzing.value = false
        }
    }

    fun dismissAnalysis() {
        _analysisResult.value = null
        _analysisError.value = null
    }

    fun setSelectedMediaType(type: MediaType) {
        _selectedMediaType.value = type
        if (type == MediaType.AUDIO) {
            _selectedFormat.value = DownloadFormat.MP3
        } else {
            _selectedFormat.value = DownloadFormat.MP4
        }
    }

    fun setSelectedVideoQuality(quality: DownloadQuality) {
        _selectedVideoQuality.value = quality
    }

    fun setSelectedAudioBitrate(bitrate: AudioBitrate) {
        _selectedAudioBitrate.value = bitrate
    }

    fun setSelectedFormat(format: DownloadFormat) {
        _selectedFormat.value = format
    }

    fun startDownload(asQueued: Boolean = false) {
        val media = _analysisResult.value ?: return
        val isAudio = _selectedMediaType.value == MediaType.AUDIO
        val format = _selectedFormat.value
        val quality = if (isAudio) _selectedAudioBitrate.value.label else _selectedVideoQuality.value.label

        // Compute estimated size based on quality & duration
        val computedSize = if (isAudio) {
            val kbps = _selectedAudioBitrate.value.kbps
            (media.durationSeconds * kbps * 125L).coerceAtLeast(3_500_000L)
        } else {
            val h = _selectedVideoQuality.value.heightPixels
            val factor = (h / 720.0).coerceAtLeast(0.5)
            (media.durationSeconds * 200_000L * factor).toLong().coerceAtLeast(12_000_000L)
        }

        viewModelScope.launch {
            val entity = DownloadEntity(
                url = media.directMediaUrl,
                title = media.title,
                thumbnailUrl = media.thumbnailUrl,
                platform = media.platform.displayName,
                mediaType = _selectedMediaType.value.name,
                format = format.extension.uppercase(),
                quality = quality,
                filePath = "",
                fileSize = computedSize,
                downloadedBytes = 0L,
                status = DownloadStatus.QUEUED.name
            )

            val newId = repository.insert(entity)
            dismissAnalysis()
            _urlInput.value = ""
            _detectedPlatform.value = null

            // Navigate to Downloads tab if started directly, or stay in queue
            if (!asQueued) {
                _currentTab.value = AppTab.DOWNLOADS
            } else {
                _currentTab.value = AppTab.QUEUE
            }

            processQueue()
        }
    }

    fun processQueue() {
        viewModelScope.launch(Dispatchers.IO) {
            val runningCount = activeDownloadJobs.size
            val slotsAvailable = (_maxConcurrentDownloads.value - runningCount).coerceAtLeast(0)
            if (slotsAvailable <= 0) return@launch

            val nextItems = repository.getNextQueued(slotsAvailable)
            for (item in nextItems) {
                if (!activeDownloadJobs.containsKey(item.id)) {
                    startSingleDownload(item.id)
                }
            }
        }
    }

    private fun startSingleDownload(downloadId: Long) {
        val job = viewModelScope.launch(Dispatchers.IO) {
            val entity = repository.getDownloadById(downloadId) ?: return@launch
            repository.updateStatus(downloadId, DownloadStatus.DOWNLOADING)

            if (_showNotifications.value) {
                DownloadForegroundService.startService(
                    app,
                    downloadId,
                    "${entity.title} (${entity.quality})"
                )
            }

            val result = downloadEngine.executeDownload(
                download = entity,
                directUrl = entity.url,
                onProgress = { downloaded, total, speedBps ->
                    val progressPercent = if (total > 0) ((downloaded * 100) / total).toInt() else 0
                    repository.updateProgress(
                        id = downloadId,
                        downloadedBytes = downloaded,
                        fileSize = total,
                        speedBps = speedBps,
                        status = DownloadStatus.DOWNLOADING.name
                    )

                    if (_showNotifications.value) {
                        val speedStr = formatSpeed(speedBps)
                        DownloadForegroundService.updateProgress(
                            app,
                            downloadId,
                            entity.title,
                            progressPercent,
                            speedStr
                        )
                    }
                }
            )

            activeDownloadJobs.remove(downloadId)

            result.onSuccess { savedFile ->
                repository.markCompleted(downloadId, savedFile.absolutePath, savedFile.length())
                if (_showNotifications.value) {
                    DownloadForegroundService.showCompletionNotification(
                        app,
                        downloadId,
                        "${entity.title} downloaded successfully"
                    )
                }
            }.onFailure { error ->
                if (downloadEngine.isCancelled(downloadId)) {
                    // paused or cancelled
                } else {
                    repository.markFailed(downloadId, error.message ?: "Download failed.")
                }
            }

            // If no more active downloads, stop foreground notification service
            if (activeDownloadJobs.isEmpty()) {
                DownloadForegroundService.stopService(app)
            }

            // Chain to next queued task
            processQueue()
        }

        activeDownloadJobs[downloadId] = job
    }

    fun pauseDownload(id: Long) {
        downloadEngine.cancel(id)
        activeDownloadJobs[id]?.cancel()
        activeDownloadJobs.remove(id)
        viewModelScope.launch {
            repository.updateStatus(id, DownloadStatus.PAUSED)
            if (activeDownloadJobs.isEmpty()) {
                DownloadForegroundService.stopService(app)
            }
            processQueue()
        }
    }

    fun resumeDownload(id: Long) {
        viewModelScope.launch {
            repository.updateStatus(id, DownloadStatus.QUEUED)
            processQueue()
        }
    }

    fun cancelDownload(id: Long) {
        downloadEngine.cancel(id)
        activeDownloadJobs[id]?.cancel()
        activeDownloadJobs.remove(id)
        viewModelScope.launch {
            repository.deleteById(id)
            if (activeDownloadJobs.isEmpty()) {
                DownloadForegroundService.stopService(app)
            }
            processQueue()
        }
    }

    fun retryDownload(id: Long) {
        viewModelScope.launch {
            val item = repository.getDownloadById(id) ?: return@launch
            repository.update(item.copy(status = DownloadStatus.QUEUED.name, downloadedBytes = 0, errorMessage = null))
            processQueue()
        }
    }

    fun pauseAll() {
        for ((id, job) in activeDownloadJobs) {
            downloadEngine.cancel(id)
            job.cancel()
        }
        activeDownloadJobs.clear()
        viewModelScope.launch {
            repository.pauseAll()
            DownloadForegroundService.stopService(app)
        }
    }

    fun resumeAll() {
        viewModelScope.launch {
            repository.resumeAll()
            processQueue()
        }
    }

    fun cancelAll() {
        for ((id, job) in activeDownloadJobs) {
            downloadEngine.cancel(id)
            job.cancel()
        }
        activeDownloadJobs.clear()
        viewModelScope.launch {
            repository.cancelAll()
            DownloadForegroundService.stopService(app)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearCompleted()
        }
    }

    fun clearQueue() {
        viewModelScope.launch {
            repository.clearQueued()
        }
    }

    fun deleteItem(download: DownloadEntity) {
        if (activeDownloadJobs.containsKey(download.id)) {
            cancelDownload(download.id)
        } else {
            viewModelScope.launch {
                repository.delete(download)
            }
        }
    }

    // Settings modifiers
    fun setMaxConcurrentDownloads(count: Int) {
        _maxConcurrentDownloads.value = count.coerceIn(1, 10)
        processQueue()
    }

    fun setDownloadOnWifiOnly(enabled: Boolean) {
        _downloadOnWifiOnly.value = enabled
    }

    fun setShowNotifications(enabled: Boolean) {
        _showNotifications.value = enabled
    }

    fun setAutoRetryFailed(enabled: Boolean) {
        _autoRetryFailed.value = enabled
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    // Formatting utilities
    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format("%.2f GB", gb)
            mb >= 1.0 -> String.format("%.1f MB", mb)
            kb >= 1.0 -> String.format("%.0f KB", kb)
            else -> "$bytes B"
        }
    }

    fun formatSpeed(speedBps: Long): String {
        if (speedBps <= 0) return "0.0 MB/s"
        val mbps = speedBps / (1024.0 * 1024.0)
        return if (mbps >= 1.0) {
            String.format("%.1f MB/s", mbps)
        } else {
            val kbps = speedBps / 1024.0
            String.format("%.0f KB/s", kbps)
        }
    }

    fun formatRemaining(seconds: Long): String {
        if (seconds <= 0) return "00:00"
        val m = seconds / 60
        val s = seconds % 60
        return String.format("%02d:%02d", m, s)
    }
}
