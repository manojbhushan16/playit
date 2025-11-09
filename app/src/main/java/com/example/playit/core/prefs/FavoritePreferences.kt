package com.example.playit.core.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.favDataStore by preferencesDataStore(name = "playit_favorites")

class FavoritePreferences(private val context: Context) {
    private val KEY_LOCAL_FAVORITES = stringSetPreferencesKey("local_favorite_ids")

    val localFavoriteIds: Flow<Set<String>> = context.favDataStore.data.map { prefs ->
        prefs[KEY_LOCAL_FAVORITES] ?: emptySet()
    }

    suspend fun toggleLocalFavorite(id: String) {
        context.favDataStore.edit { prefs ->
            val current = prefs[KEY_LOCAL_FAVORITES] ?: emptySet()
            prefs[KEY_LOCAL_FAVORITES] = if (id in current) current - id else current + id
        }
    }
}


