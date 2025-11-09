package com.example.playit.data.repository

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import com.example.playit.core.Constants
import com.example.playit.data.model.Song
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val context: Context
) {
    private val _onlineSongs = MutableStateFlow<List<Song>>(emptyList())
    val onlineSongs: StateFlow<List<Song>> get() = _onlineSongs

    private val _localSongs = MutableStateFlow<List<Song>>(emptyList())
    val localSongs: StateFlow<List<Song>> get() = _localSongs

    suspend fun getAllOnlineSongs(): List<Song> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection(Constants.COLLECTION_SONGS)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                try {
                    Song(
                        id = document.id,
                        title = document.getString(Constants.FIELD_TITLE) ?: "",
                        artist = document.getString(Constants.FIELD_ARTIST) ?: "",
                        url = document.getString(Constants.FIELD_URL) ?: "",
                        artwork = document.getString(Constants.FIELD_ARTWORK),
                        isLocal = false
                    ).also {
                        Log.d(Constants.LOG_TAG, "Loaded online song: ${it.title} with ID: ${it.id}")
                    }
                } catch (e: Exception) {
                    Log.e(Constants.LOG_TAG, "Error parsing song document: ${document.id}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, "Error getting songs from Firestore", e)
            emptyList()
        }
    }

    suspend fun fetchLocalSongs() = withContext(Dispatchers.IO) {
        try {
            val songs = mutableListOf<Song>()

            // URI and projection for querying media
            val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.MIME_TYPE
            )

            // Selection criteria for audio files
            val selection = "${MediaStore.Audio.Media.MIME_TYPE} IN (?, ?, ?, ?, ?)"
            val selectionArgs = Constants.AUDIO_MIME_TYPES
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            // Query the media store
            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (cursor.moveToNext()) {
                    try {
                        val id = cursor.getLong(idIndex)
                        val title = cursor.getString(titleIndex) ?: "Unknown Title"
                        val artist = cursor.getString(artistIndex) ?: "Unknown Artist"
                        val path = cursor.getString(dataIndex)
                        val albumId = cursor.getLong(albumIdIndex)


                        val file = java.io.File(path)
                        if (!file.exists() || file.length() < Constants.MIN_SONG_SIZE_BYTES) continue // Skip invalid files

                        val artworkUri = "content://media/external/audio/albumart/$albumId"

                        songs.add(
                            Song(
                                id = id.toString(),
                                title = title,
                                artist = artist,
                                url = path,
                                artwork = artworkUri,
                                isLocal = true
                            )
                        )
                    } catch (e: Exception) {
                        Log.e(Constants.LOG_TAG, "Error processing song from cursor", e)
                    }
                }
            }

            // Update the local songs state
            _localSongs.value = songs
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, "Error fetching local songs", e)
            Result.failure(e)
        }
    }

    suspend fun fetchOnlineSongsFromFirestore(): Result<Unit> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_SONGS).get().await()
            val songList = snapshot.documents.mapNotNull { document ->
                val id = document.id
                val title = document.getString(Constants.FIELD_TITLE) ?: ""
                val artist = document.getString(Constants.FIELD_ARTIST) ?: ""
                val artwork = document.getString(Constants.FIELD_ARTWORK)
                val url = document.getString(Constants.FIELD_URL) ?: ""
                val isFavorite = document.getBoolean(Constants.FIELD_IS_FAVORITE) ?: false

                if (title.isNotEmpty() && artist.isNotEmpty() && url.isNotEmpty()) {
                    Song(id, title, artist, artwork, url, isFavorite, isLocal = false)
                } else null
            }
            _onlineSongs.value = songList
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, "Error fetching online songs: ${e.message}", e)
            Result.failure(e)
        }
    }
    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        val localSongsList = localSongs.value
        val onlineSongsList = onlineSongs.value
        localSongsList + onlineSongsList
    }

    suspend fun toggleFavorite(song: Song): Result<Unit> {
        return try {
            if (!song.isLocal) {
                val songRef = firestore.collection(Constants.COLLECTION_SONGS).document(song.id)
                val newFavoriteStatus = !song.isFavorite
                songRef.update(Constants.FIELD_IS_FAVORITE, newFavoriteStatus).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, "Error toggling favorite for song: ${song.title}", e)
            Result.failure(e)
        }
    }
}