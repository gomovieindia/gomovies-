package com.example.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    fun getAllWatchlist(): Flow<List<WatchlistMovie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToWatchlist(movie: WatchlistMovie)

    @Query("DELETE FROM watchlist WHERE id = :movieId")
    suspend fun removeFromWatchlist(movieId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :movieId LIMIT 1)")
    fun isInWatchlist(movieId: Int): Flow<Boolean>
}
