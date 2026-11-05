package com.example.points.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.points.R
import com.example.points.data.local.PreferencesManager
import com.example.points.data.model.Brewery
import com.example.points.data.model.SearchFilter
import com.example.points.data.remote.BreweryApi
import com.example.points.data.repository.BreweryRepository
import com.example.points.util.UiState
import com.example.points.util.toErrorMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel of the main list. Holds the search state and survives rotation.
 * The last query and filter are restored from SharedPreferences.
 */
class BreweryListViewModel(
    private val repository: BreweryRepository,
    private val preferences: PreferencesManager
) : ViewModel() {

    // Mutable only inside the ViewModel; the View gets a read-only LiveData
    private val _uiState = MutableLiveData<UiState<List<Brewery>>>()
    val uiState: LiveData<UiState<List<Brewery>>> = _uiState

    /** true while the next page is being downloaded (small progress bar at the bottom). */
    private val _isLoadingMore = MutableLiveData(false)
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    /** One-time message (Toast), e.g. when the next page fails. The View clears it. */
    private val _message = MutableLiveData<Int?>(null)
    val message: LiveData<Int?> = _message

    var currentQuery: String = preferences.lastQuery
        private set
    var currentFilter: SearchFilter = preferences.lastFilter
        private set

    // Pagination state
    private var currentPage = 1
    private var hasMorePages = true

    private var searchJob: Job? = null

    init {
        loadBreweries() // first load: last saved search, or the general list
    }

    /** Called on every keystroke; waits a moment so we don't hit the API per letter. */
    fun onQueryChanged(query: String) {
        if (query == currentQuery) return
        currentQuery = query
        preferences.lastQuery = query
        loadBreweries(DEBOUNCE_MILLIS)
    }

    fun onFilterChanged(filter: SearchFilter) {
        if (filter == currentFilter) return
        currentFilter = filter
        preferences.lastFilter = filter
        loadBreweries()
    }

    fun retry() = loadBreweries()

    val isDarkMode: Boolean
        get() = preferences.isDarkMode

    /** Flips light <-> dark, saves it and returns the new value for the View to apply. */
    fun toggleTheme(): Boolean {
        val newValue = !preferences.isDarkMode
        preferences.isDarkMode = newValue
        return newValue
    }

    /** The View reports the user reached the end of the list; the ViewModel decides if there is more. */
    fun onListEndReached() {
        val shownBreweries = (_uiState.value as? UiState.Success)?.data ?: return
        if (_isLoadingMore.value == true || !hasMorePages) return

        _isLoadingMore.value = true
        searchJob = viewModelScope.launch {
            try {
                repository.getBreweries(currentQuery, currentFilter, currentPage + 1)
                    .onSuccess { nextPage -> appendPage(shownBreweries, nextPage) }
                    .onFailure { error -> _message.value = error.toErrorMessage() }
            } finally {
                _isLoadingMore.value = false // also runs if a new search cancels this job
            }
        }
    }

    fun onMessageShown() {
        _message.value = null
    }

    /** New search: always starts again from page 1. */
    private fun loadBreweries(debounceMillis: Long = 0) {
        searchJob?.cancel() // a new search replaces the previous one (or a page being loaded)
        searchJob = viewModelScope.launch {
            delay(debounceMillis)
            _uiState.value = UiState.Loading
            currentPage = 1
            repository.getBreweries(currentQuery, currentFilter, currentPage)
                .onSuccess { firstPage ->
                    hasMorePages = isFullPage(firstPage)
                    _uiState.value = toSuccessOrEmpty(firstPage)
                }
                .onFailure { error -> _uiState.value = UiState.Error(error.toErrorMessage()) }
        }
    }

    private fun appendPage(shownBreweries: List<Brewery>, nextPage: List<Brewery>) {
        currentPage++
        hasMorePages = isFullPage(nextPage)
        _uiState.value = UiState.Success(shownBreweries + nextPage)
    }

    /** The API has no "next" field: a page shorter than the page size means it was the last one. */
    private fun isFullPage(page: List<Brewery>) = page.size == BreweryApi.DEFAULT_PAGE_SIZE

    /** The API answers an empty list (not an error) when nothing matches. */
    private fun toSuccessOrEmpty(breweries: List<Brewery>): UiState<List<Brewery>> =
        if (breweries.isEmpty()) UiState.Error(R.string.search_no_results)
        else UiState.Success(breweries)

    private companion object {
        const val DEBOUNCE_MILLIS = 500L
    }
}
