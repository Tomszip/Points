package com.example.points.ui.detail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.points.R
import com.example.points.data.model.Brewery
import com.example.points.data.model.FavoriteStatus
import com.example.points.data.repository.BreweryRepository
import com.example.points.data.repository.FavoritesRepository
import com.example.points.util.UiState
import com.example.points.util.toErrorMessage
import kotlinx.coroutines.launch

/**
 * ViewModel of the detail screen.
 * Loads the brewery from the API (or from SQLite when offline)
 * and manages its Visited / Pending status.
 */
class DetailViewModel(
    private val breweryRepository: BreweryRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<Brewery>>(UiState.Loading)
    val uiState: LiveData<UiState<Brewery>> = _uiState

    /** null = not saved in any list. */
    private val _favoriteStatus = MutableLiveData<FavoriteStatus?>(null)
    val favoriteStatus: LiveData<FavoriteStatus?> = _favoriteStatus

    /** One-time message (Snackbar). The View clears it with onMessageShown(). */
    private val _message = MutableLiveData<Int?>(null)
    val message: LiveData<Int?> = _message

    private var breweryId: String? = null

    /** Safe to call on every onCreate: after a rotation it doesn't reload. */
    fun load(id: String) {
        if (id == breweryId) return
        breweryId = id
        loadBrewery(id)
        loadFavoriteStatus(id)
    }

    fun retry() {
        breweryId?.let { loadBrewery(it) }
    }

    fun onStatusSelected(status: FavoriteStatus) {
        val brewery = currentBrewery() ?: return
        viewModelScope.launch {
            favoritesRepository.save(brewery, status)
                .onSuccess {
                    _favoriteStatus.value = status
                    showMessage(if (status == FavoriteStatus.VISITED) R.string.saved_visited else R.string.saved_pending)
                }
                .onFailure { showMessage(it.toErrorMessage()) }
        }
    }

    fun onRemoveFavorite() {
        val id = breweryId ?: return
        viewModelScope.launch {
            favoritesRepository.delete(id)
                .onSuccess {
                    _favoriteStatus.value = null
                    showMessage(R.string.removed_from_lists)
                }
                .onFailure { showMessage(it.toErrorMessage()) }
        }
    }

    fun onMessageShown() {
        _message.value = null
    }

    private fun loadBrewery(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            breweryRepository.getBreweryById(id)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { error -> showSavedCopyOrError(id, error) }
        }
    }

    /** Offline support: if the brewery was saved, show the copy stored in SQLite. */
    private suspend fun showSavedCopyOrError(id: String, error: Throwable) {
        val savedCopy = favoritesRepository.getById(id).getOrNull()
        _uiState.value = if (savedCopy != null) UiState.Success(savedCopy)
        else UiState.Error(error.toErrorMessage())
    }

    private fun loadFavoriteStatus(id: String) {
        viewModelScope.launch {
            favoritesRepository.getStatus(id).onSuccess { _favoriteStatus.value = it }
        }
    }

    private fun currentBrewery(): Brewery? = (_uiState.value as? UiState.Success)?.data

    private fun showMessage(@StringRes messageRes: Int) {
        _message.value = messageRes
    }
}
