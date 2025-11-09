package com.example.playit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playit.data.model.Song
import com.example.playit.data.repository.SongRepository
import com.example.playit.core.prefs.FavoritePreferences
import com.example.playit.core.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val favoritePreferences: FavoritePreferences
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isNewUser = MutableStateFlow(false)
    val isNewUser: StateFlow<Boolean> = _isNewUser

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _localSongs = MutableStateFlow<List<Song>>(emptyList())
    private val _onlineSongs = MutableStateFlow<List<Song>>(emptyList())

    val localSongs: StateFlow<List<Song>> = _localSongs
    val onlineSongs: StateFlow<List<Song>> = _onlineSongs

    init {
        checkUserStatus()
        loadSongs()

        viewModelScope.launch {
            combine(songRepository.localSongs, favoritePreferences.localFavoriteIds) { songs, favorites ->
                songs.map { it.copy(isFavorite = favorites.contains(it.id)) }
            }.collect { mapped ->
                _localSongs.value = mapped
            }
        }

        viewModelScope.launch {
            songRepository.onlineSongs.collect { songs ->
                _onlineSongs.value = songs
            }
        }
    }

    private fun checkUserStatus() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            _isLoading.value = true
            db.collection(Constants.COLLECTION_USERS).document(userId).get()
                .addOnSuccessListener { document ->
                    _isNewUser.value = document.getBoolean(Constants.FIELD_IS_NEW_USER) ?: true
                    _isLoading.value = false
                }
                .addOnFailureListener {
                    _isNewUser.value = true
                    _isLoading.value = false
                }
        }
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load local songs
                songRepository.fetchLocalSongs()

                // Load online songs
                songRepository.fetchOnlineSongsFromFirestore().fold(
                    onSuccess = {
                        if (onlineSongs.value.isEmpty() && localSongs.value.isEmpty()) {
                            _error.value = "No songs available"
                        } else {
                            _error.value = null
                        }
                    },
                    onFailure = { e ->
                        _error.value = "Failed to load online songs: ${e.message}"
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            if (!song.isLocal) {
                songRepository.toggleFavorite(song).fold(
                    onSuccess = {
                        _onlineSongs.update { currentSongs ->
                            currentSongs.map {
                                if (it.id == song.id) it.copy(isFavorite = !it.isFavorite)
                                else it
                            }
                        }
                    },
                    onFailure = { e ->
                        Log.e("HomeScreenViewModel", "Error toggling favorite: ${e.message}")
                    }
                )
            } else {
                favoritePreferences.toggleLocalFavorite(song.id)
                _localSongs.update { currentSongs ->
                    currentSongs.map {
                        if (it.id == song.id) it.copy(isFavorite = !it.isFavorite) else it
                    }
                }
            }
        }
    }
}