package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import com.example.data.local.DownloadEntity
import com.example.data.model.DownloadStatus
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.DarkTextTertiary
import com.example.ui.theme.PlatformFacebook
import com.example.ui.theme.PlatformInstagram
import com.example.ui.theme.PlatformTikTok
import com.example.ui.theme.PlatformYouTube
import com.example.ui.theme.SearxCyan
import com.example.ui.theme.SearxIndigo
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusPaused
import com.example.ui.theme.StatusSuccess

@Composable
fun DownloadCard(
    download: DownloadEntity,
    formatBytes: (Long) -> String,
    formatSpeed: (Long) -> String,
    formatRemaining: (Long) -> String,
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onCancel: () -> Unit = {},
    onRetry: () -> Unit = {},
    onOpen: () -> Unit = {},
    onShare: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val progressFloat by animateFloatAsState(
        targetValue = (download.progressPercentage / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 250),
        label = "progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("download_card_${download.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Thumbnail + Title + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Media Thumbnail
                Box(
                    modifier = Modifier
                        .size(68.dp, 54.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, DarkBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                ) {
                    if (download.thumbnailUrl.isNotEmpty()) {
                        AsyncImage(
                            model = download.thumbnailUrl,
                            contentDescription = "Media thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                    // Platform badge tag on thumbnail
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(getPlatformColor(download.platform))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = download.platform.take(2).uppercase(),
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Title + Subtitle
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = download.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = DarkTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Format tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SearxIndigo.copy(alpha = 0.25f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${download.format} • ${download.quality}",
                                color = SearxCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Status badge
                        StatusBadge(download.status)
                    }
                }
            }

            // If active or paused, show real-time progress details
            if (download.status == DownloadStatus.DOWNLOADING.name || download.status == DownloadStatus.PAUSED.name) {
                // Progress Bar
                LinearProgressIndicator(
                    progress = { progressFloat },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (download.status == DownloadStatus.PAUSED.name) StatusPaused else SearxCyan,
                    trackColor = DarkBorder
                )

                // Detailed Statistics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress: ${download.progressPercentage}%",
                        color = DarkTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (download.status == DownloadStatus.DOWNLOADING.name) {
                        Text(
                            text = "Speed: ${formatSpeed(download.speedBps)}",
                            color = SearxCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "PAUSED",
                            color = StatusPaused,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Downloaded: ${formatBytes(download.downloadedBytes)} / ${formatBytes(download.fileSize)}",
                        color = DarkTextSecondary,
                        fontSize = 11.sp
                    )

                    if (download.status == DownloadStatus.DOWNLOADING.name && download.remainingSeconds > 0) {
                        Text(
                            text = "Remaining: ${formatRemaining(download.remainingSeconds)}",
                            color = DarkTextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }
            } else if (download.status == DownloadStatus.COMPLETED.name) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Size: ${formatBytes(download.fileSize)}",
                        color = DarkTextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Verified on device",
                        color = StatusSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (download.status == DownloadStatus.FAILED.name) {
                Text(
                    text = download.errorMessage ?: "Download encountered an error.",
                    color = StatusError,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (download.status) {
                    DownloadStatus.DOWNLOADING.name -> {
                        FilledTonalIconButton(
                            onClick = onPause,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = DarkBorder,
                                contentColor = DarkTextPrimary
                            )
                        ) {
                            Icon(Icons.Filled.Pause, contentDescription = "Pause", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalIconButton(
                            onClick = onCancel,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = StatusError.copy(alpha = 0.2f),
                                contentColor = StatusError
                            )
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Cancel", modifier = Modifier.size(18.dp))
                        }
                    }
                    DownloadStatus.PAUSED.name -> {
                        FilledTonalIconButton(
                            onClick = onResume,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = SearxIndigo.copy(alpha = 0.3f),
                                contentColor = SearxCyan
                            )
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Resume", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalIconButton(
                            onClick = onCancel,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = DarkBorder,
                                contentColor = DarkTextSecondary
                            )
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Cancel", modifier = Modifier.size(18.dp))
                        }
                    }
                    DownloadStatus.COMPLETED.name -> {
                        FilledTonalIconButton(
                            onClick = onOpen,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = SearxIndigo.copy(alpha = 0.3f),
                                contentColor = SearxCyan
                            )
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play/Open", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalIconButton(
                            onClick = onShare,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = DarkBorder,
                                contentColor = DarkTextPrimary
                            )
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalIconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = StatusError.copy(alpha = 0.15f),
                                contentColor = StatusError
                            )
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                        }
                    }
                    DownloadStatus.FAILED.name, DownloadStatus.CANCELLED.name -> {
                        FilledTonalIconButton(
                            onClick = onRetry,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = SearxIndigo.copy(alpha = 0.3f),
                                contentColor = SearxCyan
                            )
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Retry", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalIconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = DarkBorder,
                                contentColor = DarkTextSecondary
                            )
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        DownloadStatus.DOWNLOADING.name -> Pair(SearxCyan, "Downloading")
        DownloadStatus.PAUSED.name -> Pair(StatusPaused, "Paused")
        DownloadStatus.QUEUED.name -> Pair(SearxIndigo, "Queued")
        DownloadStatus.COMPLETED.name -> Pair(StatusSuccess, "Completed")
        DownloadStatus.FAILED.name -> Pair(StatusError, "Failed")
        DownloadStatus.CANCELLED.name -> Pair(DarkTextTertiary, "Cancelled")
        else -> Pair(DarkTextSecondary, status)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getPlatformColor(platform: String): Color {
    return when (platform.uppercase()) {
        "YOUTUBE" -> PlatformYouTube
        "FACEBOOK" -> PlatformFacebook
        "INSTAGRAM" -> PlatformInstagram
        "TIKTOK" -> PlatformTikTok
        else -> SearxIndigo
    }
}
