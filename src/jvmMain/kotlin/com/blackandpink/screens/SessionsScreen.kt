package com.blackandpink.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blackandpink.components.SessionCard
import com.blackandpink.model.InventoryRepository
import com.blackandpink.model.InventorySession
import com.blackandpink.model.SessionSummary
import com.blackandpink.util.DateUtil

@Composable
fun SessionsScreen(
    repository: InventoryRepository,
    onNavigateBack: () -> Unit,
    onSessionSelected: (String) -> Unit,
    onCreateNewSession: () -> Unit
) {
    var sessions by remember { mutableStateOf(repository.getAllSessions()) }
    var showDeleteConfirm by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory Sessions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = onCreateNewSession,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(Icons.Default.Add, "New Session", modifier = Modifier.padding(end = 8.dp))
                        Text("New Session")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "No sessions",
                            modifier = Modifier.size(48.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "No inventory sessions found.",
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Create a new session to start tracking inventory.",
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(onClick = onCreateNewSession) {
                            Icon(Icons.Default.Add, "New Session", modifier = Modifier.padding(end = 8.dp))
                            Text("Create New Session")
                        }
                    }
                }
            } else {
                LazyColumn {
                    items(sessions) { session ->
                        Box {
                            SessionCard(
                                name = session.name,
                                description = "",
                                startDate = session.startDate,
                                endDate = session.endDate,
                                itemCount = session.itemCount,
                                totalValue = session.totalValue,
                                onClick = { onSessionSelected(session.id) }
                            )
                            
                            // Only show delete for closed sessions
                            if (session.endDate != null) {
                                IconButton(
                                    onClick = { showDeleteConfirm = session.id },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(Icons.Default.Delete, "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog
    showDeleteConfirm?.let { sessionId ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Session") },
            text = { Text("Are you sure you want to delete this session? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        repository.deleteSession(sessionId)
                        sessions = repository.getAllSessions()
                        showDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Inventory Session") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Session Name") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, description) },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SessionDetailsScreen(
    sessionId: String,
    repository: InventoryRepository,
    onNavigateBack: () -> Unit
) {
    val session = remember {
        repository.loadSession(sessionId)
    }
    
    val isActive = remember(session) {
        session?.endDate == null
    }
    
    if (session == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Session not found",
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
    
    var showCloseDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(session.name)
                        Text(
                            text = if (isActive) "Active Session" else "Closed Session",
                            style = MaterialTheme.typography.caption
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (isActive) {
                        Button(
                            onClick = { showCloseDialog = true },
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text("Close Session")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            // Session details
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Session Details",
                        style = MaterialTheme.typography.h6
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Start Date:", style = MaterialTheme.typography.subtitle2)
                            Text(session.startDate.substringBefore('T'))
                        }
                        
                        if (session.endDate != null) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("End Date:", style = MaterialTheme.typography.subtitle2)
                                Text(session.endDate!!.substringBefore('T'))
                            }
                        }
                    }
                    
                    if (session.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Description:", style = MaterialTheme.typography.subtitle2)
                        Text(session.description)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Divider()
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Items:", style = MaterialTheme.typography.subtitle2)
                            Text("${session.items.size}")
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Value:", style = MaterialTheme.typography.subtitle2)
                            Text("${"$%.2f".format(session.items.sumOf { it.price * it.quantity })}")
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Transactions:", style = MaterialTheme.typography.subtitle2)
                            Text("${session.transactions.size}")
                        }
                    }
                }
            }
            
            // Tabs for items and transactions
            var selectedTabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("Items", "Transactions")
            
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }
            
            when (selectedTabIndex) {
                0 -> {
                    // Items tab
                    if (session.items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No items in this session.")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(session.items) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    elevation = 2.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.subtitle1
                                            )
                                            Text(
                                                text = "Code: ${item.shareableCode} | Color: ${item.color} | Category: ${repository.getCategoryById(item.categoryId)?.name ?: "Unknown"}",
                                                style = MaterialTheme.typography.caption
                                            )
                                        }
                                        
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${item.quantity} units",
                                                style = MaterialTheme.typography.body2
                                            )
                                            Text(
                                                text = "${"$%.2f".format(item.price * item.quantity)}",
                                                style = MaterialTheme.typography.subtitle2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Transactions tab
                    if (session.transactions.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No transactions in this session.")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(session.transactions.sortedByDescending { it.timestamp }) { transaction ->
                                val relatedItem = session.items.find { it.id == transaction.itemId }
                                
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    elevation = 2.dp
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = relatedItem?.name ?: "Unknown Item",
                                                style = MaterialTheme.typography.subtitle1
                                            )
                                            
                                            val typeText = when(transaction.transactionType) {
                                                com.blackandpink.model.TransactionType.ADDITION -> "+${transaction.quantity}"
                                                com.blackandpink.model.TransactionType.REMOVAL -> "-${transaction.quantity}"
                                                com.blackandpink.model.TransactionType.ADJUSTMENT -> "Set to ${transaction.quantity}"
                                            }
                                            
                                            Text(
                                                text = typeText,
                                                style = MaterialTheme.typography.subtitle2
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        Text(
                                            text = "Reason: ${transaction.reason}",
                                            style = MaterialTheme.typography.body2
                                        )
                                        
                                        Text(
                                            text = "Time: ${transaction.timestamp.substringBefore('T')} ${transaction.timestamp.substringAfter('T').substringBefore('.')}",
                                            style = MaterialTheme.typography.caption
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Close session dialog
    if (showCloseDialog) {
        AlertDialog(
            onDismissRequest = { showCloseDialog = false },
            title = { Text("Close Session") },
            text = { Text("Are you sure you want to close this inventory session? You won't be able to add more transactions to it.") },
            confirmButton = {
                Button(onClick = {
                    repository.closeCurrentSession()
                    showCloseDialog = false
                    onNavigateBack() // Navigate back after closing
                }) {
                    Text("Close Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
