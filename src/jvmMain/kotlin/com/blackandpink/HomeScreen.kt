package com.blackandpink

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Settings
import com.blackandpink.AppIcons
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToSessions: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // App Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Inventory Management System",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
        }
        
        // Main navigation options
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeCard(
                title = "Inventory",
                subtitle = "Manage your items",
                icon = AppIcons.Inventory,
                onClick = onNavigateToInventory,
                modifier = Modifier.weight(1f)
            )
            
            HomeCard(
                title = "Sessions",
                subtitle = "Track inventory sessions",
                icon = AppIcons.Receipt,
                onClick = onNavigateToSessions,
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeCard(
                title = "Analytics",
                subtitle = "View reports and statistics",
                icon = AppIcons.BarChart,
                onClick = onNavigateToAnalytics,
                modifier = Modifier.weight(1f)
            )
            
            HomeCard(
                title = "Settings",
                subtitle = "Configure application",
                icon = Icons.Default.Settings,
                onClick = onNavigateToSettings,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Footer with app version
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Inventory Management v1.0.0",
                style = MaterialTheme.typography.caption,
                modifier = Modifier.alpha(0.6f)
            )
        }
    }
}

@Composable
fun HomeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(200.dp)
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colors.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
