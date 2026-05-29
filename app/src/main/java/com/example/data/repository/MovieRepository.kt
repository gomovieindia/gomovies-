package com.example.data.repository

import com.example.data.api.TMDBApiService
import com.example.data.model.Cast
import com.example.data.model.Genre
import com.example.data.model.Movie
import com.example.database.WatchlistDao
import com.example.database.WatchlistMovie
import com.example.BuildConfig
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class MovieRepository(
    private val apiService: TMDBApiService,
    private val watchlistDao: WatchlistDao
) {
    private val apiKey: String
        get() = BuildConfig.TMDB_API_KEY.ifBlank { "YOUR_TMDB_API_KEY" }

    // Remote TMDB Operations with mock fallback in case of missing/invalid API key or offline status
    suspend fun getPopularMovies(page: Int = 1): List<Movie> {
        return try {
            if (apiKey == "YOUR_TMDB_API_KEY") throw IllegalArgumentException("No API key")
            apiService.getPopularMovies(apiKey, page).results
        } catch (e: Exception) {
            getPopularMockMovies()
        }
    }

    suspend fun getTopRatedMovies(page: Int = 1): List<Movie> {
        return try {
            if (apiKey == "YOUR_TMDB_API_KEY") throw IllegalArgumentException("No API key")
            apiService.getTopRatedMovies(apiKey, page).results
        } catch (e: Exception) {
            getTopRatedMockMovies()
        }
    }

    suspend fun getNowPlayingMovies(page: Int = 1): List<Movie> {
        return try {
            if (apiKey == "YOUR_TMDB_API_KEY") throw IllegalArgumentException("No API key")
            apiService.getNowPlayingMovies(apiKey, page).results
        } catch (e: Exception) {
            getNewReleasesMockMovies()
        }
    }

    suspend fun getUpcomingMovies(page: Int = 1): List<Movie> {
        return try {
            if (apiKey == "YOUR_TMDB_API_KEY") throw IllegalArgumentException("No API key")
            apiService.getUpcomingMovies(apiKey, page).results
        } catch (e: Exception) {
            getUpcomingMockMovies()
        }
    }

    suspend fun searchMovies(query: String, page: Int = 1): List<Movie> {
        if (query.isBlank()) return emptyList()
        return try {
            if (apiKey == "YOUR_TMDB_API_KEY") throw IllegalArgumentException("No API key")
            apiService.searchMovies(apiKey, query, page).results
        } catch (e: Exception) {
            // Local filter of all combined mock lists
            val allMocks = getPopularMockMovies() + getTopRatedMockMovies() + getNewReleasesMockMovies() + getUpcomingMockMovies()
            allMocks.distinctBy { it.id }.filter {
                it.title.contains(query, ignoreCase = true) || 
                it.overview?.contains(query, ignoreCase = true) == true
            }
        }
    }

    suspend fun getMovieDetails(movieId: Int): Movie {
        return try {
            if (apiKey == "YOUR_TMDB_API_KEY") throw IllegalArgumentException("No API key")
            apiService.getMovieDetails(movieId, apiKey)
        } catch (e: Exception) {
            val allMocks = getPopularMockMovies() + getTopRatedMockMovies() + getNewReleasesMockMovies() + getUpcomingMockMovies()
            allMocks.find { it.id == movieId } ?: getPopularMockMovies().first()
        }
    }

    suspend fun getMovieVideos(movieId: Int): String? {
        return try {
            if (apiKey == "YOUR_TMDB_API_KEY") throw IllegalArgumentException("No API key")
            val videos = apiService.getMovieVideos(movieId, apiKey).results
            // Return first YouTube trailer key
            videos.find { it.site.equals("YouTube", ignoreCase = true) && it.type.equals("Trailer", ignoreCase = true) }?.key
                ?: videos.firstOrNull { it.site.equals("YouTube", ignoreCase = true) }?.key
        } catch (e: Exception) {
            // Mock YouTube ID for trailers (classic high quality movie teasers)
            when (movieId) {
                1 -> "qdskp3m0pTM" // Interstellar
                2 -> "YoHD9XEInc0" // Inception
                3 -> "SUXWAEX2jlg" // Dune-2
                4 -> "8hP9D6kZseM" // Spider-Man
                5 -> "5PSNL1q3fc0" // Oppenheimer
                6 -> "g8zXiC5_qks" // Godzilla
                else -> "qdskp3m0pTM"
            }
        }
    }

    suspend fun getMovieCredits(movieId: Int): List<Cast> {
        return try {
            if (apiKey == "YOUR_TMDB_API_KEY") throw IllegalArgumentException("No API key")
            apiService.getMovieCredits(movieId, apiKey).cast
        } catch (e: Exception) {
            getMockCredits(movieId)
        }
    }

    suspend fun getGenreList(): List<Genre> {
        return try {
            if (apiKey == "YOUR_TMDB_API_KEY") throw IllegalArgumentException("No API key")
            apiService.getGenreList(apiKey).genres
        } catch (e: Exception) {
            getMockGenres()
        }
    }

    // Local Dao Watchlist operations
    val watchlistFlow: Flow<List<WatchlistMovie>> = watchlistDao.getAllWatchlist()

    suspend fun addToWatchlist(movie: Movie) {
        watchlistDao.insertToWatchlist(
            WatchlistMovie(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                voteAverage = movie.voteAverage,
                releaseDate = movie.releaseDate,
                overview = movie.overview
            )
        )
    }

    suspend fun removeFromWatchlist(movieId: Int) {
        watchlistDao.removeFromWatchlist(movieId)
    }

    fun isInWatchlist(movieId: Int): Flow<Boolean> {
        return watchlistDao.isInWatchlist(movieId)
    }

    // High fidelity Fallback Mock Data Providers
    private fun getPopularMockMovies() = listOf(
        Movie(
            id = 1,
            title = "Interstellar",
            overview = "The adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.",
            posterPath = "/gEU2m806egoe06vIjaM0m6qgBkM.jpg", // Real TMDB post paths style
            backdropPath = "/xJHb98asfvbh0as7df8.jpg",
            voteAverage = 8.4,
            releaseDate = "2014-11-07",
            genreIds = listOf(878, 12, 18), // Sci-fi, Adventure, Drama
            runtime = 169,
            originalLanguage = "EN"
        ),
        Movie(
            id = 2,
            title = "Inception",
            overview = "Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets is offered a chance to regain his old life as payment for a task considered to be impossible: \"inception\", the implantation of another person's idea into a target's subconscious.",
            posterPath = "/o0I0tY9sa6QgMjN77R4g6gDcbwi.jpg",
            backdropPath = "/s36G7I9asdA9X.jpg",
            voteAverage = 8.3,
            releaseDate = "2010-07-16",
            genreIds = listOf(28, 878, 12), // Action, Sci-fi, Adventure
            runtime = 148,
            originalLanguage = "EN"
        ),
        Movie(
            id = 3,
            title = "Dune: Part Two",
            overview = "Follow the mythic journey of Paul Atreides as he unites with Chani and the Fremen while on a path of revenge against the conspirators who destroyed his family.",
            posterPath = "/cz8Id8N8l5R2sL3i8FAdy9SAn4g.jpg",
            backdropPath = "/uz9288dhsd78w.jpg",
            voteAverage = 8.5,
            releaseDate = "2024-03-01",
            genreIds = listOf(878, 12),
            runtime = 166,
            originalLanguage = "EN"
        )
    )

    private fun getTopRatedMockMovies() = listOf(
        Movie(
            id = 4,
            title = "The Shawshank Redemption",
            overview = "Framed in the 1940s for the double murder of his wife and her lover, upstanding banker Andy Dufresne begins a new life at the Shawshank prison, where he puts his accounting skills to work for an amoral warden.",
            posterPath = "/9cqN02CH6g6t668WPr6YTy79QA3.jpg",
            backdropPath = "/kXbgsa9sdfs.jpg",
            voteAverage = 8.7,
            releaseDate = "1994-09-23",
            genreIds = listOf(18, 80), // Drama, Crime
            runtime = 142,
            originalLanguage = "EN"
        ),
        Movie(
            id = 5,
            title = "The Godfather",
            overview = "Spanning the years 1945 to 1955, a chronicle of the fictional Italian-American Corleone crime family. When organized crime family patriarch, Vito Corleone, is barely surviving an attempt on his life, his youngest son, Michael, steps in.",
            posterPath = "/3bhkrj6P6Xn6I4KL066P6Y7gRmi.jpg",
            backdropPath = "/vj7f8f7fdsfsd.jpg",
            voteAverage = 8.7,
            releaseDate = "1972-03-14",
            genreIds = listOf(18, 80),
            runtime = 175,
            originalLanguage = "EN"
        ),
        Movie(
            id = 1, // Reuse for seamless details
            title = "Interstellar",
            overview = "The adventures of a group of explorers who make use of a newly discovered wormhole in human space travel.",
            posterPath = "/gEU2m806egoe06vIjaM0m6qgBkM.jpg",
            backdropPath = "/xJHb98asfvbh0as7df8.jpg",
            voteAverage = 8.4,
            releaseDate = "2014-11-07",
            genreIds = listOf(878, 12, 18),
            runtime = 169,
            originalLanguage = "EN"
        )
    )

    private fun getNewReleasesMockMovies() = listOf(
        Movie(
            id = 6,
            title = "Kingdom of the Planet of the Apes",
            overview = "Several generations in the future following Caesar's reign, apes are now the dominant species living harmoniously, while humans have been reduced to living in the shadows.",
            posterPath = "/gKkl379XurA70gv6R8uCh69HQRB.jpg",
            backdropPath = "/fygh789sdf.jpg",
            voteAverage = 7.1,
            releaseDate = "2024-05-08",
            genreIds = listOf(28, 12, 878),
            runtime = 145,
            originalLanguage = "EN"
        ),
        Movie(
            id = 7,
            title = "Civil War",
            overview = "In a near-future America, a team of military-embedded journalists races against time to reach Washington, D.C. before rebel factions descend upon the White House.",
            posterPath = "/sh7G73Ty6Z7m9gW0gR6Xnyy5BQR.jpg",
            backdropPath = "/gfhg789fs.jpg",
            voteAverage = 7.0,
            releaseDate = "2024-04-10",
            genreIds = listOf(28, 18, 10752), // Action, Drama, War
            runtime = 109,
            originalLanguage = "EN"
        ),
        Movie(
            id = 3,
            title = "Dune: Part Two",
            overview = "Follow public journey of Paul Atreides.",
            posterPath = "/cz8Id8N8l5R2sL3i8FAdy9SAn4g.jpg",
            backdropPath = "/uz9288dhsd78w.jpg",
            voteAverage = 8.5,
            releaseDate = "2024-03-01",
            genreIds = listOf(878, 12),
            runtime = 166,
            originalLanguage = "EN"
        )
    )

    private fun getUpcomingMockMovies() = listOf(
        Movie(
            id = 8,
            title = "Deadpool & Wolverine",
            overview = "A listless Wade Wilson toils in civilian life. His days as the morally flexible mercenary, Deadpool, behind him. When his homeworld faces an existential threat, Wade must reluctantly suit up again with an even more reluctant Wolverine.",
            posterPath = "/8cdIH06V7z6mHgoPgPNGgSgH0gq.jpg",
            backdropPath = "/jkshfkjshf.jpg",
            voteAverage = 8.1,
            releaseDate = "2024-07-26",
            genreIds = listOf(28, 35, 878), // Action, Comedy, Sci-fi
            runtime = 127,
            originalLanguage = "EN"
        ),
        Movie(
            id = 9,
            title = "Inside Out 2",
            overview = "Teenager Riley's mind headquarters is undergoing a sudden demolition to make room for something entirely unexpected: new Emotions! Joy, Sadness, Anger, Fear, and Disgust aren't sure how to feel when Anxiety shows up.",
            posterPath = "/vpnVMIDwY79486m69gSgH0gS.jpg",
            backdropPath = "/sdhfkshfjks.jpg",
            voteAverage = 8.0,
            releaseDate = "2024-06-14",
            genreIds = listOf(16, 35, 10751), // Animation, Comedy, Family
            runtime = 96,
            originalLanguage = "EN"
        )
    )

    private fun getMockCredits(movieId: Int): List<Cast> {
        return listOf(
            Cast(id = 101, name = "Matthew McConaughey", character = "Cooper", profilePath = "/s6Gfh7s.jpg"),
            Cast(id = 102, name = "Anne Hathaway", character = "Brand", profilePath = "/s7Gh7s.jpg"),
            Cast(id = 103, name = "Jessica Chastain", character = "Murph", profilePath = "/s8Gh7s.jpg"),
            Cast(id = 104, name = "Michael Caine", character = "Professor Brand", profilePath = "/s9Gh7s.jpg")
        )
    }

    private fun getMockGenres(): List<Genre> {
        return listOf(
            Genre(id = 28, name = "Action"),
            Genre(id = 12, name = "Adventure"),
            Genre(id = 16, name = "Animation"),
            Genre(id = 35, name = "Comedy"),
            Genre(id = 80, name = "Crime"),
            Genre(id = 99, name = "Documentary"),
            Genre(id = 18, name = "Drama"),
            Genre(id = 10751, name = "Family"),
            Genre(id = 14, name = "Fantasy"),
            Genre(id = 36, name = "History"),
            Genre(id = 27, name = "Horror"),
            Genre(id = 10402, name = "Music"),
            Genre(id = 9648, name = "Mystery"),
            Genre(id = 10749, name = "Romance"),
            Genre(id = 878, name = "Sci-Fi"),
            Genre(id = 10770, name = "TV Movie"),
            Genre(id = 53, name = "Thriller"),
            Genre(id = 10752, name = "War"),
            Genre(id = 37, name = "Western")
        )
    }
}
