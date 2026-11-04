package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.AudioBitrate
import com.example.data.model.DownloadFormat
import com.example.data.model.DownloadQuality
import com.example.data.model.MediaAnalysisResult
import com.example.data.model.MediaType
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.DarkTextTertiary
import com.example.ui.theme.SearxBrandGradient
import com.example.ui.theme.SearxCyan
import com.example.ui.theme.SearxIndigo
import com.example.ui.theme.SearxViolet

@Composable
fun AnalysisDialog(
    media: MediaAnalysisResult,
    selectedMediaType: MediaType,
    selectedVideoQuality: DownloadQuality,
    selectedAudioBitrate: AudioBitrate,
    selectedFormat: DownloadFormat,
    formatBytes: (Long) -> String,
    onMediaTypeChanged: (MediaType) -> Unit,
    onVideoQualityChanged: (DownloadQuality) -> Unit,
    onAudioBitrateChanged: (AudioBitrate) -> Unit,
    onFormatChanged: (DownloadFormat) -> Unit,
    onDownloadNow: () -> Unit,
    onAddToQueue: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("media_analysis_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SearxIndigo.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with title and close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(getPlatformColor(media.platform.name))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = media.platform.displayName,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Media Analyzed",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = DarkTextSecondary
                        )
                    }
                }

                // Thumbnail & Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                ) {
                    if (media.thumbnailUrl.isNotEmpty()) {
                        AsyncImage(
                            model = media.thumbnailUrl,
                            contentDescription = "Video Thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }

                    // Duration pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        val min = media.durationSeconds / 60
                        val sec = media.durationSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", min, sec),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Title & Details
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = media.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkTextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (media.author.isNotEmpty()) {
                        Text(
                            text = "By ${media.author}",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextSecondary
                        )
                    }
                }

                // Media Type Tabs: Video vs Audio
                TabRow(
                    selectedTabIndex = if (selectedMediaType == MediaType.VIDEO) 0 else 1,
                    containerColor = DarkSurfaceVariant,
                    indicator = { tabPositions ->
                        val index = if (selectedMediaType == MediaType.VIDEO) 0 else 1
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = SearxCyan
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedMediaType == MediaType.VIDEO,
                        onClick = { onMediaTypeChanged(MediaType.VIDEO) },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Video", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        selectedContentColor = SearxCyan,
                        unselectedContentColor = DarkTextSecondary
                    )

                    Tab(
                        selected = selectedMediaType == MediaType.AUDIO,
                        onClick = { onMediaTypeChanged(MediaType.AUDIO) },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.Audiotrack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Audio (Extract)", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        selectedContentColor = SearxCyan,
                        unselectedContentColor = DarkTextSecondary
                    )
                }

                // Quality Options Section
                if (selectedMediaType == MediaType.VIDEO) {
                    Text(
                        text = "Select Video Resolution:",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextSecondary,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(media.availableResolutions) { quality ->
                            val isSelected = selectedVideoQuality == quality
                            FilterChip(
                                selected = isSelected,
                                onClick = { onVideoQualityChanged(quality) },
                                label = { Text(quality.label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SearxIndigo,
                                    selectedLabelColor = Color.White,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = DarkTextPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) SearxCyan else DarkBorder
                                )
                            )
                        }
                    }

                    // Format selector
                    Text(
                        text = "Container Format:",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextSecondary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(DownloadFormat.MP4, DownloadFormat.WEBM).forEach { fmt ->
                            val isSelected = selectedFormat == fmt
                            FilterChip(
                                selected = isSelected,
                                onClick = { onFormatChanged(fmt) },
                                label = { Text(fmt.extension.uppercase(), fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SearxViolet,
                                    selectedLabelColor = Color.White,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = DarkTextPrimary
                                )
                            )
                        }
                    }
                } else {
                    // Audio Bitrates
                    Text(
                        text = "Select Audio Quality:",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextSecondary,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(AudioBitrate.values()) { bitrate ->
                            val isSelected = selectedAudioBitrate == bitrate
                            FilterChip(
                                selected = isSelected,
                                onClick = { onAudioBitrateChanged(bitrate) },
                                label = { Text(bitrate.label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SearxIndigo,
                                    selectedLabelColor = Color.White,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = DarkTextPrimary
                                )
                            )
                        }
                    }

                    // Audio Formats (MP3, M4A, WAV, OPUS)
                    Text(
                        text = "Audio Format:",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTextSecondary,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf(DownloadFormat.MP3, DownloadFormat.M4A, DownloadFormat.WAV, DownloadFormat.OPUS)) { fmt ->
                            val isSelected = selectedFormat == fmt
                            FilterChip(
                                selected = isSelected,
                                onClick = { onFormatChanged(fmt) },
                                label = { Text(fmt.extension.uppercase(), fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SearxViolet,
                                    selectedLabelColor = Color.White,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = DarkTextPrimary
                                )
                            )
                        }
                    }
                }

                // Estimated File Size Info Box
                val estSize = if (selectedMediaType == MediaType.AUDIO) {
                    (media.durationSeconds * selectedAudioBitrate.kbps * 125L).coerceAtLeast(3_500_000L)
                } else {
                    val h = selectedVideoQuality.heightPixels
                    val factor = (h / 720.0).coerceAtLeast(0.5)
                    (media.durationSeconds * 200_000L * factor).toLong().coerceAtLeast(12_000_000L)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estimated File Size:",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextSecondary
                    )
                    Text(
                        text = "~${formatBytes(estSize)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SearxCyan,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onAddToQueue,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_to_queue_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkTextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Icon(Icons.Filled.PlaylistAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Queue", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onDownloadNow,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("download_now_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SearxIndigo)
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
