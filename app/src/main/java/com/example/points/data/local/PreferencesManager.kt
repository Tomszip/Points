package com.example.points.data.local

import android.content.Context
import androidx.core.content.edit
import com.example.points.data.model.SearchFilter

/**
 * Small key-value settings stored with SharedPreferences.
 * Remembers the last search and the chosen theme between app launches.
 */
class PreferencesManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var lastQuery: String
        get() = prefs.getString(KEY_LAST_QUERY, "").orEmpty()
        set(value) = prefs.edit { putString(KEY_LAST_QUERY, value) } // apply(): async write

    var lastFilter: SearchFilter
        get() = readEnum(KEY_LAST_FILTER, SearchFilter.NAME)
        set(value) = prefs.edit { putString(KEY_LAST_FILTER, value.name) }

    /** false = light (default), true = dark. */
    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit { putBoolean(KEY_DARK_MODE, value) }

    /** Enums are stored by name; an unknown or missing value falls back to the default. */
    private inline fun <reified T : Enum<T>> readEnum(key: String, default: T): T =
        prefs.getString(key, null)
            ?.let { saved -> runCatching { enumValueOf<T>(saved) }.getOrNull() }
            ?: default

    private companion object {
        const val PREFS_NAME = "brewery_prefs"
        const val KEY_LAST_QUERY = "last_query"
        const val KEY_LAST_FILTER = "last_filter"
        const val KEY_DARK_MODE = "dark_mode"
    }
}
