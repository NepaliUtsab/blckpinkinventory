package com.blackandpink.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.blackandpink.AppColors
import com.blackandpink.AppIcons
import com.blackandpink.model.InventoryItem
import com.blackandpink.model.TransactionType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    categoryName: String = "",
    onEditClick: (InventoryItem) -> Unit = {},
    onDeleteClick: (InventoryItem) -> Unit = {},
    onQuantityAdd: (InventoryItem) -> Unit = {},
    onQuantityRemove: (InventoryItem) -> Unit = {},
    onItemClick: (InventoryItem) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onItemClick(item) },
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row {
                if (item.color.isNotEmpty()) {
                    Text(
                        text = "Color: ${item.color}",
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(
                    text = "Category: $categoryName",
                    style = MaterialTheme.typography.caption
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row {
                Text(
                    text = "Code: ${item.shareableCode}",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.description,
                style = MaterialTheme.typography.body2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stock status with improved visual style
            Surface(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
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
                    
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(stockColor, shape = RoundedCornerShape(6.dp))
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Qty: ${item.quantity}",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "Price: $${item.price}",
                        style = MaterialTheme.typography.body2
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "Location: ${item.location}",
                        style = MaterialTheme.typography.caption
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Actions row with improved styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onQuantityRemove(item) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colors.error.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = AppIcons.Remove,
                        contentDescription = "Remove Quantity",
                        tint = MaterialTheme.colors.error
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = { onQuantityAdd(item) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Green.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Quantity",
                        tint = Color.Green
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = { onEditClick(item) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Item",
                        tint = MaterialTheme.colors.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = { onDeleteClick(item) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colors.error.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Item",
                        tint = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}
}

@Composable
fun TagChip(text: String) {
    Surface(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = AppColors.Pink,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.Pink.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.caption,
            color = AppColors.Pink,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun SessionCard(
    name: String,
    description: String,
    startDate: String,
    endDate: String?,
    itemCount: Int,
    totalValue: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.body2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colors.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Start: ${startDate.substringBefore('T')}",
                            style = MaterialTheme.typography.caption
                        )
                    }
                    
                    endDate?.let {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colors.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "End: ${it.substringBefore('T')}",
                                style = MaterialTheme.typography.caption
                            )
                        }
                    } ?: Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Green
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Active Session",
                            style = MaterialTheme.typography.caption,
                            color = Color.Green
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = "$itemCount items",
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "$${"%.2f".format(totalValue)}",
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryTransactionDialog(
    item: InventoryItem,
    onDismiss: () -> Unit,
    onSubmit: (InventoryItem, Int, TransactionType, String) -> Unit
) {
    var quantity by remember { mutableStateOf(item.quantity.toString()) }
    var reason by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf(TransactionType.ADJUSTMENT) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Inventory") },
        text = {
            Column {
                Card(
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.Inventory,
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.subtitle1,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Current Quantity: ${item.quantity}",
                                style = MaterialTheme.typography.caption
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Transaction Type",
                    style = MaterialTheme.typography.subtitle2
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    TransactionTypeOption(
                        text = "Add",
                        selected = transactionType == TransactionType.ADDITION,
                        onClick = { transactionType = TransactionType.ADDITION },
                        modifier = Modifier.weight(1f)
                    )
                    
                    TransactionTypeOption(
                        text = "Remove",
                        selected = transactionType == TransactionType.REMOVAL,
                        onClick = { transactionType = TransactionType.REMOVAL },
                        modifier = Modifier.weight(1f)
                    )
                    
                    TransactionTypeOption(
                        text = "Set",
                        selected = transactionType == TransactionType.ADJUSTMENT,
                        onClick = { transactionType = TransactionType.ADJUSTMENT },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Transaction Details",
                    style = MaterialTheme.typography.subtitle2
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { 
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            quantity = it
                        }
                    },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val quantityValue = quantity.toIntOrNull() ?: 0
                    if (quantityValue >= 0 && reason.isNotBlank()) {
                        onSubmit(item, quantityValue, transactionType, reason)
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun TransactionTypeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    
    Surface(
        color = if (selected) AppColors.Pink else Color.Transparent,
        contentColor = if (selected) Color.White else MaterialTheme.colors.onSurface,
        shape = shape,
        modifier = modifier
            .padding(4.dp)
            .border(1.dp, AppColors.Pink, shape)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.button,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InventoryItemListItem(
    item: InventoryItem,
    categoryName: String = "",
    onEditClick: (InventoryItem) -> Unit = {},
    onDeleteClick: (InventoryItem) -> Unit = {},
    onQuantityAdd: (InventoryItem) -> Unit = {},
    onQuantityRemove: (InventoryItem) -> Unit = {},
    onItemClick: (InventoryItem) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onItemClick(item) },
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        SelectionContainer {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item info section
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Code: ${item.shareableCode}",
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        
                        if (categoryName.isNotEmpty()) {
                            Text(
                                text = "• $categoryName",
                                style = MaterialTheme.typography.caption
                            )
                        }
                    }
                }
                
                // Quantity and stock status
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
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
                    
                    Text(
                        text = "${item.quantity}",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        color = stockColor
                    )
                    
                    Text(
                        text = stockStatus,
                        style = MaterialTheme.typography.caption,
                        color = stockColor
                    )
                }
                
                // Price
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "$${item.price}",
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = item.location,
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Action buttons (compact)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onQuantityRemove(item) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Remove,
                            contentDescription = "Remove Quantity",
                            tint = MaterialTheme.colors.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { onQuantityAdd(item) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Quantity",
                            tint = Color.Green,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { onEditClick(item) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Item",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
