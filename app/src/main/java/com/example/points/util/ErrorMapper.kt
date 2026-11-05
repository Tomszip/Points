package com.example.points.util

import android.database.SQLException
import androidx.annotation.StringRes
import com.example.points.R
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Maps a technical exception to a user friendly message resource.
 * Order matters: SocketTimeoutException is also an IOException.
 */
@StringRes
fun Throwable.toErrorMessage(): Int = when (this) {
    is HttpException -> R.string.error_http               // 4xx / 5xx response
    is SocketTimeoutException -> R.string.error_timeout   // server too slow
    is IOException -> R.string.error_no_connection        // no internet / DNS failure
    is JsonParseException -> R.string.error_bad_data      // unexpected JSON format
    is SQLException -> R.string.error_database            // local database failure
    else -> R.string.error_unknown
}
