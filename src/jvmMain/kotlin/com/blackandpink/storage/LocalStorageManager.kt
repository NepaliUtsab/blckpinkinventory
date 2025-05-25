package com.blackandpink.storage

import com.blackandpink.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import java.io.IOException
import java.util.zip.ZipOutputStream
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import com.blackandpink.util.DateUtil

/**
 * Manages local storage for inventory data
 */
class LocalStorageManager {
    private var appDataDir: String
    private var sessionsDir: String
    private var inventoryFile: String
    private var categoriesFile: String
    private var analyticsFile: String
    private var exportsDir: String
    private var settingsFile: String
    private val DEFAULT_APP_DIR = System.getProperty("user.home") + File.separator + ".blackandpink"
    
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    init {
        // Load settings if exists, otherwise use defaults
        val tempSettingsFile = "$DEFAULT_APP_DIR${File.separator}settings.json"
        val settings = if (File(tempSettingsFile).exists()) {
            try {
                json.decodeFromString<AppSettings>(File(tempSettingsFile).readText())
            } catch (e: Exception) {
                AppSettings()
            }
        } else {
            AppSettings()
        }
        
        // Use custom path if available, otherwise use default
        appDataDir = settings.storagePath ?: DEFAULT_APP_DIR
        settingsFile = "$appDataDir${File.separator}settings.json"
        sessionsDir = "$appDataDir${File.separator}sessions"
        inventoryFile = "$appDataDir${File.separator}inventory.json"
        categoriesFile = "$appDataDir${File.separator}categories.json"
        analyticsFile = "$appDataDir${File.separator}analytics.json"
        exportsDir = "$appDataDir${File.separator}exports"
        
        // Create directories if they don't exist
        try {
            Path(appDataDir).createDirectories()
            Path(sessionsDir).createDirectories()
            Path(exportsDir).createDirectories()
        } catch (e: Exception) {
            println("Error creating directories: ${e.message}")
            // Fall back to default directory if custom path fails
            appDataDir = DEFAULT_APP_DIR
            settingsFile = "$appDataDir${File.separator}settings.json"
            sessionsDir = "$appDataDir${File.separator}sessions"
            inventoryFile = "$appDataDir${File.separator}inventory.json"
            categoriesFile = "$appDataDir${File.separator}categories.json"
            analyticsFile = "$appDataDir${File.separator}analytics.json"
            exportsDir = "$appDataDir${File.separator}exports"
            
            Path(appDataDir).createDirectories()
            Path(sessionsDir).createDirectories()
            Path(exportsDir).createDirectories()
            
            // Reset settings to default
            saveSettings(AppSettings())
        }
    }
    
    /**
     * Update storage path and reinitialize directories
     */
    fun updateStoragePath(newPath: String?): Boolean {
        return try {
            val settings = loadSettings()
            val updatedSettings = settings.copy(storagePath = newPath)
            
            // Save settings to both old and new locations
            File("$DEFAULT_APP_DIR${File.separator}settings.json").writeText(json.encodeToString(updatedSettings))
            
            if (newPath != null) {
                // Create directories in new location
                Path(newPath).createDirectories()
                File("$newPath${File.separator}settings.json").writeText(json.encodeToString(updatedSettings))
                
                // Copy existing data to new location if needed
                if (appDataDir != newPath) {
                    File(appDataDir).copyRecursively(File(newPath), overwrite = true)
                }
            }
            
            // Reinitialize with new path
            appDataDir = newPath ?: DEFAULT_APP_DIR
            settingsFile = "$appDataDir${File.separator}settings.json"
            sessionsDir = "$appDataDir${File.separator}sessions"
            inventoryFile = "$appDataDir${File.separator}inventory.json"
            categoriesFile = "$appDataDir${File.separator}categories.json"
            analyticsFile = "$appDataDir${File.separator}analytics.json"
            exportsDir = "$appDataDir${File.separator}exports"
            
            true
        } catch (e: Exception) {
            println("Failed to update storage path: ${e.message}")
            false
        }
    }
    
    /**
     * Check if storage path is defined
     */
    fun isStoragePathDefined(): Boolean {
        return loadSettings().storagePath != null
    }
    
    /**
     * Get current storage path
     */
    fun getStoragePath(): String {
        return appDataDir
    }
    
    /**
     * Save settings to file
     */
    fun saveSettings(settings: AppSettings): Boolean {
        return try {
            File(settingsFile).writeText(json.encodeToString(settings))
            // Also update the default location settings file
            File("$DEFAULT_APP_DIR${File.separator}settings.json").writeText(json.encodeToString(settings))
            true
        } catch (e: Exception) {
            println("Error saving settings: ${e.message}")
            false
        }
    }
    
    /**
     * Load settings from file
     */
    fun loadSettings(): AppSettings {
        val file = File(settingsFile)
        return if (file.exists()) {
            try {
                json.decodeFromString(file.readText())
            } catch (e: Exception) {
                println("Error loading settings: ${e.message}")
                AppSettings()
            }
        } else {
            AppSettings()
        }
    }
    
    /**
     * Save categories to file
     */
    @Throws(IOException::class)
    fun saveCategories(categories: List<Category>): Boolean {
        return try {
            File(categoriesFile).writeText(json.encodeToString(categories))
            true
        } catch (e: Exception) {
            println("Error saving categories: ${e.message}")
            e.printStackTrace()
            throw IOException("Failed to save categories data: ${e.message}", e)
        }
    }
    
    /**
     * Load categories from file
     */
    @Throws(IOException::class)
    fun loadCategories(): List<Category> {
        val file = File(categoriesFile)
        return if (file.exists()) {
            try {
                json.decodeFromString(file.readText())
            } catch (e: Exception) {
                println("Error loading categories: ${e.message}")
                e.printStackTrace()
                throw IOException("Failed to load categories data: ${e.message}", e)
            }
        } else {
            emptyList()
        }
    }
    
    /**
     * Save inventory to file
     * @throws IOException if there's an error writing to the file
     */
    @Throws(IOException::class)
    fun saveInventory(inventory: List<InventoryItem>): Boolean {
        return try {
            File(inventoryFile).writeText(json.encodeToString(inventory))
            true
        } catch (e: Exception) {
            println("Error saving inventory: ${e.message}")
            e.printStackTrace()
            throw IOException("Failed to save inventory data: ${e.message}", e)
        }
    }
    
    /**
     * Load inventory from file
     * @return The list of inventory items or empty list if file doesn't exist
     * @throws IOException if there's an error reading from the file
     */
    @Throws(IOException::class)
    fun loadInventory(): List<InventoryItem> {
        val file = File(inventoryFile)
        return if (file.exists()) {
            try {
                json.decodeFromString(file.readText())
            } catch (e: Exception) {
                println("Error loading inventory: ${e.message}")
                e.printStackTrace()
                throw IOException("Failed to load inventory data: ${e.message}", e)
            }
        } else {
            emptyList()
        }
    }
    
    /**
     * Save a session to file
     * @throws IOException if there's an error writing to the file
     */
    @Throws(IOException::class)
    fun saveSession(session: InventorySession): Boolean {
        val sessionFile = "$sessionsDir${File.separator}session_${session.id}.json"
        try {
            File(sessionFile).writeText(json.encodeToString(session))
            updateSessionsList(session)
            return true
        } catch (e: Exception) {
            println("Error saving session: ${e.message}")
            e.printStackTrace()
            throw IOException("Failed to save session data: ${e.message}", e)
        }
    }
    
    /**
     * Load a session from file
     * @return The session or null if not found
     * @throws IOException if there's an error reading from the file
     */
    @Throws(IOException::class)
    fun loadSession(sessionId: String): InventorySession? {
        val sessionFile = "$sessionsDir${File.separator}session_$sessionId.json"
        val file = File(sessionFile)
        
        if (!file.exists()) {
            return null
        }
        
        return try {
            json.decodeFromString(file.readText())
        } catch (e: Exception) {
            println("Error loading session: ${e.message}")
            e.printStackTrace()
            throw IOException("Failed to load session data: ${e.message}", e)
        }
    }
    
    fun getAllSessions(): List<SessionSummary> {
        val summariesFile = File("$appDataDir${File.separator}sessions_list.json")
        return if (summariesFile.exists()) {
            try {
                json.decodeFromString(summariesFile.readText())
            } catch (e: Exception) {
                println("Error loading sessions list: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    private fun updateSessionsList(session: InventorySession) {
        val summariesFile = File("$appDataDir${File.separator}sessions_list.json")
        val sessionSummary = SessionSummary(
            id = session.id,
            name = session.name,
            startDate = session.startDate,
            endDate = session.endDate,
            itemCount = session.items.size,
            totalValue = session.items.sumOf { it.price * it.quantity }
        )
        
        val existingSummaries = if (summariesFile.exists()) {
            try {
                json.decodeFromString<List<SessionSummary>>(summariesFile.readText())
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
        
        val updatedSummaries = existingSummaries
            .filter { it.id != session.id }
            .plus(sessionSummary)
            .sortedByDescending { it.startDate }
        
        summariesFile.writeText(json.encodeToString(updatedSummaries))
    }
    
    fun saveAnalytics(analytics: InventoryAnalytics) {
        try {
            File(analyticsFile).writeText(json.encodeToString(analytics))
        } catch (e: Exception) {
            println("Error saving analytics: ${e.message}")
        }
    }
    
    fun loadAnalytics(): InventoryAnalytics {
        val file = File(analyticsFile)
        return if (file.exists()) {
            try {
                json.decodeFromString(file.readText())
            } catch (e: Exception) {
                println("Error loading analytics: ${e.message}")
                InventoryAnalytics(emptyMap(), emptyMap(), emptyMap())
            }
        } else {
            InventoryAnalytics(emptyMap(), emptyMap(), emptyMap())
        }
    }
    
    fun deleteSession(sessionId: String): Boolean {
        val sessionFile = File("$sessionsDir${File.separator}session_$sessionId.json")
        val deleted = if (sessionFile.exists()) {
            sessionFile.delete()
        } else false
        
        if (deleted) {
            // Update sessions list
            val summariesFile = File("$appDataDir${File.separator}sessions_list.json")
            if (summariesFile.exists()) {
                try {
                    val existingSummaries = json.decodeFromString<List<SessionSummary>>(summariesFile.readText())
                    val updatedSummaries = existingSummaries.filter { it.id != sessionId }
                    summariesFile.writeText(json.encodeToString(updatedSummaries))
                } catch (e: Exception) {
                    println("Error updating sessions list after deletion: ${e.message}")
                }
            }
        }
        
        return deleted
    }
    
    /**
     * Export all application data to a backup ZIP file
     * Returns true if successful, false otherwise
     */
    fun exportAllData(targetPath: String): Boolean {
        val targetDir = File(targetPath)
        if (!targetDir.isDirectory) {
            return false
        }
        
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val exportFile = File(targetDir, "blackandpink_backup_$timestamp.zip")
        
        try {
            ZipOutputStream(FileOutputStream(exportFile)).use { zipOut ->
                // Add settings file
                val settingsFileObj = File(settingsFile)
                if (settingsFileObj.exists()) {
                    addFileToZip(settingsFileObj, "settings.json", zipOut)
                }
                
                // Add inventory file
                val inventoryFileObj = File(inventoryFile)
                if (inventoryFileObj.exists()) {
                    addFileToZip(inventoryFileObj, "inventory.json", zipOut)
                }
                
                // Add categories file
                val categoriesFileObj = File(categoriesFile)
                if (categoriesFileObj.exists()) {
                    addFileToZip(categoriesFileObj, "categories.json", zipOut)
                }
                
                // Add analytics file
                val analyticsFileObj = File(analyticsFile)
                if (analyticsFileObj.exists()) {
                    addFileToZip(analyticsFileObj, "analytics.json", zipOut)
                }
                
                // Add sessions list
                val sessionsListFile = File("$appDataDir${File.separator}sessions_list.json")
                if (sessionsListFile.exists()) {
                    addFileToZip(sessionsListFile, "sessions_list.json", zipOut)
                }
                
                // Add all session files
                val sessionsDir = File(sessionsDir)
                if (sessionsDir.exists() && sessionsDir.isDirectory) {
                    sessionsDir.listFiles { file -> file.name.endsWith(".json") }?.forEach { sessionFile ->
                        addFileToZip(sessionFile, "sessions/${sessionFile.name}", zipOut)
                    }
                }
            }
            
            return true
        } catch (e: Exception) {
            println("Error exporting data: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Import data from a backup ZIP file
     * Returns true if successful
     */
    fun importData(zipFilePath: String): Boolean {
        val zipFile = File(zipFilePath)
        if (!zipFile.exists() || !zipFile.isFile) {
            return false
        }
        
        try {
            // Create a backup before import
            val backupDir = "$appDataDir${File.separator}backup_${DateUtil.now().replace(":", "-")}"
            Path(backupDir).createDirectories()
            File(appDataDir).copyRecursively(File(backupDir), overwrite = true)
            
            ZipInputStream(FileInputStream(zipFile)).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    val entryName = entry.name
                    val targetFile = when {
                        entryName == "settings.json" -> File(settingsFile)
                        entryName == "inventory.json" -> File(inventoryFile)
                        entryName == "categories.json" -> File(categoriesFile)
                        entryName == "analytics.json" -> File(analyticsFile) 
                        entryName == "sessions_list.json" -> File("$appDataDir${File.separator}sessions_list.json")
                        entryName.startsWith("sessions/") -> {
                            val fileName = entryName.substringAfter("sessions/")
                            File("$sessionsDir${File.separator}$fileName")
                        }
                        else -> null
                    }
                    
                    if (targetFile != null) {
                        // Create parent directories if needed
                        targetFile.parentFile?.mkdirs()
                        
                        FileOutputStream(targetFile).use { fileOut ->
                            val buffer = ByteArray(4096)
                            var bytesRead: Int
                            while (zipIn.read(buffer).also { bytesRead = it } != -1) {
                                fileOut.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                    
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }
            
            return true
        } catch (e: Exception) {
            println("Error importing data: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Helper method to add a file to a ZIP archive
     */
    private fun addFileToZip(file: File, entryName: String, zipOut: ZipOutputStream) {
        FileInputStream(file).use { fileIn ->
            zipOut.putNextEntry(ZipEntry(entryName))
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (fileIn.read(buffer).also { bytesRead = it } != -1) {
                zipOut.write(buffer, 0, bytesRead)
            }
            zipOut.closeEntry()
        }
    }
}
