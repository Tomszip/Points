package com.example.points.data.model

import com.google.gson.annotations.SerializedName

/**
 * A brewery as returned by Open Brewery DB.
 * Every optional field is nullable because the API sends null when data is missing
 * (e.g. many breweries have no phone or website).
 */
data class Brewery(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String?,
    @SerializedName("brewery_type") val type: String?,
    @SerializedName("street") val street: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("state_province") val state: String?,
    @SerializedName("postal_code") val postalCode: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("website_url") val websiteUrl: String?
) {
    /** Joins only the address parts that exist, e.g. "1501 E St, San Diego, California". */
    val fullAddress: String
        get() = listOf(street, city, state, postalCode, country)
            .filter { !it.isNullOrBlank() }
            .joinToString(", ")
}
