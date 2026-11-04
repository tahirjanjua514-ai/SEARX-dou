package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.DownloadEntity
import com.example.data.model.PlatformType
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.AnalysisDialog
import com.example.ui.components.DownloadCard
import com.example.ui.components.getPlatformColor
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.PlatformFacebook
import com.example.ui.theme.PlatformInstagram
import com.example.ui.theme.PlatformTikTok
import com.example.ui.theme.PlatformYouTube
import com.example.ui.theme.SearxBrandGradient
import com.example.ui.theme.SearxCyan
import com.example.ui.theme.SearxIndigo
import com.example.ui.theme.SearxViolet
import com.example.ui.theme.StatusError

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val urlInput by viewModel.urlInput.collectAsStateWithLifecycle()
    val detectedPlatform by viewModel.detectedPlatform.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val analysisResult by viewModel.analysisResult.collectAsStateWithLifecycle()
    val analysisError by viewModel.analysisError.collectAsStateWithLifecycle()

    val activeDownloads by viewModel.activeDownloads.collectAsStateWithLifecycle()
    val queuedDownloads by viewModel.queuedDownloads.collectAsStateWithLifecycle()
    val completedDownloads by viewModel.completedDownloads.collectAsStateWithLifecycle()
    val totalBytes by viewModel.totalCompletedBytes.collectAsStateWithLifecycle()

    val selectedMediaType by viewModel.selectedMediaType.collectAsStateWithLifecycle()
    val selectedVideoQuality by viewModel.selectedVideoQuality.collectAsStateWithLifecycle()
    val selectedAudioBitrate by viewModel.selectedAudioBitrate.collectAsStateWithLifecycle()
    val selectedFormat by viewModel.selectedFormat.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Hero Branding Header
        item {
            HeroHeader()
        }

        // URL Input & Platform Auto-Detection Card
        item {
            UrlInputCard(
                url = urlInput,
                detectedPlatform = detectedPlatform,
                isAnalyzing = isAnalyzing,
                errorMessage = analysisError,
                onUrlChanged = { viewModel.updateUrlInput(it) },
                onPasteClicked = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                    val clip = clipboard?.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                        val text = clip.getItemAt(0).text?.toString() ?: ""
                        viewModel.pasteUrl(text)
                    }
                },
                onClearClicked = { viewModel.updateUrlInput("") },
                onAnalyzeClicked = { viewModel.analyzeUrl() }
            )
        }

        // Supported Platforms Strip
        item {
            SupportedPlatformsRow()
        }

        // Quick Preset Chips for Easy Testing
        item {
            QuickPresetsRow(
                onPresetSelected = { presetUrl ->
                    viewModel.updateUrlInput(presetUrl)
                    viewModel.analyzeUrl()
                }
            )
        }

        // Statistics Overview
        item {
            StatisticsGrid(
                activeCount = activeDownloads.size,
                queueCount = queuedDownloads.size,
                completedCount = completedDownloads.size,
                totalBytes = totalBytes ?: 0L,
                formatBytes = { viewModel.formatBytes(it) }
            )
        }

        // Active Downloads Live Section (if any)
        if (activeDownloads.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Downloads (${activeDownloads.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelMedium,
                        color = SearxCyan,
                        modifier = Modifier.clickable { viewModel.selectTab(AppTab.DOWNLOADS) }
                    )
                }
            }

            items(activeDownloads.take(2).size) { index ->
                val download = activeDownloads[index]
                DownloadCard(
                    download = download,
                    formatBytes = { viewModel.formatBytes(it) },
                    formatSpeed = { viewModel.formatSpeed(it) },
                    formatRemaining = { viewModel.formatRemaining(it) },
                    onPause = { viewModel.pauseDownload(download.id) },
                    onResume = { viewModel.resumeDownload(download.id) },
                    onCancel = { viewModel.cancelDownload(download.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Modal Analysis Dialog
    analysisResult?.let { media ->
        AnalysisDialog(
            media = media,
            selectedMediaType = selectedMediaType,
            selectedVideoQuality = selectedVideoQuality,
            selectedAudioBitrate = selectedAudioBitrate,
            selectedFormat = selectedFormat,
            formatBytes = { viewModel.formatBytes(it) },
            onMediaTypeChanged = { viewModel.setSelectedMediaType(it) },
            onVideoQualityChanged = { viewModel.setSelectedVideoQuality(it) },
            onAudioBitrateChanged = { viewModel.setSelectedAudioBitrate(it) },
            onFormatChanged = { viewModel.setSelectedFormat(it) },
            onDownloadNow = { viewModel.startDownload(asQueued = false) },
            onAddToQueue = { viewModel.startDownload(asQueued = true) },
            onDismiss = { viewModel.dismissAnalysis() }
        )
    }
}

@Composable
fun HeroHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        SearxIndigo.copy(alpha = 0.25f),
                        SearxViolet.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                )
            )
            .border(1.dp, SearxIndigo.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SearxBrandGradient)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "SEARX AI",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = "MEDIA ARCHITECT",
                color = SearxCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = "All-in-One Media Downloader",
            style = MaterialTheme.typography.headlineSmall,
            color = DarkTextPrimary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Download high-speed video and audio from YouTube, TikTok, Instagram, and Facebook in up to 4K resolution.",
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextSecondary
        )
    }
}

@Composable
fun UrlInputCard(
    url: String,
    detectedPlatform: PlatformType?,
    isAnalyzing: Boolean,
    errorMessage: String?,
    onUrlChanged: (String) -> Unit,
    onPasteClicked: () -> Unit,
    onClearClicked: () -> Unit,
    onAnalyzeClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("url_input_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Paste Media Link",
                style = MaterialTheme.typography.titleSmall,
                color = DarkTextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            // Input field
            OutlinedTextField(
                value = url,
                onValueChange = onUrlChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("url_text_input"),
                placeholder = {
                    Text("https://www.youtube.com/watch?v=...", color = DarkTextSecondary)
                },
                leadingIcon = {
                    Icon(Icons.Filled.Link, contentDescription = null, tint = SearxCyan)
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        if (url.isNotEmpty()) {
                            IconButton(onClick = onClearClicked) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = DarkTextSecondary)
                            }
                        }
                        IconButton(
                            onClick = onPasteClicked,
                            modifier = Modifier.testTag("paste_button")
                        ) {
                            Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", tint = SearxIndigo)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SearxCyan,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = DarkTextPrimary,
                    unfocusedTextColor = DarkTextPrimary,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                )
            )

            // Platform Detection Chip (Animated)
            AnimatedVisibility(visible = detectedPlatform != null) {
                detectedPlatform?.let { platform ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(getPlatformColor(platform.name))
                        )
                        Text(
                            text = "Detected Platform: ${platform.displayName}",
                            color = DarkTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Error display
            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = StatusError,
                    fontSize = 12.sp
                )
            }

            // Analyze Button
            Button(
                onClick = onAnalyzeClicked,
                enabled = url.isNotBlank() && !isAnalyzing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("analyze_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SearxIndigo,
                    disabledContainerColor = DarkBorder
                )
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Analyzing Streams...", color = Color.White, fontWeight = FontWeight.SemiBold)
                } else {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze & Download", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SupportedPlatformsRow() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Supported Platforms",
            style = MaterialTheme.typography.labelMedium,
            color = DarkTextSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val platforms = listOf(
                Pair("YouTube", PlatformYouTube),
                Pair("TikTok", PlatformTikTok),
                Pair("Instagram", PlatformInstagram),
                Pair("Facebook", PlatformFacebook)
            )

            platforms.forEach { (name, color) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Text(
                            text = name,
                            color = DarkTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickPresetsRow(onPresetSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Quick Test Presets (Instant Analysis)",
            style = MaterialTheme.typography.labelMedium,
            color = DarkTextSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SearxIndigo.copy(alpha = 0.2f))
                    .border(1.dp, SearxIndigo.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable { onPresetSelected("https://www.youtube.com/watch?v=aqz-KE-bpKQ") }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("▶ YouTube 4K", color = SearxCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PlatformTikTok.copy(alpha = 0.15f))
                    .border(1.dp, PlatformTikTok.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .clickable { onPresetSelected("https://www.tiktok.com/@creator/video/7234567890") }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("🎵 TikTok Reel", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PlatformInstagram.copy(alpha = 0.15f))
                    .border(1.dp, PlatformInstagram.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .clickable { onPresetSelected("https://www.instagram.com/reel/Cx123456789/") }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("📷 IG Reel", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatisticsGrid(
    activeCount: Int,
    queueCount: Int,
    completedCount: Int,
    totalBytes: Long,
    formatBytes: (Long) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Download Engine Statistics",
            style = MaterialTheme.typography.labelMedium,
            color = DarkTextSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Active",
                value = "$activeCount",
                accentColor = SearxCyan,
                icon = Icons.Filled.Download
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "In Queue",
                value = "$queueCount",
                accentColor = SearxIndigo,
                icon = Icons.Filled.FormatListNumbered
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Completed",
                value = "$completedCount",
                accentColor = Color(0xFF10B981),
                icon = Icons.Filled.TaskAlt
            )
            StatCard(
                modifier = Modifier.weight(1.2f),
                title = "Total Saved",
                value = formatBytes(totalBytes),
                accentColor = SearxViolet,
                icon = Icons.Filled.Save
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = DarkTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}
