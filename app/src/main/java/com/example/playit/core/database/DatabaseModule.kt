package com.example.playit.core.database

import android.content.Context
import androidx.room.Room
import com.example.playit.data.local.AppDatabase
import com.example.playit.data.local.dao.OnlineTrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "waveflow.db").build()

    @Provides
    @Singleton
    fun provideOnlineTrackDao(db: AppDatabase): OnlineTrackDao = db.onlineTrackDao()
}


