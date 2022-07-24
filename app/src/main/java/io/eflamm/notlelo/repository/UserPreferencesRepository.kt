package io.eflamm.notlelo.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

data class UserPreferences(val pictureResolution: Int)

private const val USER_PREFERENCES = "user_preferences"

private object UserPreferencesKeys {
    val PICTURE_RESOLUTION = intPreferencesKey("picture_resolution")
}

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES
)

class UserPreferencesRepository(
    val context: Context
) {
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if(exception is IOException)
                emit(emptyPreferences())
            else
                throw exception
        }
        .map { preferences ->
            val pictureResolution: Int = preferences[UserPreferencesKeys.PICTURE_RESOLUTION] ?: 720
            UserPreferences(pictureResolution)
        }

    suspend fun updatePictureResolution(resolution: Int) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.PICTURE_RESOLUTION] = resolution
        }
    }
}
