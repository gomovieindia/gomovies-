package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DodgerBlue
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MovieViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var isLanguageMenuExpanded by remember { mutableStateOf(false) }
    val languages = listOf("English", "Spanish", "French", "German", "Hindi", "Mandarin")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color(0xFF0A0A0A) else Color(0xFFF2F2F7))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Large title heading
        Text(
            text = "Settings",
            color = if (isDarkTheme) TextWhite else Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Divider(color = Color(0xFF2C2C2E), modifier = Modifier.padding(bottom = 20.dp))

        // Theme Toggle Section Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF161616) else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Cinema Dark Mode",
                        color = if (isDarkTheme) TextWhite else Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Turn off lights for theater experience",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { viewModel.toggleTheme() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = DodgerBlue,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray
                    ),
                    modifier = Modifier.testTag("theme_toggle_switch")
                )
            }
        }

        // Language Dropdown Selector Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF161616) else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isLanguageMenuExpanded = !isLanguageMenuExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Display Language",
                            color = if (isDarkTheme) TextWhite else Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Current: $selectedLanguage",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand languages",
                        tint = if (isDarkTheme) TextWhite else Color.Black,
                        modifier = Modifier.testTag("language_selector_btn")
                    )
                }

                DropdownMenu(
                    expanded = isLanguageMenuExpanded,
                    onDismissRequest = { isLanguageMenuExpanded = false },
                    modifier = Modifier
                        .background(if (isDarkTheme) Color(0xFF161616) else Color.White)
                        .testTag("language_dropdown_menu")
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = lang, 
                                    color = if (isDarkTheme) TextWhite else Color.Black 
                                ) 
                            },
                            onClick = {
                                viewModel.setLanguage(lang)
                                isLanguageMenuExpanded = false
                            },
                            modifier = Modifier.testTag("lang_option_$lang")
                        )
                    }
                }
            }
        }

        // Play Store Ready App Information Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF161616) else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = DodgerBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About GoMovies",
                        color = if (isDarkTheme) TextWhite else Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "GoMovies is a cinematic digital organizer designed for Google Play. Track popular upcoming theatrical releases, save lists to offline local storage, explore trailers and streams in a hardware-accelerated dashboard.",
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Version: 1.0.0 (Production-Ready)\nDeveloper: gomovieindia@gmail.com\nSecurity: Sandbox TLS Encrypted",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Required TMDB Attribution Footer (Play Store Rule)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TMDB Logo/Notice
            Text(
                text = "Powered by The Movie Database (TMDB)",
                color = DodgerBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "This product uses the TMDB API but is not endorsed or certified by TMDB.",
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(85.dp)) // Offset Bottom Navigation Bar
    }
}
