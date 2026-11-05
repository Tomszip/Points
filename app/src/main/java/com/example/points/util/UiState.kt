package com.example.points.util

import androidx.annotation.StringRes

/**
 * Represents every possible state of a screen that loads data.
 * The View observes this state and renders accordingly.
 */
sealed class UiState<out T> {

    /** Request in progress: the View shows a ProgressBar. */
    data object Loading : UiState<Nothing>()

    /** Request finished OK: the View shows the data. */
    data class Success<out T>(val data: T) : UiState<T>()

    /** Request failed or returned nothing: the View shows the message. */
    data class Error(@param:StringRes val messageRes: Int) : UiState<Nothing>()
}
