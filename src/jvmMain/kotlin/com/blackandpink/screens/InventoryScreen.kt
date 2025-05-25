package com.blackandpink.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.blackandpink.components.InventoryItemCard
import com.blackandpink.components.InventoryItemListItem
import com.blackandpink.components.InventoryTransactionDialog
import com.blackandpink.model.*
import java.util.UUID
import com.blackandpink.util.DateUtil
import com.blackandpink.AppIcons

@Composable
fun InventoryScreen(
    repository: InventoryRepository,
    onNavigateBack: () -> Unit,
    onItemSelected: (String) -> Unit = {}
) {
    var items by remember { mutableStateOf(repository.getAllItems()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }
    var showTransactionDialog by remember { mutableStateOf(false) }
    var categories by remember { mutableStateOf(repository.getAllCategories()) }
    // This variable could be used in the future to show an error dialog
    // var showNoSessionError by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    
    // Filter variables
    var categoryFilter by remember { mutableStateOf("All") }
    var searchText by remember { mutableStateOf("") }
    var sessionFilter by remember { mutableStateOf("All") }
    var viewMode by remember { mutableStateOf("Card") } // "Card" or "List"
    
    // Get all sessions for session filter
    val sessions = remember { repository.getAllSessions() }
    val sessionNames = remember(sessions) {
        listOf("All") + sessions.map { it.name }.sorted()
    }
    
    // Calculate category names for display
    val categoryNames = remember(categories) {
        listOf("All") + categories.map { it.name }.sorted()
    }
    
    // Filter items
    val filteredItems = remember(items, categoryFilter, searchText, sessionFilter) {
        items.filter { item ->
            val category = categories.find { it.id == item.categoryId }
            val categoryName = category?.name ?: ""
            
            // Session filtering
            val sessionMatches = if (sessionFilter == "All") {
                true
            } else {
                val session = sessions.find { it.name == sessionFilter }
                session?.let { sessionSummary ->
                    val fullSession = repository.loadSession(sessionSummary.id)
                    fullSession?.transactions?.any { transaction ->
                        transaction.itemId == item.id
                    } ?: false
                } ?: false
            }
            
            (categoryFilter == "All" || categoryName == categoryFilter) &&
            sessionMatches &&
            (searchText.isEmpty() || 
             item.name.contains(searchText, ignoreCase = true) || 
             item.shareableCode.contains(searchText, ignoreCase = true) ||
             item.description.contains(searchText, ignoreCase = true))
        }
    }
    
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Inventory") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Help button
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Help"
                        )
                    }
                    
                    // Session status indicator
                    val hasActiveSession = repository.getCurrentSession() != null
                    val sessionStatus = if (hasActiveSession) {
                        "Active Session"
                    } else {
                        "No Active Session"
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (hasActiveSession) Color.Green else Color.Red,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sessionStatus,
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Category filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Category:",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                var expanded by remember { mutableStateOf(false) }
                
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(categoryFilter)
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categoryNames.forEach { category ->
                            DropdownMenuItem(
                                onClick = {
                                    categoryFilter = category
                                    expanded = false
                                }
                            ) {
                                Text(category)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Session filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Session:",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                var sessionExpanded by remember { mutableStateOf(false) }
                
                Box {
                    OutlinedButton(
                        onClick = { sessionExpanded = true },
                        modifier = Modifier.width(200.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = AppIcons.Filter,
                                contentDescription = "Filter",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(sessionFilter)
                        }
                    }
                    
                    DropdownMenu(
                        expanded = sessionExpanded,
                        onDismissRequest = { sessionExpanded = false }
                    ) {
                        sessionNames.forEach { session ->
                            DropdownMenuItem(
                                onClick = {
                                    sessionFilter = session
                                    sessionExpanded = false
                                }
                            ) {
                                Text(session)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // View mode toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "View Mode:",
                    style = MaterialTheme.typography.body1
                )
                
                Row {
                    // Card view button
                    Button(
                        onClick = { viewMode = "Card" },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (viewMode == "Card") 
                                MaterialTheme.colors.primary 
                            else 
                                MaterialTheme.colors.surface
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Grid,
                            contentDescription = "Card View",
                            modifier = Modifier.size(16.dp),
                            tint = if (viewMode == "Card") 
                                MaterialTheme.colors.onPrimary 
                            else 
                                MaterialTheme.colors.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Cards",
                            color = if (viewMode == "Card") 
                                MaterialTheme.colors.onPrimary 
                            else 
                                MaterialTheme.colors.onSurface
                        )
                    }
                    
                    // List view button
                    Button(
                        onClick = { viewMode = "List" },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (viewMode == "List") 
                                MaterialTheme.colors.primary 
                            else 
                                MaterialTheme.colors.surface
                        )
                    ) {
                        Icon(
                            imageVector = AppIcons.List,
                            contentDescription = "List View",
                            modifier = Modifier.size(16.dp),
                            tint = if (viewMode == "List") 
                                MaterialTheme.colors.onPrimary 
                            else 
                                MaterialTheme.colors.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "List",
                            color = if (viewMode == "List") 
                                MaterialTheme.colors.onPrimary 
                            else 
                                MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items found",
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn {
                    items(filteredItems) { item ->
                        val categoryName = categories.find { it.id == item.categoryId }?.name ?: "Unknown"
                        
                        if (viewMode == "Card") {
                            InventoryItemCard(
                                item = item,
                                categoryName = categoryName,
                                onEditClick = {
                                    selectedItem = item
                                    showAddDialog = true
                                },
                                onDeleteClick = {
                                    repository.deleteItem(item.id)
                                    items = repository.getAllItems()
                                },
                                onQuantityAdd = {
                                    selectedItem = item
                                    showTransactionDialog = true
                                },
                                onQuantityRemove = {
                                    selectedItem = item
                                    showTransactionDialog = true
                                },
                                onItemClick = {
                                    onItemSelected(item.id)
                                }
                            )
                        } else {
                            InventoryItemListItem(
                                item = item,
                                categoryName = categoryName,
                                onEditClick = {
                                    selectedItem = item
                                    showAddDialog = true
                                },
                                onDeleteClick = {
                                    repository.deleteItem(item.id)
                                    items = repository.getAllItems()
                                },
                                onQuantityAdd = {
                                    selectedItem = item
                                    showTransactionDialog = true
                                },
                                onQuantityRemove = {
                                    selectedItem = item
                                    showTransactionDialog = true
                                },
                                onItemClick = {
                                    onItemSelected(item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Add/edit dialog
    if (showAddDialog) {
        val isEditing = selectedItem != null
        
        val id = remember { selectedItem?.id ?: UUID.randomUUID().toString() }
        var name by remember { mutableStateOf(selectedItem?.name ?: "") }
        var description by remember { mutableStateOf(selectedItem?.description ?: "") }
        var color by remember { mutableStateOf(selectedItem?.color ?: "") }
        var categoryId by remember { mutableStateOf(selectedItem?.categoryId ?: if (categories.isNotEmpty()) categories.first().id else "") }
        var price by remember { mutableStateOf(selectedItem?.price?.toString() ?: "0.0") }
        var cost by remember { mutableStateOf(selectedItem?.cost?.toString() ?: "0.0") }
        var quantity by remember { mutableStateOf(selectedItem?.quantity?.toString() ?: "0") }
        var location by remember { mutableStateOf(selectedItem?.location ?: "") }
        var minStock by remember { mutableStateOf(selectedItem?.minStock?.toString() ?: "0") }
        var maxStock by remember { mutableStateOf(selectedItem?.maxStock?.toString() ?: "10000") }
        val shareableCode = remember { selectedItem?.shareableCode ?: InventoryItem.generateShareableCode() }
        var tagsInput by remember { mutableStateOf(selectedItem?.tags?.joinToString(",") ?: "") }
        
        var showAddCategoryDialog by remember { mutableStateOf(false) }
        var newCategoryName by remember { mutableStateOf("") }
        var newCategoryDescription by remember { mutableStateOf("") }
        
        if (showAddCategoryDialog) {
            AlertDialog(
                onDismissRequest = { showAddCategoryDialog = false },
                title = { Text("Add New Category") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            label = { Text("Category Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = newCategoryDescription,
                            onValueChange = { newCategoryDescription = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                val newCategory = repository.addCategory(newCategoryName, newCategoryDescription)
                                categories = repository.getAllCategories()
                                categoryId = newCategory.id
                                showAddCategoryDialog = false
                                newCategoryName = ""
                                newCategoryDescription = ""
                            }
                        },
                        enabled = newCategoryName.isNotBlank()
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddCategoryDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        AlertDialog(
            onDismissRequest = {
                selectedItem = null
                showAddDialog = false
            },
            title = { Text(if (isEditing) "Edit Item" else "Add New Item") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Color *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Category *",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        
                        var expanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(categories.find { it.id == categoryId }?.name ?: "Select Category")
                            }
                            
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        onClick = {
                                            categoryId = category.id
                                            expanded = false
                                        }
                                    ) {
                                        Text(category.name)
                                    }
                                }
                                
                                Divider()
                                
                                DropdownMenuItem(
                                    onClick = {
                                        expanded = false
                                        showAddCategoryDialog = true
                                    }
                                ) {
                                    Text("+ Add New Category")
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = shareableCode,
                        onValueChange = { /* Read-only */ },
                        label = { Text("Shareable Code") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        readOnly = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price *") },
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = cost,
                            onValueChange = { cost = it },
                            label = { Text("Cost *") },
                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                            singleLine = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Quantity *") },
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location *") },
                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                            singleLine = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = minStock,
                            onValueChange = { minStock = it },
                            label = { Text("Min Stock") },
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = maxStock,
                            onValueChange = { maxStock = it },
                            label = { Text("Max Stock") },
                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                            singleLine = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        label = { Text("Tags (comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "* Required fields",
                        style = MaterialTheme.typography.caption
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Check if there's an active session
                        val hasActiveSession = repository.getCurrentSession() != null
                        
                        if (!hasActiveSession) {
                            // Show alert for missing session
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Please create an active session before adding items",
                                    actionLabel = "Create Session"
                                )
                            }
                            showAddDialog = false
                            selectedItem = null
                        } else if (name.isNotBlank() && categoryId.isNotBlank() && color.isNotBlank()) {
                            val newItem = InventoryItem(
                                id = id,
                                name = name,
                                description = description,
                                color = color,
                                categoryId = categoryId,
                                shareableCode = shareableCode, 
                                price = price.toDoubleOrNull() ?: 0.0,
                                cost = cost.toDoubleOrNull() ?: 0.0,
                                quantity = quantity.toIntOrNull() ?: 0,
                                location = location,
                                minStock = minStock.toIntOrNull() ?: 0,
                                maxStock = maxStock.toIntOrNull() ?: Int.MAX_VALUE,
                                tags = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                            
                            if (isEditing) {
                                repository.updateItem(newItem)
                            } else {
                                val createdItem = repository.addItem(
                                    name = newItem.name,
                                    description = newItem.description,
                                    color = newItem.color,
                                    categoryId = newItem.categoryId,
                                    price = newItem.price,
                                    cost = newItem.cost,
                                    quantity = newItem.quantity,
                                    location = newItem.location,
                                    minStock = newItem.minStock,
                                    maxStock = newItem.maxStock,
                                    tags = newItem.tags
                                )
                                
                                if (createdItem == null) {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Failed to add item. Please create an active session first."
                                        )
                                    }
                                }
                            }
                            items = repository.getAllItems()
                            selectedItem = null
                            showAddDialog = false
                        }
                    },
                    enabled = name.isNotBlank() && categoryId.isNotBlank() && color.isNotBlank() && repository.getCurrentSession() != null
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedItem = null
                    showAddDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Transaction dialog
    if (showTransactionDialog && selectedItem != null) {
        InventoryTransactionDialog(
            item = selectedItem!!,
            onDismiss = {
                showTransactionDialog = false
                selectedItem = null
            },
            onSubmit = { item, quantity, type, reason ->
                // Check if there's an active session first
                if (repository.getCurrentSession() == null) {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = "Please create an active session before updating items"
                        )
                    }
                } else {
                    repository.recordTransaction(item.id, quantity, type, reason)
                    items = repository.getAllItems()
                }
                showTransactionDialog = false
                selectedItem = null
            }
        )
    }
    
    // Help dialog
    if (showHelpDialog) {
        SessionInfoDialog(onDismiss = { showHelpDialog = false })
    }
    
    // Session info dialog
    var showSessionInfoDialog by remember { mutableStateOf(false) }
    
    if (showSessionInfoDialog) {
        SessionInfoDialog(
            onDismiss = { showSessionInfoDialog = false }
        )
    }
}

@Composable
private fun AddItemDialog(
    item: InventoryItem? = null,
    categories: List<Category>,
    isEditing: Boolean = false,
    onSave: (InventoryItem) -> Unit,
    onDismiss: () -> Unit,
    onAddCategory: (name: String, description: String) -> String
) {
    val id = remember { item?.id ?: UUID.randomUUID().toString() }
    var name by remember { mutableStateOf(item?.name ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var color by remember { mutableStateOf(item?.color ?: "") }
    var categoryId by remember { mutableStateOf(item?.categoryId ?: if (categories.isNotEmpty()) categories.first().id else "") }
    var price by remember { mutableStateOf(item?.price?.toString() ?: "0.0") }
    var cost by remember { mutableStateOf(item?.cost?.toString() ?: "0.0") }
    var quantity by remember { mutableStateOf(item?.quantity?.toString() ?: "0") }
    var location by remember { mutableStateOf(item?.location ?: "") }
    var minStock by remember { mutableStateOf(item?.minStock?.toString() ?: "0") }
    var maxStock by remember { mutableStateOf(item?.maxStock?.toString() ?: "10000") }
    val shareableCode = remember { item?.shareableCode ?: InventoryItem.generateShareableCode() }
    var tagsInput by remember { mutableStateOf(item?.tags?.joinToString(",") ?: "") }
    
    // Category add dialog state
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryDescription by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEditing) "Edit Item" else "Add New Item")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Category *",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(categories.find { it.id == categoryId }?.name ?: "Select Category")
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    onClick = {
                                        categoryId = category.id
                                        expanded = false
                                    }
                                ) {
                                    Text(category.name)
                                }
                            }
                            
                            Divider()
                            
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    showAddCategoryDialog = true
                                }
                            ) {
                                Text("+ Add New Category")
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = shareableCode,
                    onValueChange = { /* Read-only */ },
                    label = { Text("Shareable Code") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price *") },
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("Cost *") },
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity *") },
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location *") },
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = minStock,
                        onValueChange = { minStock = it },
                        label = { Text("Min Stock") },
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = maxStock,
                        onValueChange = { maxStock = it },
                        label = { Text("Max Stock") },
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "* Required fields",
                    style = MaterialTheme.typography.caption
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && categoryId.isNotBlank()) {
                        val newItem = InventoryItem(
                            id = id,
                            name = name,
                            description = description,
                            color = color,
                            categoryId = categoryId,
                            shareableCode = shareableCode, 
                            price = price.toDoubleOrNull() ?: 0.0,
                            cost = cost.toDoubleOrNull() ?: 0.0,
                            quantity = quantity.toIntOrNull() ?: 0,
                            location = location,
                            minStock = minStock.toIntOrNull() ?: 0,
                            maxStock = maxStock.toIntOrNull() ?: Int.MAX_VALUE,
                            tags = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        )
                        onSave(newItem)
                    }
                },
                enabled = name.isNotBlank() && categoryId.isNotBlank() && color.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    // Add category dialog
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add New Category") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Category Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = newCategoryDescription,
                        onValueChange = { newCategoryDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            val newId = onAddCategory(newCategoryName, newCategoryDescription)
                            categoryId = newId
                            showAddCategoryDialog = false
                            newCategoryName = ""
                            newCategoryDescription = ""
                        }
                    },
                    enabled = newCategoryName.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddCategoryDialog = false
                        newCategoryName = ""
                        newCategoryDescription = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SessionInfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Session Information") },
        text = {
            Column {
                Text(
                    "Sessions are used to track inventory changes over time.",
                    style = MaterialTheme.typography.body1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "• You must have an active session to add or modify items",
                    style = MaterialTheme.typography.body1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    "• When a session is closed, all items are updated in the main inventory",
                    style = MaterialTheme.typography.body1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    "• All item transactions are recorded within sessions",
                    style = MaterialTheme.typography.body1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "To create a new session, navigate to the Sessions tab.",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}
