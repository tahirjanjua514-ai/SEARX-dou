package com.example.ui.screens

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MultipleStop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.SearxCyan
import com.example.ui.theme.SearxIndigo
import com.example.ui.theme.StatusError

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val maxConcurrent by viewModel.maxConcurrentDownloads.collectAsStateWithLifecycle()
    val downloadOnWifiOnly by viewModel.downloadOnWifiOnly.collectAsStateWithLifecycle()
    val showNotifications by viewModel.showNotifications.collectAsStateWithLifecycle()
    val autoRetryFailed by viewModel.autoRetryFailed.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var showClearConfirm by remember { mutableStateOf(false) }

    val downloadPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
        ?: "${context.filesDir.absolutePath}/downloads"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Header Title
        item {
            Column {
                Text(
                    text = "Preferences & Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Configure engine performance, concurrency limits, and defaults.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextSecondary
                )
            }
        }

        // Section: Download Engine Concurrency
        item {
            SettingsCard(title = "Download Engine", icon = Icons.Filled.MultipleStop) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Maximum Simultaneous Downloads",
                            color = DarkTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SearxIndigo.copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$maxConcurrent tasks",
                                color = SearxCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Slider(
                        value = maxConcurrent.toFloat(),
                        onValueChange = { viewModel.setMaxConcurrentDownloads(it.toInt()) },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = SearxCyan,
                            activeTrackColor = SearxIndigo,
                            inactiveTrackColor = DarkBorder
                        ),
                        modifier = Modifier.testTag("max_concurrent_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1 (Battery Saving)", color = DarkTextSecondary, fontSize = 11.sp)
                        Text("10 (High Speed)", color = DarkTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Section: Storage & Directory
        item {
            SettingsCard(title = "Storage & Location", icon = Icons.Filled.Folder) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Download Folder",
                        color = DarkTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = downloadPath,
                        color = SearxCyan,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = "Native Android private app storage ensures full security and no unnecessary broad storage permissions.",
                        color = DarkTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Section: Network & Notifications
        item {
            SettingsCard(title = "Network & Background Service", icon = Icons.Filled.Tune) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Wi-Fi Only
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Wi-Fi Only Downloading",
                                color = DarkTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Only initiate new downloads when connected to Wi-Fi",
                                color = DarkTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = downloadOnWifiOnly,
                            onCheckedChange = { viewModel.setDownloadOnWifiOnly(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SearxCyan,
                                checkedTrackColor = SearxIndigo
                            )
                        )
                    }

                    // Persistent Notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Background Progress Notifications",
                                color = DarkTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Keep downloads running in foreground service when minimized",
                                color = DarkTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = showNotifications,
                            onCheckedChange = { viewModel.setShowNotifications(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SearxCyan,
                                checkedTrackColor = SearxIndigo
                            )
                        )
                    }

                    // Auto-retry
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Retry Interrupted Tasks",
                                color = DarkTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Automatically resume interrupted downloads on reconnect",
                                color = DarkTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = autoRetryFailed,
                            onCheckedChange = { viewModel.setAutoRetryFailed(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SearxCyan,
                                checkedTrackColor = SearxIndigo
                            )
                        )
                    }
                }
            }
        }

        // Section: Theme Selection
        item {
            SettingsCard(title = "Appearance & Theme", icon = Icons.Filled.DarkMode) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "App Theme",
                        color = DarkTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("DARK" to "Dark (Recommended)", "LIGHT" to "Light", "SYSTEM" to "System").forEach { (mode, label) ->
                            val isSelected = themeMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setThemeMode(mode) },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SearxIndigo,
                                    selectedLabelColor = Color.White,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = DarkTextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: Data & Maintenance
        item {
            SettingsCard(title = "Data Maintenance", icon = Icons.Filled.DeleteForever) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Clear Download History",
                            color = DarkTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Wipes recorded database history and frees tracked records",
                            color = DarkTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    TextButton(
                        onClick = { showClearConfirm = true },
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                            contentColor = StatusError
                        )
                    ) {
                        Text("Clear", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: About & Compliance
        item {
            SettingsCard(title = "About SEARX AI", icon = Icons.Filled.Info) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SEARX AI – All-in-One Media Downloader",
                        color = DarkTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Version 1.0.0 • Build Release (2026)",
                        color = SearxCyan,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Engineered with Kotlin, Jetpack Compose, OkHttp HTTP Range resumption, Room Persistence, and Android Foreground Services. Developed strictly for authorized public media archiving and offline personal access.",
                        color = DarkTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All History?") },
            text = { Text("Are you sure you want to clear your completed downloads history?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearConfirm = false
                        Toast.makeText(context, "History cleared.", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Clear", color = StatusError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SearxIndigo.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = SearxCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            content()
        }
    }
}
