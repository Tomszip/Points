package com.example.points.util

import androidx.appcompat.app.AppCompatDelegate

/**
 * Switches the whole app between light and dark.
 * AppCompat recreates the open screens so they are redrawn with the new colors.
 */
fun applyTheme(isDarkMode: Boolean) {
    AppCompatDelegate.setDefaultNightMode(
        if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
    )
}
