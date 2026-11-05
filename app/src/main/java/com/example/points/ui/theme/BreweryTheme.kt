package com.example.points.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.points.R

/**
 * Compose theme. Reads the same colors.xml used by the XML screens,
 * so both parts of the app look alike.
 */
@Composable
fun BreweryTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = colorResource(R.color.amber_primary_dark),
            onPrimary = colorResource(R.color.amber_on_primary_dark),
            secondaryContainer = colorResource(R.color.amber_secondary_container_dark),
            onSecondaryContainer = colorResource(R.color.amber_on_secondary_container_dark)
        )
    } else {
        lightColorScheme(
            primary = colorResource(R.color.amber_primary),
            onPrimary = colorResource(R.color.amber_on_primary),
            secondaryContainer = colorResource(R.color.amber_secondary_container),
            onSecondaryContainer = colorResource(R.color.amber_on_secondary_container)
        )
    }
    MaterialTheme(colorScheme = colors, content = content)
}
