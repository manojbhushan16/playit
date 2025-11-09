package com.example.playit.data.repository

import android.content.Context
import android.util.Log
import com.example.playit.data.local.dao.OnlineTrackDao
import com.example.playit.data.local.entity.OnlineTrackEntity
import com.example.playit.data.remote.JamendoService
import com.example.playit.data.remote.dto.JamendoResponse
import com.example.playit.data.remote.dto.JamendoTrackDto
import com.example.playit.domain.model.Track
import com.example.playit.di.qualifiers.IoDispatcher
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class OnlineTracksRepository @Inject constructor(
    private val service: JamendoService,
    private val dao: OnlineTrackDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {
    fun observeTracks(): Flow<List<Track>> = dao.observeAll().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun refresh(clientId: String, limit: Int = 50, offset: Int = 0): Int {
        return withContext(ioDispatcher) {
            val response: JamendoResponse = if (clientId.isNotBlank()) {
                Log.d("OnlineTracksRepository", "Fetching tracks with clientId: $clientId, limit: $limit, offset: $offset")
                try {
                    val resp = service.getTracks(
                        clientId = clientId,
                        limit = limit,
                        offset = offset
                    )
                    // Check for API errors in headers
                    resp.headers?.let { headers ->
                        if (headers.code != null && headers.code != 0) {
                            val errorMsg = headers.errorMessage ?: "API Error: ${headers.code}"
                            Log.e("OnlineTracksRepository", errorMsg)
                            throw Exception(errorMsg)
                        }
                    }
                    Log.d("OnlineTracksRepository", "Received ${resp.results.size} tracks")
                    resp
                } catch (e: Exception) {
                    Log.e("OnlineTracksRepository", "API call failed", e)
                    throw e
                }
            } else {
                Log.d("OnlineTracksRepository", "Client ID is blank, loading from assets")
                // Load bundled static JSON from assets
                context.assets.open("discover.json").use { input ->
                    val adapter = moshi.adapter(JamendoResponse::class.java)
                    adapter.fromJson(input.bufferedReader().readText()) ?: JamendoResponse(null, emptyList())
                }
            }
            val now = System.currentTimeMillis()
            val entities = response.results.mapNotNull { dto ->
                // Filter out tracks without audio URL
                val audioUrl = dto.audio ?: dto.audioDownload
                if (audioUrl.isNullOrBlank()) {
                    Log.w("OnlineTracksRepository", "Skipping track ${dto.id} - no audio URL")
                    null
                } else {
                    OnlineTrackEntity(
                        id = dto.id,
                        title = dto.title,
                        artist = dto.artist,
                        durationMs = dto.duration?.toLong()?.times(1000L),
                        streamUrl = audioUrl,
                        coverUrl = dto.image,
                        cachedAtMs = now
                    )
                }
            }
            Log.d("OnlineTracksRepository", "Saving ${entities.size} valid tracks to database")
            if (offset == 0) {
                dao.clear()
            }
            dao.upsertAll(entities)
            entities.size
        }
    }
}

private fun OnlineTrackEntity.toDomain(): Track =
    Track(
        id = id,
        title = title,
        artist = artist,
        durationMs = durationMs,
        streamUrl = streamUrl,
        coverUrl = coverUrl
    )


