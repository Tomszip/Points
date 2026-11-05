package com.example.points.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Creates and upgrades the local SQLite database.
 * We store every field we show so favorites can be browsed offline.
 * Private constructor + getInstance(): only one connection for the whole app (Singleton).
 */
class BreweryDbHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_FAVORITES_TABLE)
    }

    /** No migrations yet: on a version change the table is rebuilt. */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        onCreate(db)
    }

    companion object {
        @Volatile // every thread always sees the latest value
        private var instance: BreweryDbHelper? = null

        /** synchronized: two threads can't create two instances at the same time. */
        fun getInstance(context: Context): BreweryDbHelper =
            instance ?: synchronized(this) {
                instance ?: BreweryDbHelper(context.applicationContext).also { instance = it }
            }

        private const val DB_NAME = "breweries.db"
        private const val DB_VERSION = 1

        const val TABLE_FAVORITES = "favorites"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_TYPE = "brewery_type"
        const val COL_STREET = "street"
        const val COL_CITY = "city"
        const val COL_STATE = "state"
        const val COL_POSTAL_CODE = "postal_code"
        const val COL_COUNTRY = "country"
        const val COL_PHONE = "phone"
        const val COL_WEBSITE = "website_url"
        const val COL_STATUS = "status"     // VISITED or PENDING
        const val COL_SAVED_AT = "saved_at" // millis, used to sort newest first

        private const val CREATE_FAVORITES_TABLE = """
            CREATE TABLE $TABLE_FAVORITES (
                $COL_ID TEXT PRIMARY KEY,
                $COL_NAME TEXT,
                $COL_TYPE TEXT,
                $COL_STREET TEXT,
                $COL_CITY TEXT,
                $COL_STATE TEXT,
                $COL_POSTAL_CODE TEXT,
                $COL_COUNTRY TEXT,
                $COL_PHONE TEXT,
                $COL_WEBSITE TEXT,
                $COL_STATUS TEXT NOT NULL,
                $COL_SAVED_AT INTEGER NOT NULL
            )
        """
    }
}
