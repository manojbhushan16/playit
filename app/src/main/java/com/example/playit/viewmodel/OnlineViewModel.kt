package com.example.playit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playit.BuildConfig
import com.example.playit.data.repository.OnlineTracksRepository
import com.example.playit.domain.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlineViewModel @Inject constructor(
    private val repo: OnlineTracksRepository
) : ViewModel() {

    val tracks: StateFlow<List<Track>> = repo.observeTracks()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _loadingMore = MutableStateFlow(false)
    val loadingMore: StateFlow<Boolean> = _loadingMore

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var offset: Int = 0
    private val pageSize: Int = 50
    private var hasMore = true

    fun refresh() {
        val clientId = BuildConfig.JAMENDO_CLIENT_ID
        Log.d("OnlineViewModel", "Refresh called. Client ID: ${if (clientId.isNotBlank()) "SET" else "EMPTY"}")
        _loading.value = true
        _loadingMore.value = false
        _error.value = null
        hasMore = true
        viewModelScope.launch {
            try {
                offset = 0
                val count = repo.refresh(clientId, limit = pageSize, offset = offset)
                Log.d("OnlineViewModel", "Loaded $count tracks")
                offset += pageSize
                hasMore = count >= pageSize
                if (count == 0 && clientId.isNotBlank()) {
                    _error.value = "No tracks found. Check your internet connection or try again later."
                }
            } catch (t: Throwable) {
                Log.e("OnlineViewModel", "Error loading tracks", t)
                _error.value = t.message ?: "Failed to load tracks: ${t.javaClass.simpleName}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadMore() {
        if (_loadingMore.value || !hasMore) return
        val clientId = BuildConfig.JAMENDO_CLIENT_ID
        _loadingMore.value = true
        viewModelScope.launch {
            try {
                val count = repo.refresh(clientId, limit = pageSize, offset = offset)
                offset += pageSize
                hasMore = count >= pageSize
            } catch (t: Throwable) {
                _error.value = t.message ?: "Failed to load more"
            } finally {
                _loadingMore.value = false
            }
        }
    }
}


