package com.example.playit.data.remote

import com.example.playit.data.remote.dto.JamendoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JamendoService {
    @GET("v3.0/tracks/")
    suspend fun getTracks(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("include") include: String = "musicinfo",
        @Query("order") order: String = "popularity_total"
    ): JamendoResponse
}


