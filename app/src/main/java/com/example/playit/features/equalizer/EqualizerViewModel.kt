package com.example.playit.features.equalizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playit.core.prefs.EqualizerPreferences
import com.example.playit.player.audiofx.AudioFxController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val audioFx: AudioFxController,
    private val prefs: EqualizerPreferences
) : ViewModel() {

    private val _enabled = MutableStateFlow(true)
    val enabled: StateFlow<Boolean> = _enabled

    fun setEnabled(value: Boolean) {
        _enabled.value = value
        viewModelScope.launch { prefs.setEnabled(value) }
    }

    fun applyPresetIndex(index: Int) {
        audioFx.applyPreset(index.toShort())
        viewModelScope.launch { prefs.setPreset(audioFx.presets().getOrNull(index) ?: "") }
    }

    fun setBandLevel(band: Int, level: Int) {
        audioFx.setBandLevel(band.toShort(), level.toShort())
    }

    fun setBassBoost(strength: Int) {
        audioFx.setBassBoostStrength(strength.toShort())
        viewModelScope.launch { prefs.setBassBoost(strength) }
    }

    fun setVirtualizer(strength: Int) {
        audioFx.setVirtualizerStrength(strength.toShort())
        viewModelScope.launch { prefs.setVirtualizer(strength) }
    }

    fun presets(): List<String> = audioFx.presets()

    fun bandCount(): Int = audioFx.bandCount().toInt()

    fun bandLevelRange(): Pair<Int, Int> = audioFx.bandLevelRange().let { it[0].toInt() to it[1].toInt() }

    fun getBandLevel(band: Int): Int = audioFx.getBandLevel(band.toShort()).toInt()

    fun getBassBoostStrength(): Int = audioFx.getBassBoostStrength().toInt()

    fun getVirtualizerStrength(): Int = audioFx.getVirtualizerStrength().toInt()
}


