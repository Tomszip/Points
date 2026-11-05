package com.example.points.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.points.data.local.BreweryDbHelper
import com.example.points.data.local.FavoritesDao
import com.example.points.data.local.PreferencesManager
import com.example.points.data.repository.BreweryRepository
import com.example.points.data.repository.FavoritesRepository
import com.example.points.ui.detail.DetailViewModel
import com.example.points.ui.favorites.FavoritesViewModel
import com.example.points.ui.list.BreweryListViewModel

/**
 * Factory pattern: builds ViewModels that need constructor parameters.
 * Uses the application context so no Activity is leaked.
 */
class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(BreweryListViewModel::class.java) ->
            BreweryListViewModel(BreweryRepository(), PreferencesManager(appContext))

        modelClass.isAssignableFrom(DetailViewModel::class.java) ->
            DetailViewModel(BreweryRepository(), favoritesRepository())

        modelClass.isAssignableFrom(FavoritesViewModel::class.java) ->
            FavoritesViewModel(favoritesRepository())

        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    } as T

    private fun favoritesRepository() =
        FavoritesRepository(FavoritesDao(BreweryDbHelper.getInstance(appContext)))
}
