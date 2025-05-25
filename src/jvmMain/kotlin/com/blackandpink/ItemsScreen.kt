package com.blackandpink

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blackandpink.components.ItemCard
import com.blackandpink.model.Item
import com.blackandpink.model.ItemRepository

@Composable
fun ItemsScreen(onNavigateBack: () -> Unit) {
    val itemRepository = remember { ItemRepository() }
    var items by remember { mutableStateOf(itemRepository.getAllItems()) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Items") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { 
                    selectedItem = null
                    showDialog = true 
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        )
        
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No items yet. Click the '+' button to add one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    ItemCard(
                        item = item,
                        onEditClick = { 
                            selectedItem = it
                            showDialog = true
                        },
                        onDeleteClick = {
                            itemRepository.deleteItem(it.id)
                            items = itemRepository.getAllItems()
                        }
                    )
                }
                
                item {
                    // Add some padding at the bottom
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
    
    if (showDialog) {
        ItemDialog(
            item = selectedItem,
            onDismiss = { showDialog = false },
            onSave = { item ->
                if (selectedItem == null) {
                    itemRepository.addItem(item)
                } else {
                    itemRepository.updateItem(item)
                }
                items = itemRepository.getAllItems()
                showDialog = false
            }
        )
    }
}

@Composable
fun ItemDialog(
    item: Item?,
    onDismiss: () -> Unit,
    onSave: (Item) -> Unit
) {
    val isNewItem = item == null
    val id = item?.id ?: "${System.currentTimeMillis()}"
    var name by remember { mutableStateOf(item?.name ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var tagsInput by remember { mutableStateOf(item?.tags?.joinToString(", ") ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNewItem) "Add New Item" else "Edit Item") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                
                TextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val tags = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val newItem = Item(id, name, description, tags = tags)
                onSave(newItem)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
