package com.example.playit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.playit.ui.components.NowPlayingBar
import com.example.playit.ui.navigation.Destinations
import com.example.playit.ui.navigation.MainNavHost
import com.example.playit.viewmodel.MediaPlayerViewModel

@Composable
fun MainScreen(

    viewModel: MediaPlayerViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val screensWithBottomNavBar = listOf(
        Destinations.HOME,
        Destinations.ONLINE,
        Destinations.PROFILE,
        Destinations.PLAYER
    )
    val screensWithMiniPlayer= listOf(
        Destinations.HOME,
        Destinations.ONLINE
    )

    Scaffold(
        bottomBar = {
            Column {
                if(currentRoute in screensWithMiniPlayer){
                    NowPlayingBar(
                        viewModel = viewModel,
                        onPlayerClick = { navController.navigate(Destinations.PLAYER) }
                    )
                }

                if (currentRoute in screensWithBottomNavBar) {
                    BottomNavigationBar(navController = navController)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            MainNavHost(
                navController = navController as NavHostController,
                viewModel = viewModel
            )
        }
    }
}





