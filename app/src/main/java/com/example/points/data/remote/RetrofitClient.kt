package com.example.points.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton that builds the Retrofit instance only once
 * and exposes the API implementation to the rest of the app.
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.openbrewerydb.org/v1/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON <-> Kotlin objects
            .build()
    }

    /** Retrofit generates the implementation of the interface at runtime. */
    val api: BreweryApi by lazy {
        retrofit.create(BreweryApi::class.java)
    }
}
