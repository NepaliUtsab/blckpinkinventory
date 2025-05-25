package com.blackandpink.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import com.blackandpink.util.DateUtil

class InventoryModelTest {
    
    @Test
    fun `test InventoryItem serialization and deserialization`() {
        val item = InventoryItem(
            id = "test-1",
            name = "Test Item",
            description = "A test item for unit testing",
            color = "Red",
            categoryId = "cat-1",
            shareableCode = "ABC123",
            price = 19.99,
            cost = 10.0,
            quantity = 5,
            location = "Shelf A",
            minStock = 3,
            maxStock = 10,
            tags = listOf("test", "sample")
        )
        
        val json = Json { prettyPrint = true; encodeDefaults = true }
        val serialized = json.encodeToString(item)
        val deserialized = json.decodeFromString<InventoryItem>(serialized)
        
        assertEquals(item.id, deserialized.id)
        assertEquals(item.name, deserialized.name)
        assertEquals(item.price, deserialized.price)
        assertEquals(item.quantity, deserialized.quantity)
        assertEquals(item.tags, deserialized.tags)
    }
    
    @Test
    fun `test InventoryTransaction serialization and deserialization`() {
        val transaction = InventoryTransaction(
            id = "txn-1",
            itemId = "item-1",
            quantity = 3,
            transactionType = TransactionType.ADDITION,
            reason = "Stock replenishment"
        )
        
        val json = Json { prettyPrint = true; encodeDefaults = true }
        val serialized = json.encodeToString(transaction)
        val deserialized = json.decodeFromString<InventoryTransaction>(serialized)
        
        assertEquals(transaction.id, deserialized.id)
        assertEquals(transaction.itemId, deserialized.itemId)
        assertEquals(transaction.quantity, deserialized.quantity)
        assertEquals(transaction.transactionType, deserialized.transactionType)
        assertEquals(transaction.reason, deserialized.reason)
        assertNotNull(deserialized.timestamp)
    }
    
    @Test
    fun `test InventorySession serialization and deserialization`() {
        val item = InventoryItem(
            id = "test-1",
            name = "Test Item",
            description = "A test item for unit testing",
            color = "Blue",
            categoryId = "cat-1",
            shareableCode = "DEF456",
            price = 19.99,
            cost = 10.0,
            quantity = 5,
            location = "Shelf A"
        )
        
        val transaction = InventoryTransaction(
            id = "txn-1",
            itemId = "test-1",
            quantity = 3,
            transactionType = TransactionType.ADDITION,
            reason = "Test transaction"
        )
        
        val session = InventorySession(
            id = "session-1",
            name = "Test Session",
            description = "A test session",
            items = mutableListOf(item),
            transactions = mutableListOf(transaction)
        )
        
        val json = Json { prettyPrint = true; encodeDefaults = true }
        val serialized = json.encodeToString(session)
        val deserialized = json.decodeFromString<InventorySession>(serialized)
        
        assertEquals(session.id, deserialized.id)
        assertEquals(session.name, deserialized.name)
        assertEquals(1, deserialized.items.size)
        assertEquals(1, deserialized.transactions.size)
        assertEquals(item.id, deserialized.items[0].id)
        assertEquals(transaction.id, deserialized.transactions[0].id)
    }
    
    @Test
    fun `test SessionSummary serialization and deserialization`() {
        val summary = SessionSummary(
            id = "session-1",
            name = "Test Session",
            startDate = DateUtil.now(),
            itemCount = 5,
            totalValue = 299.95
        )
        
        val json = Json { prettyPrint = true; encodeDefaults = true }
        val serialized = json.encodeToString(summary)
        val deserialized = json.decodeFromString<SessionSummary>(serialized)
        
        assertEquals(summary.id, deserialized.id)
        assertEquals(summary.name, deserialized.name)
        assertEquals(summary.itemCount, deserialized.itemCount)
        assertEquals(summary.totalValue, deserialized.totalValue)
    }
    
    @Test
    fun `test InventoryAnalytics serialization and deserialization`() {
        val analytics = InventoryAnalytics(
            itemValueByCategory = mapOf(
                "Category A" to 100.0,
                "Category B" to 200.0
            ),
            stockLevelsByItem = mapOf(
                "item-1" to 5,
                "item-2" to 10
            ),
            transactionHistory = mapOf(
                "item-1" to listOf(
                    InventoryTransaction(
                        id = "txn-1",
                        itemId = "item-1",
                        quantity = 3,
                        transactionType = TransactionType.ADDITION,
                        reason = "Test transaction"
                    )
                )
            )
        )
        
        val json = Json { prettyPrint = true; encodeDefaults = true }
        val serialized = json.encodeToString(analytics)
        val deserialized = json.decodeFromString<InventoryAnalytics>(serialized)
        
        assertEquals(analytics.itemValueByCategory.size, deserialized.itemValueByCategory.size)
        assertEquals(analytics.stockLevelsByItem.size, deserialized.stockLevelsByItem.size)
        assertEquals(analytics.transactionHistory.size, deserialized.transactionHistory.size)
        assertEquals(100.0, deserialized.itemValueByCategory["Category A"])
        assertEquals(200.0, deserialized.itemValueByCategory["Category B"])
        assertEquals(5, deserialized.stockLevelsByItem["item-1"])
        assertEquals(10, deserialized.stockLevelsByItem["item-2"])
        assertEquals(1, deserialized.transactionHistory["item-1"]?.size)
    }
}
