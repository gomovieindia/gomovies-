package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MovieCard
import com.example.ui.components.ShimmerMovieCard
import com.example.ui.theme.DodgerBlue
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.MovieViewModel

@Composable
fun GenreScreen(
    viewModel: MovieViewModel,
    onNavigateToDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val genres by viewModel.genresList.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val moviesByGenre by viewModel.moviesByGenre.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val genreGradients = listOf(
        Brush.horizontalGradient(colors = listOf(Color(0xFFFF5E62), Color(0xFFFF9966))), // Coral sunset
        Brush.horizontalGradient(colors = listOf(Color(0xFF3A7BD5), Color(0xFF3A6073))), // Oceanic
        Brush.horizontalGradient(colors = listOf(Color(0xFF4CA1AF), Color(0xFFC4E0E5))), // Minty
        Brush.horizontalGradient(colors = listOf(Color(0xFF11998E), Color(0xFF38EF7D))), // Forest
        Brush.horizontalGradient(colors = listOf(Color(0xFF7F00FF), Color(0xFFE100FF))), // Retro
        Brush.horizontalGradient(colors = listOf(Color(0xFF396AFC), Color(0xFF2948FF))), // Royal Blue
        Brush.horizontalGradient(colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF))), // Sky Deep
        Brush.horizontalGradient(colors = listOf(Color(0xFFF12711), Color(0xFFF5AF19))), // Flame
        Brush.horizontalGradient(colors = listOf(Color(0xFF8A2387), Color(0xFFE94057))), // Purple pink
        Brush.horizontalGradient(colors = listOf(Color(0xFF2C3E50), Color(0xFF3498DB)))  // Slate tech
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        // High visibility Header Title
        Spacer(modifier = Modifier.height(30.dp))
        
        Text(
            text = "Browse Categories",
            color = TextWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Horizontal scrolling category selection list
        if (genres.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(genres) { genre ->
                    val isSelected = selectedGenre?.id == genre.id
                    val index = genres.indexOf(genre)
                    val bgBrush = genreGradients[index % genreGradients.size]

                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgBrush)
                            .clickable { viewModel.selectGenre(genre) }
                            .testTag("genre_tab_${genre.name}")
                    ) {
                        // Blurred Overlay for selected category
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (isSelected) Color.Black.copy(alpha = 0.5f) 
                                    else Color.Transparent
                                )
                        )
                        Text(
                            text = genre.name,
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(4.dp)
                        )
                    }
                }
            }
        } else if (isLoading) {
            CircularProgressIndicator(
                color = DodgerBlue,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Divider(color = Color(0xFF2C2C2E), modifier = Modifier.padding(bottom = 16.dp))

        // Selected category feedback description
        selectedGenre?.let { currentGenre ->
            Text(
                text = "${currentGenre.name} Movies",
                color = DodgerBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Active listing vertical grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (isLoading && moviesByGenre.isEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(110.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(6) {
                        ShimmerMovieCard()
                    }
                }
            } else if (moviesByGenre.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_input_add),
                        contentDescription = "Empty",
                        tint = TextGray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No movies currently in this genre",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(110.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.testTag("genre_movies_grid")
                ) {
                    items(moviesByGenre) { movie ->
                        MovieCard(
                            movie = movie,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.selectMovie(movie)
                                onNavigateToDetail()
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(75.dp)) // Offset Bottom Navigation Bar
    }
}
