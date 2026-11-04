package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.AppTab
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.SearxCyan
import com.example.ui.theme.SearxIndigo

@Composable
fun SearxBottomNavBar(
    currentTab: AppTab,
    activeCount: Int,
    queueCount: Int,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("searx_bottom_navigation"),
        containerColor = DarkSurface,
        tonalElevation = 8.dp
    ) {
        AppTab.values().forEach { tab ->
            val isSelected = currentTab == tab

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    when (tab) {
                        AppTab.HOME -> {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = tab.title
                            )
                        }
                        AppTab.DOWNLOADS -> {
                            BadgedBox(
                                badge = {
                                    if (activeCount > 0) {
                                        Badge(
                                            containerColor = SearxCyan,
                                            contentColor = Color.Black
                                        ) {
                                            Text("$activeCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Filled.Download else Icons.Outlined.Download,
                                    contentDescription = tab.title
                                )
                            }
                        }
                        AppTab.QUEUE -> {
                            BadgedBox(
                                badge = {
                                    if (queueCount > 0) {
                                        Badge(
                                            containerColor = SearxIndigo,
                                            contentColor = Color.White
                                        ) {
                                            Text("$queueCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Filled.FormatListNumbered else Icons.Outlined.FormatListNumbered,
                                    contentDescription = tab.title
                                )
                            }
                        }
                        AppTab.HISTORY -> {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.History else Icons.Outlined.History,
                                contentDescription = tab.title
                            )
                        }
                        AppTab.SETTINGS -> {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = tab.title
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = SearxCyan,
                    indicatorColor = SearxIndigo.copy(alpha = 0.5f),
                    unselectedIconColor = DarkTextSecondary,
                    unselectedTextColor = DarkTextSecondary
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
            )
        }
    }
}
