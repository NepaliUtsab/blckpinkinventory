package com.blackandpink.storage

import com.blackandpink.model.*
import kotlin.test.*
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

class LocalStorageManagerTest {
    
    private lateinit var storageManager: LocalStorageManager
    private lateinit var testAppDataDir: String
    private lateinit var testInventoryFile: String
    
    @BeforeTest
    fun setup() {
        // Use a temporary directory for testing
        testAppDataDir = System.getProperty("java.io.tmpdir") + File.separator + "blackandpink_test_" + UUID.randomUUID().toString()
        File(testAppDataDir).mkdirs()
        File(testAppDataDir + File.separator + "sessions").mkdirs()
        
        // Use reflection to modify the private appDataDir field
        val field = LocalStorageManager::class.java.getDeclaredField("appDataDir")
        field.isAccessible = true
        
        storageManager = LocalStorageManager()
        field.set(storageManager, testAppDataDir)
        
        // Update the inventory file path
        val inventoryField = LocalStorageManager::class.java.getDeclaredField("inventoryFile")
        inventoryField.isAccessible = true
        testInventoryFile = testAppDataDir + File.separator + "inventory.json"
        inventoryField.set(storageManager, testInventoryFile)
    }
    
    @AfterTest
    fun cleanup() {
        // Delete test directory
        File(testAppDataDir).deleteRecursively()
    }
    
    @Test
    fun `test save and load inventory`() {
        val items = listOf(
            InventoryItem(
                id = "test-1",
                name = "Test Item",
                description = "A test item for unit testing",
                color = "Yellow",
                categoryId = "test-cat",
                shareableCode = "PQR789",
                price = 19.99,
                cost = 10.0,
                quantity = 5,
                location = "Shelf A"
            )
        )
        
        storageManager.saveInventory(items)
        
        // Verify file exists
        assertTrue(File(testInventoryFile).exists())
        
        // Load inventory and verify data
        val loadedItems = storageManager.loadInventory()
        assertEquals(1, loadedItems.size)
        assertEquals("test-1", loadedItems[0].id)
        assertEquals("Test Item", loadedItems[0].name)
        assertEquals(19.99, loadedItems[0].price)
    }
    
    @Test
    fun `test save and load session`() {
        val session = InventorySession(
            id = "session-1",
            name = "Test Session",
            description = "A test session"
        )
        
        storageManager.saveSession(session)
        
        // Verify session file exists
        val sessionFile = testAppDataDir + File.separator + "sessions" + File.separator + "session_session-1.json"
        assertTrue(File(sessionFile).exists())
        
        // Load session and verify data
        val loadedSession = storageManager.loadSession("session-1")
        assertNotNull(loadedSession)
        assertEquals("session-1", loadedSession.id)
        assertEquals("Test Session", loadedSession.name)
    }
    
    @Test
    fun `test export and import data`() {
        // Create test data
        val items = listOf(
            InventoryItem(
                id = "test-1",
                name = "Test Export Item",
                description = "A test item for export testing",
                color = "Purple",
                categoryId = "export-cat",
                shareableCode = "EXP123",
                price = 29.99,
                cost = 15.0,
                quantity = 10,
                location = "Export Shelf"
            )
        )
        
        // Save test data
        storageManager.saveInventory(items)
        
        // Export data
        val exportFile = testAppDataDir + File.separator + "export_test.zip"
        val exportedPath = storageManager.exportAllData(exportFile)
        
        // Verify export file exists
        assertNotNull(exportedPath)
        assertTrue(File(exportFile).exists())
        
        // Clear data
        File(testInventoryFile).delete()
        
        // Import data
        val importSuccess = storageManager.importData(exportFile)
        assertTrue(importSuccess)
        
        // Verify imported data
        val loadedItems = storageManager.loadInventory()
        assertEquals(1, loadedItems.size)
        assertEquals("test-1", loadedItems[0].id)
        assertEquals("Test Export Item", loadedItems[0].name)
    }
}
