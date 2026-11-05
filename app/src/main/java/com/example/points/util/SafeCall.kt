package com.example.points.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Runs a blocking or network call on the IO thread pool and wraps the outcome in a Result,
 * so exceptions never reach the ViewModel uncaught.
 * Shared by the API and SQLite repositories.
 */
suspend fun <T> safeCall(call: suspend () -> T): Result<T> =
    withContext(Dispatchers.IO) {
        try {
            Result.success(call())
        } catch (e: CancellationException) {
            throw e // coroutine was cancelled (user left the screen): not an error
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
