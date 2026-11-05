package com.example.points.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.points.R
import com.example.points.data.model.Brewery
import com.example.points.data.model.FavoriteStatus
import com.example.points.data.repository.FavoritesRepository
import com.example.points.util.UiState
import com.example.points.util.toErrorMessage
import kotlinx.coroutines.launch

/**
 * ViewModel of the favorites tab. Reads the Visited / Pending lists from SQLite,
 * so this screen works without internet.
 */
class FavoritesViewModel(
    private val repository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<List<Brewery>>>()
    val uiState: LiveData<UiState<List<Brewery>>> = _uiState

    var selectedStatus: FavoriteStatus = FavoriteStatus.VISITED
        private set

    fun onStatusSelected(status: FavoriteStatus) {
        if (status == selectedStatus) return
        selectedStatus = status
        loadFavorites()
    }

    /** Called every time the tab becomes visible, because the detail screen may have changed the data. */
    fun loadFavorites() {
        viewModelScope.launch {
            repository.getByStatus(selectedStatus)
                .onSuccess { breweries -> _uiState.value = toSuccessOrEmpty(breweries) }
                .onFailure { error -> _uiState.value = UiState.Error(error.toErrorMessage()) }
        }
    }

    private fun toSuccessOrEmpty(breweries: List<Brewery>): UiState<List<Brewery>> = when {
        breweries.isNotEmpty() -> UiState.Success(breweries)
        selectedStatus == FavoriteStatus.VISITED -> UiState.Error(R.string.empty_visited)
        else -> UiState.Error(R.string.empty_pending)
    }
}
