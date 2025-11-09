package com.example.playit.ui.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playit.core.Constants
import com.example.playit.ui.screens.*
import com.example.playit.viewmodel.MediaPlayerViewModel
import com.google.firebase.auth.FirebaseAuth

object Destinations {
    const val LOGIN = "login"
    const val HOME = "home"
    const val ONLINE = "online"
    const val PROFILE = "profile"
    const val SIGN_UP = "sign_up"
    const val PLAYER = "player"

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MediaPlayerViewModel = hiltViewModel()
) {
    val auth = FirebaseAuth.getInstance()
    NavHost(
        navController = navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
            Destinations.HOME
        } else {
            Destinations.LOGIN
        }
    ) {
        composable(Destinations.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                    }
                },
                onSignUp = {
                    navController.navigate(Destinations.SIGN_UP)
                }
            )
        }

        composable(Destinations.HOME) {
            HomeScreen(
                onSongClick = { song ->
                    Log.d(Constants.LOG_TAG, "Starting playback for song: ${song.title}")
                    viewModel.playSong(song)
                },
                onLogout = {
                    auth.signOut()
                    navController.navigate(Destinations.LOGIN) {
                        // Clear backstack on logout
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(Destinations.ONLINE) {
            OnlineScreen(
                navController = navController,
                onPlayTracks = { songs, startIndex ->
                    if (songs.isNotEmpty()) {
                        viewModel.playSongs(songs, startIndex)
                    }
                }
            )
        }

        composable(Destinations.PROFILE) {
            ProfileScreen(
                onLogout = {
                    auth.signOut()
                    navController.navigate(Destinations.LOGIN) {
                        // Clear backstack on logout
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        // Sign Up Screen
        composable(Destinations.SIGN_UP) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Destinations.HOME) {
                        // Clear backstack so user cannot go back to sign-up after sign-up success
                        popUpTo(Destinations.SIGN_UP) { inclusive = true }
                    }
                },
                navController = navController
            )
        }


        composable(Destinations.PLAYER) {
            PlayerScreen(
                viewModel = viewModel,
                onNavigateUp = { navController.navigateUp() },
                navController = navController
            )
        }
    }
}




