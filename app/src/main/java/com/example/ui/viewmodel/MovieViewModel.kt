package com.example.ui.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Cast
import com.example.data.model.Genre
import com.example.data.model.Movie
import com.example.data.repository.MovieRepository
import com.example.database.WatchlistMovie
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    // Theme state
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Language state
    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    // Home view states
    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies.asStateFlow()

    private val _topRatedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val topRatedMovies: StateFlow<List<Movie>> = _topRatedMovies.asStateFlow()

    private val _nowPlayingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val nowPlayingMovies: StateFlow<List<Movie>> = _nowPlayingMovies.asStateFlow()

    private val _upcomingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val upcomingMovies: StateFlow<List<Movie>> = _upcomingMovies.asStateFlow()

    private val _genresList = MutableStateFlow<List<Genre>>(emptyList())
    val genresList: StateFlow<List<Genre>> = _genresList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Browse by Genre states
    private val _selectedGenre = MutableStateFlow<Genre?>(null)
    val selectedGenre: StateFlow<Genre?> = _selectedGenre.asStateFlow()

    private val _moviesByGenre = MutableStateFlow<List<Movie>>(emptyList())
    val moviesByGenre: StateFlow<List<Movie>> = _moviesByGenre.asStateFlow()

    // Smart Search states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> = _searchResults.asStateFlow()

    private val _isSearchLoading = MutableStateFlow(false)
    val isSearchLoading: StateFlow<Boolean> = _isSearchLoading.asStateFlow()

    // Movie Detail states
    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    private val _selectedMovieCast = MutableStateFlow<List<Cast>>(emptyList())
    val selectedMovieCast: StateFlow<List<Cast>> = _selectedMovieCast.asStateFlow()

    private val _selectedMovieTrailer = MutableStateFlow<String?>(null)
    val selectedMovieTrailer: StateFlow<String?> = _selectedMovieTrailer.asStateFlow()

    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading: StateFlow<Boolean> = _isDetailLoading.asStateFlow()

    // Watchlist items (Observed reactive Room stream)
    val watchlistMovies: StateFlow<List<WatchlistMovie>> = repository.watchlistFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isCurrentMovieInWatchlist = MutableStateFlow(false)
    val isCurrentMovieInWatchlist: StateFlow<Boolean> = _isCurrentMovieInWatchlist.asStateFlow()

    init {
        refreshAllData()
    }

    fun refreshAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Fetch basic lists concurrently
                launch { 
                    _popularMovies.value = repository.getPopularMovies() 
                }
                launch { 
                    _topRatedMovies.value = repository.getTopRatedMovies() 
                }
                launch { 
                    _nowPlayingMovies.value = repository.getNowPlayingMovies() 
                }
                launch { 
                    _upcomingMovies.value = repository.getUpcomingMovies() 
                }
                launch { 
                    val genres = repository.getGenreList()
                    _genresList.value = genres
                    if (genres.isNotEmpty() && _selectedGenre.value == null) {
                        selectGenre(genres.first())
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load movie lists. Displaying saved offline database entries."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectGenre(genre: Genre) {
        _selectedGenre.value = genre
        viewModelScope.launch {
            try {
                // Combine our local mock or remote lists to filter by selected genre to demonstrate beautifully
                val combined = _popularMovies.value + _topRatedMovies.value + _nowPlayingMovies.value + _upcomingMovies.value
                val filtered = combined.distinctBy { it.id }.filter { movie ->
                    movie.genreIds?.contains(genre.id) == true
                }
                
                if (filtered.isEmpty()) {
                    // Fallback to general list if specific genre is empty in offline state
                    _moviesByGenre.value = _popularMovies.value.shuffled()
                } else {
                    _moviesByGenre.value = filtered
                }
            } catch (e: Exception) {
                _moviesByGenre.value = emptyList()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.trim().length >= 2) {
            viewModelScope.launch {
                _isSearchLoading.value = true
                try {
                    _searchResults.value = repository.searchMovies(query.trim())
                } catch (e: Exception) {
                    _searchResults.value = emptyList()
                } finally {
                    _isSearchLoading.value = false
                }
            }
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
        viewModelScope.launch {
            _isDetailLoading.value = true
            _selectedMovieCast.value = emptyList()
            _selectedMovieTrailer.value = null
            try {
                // Check local watchlist state for this selected movie
                repository.isInWatchlist(movie.id).collectLatest { inWatchlist ->
                    _isCurrentMovieInWatchlist.value = inWatchlist
                }
            } catch (e: Exception) {
                // Ignore background collectors
            }
        }

        viewModelScope.launch {
            try {
                // Fetch full details, cast and trailer concurrently
                launch {
                    val fullDetails = repository.getMovieDetails(movie.id)
                    // Blend runtime and language back into selected object
                    _selectedMovie.value = movie.copy(
                        runtime = fullDetails.runtime ?: movie.runtime,
                        originalLanguage = fullDetails.originalLanguage ?: movie.originalLanguage
                    )
                }
                launch {
                    _selectedMovieCast.value = repository.getMovieCredits(movie.id)
                }
                launch {
                    _selectedMovieTrailer.value = repository.getMovieVideos(movie.id)
                }
            } catch (e: Exception) {
                _selectedMovieCast.value = emptyList()
            } finally {
                _isDetailLoading.value = false
            }
        }
    }

    fun toggleWatchlist(movie: Movie) {
        viewModelScope.launch {
            if (_isCurrentMovieInWatchlist.value) {
                repository.removeFromWatchlist(movie.id)
                _isCurrentMovieInWatchlist.value = false
            } else {
                repository.addToWatchlist(movie)
                _isCurrentMovieInWatchlist.value = true
            }
        }
    }

    fun toggleWatchlistFromMovie(watchlistMovie: WatchlistMovie) {
        viewModelScope.launch {
            repository.removeFromWatchlist(watchlistMovie.id)
        }
    }
}

// ViewModelFactory definition to pass our repository
class MovieViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
