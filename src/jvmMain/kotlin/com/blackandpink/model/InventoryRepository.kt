package com.blackandpink.model

import com.blackandpink.storage.LocalStorageManager
import com.blackandpink.util.DateUtil
import java.time.LocalDateTime
import java.util.UUID

/**
 * Repository that manages inventory data and sessions
 */
class InventoryRepository {
    private val storageManager = LocalStorageManager()
    private var currentInventory: MutableList<InventoryItem> = mutableListOf()
    private var categories: MutableList<Category> = mutableListOf()
    private var currentSession: InventorySession? = null
    private var allSessions: List<SessionSummary> = emptyList()
    private var analytics: InventoryAnalytics = InventoryAnalytics(emptyMap(), emptyMap(), emptyMap())
    private var settings: AppSettings = AppSettings()
    
    init {
        // Load settings first
        loadSettings()
        
        // Only load data if storage path is defined
        if (isStoragePathDefined()) {
            loadCategories()
            loadInventory()
            loadSessionsList()
            loadAnalytics()
        }
    }
    
    /**
     * Check if storage path is defined
     */
    fun isStoragePathDefined(): Boolean {
        return storageManager.isStoragePathDefined()
    }
    
    /**
     * Get the current storage path
     */
    fun getStoragePath(): String {
        return storageManager.getStoragePath()
    }
    
    /**
     * Update the storage path
     */
    fun updateStoragePath(path: String?): Boolean {
        val success = storageManager.updateStoragePath(path)
        if (success) {
            settings = settings.copy(storagePath = path)
            saveSettings()
            
            // Reload data with new path
            loadCategories()
            loadInventory()
            loadSessionsList()
            loadAnalytics()
        }
        return success
    }
    
    /**
     * Load settings from storage
     */
    fun loadSettings(): AppSettings {
        settings = storageManager.loadSettings()
        return settings
    }
    
    /**
     * Save current settings to storage
     */
    fun saveSettings(): Boolean {
        return storageManager.saveSettings(settings)
    }
    
    /**
     * Update settings
     */
    fun updateSettings(updatedSettings: AppSettings): Boolean {
        settings = updatedSettings
        return saveSettings()
    }
    
    /**
     * Get current application settings
     */
    fun getSettings(): AppSettings {
        return settings
    }
    
    /**
     * Get all categories
     */
    fun getAllCategories(): List<Category> = categories.toList()
    
    /**
     * Get a category by ID
     */
    fun getCategoryById(id: String): Category? = categories.find { it.id == id }
    
    /**
     * Get a category by name
     */
    fun getCategoryByName(name: String): Category? = categories.find { it.name == name }
    
    /**
     * Add a new category
     */
    fun addCategory(name: String, description: String): Category {
        val category = Category(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description
        )
        categories.add(category)
        saveCategories()
        return category
    }
    
    /**
     * Update an existing category
     */
    fun updateCategory(category: Category): Boolean {
        val index = categories.indexOfFirst { it.id == category.id }
        if (index != -1) {
            categories[index] = category
            saveCategories()
            return true
        }
        return false
    }
    
    /**
     * Delete a category
     */
    fun deleteCategory(id: String): Boolean {
        val hasInventoryItems = currentInventory.any { it.categoryId == id }
        if (hasInventoryItems) {
            // Cannot delete category that has items
            return false
        }
        
        val removed = categories.removeIf { it.id == id }
        if (removed) {
            saveCategories()
        }
        return removed
    }
    
    /**
     * Save categories to storage
     */
    private fun saveCategories() {
        if (isStoragePathDefined()) {
            storageManager.saveCategories(categories)
        }
    }
    
    /**
     * Load categories from storage
     */
    private fun loadCategories() {
        if (isStoragePathDefined()) {
            try {
                categories = storageManager.loadCategories().toMutableList()
            } catch (e: Exception) {
                println("Failed to load categories: ${e.message}")
                categories = mutableListOf()
            }
        }
    }
    
    /**
     * Get all inventory items
     */
    fun getAllItems(): List<InventoryItem> = currentInventory.toList()
    
    /**
     * Get an inventory item by ID
     */
    fun getItemById(id: String): InventoryItem? = currentInventory.find { it.id == id }
    
    /**
     * Get an inventory item by shareable code
     */
    fun getItemByShareableCode(code: String): InventoryItem? = currentInventory.find { it.shareableCode == code }
    
    /**
     * Generate a unique shareable code
     */
    fun generateUniqueShareableCode(): String {
        var code = InventoryItem.generateShareableCode()
        while (getItemByShareableCode(code) != null) {
            code = InventoryItem.generateShareableCode()
        }
        return code
    }
    
    /**
     * Add a new inventory item
     * Returns null if there is no active session
     */
    fun addItem(
        name: String,
        description: String,
        color: String,
        categoryId: String,
        price: Double,
        cost: Double,
        quantity: Int,
        location: String,
        minStock: Int = 0,
        maxStock: Int = Int.MAX_VALUE,
        imageUrl: String? = null,
        tags: List<String> = emptyList()
    ): InventoryItem? {
        // Check if there's an active session
        val session = currentSession ?: return null
        
        val item = InventoryItem(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            color = color,
            categoryId = categoryId,
            shareableCode = generateUniqueShareableCode(),
            price = price,
            cost = cost,
            quantity = quantity,
            location = location,
            minStock = minStock,
            maxStock = maxStock,
            imageUrl = imageUrl,
            tags = tags
        )
        
        // Add to both inventory and current session
        currentInventory.add(item)
        session.items.add(item)
        
        // Record transaction
        val transaction = InventoryTransaction(
            id = UUID.randomUUID().toString(),
            itemId = item.id,
            quantity = quantity,
            transactionType = TransactionType.ADDITION,
            reason = "Initial item creation"
        )
        session.transactions.add(transaction)
        
        // Save changes
        saveInventory()
        storageManager.saveSession(session)
        updateAnalytics()
        
        return item
    }
    
    /**
     * Update an existing inventory item
     */
    fun updateItem(item: InventoryItem) {
        val index = currentInventory.indexOfFirst { it.id == item.id }
        if (index != -1) {
            currentInventory[index] = item
            saveInventory()
            updateAnalytics()
        }
    }
    
    /**
     * Delete an inventory item
     */
    fun deleteItem(id: String) {
        val removed = currentInventory.removeIf { it.id == id }
        if (removed) {
            saveInventory()
            updateAnalytics()
        }
    }
    
    /**
     * Load inventory from storage
     */
    private fun loadInventory() {
        if (isStoragePathDefined()) {
            try {
                currentInventory = storageManager.loadInventory().toMutableList()
            } catch (e: Exception) {
                println("Failed to load inventory: ${e.message}")
                currentInventory = mutableListOf()
            }
        }
    }
    
    /**
     * Save inventory to storage
     */
    private fun saveInventory() {
        if (isStoragePathDefined()) {
            storageManager.saveInventory(currentInventory)
        }
    }
    
    /**
     * Load sessions list
     */
    private fun loadSessionsList() {
        if (isStoragePathDefined()) {
            allSessions = storageManager.getAllSessions()
        }
    }
    
    /**
     * Load analytics
     */
    private fun loadAnalytics() {
        if (isStoragePathDefined()) {
            analytics = storageManager.loadAnalytics()
        }
    }
    
    /**
     * Create a new inventory session
     */
    fun createSession(name: String, description: String): InventorySession? {
        if (!isStoragePathDefined()) {
            return null
        }
        
        val session = InventorySession(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            items = currentInventory.toMutableList()
        )
        currentSession = session
        storageManager.saveSession(session)
        refreshSessions()
        return session
    }
    
    /**
     * Load an existing session
     */
    fun loadSession(sessionId: String): InventorySession? {
        if (!isStoragePathDefined()) {
            return null
        }
        
        val session = storageManager.loadSession(sessionId)
        if (session != null) {
            currentSession = session
        }
        return session
    }
    
    /**
     * Close the current session and transfer items to backlog
     */
    fun closeCurrentSession(): Boolean {
        if (!isStoragePathDefined()) {
            return false
        }
        
        currentSession?.let { session ->
            // Update the main inventory with the latest quantities from the session
            session.items.forEach { sessionItem ->
                val inventoryItemIndex = currentInventory.indexOfFirst { it.id == sessionItem.id }
                if (inventoryItemIndex != -1) {
                    currentInventory[inventoryItemIndex] = sessionItem
                }
            }
            
            // Mark session as closed
            session.endDate = DateUtil.now()
            storageManager.saveSession(session)
            
            // Save updated inventory
            saveInventory()
            
            currentSession = null
            refreshSessions()
            updateAnalytics()
            return true
        }
        return false
    }
    
    /**
     * Get all sessions
     */
    fun getAllSessions(): List<SessionSummary> = allSessions
    
    /**
     * Get the current active session
     */
    fun getCurrentSession(): InventorySession? = currentSession
    
    /**
     * Refresh the sessions list
     */
    private fun refreshSessions() {
        if (isStoragePathDefined()) {
            allSessions = storageManager.getAllSessions()
        }
    }
    
    /**
     * Delete a session
     */
    fun deleteSession(sessionId: String): Boolean {
        if (!isStoragePathDefined()) {
            return false
        }
        
        if (storageManager.deleteSession(sessionId)) {
            refreshSessions()
            return true
        }
        return false
    }
    
    /**
     * Record a transaction in the current session
     */
    fun recordTransaction(
        itemId: String,
        quantity: Int,
        type: TransactionType, 
        reason: String
    ): Boolean {
        if (!isStoragePathDefined()) {
            return false
        }
        
        val currentSession = currentSession ?: return false
        
        val transaction = InventoryTransaction(
            id = UUID.randomUUID().toString(),
            itemId = itemId,
            quantity = quantity,
            transactionType = type,
            reason = reason
        )
        
        // Update item quantity
        val item = currentInventory.find { it.id == itemId } ?: return false
        when (type) {
            TransactionType.ADDITION -> item.quantity += quantity
            TransactionType.REMOVAL -> item.quantity -= quantity
            TransactionType.ADJUSTMENT -> item.quantity = quantity
        }
        
        // Add transaction to session
        currentSession.transactions.add(transaction)
        
        // Update session item
        val sessionItem = currentSession.items.find { it.id == itemId }
        if (sessionItem != null) {
            val index = currentSession.items.indexOf(sessionItem)
            currentSession.items[index] = item
        }
        
        // Save changes
        saveInventory()
        storageManager.saveSession(currentSession)
        updateAnalytics()
        
        return true
    }
    
    /**
     * Update analytics
     */
    private fun updateAnalytics() {
        if (!isStoragePathDefined()) {
            return
        }
        
        val valueByCategory = mutableMapOf<String, Double>()
        val stockLevels = mutableMapOf<String, Int>()
        val transactionMap = mutableMapOf<String, List<InventoryTransaction>>()
        
        // Calculate value by category
        for (item in currentInventory) {
            val categoryId = item.categoryId
            val value = item.price * item.quantity
            valueByCategory[categoryId] = (valueByCategory[categoryId] ?: 0.0) + value
            stockLevels[item.id] = item.quantity
        }
        
        // Collect transactions
        currentSession?.let { session ->
            for (transaction in session.transactions) {
                val itemId = transaction.itemId
                val transactions = transactionMap[itemId]?.plus(transaction) ?: listOf(transaction)
                transactionMap[itemId] = transactions
            }
        }
        
        analytics = InventoryAnalytics(valueByCategory, stockLevels, transactionMap)
        storageManager.saveAnalytics(analytics)
    }
    
    /**
     * Get analytics
     */
    fun getAnalytics(): InventoryAnalytics = analytics
    
    /**
     * Export all data
     */
    fun exportAllData(targetPath: String): Boolean {
        if (!isStoragePathDefined()) {
            return false
        }
        return storageManager.exportAllData(targetPath)
    }
    
    /**
     * Import all data
     */
    fun importData(sourcePath: String): Boolean {
        if (!isStoragePathDefined()) {
            return false
        }
        
        val success = storageManager.importData(sourcePath)
        if (success) {
            loadCategories()
            loadInventory()
            refreshSessions()
            loadAnalytics()
        }
        return success
    }
}
