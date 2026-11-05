package com.example.points.data.repository

import com.example.points.data.local.FavoritesDao
import com.example.points.data.model.Brewery
import com.example.points.data.model.FavoriteStatus
import com.example.points.util.safeCall

/**
 * Access point to the local favorites (Visited / Pending).
 * Every call runs on a background thread through safeCall,
 * because SQLite must never block the UI thread.
 */
class FavoritesRepository(private val dao: FavoritesDao) {

    suspend fun save(brewery: Brewery, status: FavoriteStatus): Result<Unit> = safeCall {
        dao.save(brewery, status)
    }

    suspend fun delete(id: String): Result<Unit> = safeCall {
        dao.delete(id)
    }

    suspend fun getByStatus(status: FavoriteStatus): Result<List<Brewery>> = safeCall {
        dao.getByStatus(status)
    }

    suspend fun getById(id: String): Result<Brewery?> = safeCall {
        dao.getById(id)
    }

    suspend fun getStatus(id: String): Result<FavoriteStatus?> = safeCall {
        dao.getStatus(id)
    }
}
