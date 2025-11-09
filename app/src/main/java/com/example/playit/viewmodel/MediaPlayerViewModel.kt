package com.example.playit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.playit.data.model.Song
import com.example.playit.data.repository.SongRepository
import com.example.playit.media.MusicController
import com.example.playit.media.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MediaPlayerViewModel @Inject constructor(
    private val musicController: MusicController,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled = _isShuffleEnabled.asStateFlow()

    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled = _isRepeatEnabled.asStateFlow()

    fun getCurrentPosition(): Long {
        return musicController.getCurrentPosition()
    }

    init {
        musicController.mediaControllerCallback = { state, song, position, duration, shuffle, repeat ->
            viewModelScope.launch {
                _currentPosition.value = position
                _duration.value = duration
                _progress.value = if (duration > 0) position.toFloat() / duration else 0f
                _currentSong.value = song
                _isPlaying.value = state == PlayerState.Playing
                _isShuffleEnabled.value = shuffle
                _isRepeatEnabled.value = repeat
            }
        }
    }

    fun playSong(song: Song) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val allSongs = withContext(Dispatchers.IO) {
                    songRepository.getAllSongs()
                }

                val songIndex = allSongs.indexOfFirst { it.id == song.id }
                if (songIndex != -1) {
                    withContext(Dispatchers.Main) {
                        musicController.addMediaItems(allSongs)
                        musicController.play(songIndex)
                    }
                }
            } catch (e: Exception) {
                Log.e("MediaPlayerViewModel", "Error playing song: ${song.title}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun togglePlayPause() {
        if (musicController.getCurrentSong() != null) {
            if (isPlaying.value) musicController.pause() else musicController.resume()
        }
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                withContext(Dispatchers.Main) {
                    musicController.addMediaItems(songs)
                    musicController.play(startIndex.coerceIn(0, songs.lastIndex))
                }
            } catch (e: Exception) {
                Log.e("MediaPlayerViewModel", "Error playing provided songs list", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun skipToNext() = musicController.skipToNextSong()
    fun skipToPrevious() = musicController.skipToPreviousSong()
    fun seekTo(position: Long) = musicController.seekTo(position)

    fun toggleShuffle() = musicController.toggleShuffle()
    fun toggleRepeatOne() = musicController.toggleRepeatOne()

    override fun onCleared() {
        super.onCleared()
        musicController.destroy()
    }
}



