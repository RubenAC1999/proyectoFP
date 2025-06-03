package com.example.gametracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gametracker.ui.theme.darkGray
import com.example.gametracker.ui.theme.grisClaro
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.example.gametracker.viewmodel.GameListViewModel
import com.example.gametracker.viewmodel.UserViewModel

@Composable
fun PublicProfileScreenContent(
    userId: String,
    userViewModel: UserViewModel,
    gameListViewModel: GameListViewModel,
    navController: NavController
) {
    val user = userViewModel.publicUser.value
    val userGames = userViewModel.userGameEntries.value

    // Cargar datos si aún no se han cargado
    LaunchedEffect(userId) {
        userViewModel.loadUserById(userId)
        userViewModel.loadUserGameEntries(userId)
    }

    if (user == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkGray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Cargando perfil...", color = hueso)
        }
    } else {
        // Mostrar con tu mismo diseño, sin controles de admin
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(darkGray)
                .padding(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(25.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = grisClaro)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = user.profilePicUrl,
                                    contentDescription = "Foto de perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(darkGray)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = user.displayName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = naranja
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        CompactUserStats(userGameList = userGames)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // Mostrar sus juegos igual que en tu perfil
            val groupedGames = userGames.groupBy { it.status.lowercase() }
            val statusOrder = listOf("jugando", "completado", "dropeado", "wishlist")
            val statusTitles = mapOf(
                "jugando" to "Jugando actualmente",
                "completado" to "Completados",
                "dropeado" to "Abandonados",
                "wishlist" to "Wishlist"
            )

            statusOrder.forEach { statusKey ->
                val baseList = groupedGames[statusKey].orEmpty()
                val sortedList = baseList.sortedByDescending { it.addedAt }

                if (sortedList.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = statusTitles[statusKey] ?: statusKey.capitalize(),
                            style = MaterialTheme.typography.titleMedium,
                            color = hueso,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(sortedList) { game ->
                        ExpandableGameCard(game)
                    }
                }
            }
        }
    }
}

