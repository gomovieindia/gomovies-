package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistMovie(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String?,
    val overview: String?,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun getFullPosterUrl(): String {
        return if (posterPath.isNullOrEmpty()) "" else "https://image.tmdb.org/t/p/w500$posterPath"
    }
}
