package com.karnegar.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object ThemeMode {
    const val SYSTEM = 0
    const val LIGHT = 1
    const val DARK = 2
}

object SettingsRepository {

    private val KEY_THEME = intPreferencesKey("theme_mode")

    fun themeFlow(context: Context): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[KEY_THEME] ?: ThemeMode.SYSTEM }

    suspend fun setThemeMode(context: Context, mode: Int) {
        context.dataStore.edit { prefs -> prefs[KEY_THEME] = mode }
    }
}
