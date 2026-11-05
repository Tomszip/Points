package com.example.points.data.repository

import com.example.points.data.model.Brewery
import com.example.points.data.model.SearchFilter
import com.example.points.data.remote.BreweryApi
import com.example.points.data.remote.RetrofitClient
import com.example.points.util.safeCall

/**
 * Single source of truth for remote brewery data.
 * ViewModels talk to this class and never to Retrofit directly.
 *
 * @param api injected through the constructor (manual Dependency Injection),
 * so it can be replaced with a fake implementation in tests.
 */
class BreweryRepository(
    private val api: BreweryApi = RetrofitClient.api
) {

    /**
     * Blank query -> general list. Otherwise filters by name or city.
     * The API expects spaces as underscores in by_city (e.g. "san_diego").
     */
    suspend fun getBreweries(
        query: String,
        filter: SearchFilter,
        page: Int = 1
    ): Result<List<Brewery>> = safeCall {
        val cleanQuery = query.trim().takeIf { it.isNotEmpty() }
        when (filter) {
            SearchFilter.NAME -> api.getBreweries(name = cleanQuery, page = page)
            SearchFilter.CITY -> api.getBreweries(city = cleanQuery?.replace(' ', '_'), page = page)
        }
    }

    suspend fun getBreweryById(id: String): Result<Brewery> = safeCall {
        api.getBreweryById(id)
    }
}
