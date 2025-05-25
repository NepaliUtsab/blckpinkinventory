package com.blackandpink.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.blackandpink.AppColors
import com.blackandpink.AppIcons
import com.blackandpink.Screen

@Composable
fun SidebarNavigation(
    currentScreen: Screen,
    onScreenChange: (Screen) -> Unit
) {
    Surface(
        color = MaterialTheme.colors.surface,
        elevation = 8.dp,
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // App title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Pink)
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Inventory Manager",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigation items
            NavigationItem(
                icon = AppIcons.Inventory,
                title = "Inventory",
                isSelected = currentScreen is Screen.Inventory || currentScreen is Screen.ItemDetail,
                onClick = { onScreenChange(Screen.Inventory) }
            )
            
            NavigationItem(
                icon = AppIcons.Receipt,
                title = "Sessions",
                isSelected = currentScreen is Screen.Sessions || 
                           currentScreen is Screen.NewSession || 
                           currentScreen is Screen.SessionDetails,
                onClick = { onScreenChange(Screen.Sessions) }
            )
            
            NavigationItem(
                icon = AppIcons.BarChart,
                title = "Analytics",
                isSelected = currentScreen is Screen.Analytics,
                onClick = { onScreenChange(Screen.Analytics) }
            )
            
            NavigationItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                isSelected = currentScreen is Screen.Settings,
                onClick = { onScreenChange(Screen.Settings) }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // App version at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "v1.0.0",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun NavigationItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        AppColors.Pink.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }
    
    val textColor = if (isSelected) {
        AppColors.Pink
    } else {
        MaterialTheme.colors.onSurface
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            color = textColor
        )
    }
}
