package com.example.points.data.local

import android.content.Context
import androidx.core.content.edit
import com.example.points.data.model.SearchFilter

/**
 * Small key-value settings stored with SharedPreferences.
 * Remembers the last search so the list looks the same when the app is reopened.
 */
class PreferencesManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var lastQuery: String
        get() = prefs.getString(KEY_LAST_QUERY, "").orEmpty()
        set(value) = prefs.edit { putString(KEY_LAST_QUERY, value) } // apply(): async write

    var lastFilter: SearchFilter
        get() = prefs.getString(KEY_LAST_FILTER, null)
            ?.let { saved -> runCatching { SearchFilter.valueOf(saved) }.getOrNull() }
            ?: SearchFilter.NAME
        set(value) = prefs.edit { putString(KEY_LAST_FILTER, value.name) }

    private companion object {
        const val PREFS_NAME = "brewery_prefs"
        const val KEY_LAST_QUERY = "last_query"
        const val KEY_LAST_FILTER = "last_filter"
    }
}
