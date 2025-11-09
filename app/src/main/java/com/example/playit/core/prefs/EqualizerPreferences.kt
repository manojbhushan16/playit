package com.example.playit.core.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.eqDataStore by preferencesDataStore(name = "equalizer")

@Singleton
class EqualizerPreferences @Inject constructor(@ApplicationContext private val context: Context) {
    object Keys {
        val enabled = booleanPreferencesKey("enabled")
        val preset = stringPreferencesKey("preset")
        val bassBoost = intPreferencesKey("bassBoostStrength")
        val virtualizer = intPreferencesKey("virtualizerStrength")
    }

    fun observe(): Flow<Map<Preferences.Key<*>, Any>> =
        context.eqDataStore.data.map { prefs -> prefs.asMap() }

    suspend fun setEnabled(value: Boolean) {
        context.eqDataStore.edit { it[Keys.enabled] = value }
    }

    suspend fun setPreset(name: String) {
        context.eqDataStore.edit { it[Keys.preset] = name }
    }

    suspend fun setBassBoost(strength: Int) {
        context.eqDataStore.edit { it[Keys.bassBoost] = strength }
    }

    suspend fun setVirtualizer(strength: Int) {
        context.eqDataStore.edit { it[Keys.virtualizer] = strength }
    }
}


