package com.blackandpink.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import java.io.File
import java.util.UUID

class InventoryRepositoryTest {
    
    @Test
    fun `test export and import data`() {
        // Create a repository with test data
        val repository = InventoryRepository()
        
        // Add a test item
        val testItem = InventoryItem(
            id = UUID.randomUUID().toString(),
            name = "Test Export Item",
            description = "An item to test export/import functionality",
            color = "Green",
            categoryId = "test-cat",
            shareableCode = "XYZ789",
            price = 29.99,
            cost = 15.0,
            quantity = 10,
            location = "Export Test Location"
        )
        
        repository.addItem(
            name = testItem.name,
            description = testItem.description,
            color = testItem.color,
            categoryId = testItem.categoryId,
            price = testItem.price,
            cost = testItem.cost,
            quantity = testItem.quantity,
            location = testItem.location
        )
        
        // Create a temporary file for export
        val tempFile = File.createTempFile("inventory_export_test", ".zip")
        tempFile.deleteOnExit()
        
        // Export the data
        val exportSuccess = repository.exportAllData(tempFile.absolutePath)
        
        // Verify export succeeded
        assertTrue(exportSuccess)
        assertTrue(tempFile.exists())
        assertTrue(tempFile.length() > 0)
        
        // Create a new repository for import
        val importRepository = InventoryRepository()
        
        // Import the data
        val importSuccess = importRepository.importData(tempFile.absolutePath)
        
        // Verify import succeeded
        assertTrue(importSuccess)
        
        // Verify the imported data contains our test item
        val importedItems = importRepository.getAllItems()
        assertTrue(importedItems.isNotEmpty())
        
        // Find our test item
        val importedItem = importedItems.find { it.shareableCode == "XYZ789" }
        assertNotNull(importedItem)
        assertEquals("Test Export Item", importedItem.name)
        assertEquals(10, importedItem.quantity)
        assertEquals(29.99, importedItem.price)
    }
}
