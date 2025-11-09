package com.example.playit.media

import com.example.playit.data.model.Song

interface MusicController {
    var mediaControllerCallback: ((
        playerState: PlayerState,
        currentSong: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        isRepeatOneEnabled: Boolean
    ) -> Unit)?

    fun addMediaItems(songs: List<Song>)
    fun play(mediaItemIndex: Int = 0)
    fun resume()
    fun pause()
    fun seekTo(position: Long)
    fun skipToNextSong()
    fun skipToPreviousSong()
    fun getCurrentPosition(): Long
    fun getCurrentSong(): Song?
    fun toggleShuffle()
    fun toggleRepeatOne()
    fun destroy()
} 