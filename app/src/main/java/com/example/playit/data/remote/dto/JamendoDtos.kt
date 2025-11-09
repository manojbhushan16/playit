package com.example.playit.data.remote.dto

import com.squareup.moshi.Json

data class JamendoResponse(
    val headers: JamendoHeaders?,
    val results: List<JamendoTrackDto>
)

data class JamendoHeaders(
    val status: String?,
    @Json(name = "code") val code: Int?,
    @Json(name = "error_message") val errorMessage: String?
)

data class JamendoTrackDto(
    val id: String,
    @Json(name = "name") val title: String,
    @Json(name = "artist_name") val artist: String,
    val duration: Int?,
    val audio: String?,
    @Json(name = "audio_download") val audioDownload: String?,
    val image: String?
)


