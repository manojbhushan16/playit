package com.example.playit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.media3.common.util.UnstableApi
import com.example.playit.media.MusicController
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
@UnstableApi
class PlayItApp : Application() {
    
    @Inject
    lateinit var musicController: MusicController
    


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.channel_id),
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_description)
                setShowBadge(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        // Note: onTerminate is not guaranteed to be called on modern Android
        // but kept for completeness
        if (::musicController.isInitialized) {
            musicController.destroy()
        }
    }
} 