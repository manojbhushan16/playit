package com.example.playit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playit.data.local.dao.OnlineTrackDao
import com.example.playit.data.local.entity.OnlineTrackEntity

@Database(
    entities = [OnlineTrackEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun onlineTrackDao(): OnlineTrackDao
}


