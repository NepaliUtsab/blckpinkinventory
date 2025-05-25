package com.blackandpink.model

import kotlinx.serialization.*
import java.time.LocalDateTime
import com.blackandpink.util.DateUtil
import kotlin.random.Random

/**
 * Represents a product category
 */
@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: String = DateUtil.now()
)

/**
 * Represents an inventory item with stock management information
 */
@Serializable
data class InventoryItem(
    val id: String,
    val name: String,
    val description: String,
    val color: String = "",
    val categoryId: String,
    val shareableCode: String, // Six-digit alphanumeric code
    val price: Double,
    val cost: Double,
    var quantity: Int,
    val location: String,
    val minStock: Int = 0,
    val maxStock: Int = Int.MAX_VALUE,
    val imageUrl: String? = null,
    val tags: List<String> = emptyList(),
    val lastUpdated: String = DateUtil.now()
) {
    companion object {
        /**
         * Generates a random 6-character alphanumeric shareable code
         */
        fun generateShareableCode(): String {
            val allowedChars = ('A'..'Z') + ('0'..'9')
            return (1..6)
                .map { allowedChars.random() }
                .joinToString("")
        }
    }
}

/**
 * Represents a transaction (addition or removal of inventory)
 */
@Serializable
data class InventoryTransaction(
    val id: String,
    val itemId: String,
    val quantity: Int,
    val transactionType: TransactionType,
    val reason: String,
    val timestamp: String = DateUtil.now()
)

/**
 * Type of inventory transaction
 */
@Serializable
enum class TransactionType {
    ADDITION, REMOVAL, ADJUSTMENT
}

/**
 * Represents an inventory counting session
 */
@Serializable
data class InventorySession(
    val id: String,
    val name: String,
    val description: String,
    val startDate: String = DateUtil.now(),
    var endDate: String? = null,
    val items: MutableList<InventoryItem> = mutableListOf(),
    val transactions: MutableList<InventoryTransaction> = mutableListOf()
)

/**
 * Summary information for a session (used in the sessions list)
 */
@Serializable
data class SessionSummary(
    val id: String,
    val name: String,
    val startDate: String,
    val endDate: String? = null,
    val itemCount: Int,
    val totalValue: Double
)

/**
 * Analytics data for inventory
 */
@Serializable
data class InventoryAnalytics(
    val itemValueByCategory: Map<String, Double>,
    val stockLevelsByItem: Map<String, Int>,
    val transactionHistory: Map<String, List<InventoryTransaction>>
)

/**
 * Application settings
 */
@Serializable
data class AppSettings(
    val storagePath: String? = null,
    val darkMode: Boolean = true,
    val enableNotifications: Boolean = false,
    val lowStockAlerts: Boolean = true
)
