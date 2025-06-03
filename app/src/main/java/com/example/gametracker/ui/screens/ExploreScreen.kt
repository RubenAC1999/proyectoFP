package com.example.gametracker.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.example.gametracker.model.UserModel
import com.example.gametracker.ui.theme.darkGray
import com.example.gametracker.ui.theme.grisClaro
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.example.gametracker.viewmodel.GameViewModel
import com.example.gametracker.viewmodel.UserViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@Composable
fun ExploreScreenContent(
    gameViewModel: GameViewModel,
    userViewModel: UserViewModel,
    apiKey: String,
    navController: NavController
) {
    val genreGamesMap by gameViewModel.genreGamesMap
    val searchResults by gameViewModel.searchResults
    val gamesByYear by gameViewModel.gamesByYear
    val recommendedGames by gameViewModel.recommendedGames
    val userGames = userViewModel.userGameEntries.value

    val availableYears = listOf("2024", "2023", "2022")
    val availableGenres = listOf("Action", "Adventure", "Indie", "RPG")

    var selectedYear by remember { mutableStateOf("2024") }
    var selectedGenre by remember { mutableStateOf("Action") }
    var selectedTab by remember { mutableStateOf("Juegos") }

    var searchQuery by remember { mutableStateOf("") }
    var showSearchOverlay by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        gameViewModel.loadGamesByGenres(apiKey)
        gameViewModel.loadGamesByYear(apiKey)
    }

    LaunchedEffect(userGames) {
        val favoriteGenres = gameViewModel.getFavoriteGenres(userGames)
        gameViewModel.loadPersonalRecommendations(apiKey, favoriteGenres)
    }

    LaunchedEffect(Unit) {
        Firebase.auth.currentUser?.uid?.let {
            userViewModel.loadUserGameEntries(it)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(darkGray)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(grisClaro)
                    .clickable {
                        Log.d("ExploreScreen", "Box clicked")
                        showSearchOverlay = true
                    }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("Buscar juegos o usuarios...", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))


            Spacer(modifier = Modifier.height(16.dp))

            // Filtro por año
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

            Spacer(modifier = Modifier.height(12.dp))
            gamesByYear["Mejores de $selectedYear"]?.let { games ->
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

            Spacer(modifier = Modifier.height(16.dp))

            // 🎮 Filtro por género
            Text("Filtrar por género", color = hueso, style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                availableGenres.forEach { genre ->
                    val isSelected = selectedGenre == genre
                    Text(
                        text = genre,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) naranja else grisClaro)
                            .clickable { selectedGenre = genre }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else hueso
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            genreGamesMap[selectedGenre]?.let { games ->
                Text("Juegos de $selectedGenre", color = hueso, style = MaterialTheme.typography.titleMedium)
                LazyRow {
                    items(games) { game ->
                        GameCard(game = game, onClick = {
                            navController.navigate("game_detail/${game.id}")
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Recomendado según tus gustos", style = MaterialTheme.typography.titleMedium, color = hueso)
            LazyRow {
                items(recommendedGames) { game ->
                    GameCard(game = game, onClick = {
                        navController.navigate("game_detail/${game.id}")
                    })
                }
            }
        }

        if (showSearchOverlay) {
            SearchOverlay(
                searchQuery = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    if (selectedTab == "Juegos" && it.length > 1) {
                        gameViewModel.searchGames(it, apiKey)
                    } else if (selectedTab == "Juegos") {
                        gameViewModel.clearSearchResults()
                    }
                },


                selectedTab = selectedTab,
                onTabChange = { selectedTab = it },
                gameResults = gameViewModel.searchResults.value,
                userResults = userViewModel.searchResults.value,
                onClose = { showSearchOverlay = false },
                onGameClick = { game ->
                    showSearchOverlay = false
                    navController.navigate("game_detail/${game.id}")
                },
                onUserFollow = { user ->
                    // lógica para seguir
                },
                onUserReport = { user ->
                    // lógica para reportar
                },
                navController = navController
            )
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

@Composable
fun UserSearchCard(
    user: UserModel,
    navController: NavController,
    onFollow: () -> Unit,
    onReport: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("public_profile/${user.uid}")
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.profilePicUrl,
                contentDescription = user.displayName,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(user.displayName, style = MaterialTheme.typography.titleMedium, color = naranja)
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = grisClaro)
            }

            IconButton(onClick = { navController.navigate("public_profile/${user.uid}") }) {
                Icon(Icons.Default.Person, contentDescription = "Ver perfil", tint = naranja)
            }


            IconButton(onClick = onFollow) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Seguir", tint = Color.Green)
            }

            IconButton(onClick = onReport) {
                Icon(Icons.Default.Report, contentDescription = "Reportar", tint = Color.Red)
            }
        }
    }
}

@Composable
fun SearchOverlay(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedTab: String,
    onTabChange: (String) -> Unit,
    gameResults: List<GameModel.Game>,
    userResults: List<UserModel>,
    onClose: () -> Unit,
    onGameClick: (GameModel.Game) -> Unit,
    onUserFollow: (UserModel) -> Unit,
    onUserReport: (UserModel) -> Unit,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(16.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.height(50.dp))
            TextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar...", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = grisClaro, // Fondo cuando está enfocado
                    unfocusedContainerColor = grisClaro, // Fondo cuando no está enfocado
                    cursorColor = naranja, // Color del cursor
                    focusedTextColor = hueso, // Color del texto cuando está enfocado
                    unfocusedTextColor = hueso, // Color del texto cuando no está enfocado
                    focusedLeadingIconColor = hueso, // Color del ícono de búsqueda cuando está enfocado
                    unfocusedLeadingIconColor = hueso, // Color del ícono de búsqueda cuando no está enfocado
                    unfocusedIndicatorColor = Color.Transparent, // Elimina la línea morada cuando no está enfocado
                    focusedIndicatorColor = Color.Transparent // Elimina la línea morada cuando está enfocado
                ),
                singleLine = true
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Juegos", "Usuarios").forEach { tab ->
                    Text(
                        text = tab,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == tab) naranja else grisClaro)
                            .clickable { onTabChange(tab) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (selectedTab == tab) Color.White else hueso
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                when (selectedTab) {
                    "Juegos" -> items(gameResults) { game ->
                        SearchGameCard(game = game) {
                            onGameClick(game)
                            onClose()
                        }
                    }

                    "Usuarios" -> items(userResults) { user ->
                        UserSearchCard(
                            user = user,
                            onFollow = { onUserFollow(user) },
                            onReport = { onUserReport(user) },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}





