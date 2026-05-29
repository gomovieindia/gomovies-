package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieResponse(
    @Json(name = "results") val results: List<Movie>
)

@JsonClass(generateAdapter = true)
data class Movie(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "overview") val overview: String?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>?,
    @Json(name = "runtime") val runtime: Int? = null,
    @Json(name = "original_language") val originalLanguage: String? = null
) {
    fun getFullPosterUrl(): String {
        return if (posterPath.isNullOrEmpty()) "" else "https://image.tmdb.org/t/p/w500$posterPath"
    }

    fun getFullBackdropUrl(): String {
        return if (backdropPath.isNullOrEmpty()) "" else "https://image.tmdb.org/t/p/w780$backdropPath"
    }
}

@JsonClass(generateAdapter = true)
data class GenreResponse(
    @Json(name = "genres") val genres: List<Genre>
)

@JsonClass(generateAdapter = true)
data class Genre(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class VideoResponse(
    @Json(name = "results") val results: List<Video>
)

@JsonClass(generateAdapter = true)
data class Video(
    @Json(name = "id") val id: String,
    @Json(name = "key") val key: String, // YouTube Key
    @Json(name = "site") val site: String,
    @Json(name = "type") val type: String
)

@JsonClass(generateAdapter = true)
data class CreditsResponse(
    @Json(name = "cast") val cast: List<Cast>
)

@JsonClass(generateAdapter = true)
data class Cast(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "character") val character: String,
    @Json(name = "profile_path") val profilePath: String?
) {
    fun getFullProfileUrl(): String {
        return if (profilePath.isNullOrEmpty()) "" else "https://image.tmdb.org/t/p/w185$profilePath"
    }
}
