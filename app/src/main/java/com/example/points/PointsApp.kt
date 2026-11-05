package com.example.points

import android.app.Application
import com.example.points.data.local.PreferencesManager
import com.example.points.util.applyTheme

/**
 * Runs once when the app process starts, before any Activity.
 * Applies the saved theme so the first screen is already drawn with it (no flicker).
 */
class PointsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        applyTheme(PreferencesManager(this).isDarkMode)
    }
}
