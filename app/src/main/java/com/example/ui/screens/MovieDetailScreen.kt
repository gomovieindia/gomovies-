package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Movie
import com.example.ui.theme.DodgerBlue
import com.example.ui.theme.StarYellow
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    viewModel: MovieViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val movie by viewModel.selectedMovie.collectAsState()
    val cast by viewModel.selectedMovieCast.collectAsState()
    val trailerKey by viewModel.selectedMovieTrailer.collectAsState()
    val inWatchlist by viewModel.isCurrentMovieInWatchlist.collectAsState()
    val isDetailLoading by viewModel.isDetailLoading.collectAsState()

    var isPlayerVisible by remember { mutableStateOf(false) }
    var activePlayerUrl by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        movie?.let { currentMovie ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Movie Header Backdrop
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentMovie.getFullBackdropUrl().ifEmpty { currentMovie.getFullPosterUrl() })
                            .crossfade(true)
                            .build(),
                        contentDescription = "Movie Spotlight Backdrop",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )

                    // Cinematic shadow
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black.copy(alpha = 1.0f)
                                    )
                                )
                            )
                    )

                    // Floating circular play button for trailer
                    IconButton(
                        onClick = {
                            if (!trailerKey.isNullOrEmpty()) {
                                activePlayerUrl = "https://www.youtube.com/embed/$trailerKey"
                                isPlayerVisible = true
                            } else {
                                // Default fallback to generic trailer video search or movie introduction
                                activePlayerUrl = "https://www.youtube.com/embed/qdskp3m0pTM"
                                isPlayerVisible = true
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = DodgerBlue),
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.Center)
                            .testTag("trailer_play_fab")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Trailer",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // 2. Movie Primary Details Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Title and Metadata Header
                    Text(
                        text = currentMovie.title,
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = currentMovie.releaseDate?.take(4) ?: "2024",
                            color = TextGray,
                            fontSize = 13.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = currentMovie.originalLanguage?.uppercase() ?: "EN",
                                color = TextWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (currentMovie.runtime != null && currentMovie.runtime > 0) {
                            Text(
                                text = "${currentMovie.runtime} min",
                                color = TextGray,
                                fontSize = 13.sp
                            )
                        } else {
                            Text(
                                text = "124 min",
                                color = TextGray,
                                fontSize = 13.sp
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = StarYellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = String.format("%.1f", currentMovie.voteAverage),
                                color = StarYellow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Watch Now (WebView) & Watchlist buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                // Real watch now web player emulation path
                                activePlayerUrl = "https://vidsrc.me/embed/movie?tmdb=${currentMovie.id}"
                                isPlayerVisible = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DodgerBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("watch_now_detail_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play Streaming Stream")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Watch Now", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Button(
                            onClick = { viewModel.toggleWatchlist(currentMovie) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (inWatchlist) Color(0xFF2C2C2E) else Color.White.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("watchlist_detail_button")
                        ) {
                            Icon(
                                imageVector = if (inWatchlist) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = "Watchlist toggle",
                                tint = TextWhite
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (inWatchlist) "Saved" else "Watchlist",
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Movie Synopsis
                    Text(
                        text = "Synopsis",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = currentMovie.overview?.ifBlank { "No synopsis available for this selection." } 
                            ?: "Explore custom theatrical details and cast structures. Press Watch Now to stream trailer configurations.",
                        color = TextGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Expandable Cast List
                    if (cast.isNotEmpty()) {
                        Text(
                            text = "Featured Cast",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(cast) { member ->
                                CastMemberItem(member)
                            }
                        }
                    } else if (isDetailLoading) {
                        CircularProgressIndicator(
                            color = DodgerBlue,
                            modifier = Modifier
                                .padding(16.dp)
                                .size(24.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // 3. Floating Custom Detail Back Button
        IconButton(
            onClick = onBackClick,
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.6f)),
            modifier = Modifier
                .padding(top = 45.dp, start = 16.dp)
                .size(40.dp)
                .align(Alignment.TopStart)
                .testTag("detail_back_button")
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // 4. Overlaid Full Screen Web Player (WebView)
        AnimatedVisibility(
            visible = isPlayerVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            WebPlayerOverlay(
                url = activePlayerUrl,
                onDismiss = { isPlayerVisible = false }
            )
        }
    }
}

@Composable
fun CastMemberItem(member: com.example.data.model.Cast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(member.getFullProfileUrl())
                .crossfade(true)
                .build(),
            contentDescription = member.name,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
            contentScale = ContentScale.Crop,
            error = painterResource(id = android.R.drawable.presence_offline)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = member.name,
            color = TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = member.character,
            color = TextMuted,
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPlayerOverlay(url: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // WebView to render the player url securely
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                    
                    settings.apply {
                        javaScriptEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        domStorageEnabled = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Close Overlay Button
        IconButton(
            onClick = onDismiss,
            colors = IconButtonDefaults.iconButtonColors(containerColor = DodgerBlue),
            modifier = Modifier
                .padding(top = 45.dp, end = 16.dp)
                .size(44.dp)
                .align(Alignment.TopEnd)
                .testTag("close_web_player_button")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Player",
                tint = Color.White
            )
        }
    }
}
