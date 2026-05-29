package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Movie
import com.example.ui.components.MovieCard
import com.example.ui.components.ShimmerMovieCard
import com.example.ui.theme.DodgerBlue
import com.example.ui.theme.StarYellow
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.MovieViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MovieViewModel,
    onNavigateToDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val popularMovies by viewModel.popularMovies.collectAsState()
    val topRatedMovies by viewModel.topRatedMovies.collectAsState()
    val nowPlayingMovies by viewModel.nowPlayingMovies.collectAsState()
    val upcomingMovies by viewModel.upcomingMovies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val watchlist by viewModel.watchlistMovies.collectAsState()

    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    val handleRefresh = {
        scope.launch {
            isRefreshing = true
            viewModel.refreshAllData()
            delay(1000) // Aesthetic delay for user feedback
            isRefreshing = false
        }
    }

    // Hero movie is the first movie of the trending/popular list
    val heroMovie = popularMovies.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Featured Hero Movie Banner
            if (heroMovie != null) {
                HeroBanner(
                    movie = heroMovie,
                    isInWatchlist = watchlist.any { it.id == heroMovie.id },
                    onPlayTrailer = {
                        viewModel.selectMovie(heroMovie)
                        onNavigateToDetail()
                    },
                    onToggleWatchlist = {
                        viewModel.toggleWatchlist(heroMovie)
                    },
                    onDetailsClick = {
                        viewModel.selectMovie(heroMovie)
                        onNavigateToDetail()
                    }
                )
            } else if (isLoading) {
                // Shimmer Loader for Hero
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.DarkGray, Color.Black)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pull to Refresh indicator showing inside the list flow
            if (isRefreshing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Error notice
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // 2. Trending Now Section
            MovieRowSection(
                title = "Trending Now",
                movies = popularMovies,
                isLoading = isLoading,
                onMovieSelect = { movie ->
                    viewModel.selectMovie(movie)
                    onNavigateToDetail()
                }
            )

            // 3. New Releases Section
            Spacer(modifier = Modifier.height(16.dp))
            MovieRowSection(
                title = "Latest Releases",
                movies = nowPlayingMovies,
                isLoading = isLoading,
                onMovieSelect = { movie ->
                    viewModel.selectMovie(movie)
                    onNavigateToDetail()
                }
            )

            // 4. Top Rated Movie Section
            Spacer(modifier = Modifier.height(16.dp))
            MovieRowSection(
                title = "Top Rated Movies",
                movies = topRatedMovies,
                isLoading = isLoading,
                onMovieSelect = { movie ->
                    viewModel.selectMovie(movie)
                    onNavigateToDetail()
                }
            )

            // 5. Coming Soon Section
            Spacer(modifier = Modifier.height(16.dp))
            MovieRowSection(
                title = "Coming Soon",
                movies = upcomingMovies,
                isLoading = isLoading,
                onMovieSelect = { movie ->
                    viewModel.selectMovie(movie)
                    onNavigateToDetail()
                }
            )

            Spacer(modifier = Modifier.height(80.dp)) // Extra padding for Bottom Navigation Bar
        }

        // Swipe-down Refresh Button (Fallback standard UX to trigger pulling)
        FloatingActionButton(
            onClick = { handleRefresh() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 16.dp)
                .size(46.dp)
                .testTag("pull_to_refresh_button")
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.stat_notify_sync),
                contentDescription = "Refresh movies",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HeroBanner(
    movie: Movie,
    isInWatchlist: Boolean,
    onPlayTrailer: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .testTag("hero_banner")
    ) {
        // High quality background backdrop image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.getFullBackdropUrl().ifEmpty { movie.getFullPosterUrl() })
                .crossfade(true)
                .build(),
            contentDescription = "Hero Back Drop",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = painterResource(id = android.R.drawable.ic_menu_gallery)
        )

        // Movie Poster overlaying gradient (Cinema grade fading out to theater black)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 1.0f)
                        ),
                        startY = 100f
                    )
                )
        )

        // Contents
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // IMDb Label Star Rating Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating STAR",
                    tint = StarYellow,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${movie.voteAverage}/10 (IMDb)",
                    color = StarYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Big Bold Title
            Text(
                text = movie.title,
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 28.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Release year + badge helpers
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 14.dp)
            ) {
                Text(
                    text = movie.releaseDate?.take(4) ?: "2024",
                    color = TextGray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "HD", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "FEATURED", color = DodgerBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Action Buttons Layer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Watch Now/Play trailer action inside
                Button(
                    onClick = onPlayTrailer,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                        .height(44.dp)
                        .testTag("play_hero_button")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Watch Trailer", fontWeight = FontWeight.Bold)
                }

                // Add to My Watchlist State Button
                OutlinedButton(
                    onClick = onToggleWatchlist,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = BorderStroke(1.dp, Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                        .height(44.dp)
                        .testTag("watchlist_hero_button")
                ) {
                    Icon(
                        imageVector = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Watchlist Action"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isInWatchlist) "In Watchlist" else "My Watchlist",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MovieRowSection(
    title: String,
    movies: List<Movie>,
    isLoading: Boolean,
    onMovieSelect: (Movie) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (isLoading && movies.isEmpty()) {
            LazyRow(contentPadding = PaddingValues(horizontal = 12.dp)) {
                items(5) {
                    ShimmerMovieCard()
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(movies) { movie ->
                    MovieCard(movie = movie, onClick = { onMovieSelect(movie) })
                }
            }
        }
    }
}
