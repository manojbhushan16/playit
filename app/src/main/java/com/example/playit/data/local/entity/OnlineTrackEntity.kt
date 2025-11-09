package com.example.playit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "online_tracks")
data class OnlineTrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val durationMs: Long?,
    val streamUrl: String?,
    val coverUrl: String?,
    val cachedAtMs: Long
)


