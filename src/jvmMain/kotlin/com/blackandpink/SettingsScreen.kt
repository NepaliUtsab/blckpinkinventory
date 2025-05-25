package com.blackandpink

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import com.blackandpink.AppIcons
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.blackandpink.model.InventoryRepository
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    isDarkTheme: MutableState<Boolean> = remember { mutableStateOf(true) },
    repository: InventoryRepository? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    
    // Get repository settings
    val repoNotNull = repository ?: return
    val settings = repoNotNull.getSettings()
    
    // Storage path settings
    var storagePath by remember { mutableStateOf(repoNotNull.getStoragePath()) }
    var isStoragePathDefined by remember { mutableStateOf(repoNotNull.isStoragePathDefined()) }
    var showStoragePathDialog by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(settings.enableNotifications) }
    var lowStockAlerts by remember { mutableStateOf(settings.lowStockAlerts) }
    
    // Check for storage path when screen opens
    LaunchedEffect(Unit) {
        if (!isStoragePathDefined) {
            showStoragePathDialog = true
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text("Settings", style = MaterialTheme.typography.h5)
        }
        
        Divider()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Storage Settings", style = MaterialTheme.typography.h6)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Storage path settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Storage Path")
                Text(storagePath, style = MaterialTheme.typography.caption)
            }
            Button(onClick = { showStoragePathDialog = true }) {
                Text("Change")
            }
        }
        
        if (!isStoragePathDefined) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Please set a storage path before using the application",
                style = MaterialTheme.typography.caption,
                color = Color.Red
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Appearance", style = MaterialTheme.typography.h6)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Theme settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode")
            Switch(
                checked = isDarkTheme.value,
                onCheckedChange = { 
                    isDarkTheme.value = it 
                    // Update settings
                    val updatedSettings = settings.copy(darkMode = it)
                    repoNotNull.updateSettings(updatedSettings)
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Notifications", style = MaterialTheme.typography.h6)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Notification settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Notifications")
            Switch(
                checked = notifications,
                onCheckedChange = { 
                    notifications = it
                    // Update settings
                    val updatedSettings = settings.copy(enableNotifications = it)
                    repoNotNull.updateSettings(updatedSettings) 
                }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Low Stock Alerts")
            Switch(
                checked = lowStockAlerts,
                onCheckedChange = { 
                    lowStockAlerts = it
                    // Update settings
                    val updatedSettings = settings.copy(lowStockAlerts = it)
                    repoNotNull.updateSettings(updatedSettings)
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Data Management", style = MaterialTheme.typography.h6)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Data export/import options
        Button(
            onClick = { 
                if (isStoragePathDefined) {
                    showExportDialog = true
                } else {
                    statusMessage = Pair("Please set a storage path first", false)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = AppIcons.Upload,
                contentDescription = "Export"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export Data")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = { 
                if (isStoragePathDefined) {
                    showImportDialog = true
                } else {
                    statusMessage = Pair("Please set a storage path first", false)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = AppIcons.Download,
                contentDescription = "Import"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Import Data")
        }
        
        // Status message
        statusMessage?.let { (message, success) ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = if (success) Color.Green else Color.Red,
                style = MaterialTheme.typography.body2
            )
        }
    }
    
    // Storage path dialog
    if (showStoragePathDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (isStoragePathDefined) {
                    showStoragePathDialog = false 
                }
            },
            title = { Text("Select Storage Path") },
            text = { 
                Text("Select a directory where your inventory data will be stored.${if (!isStoragePathDefined) " This is required to use the application." else ""}") 
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        val dir = selectDirectory()
                        if (dir != null && dir.isNotEmpty()) {
                            val success = repoNotNull.updateStoragePath(dir)
                            if (success) {
                                storagePath = dir
                                isStoragePathDefined = true
                                statusMessage = Pair("Storage path updated successfully", true)
                                showStoragePathDialog = false
                            } else {
                                statusMessage = Pair("Failed to update storage path", false)
                            }
                        }
                    }
                }) {
                    Text("Select Directory")
                }
            },
            dismissButton = {
                if (isStoragePathDefined) {
                    Button(onClick = { showStoragePathDialog = false }) {
                        Text("Cancel")
                    }
                } else {
                    Button(onClick = {
                        // Use default path
                        val success = repoNotNull.updateStoragePath(null)
                        if (success) {
                            storagePath = repoNotNull.getStoragePath()
                            isStoragePathDefined = true
                            statusMessage = Pair("Using default storage path", true)
                            showStoragePathDialog = false
                        } else {
                            statusMessage = Pair("Failed to use default storage path", false)
                        }
                    }) {
                        Text("Use Default")
                    }
                }
            }
        )
    }
    
    // Export dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Data") },
            text = { Text("Select a directory to export all your inventory data.") },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        val dir = selectDirectory()
                        if (dir != null && dir.isNotEmpty() && repository != null) {
                            val success = repository.exportAllData(dir)
                            statusMessage = if (success) {
                                Pair("Data exported successfully to $dir", true)
                            } else {
                                Pair("Failed to export data", false)
                            }
                            showExportDialog = false
                        }
                    }
                }) {
                    Text("Select Export Directory")
                }
            },
            dismissButton = {
                Button(onClick = { showExportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Import dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Data") },
            text = { Text("Select the Black & Pink data file to import. This will overwrite any existing data.") },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        val file = selectFile("zip")
                        if (file.isNotEmpty() && repository != null) {
                            val success = repository.importData(file)
                            statusMessage = if (success) {
                                Pair("Data imported successfully", true)
                            } else {
                                Pair("Failed to import data", false)
                            }
                            showImportDialog = false
                        }
                    }
                }) {
                    Text("Select Import File")
                }
            },
            dismissButton = {
                Button(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

suspend fun selectDirectory(): String? = withContext(Dispatchers.IO) {
    val fileChooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        dialogTitle = "Select Directory"
    }
    
    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile.absolutePath
    } else null
}

suspend fun selectFile(extension: String): String = withContext(Dispatchers.IO) {
    val fileChooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.FILES_ONLY
        dialogTitle = "Select File"
        fileFilter = FileNameExtensionFilter("${extension.uppercase()} files", extension)
    }
    
    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile.absolutePath
    } else ""
}
