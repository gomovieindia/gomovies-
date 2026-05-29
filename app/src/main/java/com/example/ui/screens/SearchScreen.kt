package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MovieCard
import com.example.ui.components.ShimmerMovieCard
import com.example.ui.theme.DodgerBlue
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MovieViewModel,
    onNavigateToDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResultsByQuery by viewModel.searchResults.collectAsState()
    val isSearchLoading by viewModel.isSearchLoading.collectAsState()
    val genres by viewModel.genresList.collectAsState()

    // Filters Selected
    var selectedFilterGenre by remember { mutableStateOf<Int?>(null) }
    var selectedYearFilter by remember { mutableStateOf<String?>(null) }
    var selectedRatingFilter by remember { mutableStateOf<Double?>(null) }

    val filterYears = listOf("All Years", "2024", "2023", "2010", "1994", "1972")
    val filterRatings = listOf("Any Rating", "⭐ 8.0+", "⭐ 7.0+", "⭐ 6.0+")

    // Compute final filtered outputs
    val filteredResults = remember(searchResultsByQuery, selectedFilterGenre, selectedYearFilter, selectedRatingFilter) {
        var list = searchResultsByQuery
        
        // 1. Filter by Genre ID
        selectedFilterGenre?.let { genreId ->
            list = list.filter { it.genreIds?.contains(genreId) == true }
        }

        // 2. Filter by Year
        if (!selectedYearFilter.isNullOrEmpty() && selectedYearFilter != "All Years") {
            list = list.filter { it.releaseDate?.startsWith(selectedYearFilter!!) == true }
        }

        // 3. Filter by Star Rating
        selectedRatingFilter?.let { minRating ->
            list = list.filter { it.voteAverage >= minRating }
        }

        list
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0A0A))
                    .padding(top = 45.dp, bottom = 8.dp)
            ) {
                // Master Custom Search Bar Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text(text = "Search by title, actor, genre...", color = TextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = DodgerBlue,
                        unfocusedBorderColor = Color(0xFF2C2C2E),
                        focusedContainerColor = Color(0xFF161616),
                        unfocusedContainerColor = Color(0xFF161616)
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("search_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable filters section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Genre filter chips
                    ScrollableFilterRow(
                        title = "Genres",
                        items = listOf("All") + genres.map { it.name },
                        selectedItem = genres.find { it.id == selectedFilterGenre }?.name ?: "All",
                        onItemSelect = { selectedName ->
                            selectedFilterGenre = if (selectedName == "All") null else genres.find { it.name == selectedName }?.id
                        }
                    )

                    // Release Year filter chips
                    ScrollableFilterRow(
                        title = "Years",
                        items = filterYears,
                        selectedItem = selectedYearFilter ?: "All Years",
                        onItemSelect = { selectedName ->
                            selectedYearFilter = if (selectedName == "All Years") null else selectedName
                        }
                    )

                    // Ratings Filter chips
                    ScrollableFilterRow(
                        title = "IMDb Rating",
                        items = filterRatings,
                        selectedItem = when (selectedRatingFilter) {
                            8.0 -> "⭐ 8.0+"
                            7.0 -> "⭐ 7.0+"
                            6.0 -> "⭐ 6.0+"
                            else -> "Any Rating"
                        },
                        onItemSelect = { selectedName ->
                            selectedRatingFilter = when (selectedName) {
                                "⭐ 8.0+" -> 8.0
                                "⭐ 7.0+" -> 7.0
                                "⭐ 6.0+" -> 6.0
                                else -> null
                            }
                        }
                    )
                }
            }
        },
        containerColor = Color(0xFF0A0A0A)
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0A0A0A))
        ) {
            if (isSearchLoading) {
                // Shimmer grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(110.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(9) {
                        ShimmerMovieCard()
                    }
                }
            } else if (searchQuery.trim().length < 2) {
                // Empty state or search catalog tip
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Catalog",
                        tint = TextMuted,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Explore Blockbusters",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter movie name to search. Filter results by specific releases, reviews, or active genre types.",
                        color = TextGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            } else if (filteredResults.isEmpty()) {
                // No results matches
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No movies found",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "We couldn't matching movies under \"$searchQuery\" or selected rating constraints.",
                        color = TextGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Movie results grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(110.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.testTag("search_results_grid")
                ) {
                    items(filteredResults) { movie ->
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
    }
}

@Composable
fun ScrollableFilterRow(
    title: String,
    items: List<String>,
    selectedItem: String,
    onItemSelect: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "$title:",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach { value ->
                val isSelected = value == selectedItem
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) DodgerBlue else Color(0xFF1C1C1E))
                        .clickable { onItemSelect(value) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = value,
                        color = if (isSelected) Color.Black else TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
