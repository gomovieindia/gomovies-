package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Movie
import com.example.database.WatchlistMovie
import com.example.ui.theme.DodgerBlue
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.MovieViewModel

@Composable
fun WatchlistScreen(
    viewModel: MovieViewModel,
    onNavigateToDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val watchlistItems by viewModel.watchlistMovies.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Large title heading
        Text(
            text = "My Watchlist",
            color = TextWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        Text(
            text = "${watchlistItems.size} movies saved locally",
            color = TextGray,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Divider(color = Color(0xFF2C2C2E), modifier = Modifier.padding(bottom = 16.dp))

        if (watchlistItems.isEmpty()) {
            // Elegant placeholder empty state for initial configuration
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Empty Watchlist",
                    tint = TextGray,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Watchlist is empty",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Save movies you want to follow by tapping 'My Watchlist' on any detail or spotlight hero screen.",
                    color = TextGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        } else {
            // Grid of saved movies
            LazyVerticalGrid(
                columns = GridCells.Adaptive(110.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("watchlist_movies_grid")
            ) {
                items(watchlistItems, key = { it.id }) { item ->
                    WatchlistMovieCard(
                        movie = item,
                        onCardClick = {
                            // Re-wrap to Movie object for transition
                            val movieObj = Movie(
                                id = item.id,
                                title = item.title,
                                overview = item.overview,
                                posterPath = item.posterPath,
                                backdropPath = null,
                                voteAverage = item.voteAverage,
                                releaseDate = item.releaseDate,
                                genreIds = null
                            )
                            viewModel.selectMovie(movieObj)
                            onNavigateToDetail()
                        },
                        onDeleteClick = {
                            viewModel.toggleWatchlistFromMovie(item)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(75.dp)) // Offset Bottom Navigation Bar
    }
}

@Composable
fun WatchlistMovieCard(
    movie: WatchlistMovie,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onCardClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(190.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2C2C2E))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.getFullPosterUrl())
                    .crossfade(true)
                    .build(),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(id = android.R.drawable.ic_menu_gallery)
            )

            // Transparent Quick Delete Action Overlay
            IconButton(
                onClick = onDeleteClick,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                modifier = Modifier
                    .padding(6.dp)
                    .size(30.dp)
                    .align(Alignment.TopEnd)
                    .testTag("delete_watchlist_btn_${movie.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete item",
                    tint = Color(0xFFFF3B30),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = movie.title,
            color = TextWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = movie.releaseDate?.take(4) ?: "",
            color = TextGray,
            fontSize = 10.sp
        )
    }
}
