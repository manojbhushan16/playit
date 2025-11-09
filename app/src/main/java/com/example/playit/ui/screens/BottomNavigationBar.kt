package com.example.playit.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.playit.R
import com.example.playit.ui.navigation.Destinations


@Composable
fun BottomNavigationBar(
    navController: NavController,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route

    // Elevation transition effect for a modern sleek look
    val elevation by animateDpAsState(
        targetValue = if (currentDestination == Destinations.HOME) 10.dp else 4.dp,
        animationSpec = tween(durationMillis = 300)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = elevation,
        modifier = Modifier
            .height(90.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        // Home Navigation Item
        NavigationItem(
            iconRes = R.drawable.home,

            isSelected = currentDestination == Destinations.HOME,
            onClick = { navController.navigateIfNotCurrent(Destinations.HOME) }
        )

        // Online (Discover) Navigation Item
        NavigationItem(
            iconRes = R.drawable.live,
            isSelected = currentDestination == Destinations.ONLINE,
            onClick = { navController.navigateIfNotCurrent(Destinations.ONLINE) }
        )

        // Now Playing Navigation Item
        NavigationItem(
            iconRes = R.drawable.songs,

            isSelected = currentDestination?.startsWith(Destinations.PLAYER) == true,
            onClick = { navController.navigateIfNotCurrent(Destinations.PLAYER) }
        )
    }
}

@Composable
fun RowScope.NavigationItem(
    iconRes: Int,

    isSelected: Boolean,
    onClick: () -> Unit
) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    // Animating icon size change based on selected state
    val iconSize by animateDpAsState(
        targetValue = if (isSelected) 36.dp else 28.dp,
        animationSpec = tween(durationMillis = 300)
    )

    NavigationBarItem(
        icon = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                // Icon with dynamic size and color
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = if (isSelected) selectedColor else unselectedColor,
                    modifier = Modifier.size(iconSize)
                )


            }
        },
        selected = isSelected,
        onClick = { onClick() },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = selectedColor,
            unselectedIconColor = unselectedColor,
            selectedTextColor = selectedColor,
            unselectedTextColor = unselectedColor,
            indicatorColor = Color.Transparent // No background for selected items
        )
    )
}

// Extension function for navigating to a destination if it's not already the current one
fun NavController.navigateIfNotCurrent(destination: String) {
    if (currentBackStackEntry?.destination?.route != destination) {
        navigate(destination)
    }
}

