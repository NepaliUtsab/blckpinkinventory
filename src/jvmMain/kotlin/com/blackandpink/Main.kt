package com.blackandpink

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import java.awt.Desktop
import java.net.URI
import java.awt.Taskbar
import java.awt.Toolkit
import androidx.compose.ui.window.ApplicationScope
import com.blackandpink.model.InventoryRepository
import com.blackandpink.screens.*

sealed class Screen {
    object Home : Screen()
    object Settings : Screen()
    object Inventory : Screen()
    data class ItemDetail(val itemId: String) : Screen()
    object Sessions : Screen()
    object NewSession : Screen()
    data class SessionDetails(val sessionId: String) : Screen()
    object Analytics : Screen()
}

fun main() = application {
    val isDarkTheme = remember { mutableStateOf(true) }
    val repository = remember { InventoryRepository() }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Inventory Manager",
        state = rememberWindowState(width = 1000.dp, height = 700.dp)
    ) {
        // Main application content
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
        
        MaterialTheme(
            colors = if (isDarkTheme.value) {
                darkColors(
                    primary = AppColors.Pink,
                    primaryVariant = AppColors.DarkPink,
                    background = AppColors.Black,
                    surface = AppColors.Gray
                )
            } else {
                lightColors(
                    primary = AppColors.Pink,
                    primaryVariant = AppColors.DarkPink,
                    background = AppColors.White,
                    surface = AppColors.White
                )
            }
        ) {
            Surface(color = MaterialTheme.colors.background) {
                // Using sidebar navigation
                Row(modifier = Modifier.fillMaxSize()) {
                    // Sidebar for navigation
                    com.blackandpink.components.SidebarNavigation(
                        currentScreen = currentScreen,
                        onScreenChange = { screen -> 
                            currentScreen = screen
                        }
                    )
                    
                    // Main content area
                    Box(modifier = Modifier.weight(1f)) {
                        when (val screen = currentScreen) {
                            is Screen.Home -> Screen.Inventory.let { currentScreen = it }
                            is Screen.Settings -> SettingsScreen(
                                onNavigateBack = { currentScreen = Screen.Home },
                                isDarkTheme = isDarkTheme,
                                repository = repository
                            )
                            is Screen.Inventory -> InventoryScreen(
                                repository = repository,
                                onNavigateBack = { currentScreen = Screen.Home },
                                onItemSelected = { itemId -> 
                                    currentScreen = Screen.ItemDetail(itemId)
                                }
                            )
                            is Screen.ItemDetail -> ItemDetailScreen(
                                itemId = screen.itemId,
                                repository = repository,
                                onNavigateBack = { currentScreen = Screen.Inventory },
                                onEditItem = { _ -> 
                                    // TODO: Handle item editing
                                    currentScreen = Screen.Inventory
                                }
                            )
                            is Screen.Sessions -> SessionsScreen(
                                repository = repository,
                                onNavigateBack = { currentScreen = Screen.Home },
                                onSessionSelected = { sessionId -> 
                                    currentScreen = Screen.SessionDetails(sessionId) 
                                },
                                onCreateNewSession = {
                                    currentScreen = Screen.NewSession
                                }
                            )
                            is Screen.NewSession -> {
                                var showDialog by remember { mutableStateOf(true) }
                                
                                if (showDialog) {
                                    CreateSessionDialog(
                                        onDismiss = {
                                            showDialog = false
                                            currentScreen = Screen.Sessions
                                        },
                                        onConfirm = { name, description ->
                                            repository.createSession(name, description)
                                            showDialog = false
                                            currentScreen = Screen.Sessions
                                        }
                                    )
                                }
                            }
                            is Screen.SessionDetails -> SessionDetailsScreen(
                                sessionId = screen.sessionId,
                                repository = repository,
                                onNavigateBack = { currentScreen = Screen.Sessions }
                            )
                            is Screen.Analytics -> AnalyticsScreen(
                                repository = repository,
                                onNavigateBack = { currentScreen = Screen.Home }
                            )
                        }
                    }
                }
            }
        }
    }
}
