package com.example.points.data.local

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.points.data.local.BreweryDbHelper.Companion.COL_CITY
import com.example.points.data.local.BreweryDbHelper.Companion.COL_COUNTRY
import com.example.points.data.local.BreweryDbHelper.Companion.COL_ID
import com.example.points.data.local.BreweryDbHelper.Companion.COL_NAME
import com.example.points.data.local.BreweryDbHelper.Companion.COL_PHONE
import com.example.points.data.local.BreweryDbHelper.Companion.COL_POSTAL_CODE
import com.example.points.data.local.BreweryDbHelper.Companion.COL_SAVED_AT
import com.example.points.data.local.BreweryDbHelper.Companion.COL_STATE
import com.example.points.data.local.BreweryDbHelper.Companion.COL_STATUS
import com.example.points.data.local.BreweryDbHelper.Companion.COL_STREET
import com.example.points.data.local.BreweryDbHelper.Companion.COL_TYPE
import com.example.points.data.local.BreweryDbHelper.Companion.COL_WEBSITE
import com.example.points.data.local.BreweryDbHelper.Companion.TABLE_FAVORITES
import com.example.points.data.model.Brewery
import com.example.points.data.model.FavoriteStatus

/**
 * Data Access Object: the only class that writes SQL for the favorites table.
 * Its functions are blocking, so callers must run them off the main thread.
 */
class FavoritesDao(private val dbHelper: BreweryDbHelper) {

    /** Inserts the brewery, or replaces it if it was already saved (e.g. status change). */
    fun save(brewery: Brewery, status: FavoriteStatus) {
        dbHelper.writableDatabase.insertWithOnConflict(
            TABLE_FAVORITES, null, toContentValues(brewery, status),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun delete(id: String) {
        dbHelper.writableDatabase.delete(TABLE_FAVORITES, "$COL_ID = ?", arrayOf(id))
    }

    /** Breweries of one list (Visited or Pending), newest first. */
    fun getByStatus(status: FavoriteStatus): List<Brewery> =
        dbHelper.readableDatabase.query(
            TABLE_FAVORITES, null, "$COL_STATUS = ?", arrayOf(status.name),
            null, null, "$COL_SAVED_AT DESC"
        ).use { cursor -> cursor.toBreweryList() } // use{} always closes the cursor

    /** The saved copy of a brewery, used to show the detail without internet. */
    fun getById(id: String): Brewery? =
        dbHelper.readableDatabase.query(
            TABLE_FAVORITES, null, "$COL_ID = ?", arrayOf(id), null, null, null
        ).use { cursor -> if (cursor.moveToFirst()) cursor.toBrewery() else null }

    /** Returns the list the brewery belongs to, or null if it isn't saved. */
    fun getStatus(id: String): FavoriteStatus? =
        dbHelper.readableDatabase.query(
            TABLE_FAVORITES, arrayOf(COL_STATUS), "$COL_ID = ?", arrayOf(id),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) FavoriteStatus.valueOf(cursor.getString(0)) else null
        }

    private fun toContentValues(brewery: Brewery, status: FavoriteStatus) =
        ContentValues().apply {
            put(COL_ID, brewery.id)
            put(COL_NAME, brewery.name)
            put(COL_TYPE, brewery.type)
            put(COL_STREET, brewery.street)
            put(COL_CITY, brewery.city)
            put(COL_STATE, brewery.state)
            put(COL_POSTAL_CODE, brewery.postalCode)
            put(COL_COUNTRY, brewery.country)
            put(COL_PHONE, brewery.phone)
            put(COL_WEBSITE, brewery.websiteUrl)
            put(COL_STATUS, status.name)
            put(COL_SAVED_AT, System.currentTimeMillis())
        }

    private fun Cursor.toBreweryList(): List<Brewery> {
        val breweries = mutableListOf<Brewery>()
        while (moveToNext()) breweries.add(toBrewery())
        return breweries
    }

    private fun Cursor.toBrewery() = Brewery(
        id = text(COL_ID).orEmpty(),
        name = text(COL_NAME),
        type = text(COL_TYPE),
        street = text(COL_STREET),
        city = text(COL_CITY),
        state = text(COL_STATE),
        postalCode = text(COL_POSTAL_CODE),
        country = text(COL_COUNTRY),
        phone = text(COL_PHONE),
        websiteUrl = text(COL_WEBSITE)
    )

    /** Reads a nullable text column by name. */
    private fun Cursor.text(column: String): String? =
        getColumnIndexOrThrow(column).let { index -> if (isNull(index)) null else getString(index) }
}
