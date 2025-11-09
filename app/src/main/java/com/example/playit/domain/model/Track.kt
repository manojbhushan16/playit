package com.example.playit.domain.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val durationMs: Long?,
    val streamUrl: String?,
    val coverUrl: String?
)


