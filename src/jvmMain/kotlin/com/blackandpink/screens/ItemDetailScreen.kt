package com.blackandpink.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.blackandpink.components.TagChip
import com.blackandpink.components.InventoryTransactionDialog
import com.blackandpink.model.*
import com.blackandpink.util.DateUtil
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.blackandpink.AppColors
import com.blackandpink.AppIcons

/**
 * Screen to display detailed information about an inventory item
 */
@Composable
fun ItemDetailScreen(
    itemId: String,
    repository: InventoryRepository,
    onNavigateBack: () -> Unit,
    onEditItem: (InventoryItem) -> Unit
) {
    // Load item details
    val item = remember { repository.getItemById(itemId) }
    val category = remember(item) { 
        item?.categoryId?.let { repository.getCategoryById(it) }
    }
    
    // Get item transactions
    val analytics = remember { repository.getAnalytics() }
    val transactions = remember(analytics) {
        analytics.transactionHistory[itemId] ?: emptyList()
    }
    
    if (item == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Item not found",
                    style = MaterialTheme.typography.h6
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = onNavigateBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }
    
    var showTransactionDialog by remember { mutableStateOf(false) }
    var showSessionInfoDialog by remember { mutableStateOf(false) }
    val hasActiveSession = repository.getCurrentSession() != null
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditItem(item) }) {
                        Icon(Icons.Default.Edit, "Edit Item")
                    }
                },
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        floatingActionButton = {
            if (hasActiveSession) {
                FloatingActionButton(
                    onClick = { showTransactionDialog = true },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
            }
        }
    ) { padding ->
        SelectionContainer {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
            // General item information card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Item Details",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Name:", style = MaterialTheme.typography.subtitle2)
                                Text(item.name, style = MaterialTheme.typography.body1)
                            }
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Shareable Code:", style = MaterialTheme.typography.subtitle2)
                                Text(item.shareableCode, style = MaterialTheme.typography.body1)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Color:", style = MaterialTheme.typography.subtitle2)
                                Text(item.color, style = MaterialTheme.typography.body1)
                            }
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Category:", style = MaterialTheme.typography.subtitle2)
                                Text(category?.name ?: "Unknown", style = MaterialTheme.typography.body1)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (item.description.isNotBlank()) {
                            Text("Description:", style = MaterialTheme.typography.subtitle2)
                            Text(item.description, style = MaterialTheme.typography.body1)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Price:", style = MaterialTheme.typography.subtitle2)
                                Text("$${item.price}", style = MaterialTheme.typography.body1)
                            }
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Cost:", style = MaterialTheme.typography.subtitle2)
                                Text("$${item.cost}", style = MaterialTheme.typography.body1)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Quantity section with quick modification controls
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 2.dp,
                            backgroundColor = MaterialTheme.colors.surface,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Quantity Management",
                                    style = MaterialTheme.typography.subtitle1,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Current quantity display with stock status
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Color indicator for stock level
                                    val stockStatus = when {
                                        item.quantity <= item.minStock -> "Low"
                                        item.quantity >= item.maxStock -> "Full"
                                        else -> "OK"
                                    }
                                    
                                    val stockColor = when(stockStatus) {
                                        "Low" -> Color.Red
                                        "OK" -> Color.Green
                                        else -> Color.Blue
                                    }
                                    
                                    Surface(
                                        color = stockColor,
                                        shape = MaterialTheme.shapes.small,
                                        modifier = Modifier.size(10.dp)
                                    ) {}
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        "Current Quantity: ${item.quantity} (${stockStatus})",
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Quick action buttons for inventory adjustments
                                if (hasActiveSession) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        // Remove 1
                                        QuickActionButton(
                                            text = "-1",
                                            onClick = {
                                                repository.recordTransaction(
                                                    itemId = item.id,
                                                    quantity = 1,
                                                    type = TransactionType.REMOVAL,
                                                    reason = "Quick removal"
                                                )
                                            },
                                            icon = AppIcons.Remove,
                                            backgroundColor = Color.Red.copy(alpha = 0.8f)
                                        )
                                        
                                        // Remove 5
                                        QuickActionButton(
                                            text = "-5",
                                            onClick = {
                                                repository.recordTransaction(
                                                    itemId = item.id,
                                                    quantity = 5,
                                                    type = TransactionType.REMOVAL,
                                                    reason = "Bulk removal"
                                                )
                                            },
                                            icon = AppIcons.Remove,
                                            backgroundColor = Color.Red.copy(alpha = 0.6f)
                                        )
                                        
                                        // Add 1
                                        QuickActionButton(
                                            text = "+1",
                                            onClick = {
                                                repository.recordTransaction(
                                                    itemId = item.id,
                                                    quantity = 1,
                                                    type = TransactionType.ADDITION,
                                                    reason = "Quick addition"
                                                )
                                            },
                                            icon = Icons.Default.Add,
                                            backgroundColor = Color.Green.copy(alpha = 0.6f)
                                        )
                                        
                                        // Add 5
                                        QuickActionButton(
                                            text = "+5",
                                            onClick = {
                                                repository.recordTransaction(
                                                    itemId = item.id,
                                                    quantity = 5,
                                                    type = TransactionType.ADDITION,
                                                    reason = "Bulk addition"
                                                )
                                            },
                                            icon = Icons.Default.Add,
                                            backgroundColor = Color.Green.copy(alpha = 0.8f)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    OutlinedButton(
                                        onClick = { showTransactionDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Custom Transaction")
                                    }
                                } else {
                                    // Show message if no active session
                                    OutlinedButton(
                                        onClick = { showSessionInfoDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Create Session to Modify Quantity")
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Location:", style = MaterialTheme.typography.subtitle2)
                                Text(item.location, style = MaterialTheme.typography.body1)
                            }
                        }
                        
                        if (item.tags.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text("Tags:", style = MaterialTheme.typography.subtitle2)
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                item.tags.forEach { tag ->
                                    TagChip(tag)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Divider()
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            "Last Updated: ${item.lastUpdated.substringBefore('T')}",
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
            
            // Item history/transactions card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Transaction History",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (transactions.isEmpty()) {
                            Text(
                                "No transactions recorded for this item",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            Column {
                                transactions.sortedByDescending { it.timestamp }.forEach { transaction ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(0.7f)) {
                                            // Transaction type and reason
                                            val typeText = when(transaction.transactionType) {
                                                TransactionType.ADDITION -> "Added"
                                                TransactionType.REMOVAL -> "Removed"
                                                TransactionType.ADJUSTMENT -> "Adjusted"
                                            }
                                            
                                            Text(
                                                "$typeText: ${transaction.reason}",
                                                style = MaterialTheme.typography.body1
                                            )
                                            
                                            Text(
                                                "Date: ${transaction.timestamp.substringBefore('T')} ${transaction.timestamp.substringAfter('T').substringBefore('.')}",
                                                style = MaterialTheme.typography.caption
                                            )
                                        }
                                        
                                        // Quantity change
                                        val quantityText = when(transaction.transactionType) {
                                            TransactionType.ADDITION -> "+${transaction.quantity}"
                                            TransactionType.REMOVAL -> "-${transaction.quantity}"
                                            TransactionType.ADJUSTMENT -> "=${transaction.quantity}"
                                        }
                                        
                                        val quantityColor = when(transaction.transactionType) {
                                            TransactionType.ADDITION -> Color.Green
                                            TransactionType.REMOVAL -> Color.Red
                                            TransactionType.ADJUSTMENT -> MaterialTheme.colors.onSurface
                                        }
                                        
                                        Text(
                                            quantityText,
                                            style = MaterialTheme.typography.subtitle1,
                                            fontWeight = FontWeight.Bold,
                                            color = quantityColor,
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }
                                    
                                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }
    
    // Transaction dialog
    if (showTransactionDialog) {
        InventoryTransactionDialog(
            item = item,
            onDismiss = { showTransactionDialog = false },
            onSubmit = { updatedItem: InventoryItem, quantity: Int, type: TransactionType, reason: String ->
                repository.recordTransaction(updatedItem.id, quantity, type, reason)
                showTransactionDialog = false
            }
        )
    }
    
    // Session info dialog
    if (showSessionInfoDialog) {
        AlertDialog(
            onDismissRequest = { showSessionInfoDialog = false },
            title = { Text("Active Session Required") },
            text = { 
                Text("You need to create and activate a session before modifying inventory. " +
                    "Sessions allow tracking of inventory changes and maintaining history.")
            },
            confirmButton = { 
                Button(
                    onClick = { showSessionInfoDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(40.dp).width(60.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.button,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}
