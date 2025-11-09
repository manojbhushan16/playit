package com.example.playit.data.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val artwork: String? = null,
    val url: String,
    val isFavorite: Boolean = false,
    val isLocal: Boolean = false
)
