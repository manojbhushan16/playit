package com.example.playit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.playit.data.model.Song
import com.example.playit.viewmodel.OnlineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineScreen(
    viewModel: OnlineViewModel = hiltViewModel(),
    navController: NavController? = null,
    onPlayTracks: (List<Song>, Int) -> Unit = { _, _ -> }
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "online songs Screen")
    }
}

