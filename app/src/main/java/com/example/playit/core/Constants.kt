package com.example.playit.core

/**
 * Application-wide constants
 */
object Constants {
    // Media
    const val MIN_SONG_SIZE_BYTES = 25 * 1024L // 25 KB minimum file size
    
    // Audio MIME types
    val AUDIO_MIME_TYPES = arrayOf(
        "audio/mpeg",  // MP3
        "audio/x-wav", // WAV
        "audio/ogg",   // OGG
        "audio/aac",   // AAC
        "audio/x-m4a"  // M4A
    )
    
    // Firestore Collections
    const val COLLECTION_SONGS = "songs"
    const val COLLECTION_USERS = "users"
    
    // Firestore Fields
    const val FIELD_TITLE = "title"
    const val FIELD_ARTIST = "artist"
    const val FIELD_URL = "url"
    const val FIELD_ARTWORK = "artwork"
    const val FIELD_IS_FAVORITE = "isFavorite"
    const val FIELD_IS_NEW_USER = "isNewUser"
    
    // Logging
    const val LOG_TAG = "PlayIt"
    
    // Time formatting
    const val TIME_FORMAT_PATTERN = "%d:%02d"
}

