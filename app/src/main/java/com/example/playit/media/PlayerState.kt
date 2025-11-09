package com.example.playit.media

sealed class PlayerState {
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Loading : PlayerState()
    object Stopped : PlayerState()
    data class Error(val message: String) : PlayerState()
} 