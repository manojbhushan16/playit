package com.example.playit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playit.data.local.entity.OnlineTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OnlineTrackDao {
    @Query("SELECT * FROM online_tracks ORDER BY cachedAtMs DESC")
    fun observeAll(): Flow<List<OnlineTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<OnlineTrackEntity>)

    @Query("DELETE FROM online_tracks")
    suspend fun clear()
}


