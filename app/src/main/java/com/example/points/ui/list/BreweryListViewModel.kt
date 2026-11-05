package com.example.points.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.points.R
import com.example.points.data.local.PreferencesManager
import com.example.points.data.model.Brewery
import com.example.points.data.model.SearchFilter
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

    var currentQuery: String = preferences.lastQuery
        private set
    var currentFilter: SearchFilter = preferences.lastFilter
        private set

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

    private fun loadBreweries(debounceMillis: Long = 0) {
        searchJob?.cancel() // a new search replaces the previous one
        searchJob = viewModelScope.launch {
            delay(debounceMillis)
            _uiState.value = UiState.Loading
            repository.getBreweries(currentQuery, currentFilter)
                .onSuccess { breweries -> _uiState.value = toSuccessOrEmpty(breweries) }
                .onFailure { error -> _uiState.value = UiState.Error(error.toErrorMessage()) }
        }
    }

    /** The API answers an empty list (not an error) when nothing matches. */
    private fun toSuccessOrEmpty(breweries: List<Brewery>): UiState<List<Brewery>> =
        if (breweries.isEmpty()) UiState.Error(R.string.search_no_results)
        else UiState.Success(breweries)

    private companion object {
        const val DEBOUNCE_MILLIS = 500L
    }
}
