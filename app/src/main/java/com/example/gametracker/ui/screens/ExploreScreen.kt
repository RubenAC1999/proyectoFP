package com.example.gametracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gametracker.model.GameModel
import com.example.gametracker.ui.theme.darkGray
import com.example.gametracker.ui.theme.grisClaro
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.example.gametracker.viewmodel.GameViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer


@Composable
fun ExploreScreenContent(
    gameViewModel: GameViewModel,
    apiKey: String,
    navController: NavController
) {
    val genreGamesMap by gameViewModel.genreGamesMap
    val searchResults by gameViewModel.searchResults
    val gamesByYear by gameViewModel.gamesByYear

    var searchQuery by remember { mutableStateOf("") }
    val availableYears = listOf("2024", "2023", "2022")
    var selectedYear by remember { mutableStateOf("2024") }

    LaunchedEffect(Unit) {
        gameViewModel.loadGamesByGenres(apiKey)
        gameViewModel.loadGamesByYear(apiKey)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(darkGray)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                if (it.length > 2) {
                    gameViewModel.searchGames(apiKey, it)
                } else {
                    gameViewModel.clearSearchResults()
                }
            },
            placeholder = { Text("Buscar juegos...", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = grisClaro,
                unfocusedContainerColor = grisClaro,
                cursorColor = naranja
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchResults.isNotEmpty()) {
            Text(
                "Resultados para \"$searchQuery\"",
                color = hueso,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            searchResults.forEach { game ->
                SearchGameCard(game = game) {
                    navController.navigate("game_detail/${game.id}")
                }
            }
        } else {
            Text("Filtrar por año", color = hueso, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                availableYears.forEach { year ->
                    val isSelected = selectedYear == year
                    Text(
                        text = year,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) naranja else grisClaro)
                            .clickable { selectedYear = year }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else hueso
                    )
                }
            }

            gamesByYear["Mejores de $selectedYear"]?.let { games ->
                Spacer(modifier = Modifier.height(12.dp))
                Text("Mejores de $selectedYear", color = hueso, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow {
                    items(games) { game ->
                        GameCard(game = game, onClick = {
                            navController.navigate("game_detail/${game.id}")
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            genreGamesMap.forEach { (genre, games) ->
                Text(
                    text = genre,
                    style = MaterialTheme.typography.titleMedium,
                    color = hueso,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyRow {
                    items(games) { game ->
                        GameCard(game = game, onClick = {
                            navController.navigate("game_detail/${game.id}")
                        })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}



    @Composable
    fun GameCard(game: GameModel.Game?, onClick: () -> Unit, isLoading: Boolean = false) {
        val shape = RoundedCornerShape(8.dp)

        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(120.dp)
                .clip(shape)
                .background(grisClaro)
                .clickable(enabled = !isLoading) { if (game != null) onClick() }
        ) {
            AsyncImage(
                model = game?.imageUrl ?: "",
                contentDescription = game?.name ?: "placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .placeholder(
                        visible = isLoading,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = Color.Gray
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = game?.name ?: "",
                color = naranja,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .placeholder(
                        visible = isLoading,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = Color.Gray
                    )
            )
        }
    }

@Composable
fun SearchGameCard(game: GameModel.Game, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = game.imageUrl,
                contentDescription = game.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(game.name, style = MaterialTheme.typography.titleMedium, color = naranja)
                Text(
                    text = game.genres.firstOrNull()?.name ?: "Sin género",
                    style = MaterialTheme.typography.bodySmall,
                    color = grisClaro
                )
            }
        }
    }
}



