package com.example.playit.player.audiofx

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioFxController @Inject constructor() {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    fun attachToSession(audioSessionId: Int) {
        release()
        if (audioSessionId == 0 || audioSessionId == Equalizer.ERROR) return
        try {
            equalizer = Equalizer(0, audioSessionId).apply { enabled = true }
            bassBoost = BassBoost(0, audioSessionId).apply { enabled = true }
            virtualizer = Virtualizer(0, audioSessionId).apply { enabled = false }
        } catch (_: Throwable) {
            // Devices may not support effects; fail silently
        }
    }

    fun setBandLevel(band: Short, level: Short) {
        equalizer?.let {
            val clamped = level.coerceIn(it.bandLevelRange[0], it.bandLevelRange[1])
            it.setBandLevel(band, clamped)
        }
    }

    fun setBassBoostStrength(strength: Short) {
        bassBoost?.setStrength(strength.coerceIn(0, 1000))
    }

    fun setVirtualizerStrength(strength: Short) {
        virtualizer?.apply {
            enabled = strength > 0
            setStrength(strength.coerceIn(0, 1000))
        }
    }

    fun applyPreset(preset: Short) {
        equalizer?.usePreset(preset)
    }

    fun presets(): List<String> = buildList {
        val eq = equalizer ?: return@buildList
        for (i in 0 until eq.numberOfPresets) add(eq.getPresetName(i.toShort()))
    }

    fun bandCount(): Short = equalizer?.numberOfBands ?: 0

    fun bandLevelRange(): ShortArray = equalizer?.bandLevelRange ?: shortArrayOf(0, 0)

    fun getBandLevel(band: Short): Short {
        return equalizer?.getBandLevel(band) ?: 0
    }

    fun getBassBoostStrength(): Short {
        return try {
            bassBoost?.getRoundedStrength()?.takeIf { it >= 0 } ?: 0
        } catch (e: Exception) {
            0
        }
    }

    fun getVirtualizerStrength(): Short {
        return try {
            virtualizer?.getRoundedStrength()?.takeIf { it >= 0 } ?: 0
        } catch (e: Exception) {
            0
        }
    }

    fun release() {
        equalizer?.release(); equalizer = null
        bassBoost?.release(); bassBoost = null
        virtualizer?.release(); virtualizer = null
    }
}


