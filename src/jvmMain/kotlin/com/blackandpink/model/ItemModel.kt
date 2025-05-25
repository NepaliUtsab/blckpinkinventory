package com.blackandpink.model

/**
 * Represents a generic item in our application
 */
data class Item(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val tags: List<String> = emptyList()
)

/**
 * Repository that handles items data
 */
class ItemRepository {
    // In-memory database of items
    private val items = mutableListOf<Item>()

    fun getAllItems(): List<Item> = items.toList()
    
    fun getItemById(id: String): Item? = items.find { it.id == id }
    
    fun addItem(item: Item) {
        items.add(item)
    }
    
    fun updateItem(item: Item) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
        }
    }
    
    fun deleteItem(id: String) {
        items.removeIf { it.id == id }
    }
}
