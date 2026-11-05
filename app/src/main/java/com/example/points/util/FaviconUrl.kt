package com.example.points.util

import com.example.points.data.model.Brewery
import java.net.URI

private const val FAVICON_SERVICE = "https://www.google.com/s2/favicons?sz=128&domain="

/**
 * The API has no images, so we show the brewery website icon (favicon).
 * Returns null when there is no valid website: Picasso then shows the placeholder.
 * Example: "http://10barrel.com" -> ".../favicons?sz=128&domain=10barrel.com"
 */
fun Brewery.faviconUrl(): String? {
    val host = try {
        websiteUrl?.let { URI(it.trim()).host }
    } catch (e: Exception) {
        null // malformed URL coming from the API
    }
    return host?.takeIf { it.isNotBlank() }?.let { FAVICON_SERVICE + it }
}
