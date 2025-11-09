package com.example.playit.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.example.playit.data.repository.SongRepository
import com.example.playit.core.prefs.FavoritePreferences
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideSongRepository(
        db: FirebaseFirestore,
        @ApplicationContext context: Context
    ): SongRepository {
        return SongRepository(db, context)
    }

    @Provides
    @Singleton
    fun provideFavoritePreferences(
        @ApplicationContext context: Context
    ): FavoritePreferences = FavoritePreferences(context)

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
} 