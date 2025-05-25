package com.blackandpink.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blackandpink.AppColors
import com.blackandpink.model.InventoryRepository
import kotlin.math.min
import kotlin.random.Random

@Composable
fun AnalyticsScreen(
    repository: InventoryRepository,
    onNavigateBack: () -> Unit
) {
    val analytics = remember { repository.getAnalytics() }
    val items = remember { repository.getAllItems() }
    
    // Generate colors for categories
    val categoryColors = remember {
        analytics.itemValueByCategory.keys.associate { category ->
            category to Color(
                red = Random.nextInt(80, 200),
                green = Random.nextInt(80, 200),
                blue = Random.nextInt(80, 200),
                alpha = 255
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory Analytics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            // Overview card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Inventory Summary",
                            style = MaterialTheme.typography.h6
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SummaryCard(
                                title = "Total Items",
                                value = "${items.size}",
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            )
                            
                            SummaryCard(
                                title = "Total Stock Value",
                                value = "${"$%.2f".format(items.sumOf { it.price * it.quantity })}",
                                modifier = Modifier.weight(1f).padding(start = 8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SummaryCard(
                                title = "Categories",
                                value = "${analytics.itemValueByCategory.size}",
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            )
                            
                            SummaryCard(
                                title = "Low Stock Items",
                                value = "${items.count { it.quantity <= it.minStock }}",
                                modifier = Modifier.weight(1f).padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            
            // Charts and detailed breakdowns
            if (analytics.itemValueByCategory.isNotEmpty()) {
                // Category Chart
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Value by Category",
                                style = MaterialTheme.typography.h6
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Pie chart
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CategoryPieChart(
                                    categories = analytics.itemValueByCategory,
                                    colors = categoryColors
                                )
                                
                                Text(
                                    text = "${"$%.2f".format(analytics.itemValueByCategory.values.sum())}",
                                    style = MaterialTheme.typography.h6
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Legend
                            analytics.itemValueByCategory.entries.chunked(2).forEach { chunk ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    chunk.forEach { (category, value) ->
                                        LegendItem(
                                            color = categoryColors[category] ?: Color.Gray,
                                            category = category,
                                            value = "${"$%.2f".format(value)}",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    
                                    // If we have an odd number of categories, add an empty cell
                                    if (chunk.size == 1) {
                                        Box(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Low stock items
            val lowStockItems = items.filter { it.quantity <= it.minStock }
            if (lowStockItems.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Low Stock Items",
                                style = MaterialTheme.typography.h6
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            lowStockItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.subtitle1
                                        )
                                        Text(
                                            text = "Category: ${repository.getAllCategories().find { it.id == item.categoryId }?.name ?: "Unknown"}",
                                            style = MaterialTheme.typography.caption
                                        )
                                    }
                                    
                                    Text(
                                        text = "${item.quantity}/${item.minStock}",
                                        style = MaterialTheme.typography.body2,
                                        color = Color.Red
                                    )
                                }
                                
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }
            
            // Top items by value
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Top Items by Value",
                            style = MaterialTheme.typography.h6
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        items
                            .sortedByDescending { it.price * it.quantity }
                            .take(5)
                            .forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.subtitle1
                                        )
                                        Text(
                                            text = "${item.quantity} × ${"$%.2f".format(item.price)}",
                                            style = MaterialTheme.typography.caption
                                        )
                                    }
                                    
                                    Text(
                                        text = "${"$%.2f".format(item.price * item.quantity)}",
                                        style = MaterialTheme.typography.body2,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
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

@Composable
fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CategoryPieChart(
    categories: Map<String, Double>,
    colors: Map<String, Color>,
    modifier: Modifier = Modifier
) {
    val totalValue = categories.values.sum()
    
    Canvas(modifier = modifier.size(200.dp)) {
        var startAngle = 0f
        
        categories.forEach { (category, value) ->
            val sweepAngle = (value / totalValue * 360f).toFloat()
            val color = colors[category] ?: Color.Gray
            
            drawPie(startAngle, sweepAngle, color)
            startAngle += sweepAngle
        }
        
        // Draw center circle for donut effect
        val surfaceColor = androidx.compose.ui.graphics.Color.White
        drawCircle(
            color = surfaceColor,
            radius = size.minDimension * 0.3f,
            center = Offset(size.width / 2, size.height / 2)
        )
    }
}

private fun DrawScope.drawPie(
    startAngle: Float,
    sweepAngle: Float,
    color: Color
) {
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = true,
        size = Size(size.minDimension, size.minDimension),
        topLeft = Offset(
            (size.width - size.minDimension) / 2,
            (size.height - size.minDimension) / 2
        )
    )
    
    // Draw outline
    drawArc(
        color = Color.White,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = true,
        style = Stroke(width = 1f),
        size = Size(size.minDimension, size.minDimension),
        topLeft = Offset(
            (size.width - size.minDimension) / 2,
            (size.height - size.minDimension) / 2
        )
    )
}

@Composable
fun LegendItem(
    color: Color,
    category: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color = color)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = category,
                style = MaterialTheme.typography.caption
            )
            Text(
                text = value,
                style = MaterialTheme.typography.body2
            )
        }
    }
}
