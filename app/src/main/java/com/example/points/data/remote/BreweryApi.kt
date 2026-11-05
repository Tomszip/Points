package com.example.points.data.remote

import com.example.points.data.model.Brewery
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit definition of the Open Brewery DB endpoints used by the app.
 * Base URL: https://api.openbrewerydb.org/v1/
 * A null @Query is skipped by Retrofit, so one method covers list, name and city filters.
 */
interface BreweryApi {

    /**
     * Examples:
     * breweries?page=1&per_page=20                 -> general list
     * breweries?by_name=dog&page=1&per_page=20     -> filter by name
     * breweries?by_city=san_diego&page=1&per_page=20 -> filter by city
     */
    @GET("breweries")
    suspend fun getBreweries(
        @Query("by_name") name: String? = null,
        @Query("by_city") city: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = DEFAULT_PAGE_SIZE
    ): List<Brewery>

    /** Example: breweries/ef970757-fe42-416f-931d-722451f1f59c */
    @GET("breweries/{id}")
    suspend fun getBreweryById(@Path("id") id: String): Brewery

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
