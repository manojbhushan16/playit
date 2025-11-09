package com.example.playit.media
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import android.util.Log
import com.example.playit.core.Constants
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.playit.core.extensions.toPlayerState
import com.example.playit.core.extensions.toSong
import com.example.playit.data.model.Song
import com.example.playit.di.qualifiers.MainScope
import com.example.playit.media.MusicController
import com.example.playit.media.PlaybackService
import com.example.playit.media.PlayerState
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
@OptIn(UnstableApi::class)
@Singleton
class MusicControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @MainScope private val mainScope: CoroutineScope
) : MusicController {

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    override var mediaControllerCallback: ((
        playerState: PlayerState,
        currentSong: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        isRepeatOneEnabled: Boolean
    ) -> Unit)? = null

    init {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )
        mediaControllerFuture = MediaController.Builder(context, sessionToken)
            .buildAsync()
        mediaControllerFuture.addListener(
            { setupController() },
            MoreExecutors.directExecutor()
        )
    }

    private fun setupController() {
        try {
            Log.d(Constants.LOG_TAG, "Setting up media controller")
            mediaController?.addListener(object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)

                    with(player) {
                        Log.d(Constants.LOG_TAG, "Player event: isPlaying=$isPlaying, currentPosition=$currentPosition")
                        mainScope.launch {
                            mediaControllerCallback?.invoke(
                                playbackState.toPlayerState(isPlaying),
                                currentMediaItem?.toSong(),
                                currentPosition.coerceAtLeast(0L),
                                duration.coerceAtLeast(0L),
                                shuffleModeEnabled,
                                repeatMode == Player.REPEAT_MODE_ONE
                            )
                        }
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    Log.d(Constants.LOG_TAG, "Playback state changed to: $playbackState")
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    Log.d(Constants.LOG_TAG, "IsPlaying changed to: $isPlaying")
                }
            })
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, "Error setting up controller", e)
        }
    }

    override fun addMediaItems(songs: List<Song>) {
        mainScope.launch {
            try {
                Log.d(Constants.LOG_TAG, "Adding ${songs.size} songs to playlist")
                val mediaItems = songs.map { song ->
                    Log.d(Constants.LOG_TAG, "Creating MediaItem for song: ${song.title} with ID: ${song.id}, URL: ${song.url}")
                    val uri = Uri.parse(song.url)
                    MediaItem.Builder()
                        .setMediaId(song.id)
                        .setUri(uri)
                        .setRequestMetadata(
                            MediaItem.RequestMetadata.Builder()
                                .setMediaUri(uri)
                                .build()
                        )
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(song.title)
                                .setArtist(song.artist)
                                .setArtworkUri(song.artwork?.let { Uri.parse(it) })
                                .build()
                        )
                        .build()
                }
                withContext(Dispatchers.Main) {
                    mediaController?.setMediaItems(mediaItems)
                    Log.d(Constants.LOG_TAG, "MediaItems set. First item URL: ${mediaItems.firstOrNull()?.localConfiguration?.uri}")
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "Error adding media items", e)
            }
        }
    }

    override fun play(mediaItemIndex: Int) {
        mainScope.launch(Dispatchers.Main) {
            try {
                Log.d(Constants.LOG_TAG, "Starting playback at index: $mediaItemIndex")
                mediaController?.apply {
                    seekToDefaultPosition(mediaItemIndex)
                    playWhenReady = true
                    prepare()
                    Log.d(Constants.LOG_TAG, "Player prepared and ready to play")
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "Error starting playback", e)
            }
        }
    }

    override fun resume() {
        mainScope.launch(Dispatchers.Main) {
            try {
                mediaController?.apply {
                    playWhenReady = true
                    if (playbackState == Player.STATE_IDLE) {
                        prepare()
                    }
                    play()
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "Error resuming playback", e)
            }
        }
    }

    override fun pause() {
        mainScope.launch(Dispatchers.Main) {
            try {
                mediaController?.pause()
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "Error pausing playback", e)
            }
        }
    }

    override fun seekTo(position: Long) {
        mainScope.launch(Dispatchers.Main) {
            try {
                Log.d(Constants.LOG_TAG, "Seeking to position: $position")
                mediaController?.seekTo(position)
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "Error seeking", e)
            }
        }
    }

    override fun skipToNextSong() {
        mainScope.launch(Dispatchers.Main) {
            try {
                mediaController?.apply {
                    if (hasNextMediaItem()) {
                        seekToNext()
                        prepare()
                        play()
                    }
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "Error skipping to next", e)
            }
        }
    }

    override fun skipToPreviousSong() {
        mainScope.launch(Dispatchers.Main) {
            try {
                mediaController?.apply {
                    if (hasPreviousMediaItem()) {
                        seekToPrevious()
                        prepare()
                        play()
                    }
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "Error skipping to previous", e)
            }
        }
    }

    override fun getCurrentPosition(): Long = 
        mediaController?.currentPosition ?: 0L

    override fun getCurrentSong(): Song? = 
        mediaController?.currentMediaItem?.toSong()

    override fun toggleShuffle() {
        mediaController?.let {
            it.shuffleModeEnabled = !it.shuffleModeEnabled
        }
    }

    override fun toggleRepeatOne() {
        mediaController?.let {
            it.repeatMode = if (it.repeatMode == Player.REPEAT_MODE_ONE) {
                Player.REPEAT_MODE_OFF
            } else {
                Player.REPEAT_MODE_ONE
            }
        }
    }

    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
        mediaControllerCallback = null
    }

} 