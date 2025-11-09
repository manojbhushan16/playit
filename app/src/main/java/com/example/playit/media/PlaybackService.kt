package com.example.playit.media

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.playit.MainActivity
import com.example.playit.player.audiofx.AudioFxController
import com.google.common.util.concurrent.Futures.immediateFuture
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    @Inject
    lateinit var exoPlayer: ExoPlayer
    @Inject
    lateinit var audioFxController: AudioFxController

    private var mediaSession: MediaSession? = null
    private var audioNoisyReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()

        setupAudioNoisyReceiver()
        setupExoPlayer()
        setupMediaSession()
    }

    private fun setupExoPlayer() {
        exoPlayer.apply {
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onAudioSessionIdChanged(audioSessionId: Int) {
                    audioFxController.attachToSession(audioSessionId)
                }
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> prepare()
                    }
                }
            })
            // Attach immediately if available
            audioFxController.attachToSession(audioSessionId)
        }
    }

    private fun setupAudioNoisyReceiver() {
        audioNoisyReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent?.action) {
                    exoPlayer.pause()
                }
            }
        }
        registerReceiver(
            audioNoisyReceiver,
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        )
    }

    private fun setupMediaSession() {
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(MediaSessionCallback())
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()
    }

    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: List<MediaItem>
        ) = immediateFuture(
            mediaItems.map { mediaItem ->
                mediaItem.buildUpon()
                    .setUri(mediaItem.requestMetadata.mediaUri ?: Uri.parse(mediaItem.mediaId))
                    .build()
            }
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        try {
            audioNoisyReceiver?.let {
                try {
                    unregisterReceiver(it)
                } catch (e: Exception) {
                    // Receiver may already be unregistered
                }
                audioNoisyReceiver = null
            }
            mediaSession?.release()
            mediaSession = null
            audioFxController.release()
            exoPlayer.release()
        } catch (e: Exception) {
            // Handle cleanup errors gracefully
        }
        super.onDestroy()
    }
}