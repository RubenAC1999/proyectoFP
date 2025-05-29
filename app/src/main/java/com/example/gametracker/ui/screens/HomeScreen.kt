package com.example.gametracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.gametracker.data.repository.AuthRepository
import com.example.gametracker.ui.navigation.Routes
import com.example.gametracker.ui.theme.darkGray
import com.example.gametracker.ui.theme.grisClaro
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.example.gametracker.viewmodel.GameViewModel
import com.example.gametracker.viewmodel.UserViewModel

@Composable
fun HomeScreenContent(navController: NavController, userViewModel: UserViewModel, gameViewModel: GameViewModel) {
    val currentUserId = AuthRepository.getCurrentUser()?.uid
    val user by userViewModel.user.collectAsState()
    val topRatedGames = gameViewModel.topRatedGames.value
    val popularGames = gameViewModel.popularGames.value

    val apiKey = "e4480f64ecde4fefb4d3cc23e566f83f"

    LaunchedEffect(currentUserId) {
        currentUserId?.let {
            userViewModel.loadUserData(it)
        }
    }

    LaunchedEffect(apiKey) {
        gameViewModel.loadTopRateGames(apiKey)
        gameViewModel.loadMostPopularGames(apiKey)
    }

    var showWarningDialog by remember { mutableStateOf(false) }
    var hasShowWarning by remember { mutableStateOf(false) }

    LaunchedEffect(user?.warningMessage) {
        if (!hasShowWarning && !user?.warningMessage.isNullOrBlank()) {
            showWarningDialog = true
            hasShowWarning = true
        }
    }

    LaunchedEffect(user) {
        val currentUser = user
        if (currentUser != null && !currentUser.hasReadWarning && !currentUser.warningMessage.isNullOrEmpty()) {
            showWarningDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGray)
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController)
            },
            modifier = Modifier
                .fillMaxSize()
                .background(darkGray)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(darkGray)
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Bienvenido${user?.displayName?.let { ", $it" } ?: ""}",
                    color = naranja,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                var searchQuery by remember { mutableStateOf("") }
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar juegos...", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = hueso
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = grisClaro,
                        unfocusedContainerColor = grisClaro,
                        disabledContainerColor = grisClaro,
                        focusedTextColor = hueso,
                        unfocusedTextColor = hueso,
                        disabledTextColor = hueso,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = naranja,
                        focusedLeadingIconColor = hueso,
                        unfocusedLeadingIconColor = hueso
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Mejor valorados", style = MaterialTheme.typography.titleMedium, color = hueso)
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow {
                    items(topRatedGames) { game ->
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .width(120.dp) // Tamaño de la caja
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    navController.navigate("game_detail/${game.id}")
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = game.imageUrl,
                                    contentDescription = game.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = game.name,
                                    color = naranja,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )

                              Text(
                                   text = game.genres.firstOrNull()?.name ?: "Sin género",
                                    color = hueso,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Más populares", style = MaterialTheme.typography.titleMedium, color = hueso)
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow {
                    items(popularGames) { game ->
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .width(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = game.imageUrl,
                                    contentDescription = game.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = game.name,
                                    color = naranja,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = game.genres.firstOrNull()?.name ?: "Sin género",
                                    color = hueso,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    color = hueso
                )
            }
        }

        if (showWarningDialog && user != null) {
            AlertDialog(
                onDismissRequest = { showWarningDialog = false },
                title = {
                    Text(
                        text = "Aviso importante",
                        color = naranja,
                        style = MaterialTheme.typography.headlineSmall
                    )
                        },
                text = {
                    Text(
                        text = user?.warningMessage ?: "",
                        color = hueso,
                        style = MaterialTheme.typography.bodyMedium)
                       },
                confirmButton = {
                    TextButton(onClick = {
                        showWarningDialog = false
                        user?.let {
                            userViewModel.markWarningAsRead(it.uid)
                        }
                    }) {
                        Text(
                            "Entendido",
                            color = naranja,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                containerColor = Color(0xFF2C2C2C),
                titleContentColor = naranja,
                textContentColor = hueso
            )
        }
    }
}

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination?.route

        NavigationBar(
            containerColor = grisClaro,
            tonalElevation = 8.dp
        ) {
            NavigationBarItem(
                selected = currentDestination == Routes.HOME,
                onClick = { navController.navigate(Routes.HOME) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = hueso,
                    unselectedIconColor = naranja,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = { /* Buscar */ },
                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = hueso,
                    unselectedIconColor = naranja,
                    indicatorColor = Color.Transparent
                )
            )
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(naranja, shape = CircleShape)
                    .padding(12.dp)
                    .clickable { /* Añadir */ },
               contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = hueso
                )
            }
            NavigationBarItem(
                selected = false,
                onClick = { /* Accion para chat */ },
                icon = { Icon(Icons.Default.Notifications, contentDescription = "Chat") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = hueso,
                    unselectedIconColor = naranja,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = currentDestination == Routes.ACCOUNT,
                onClick = { navController.navigate(Routes.ACCOUNT) },
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = hueso,
                    unselectedIconColor = naranja,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }



@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Datos estáticos para el preview
    val userDisplayName = "John Doe"  // Nombre de usuario estático
    val searchQuery = remember { mutableStateOf("") }
    val juegos = List(10) { "Juego $it" }  // Lista de juegos simulada

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGray)
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = rememberNavController())
            },
            modifier = Modifier
                .fillMaxSize()

        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(darkGray)
                    .padding(padding)
                    .padding(16.dp)

            ) {
                // Reemplazamos el texto dinámico por el valor estático de usuario
                Text(
                    text = "Bienvenido, $userDisplayName",
                    style = MaterialTheme.typography.headlineMedium,
                    color = naranja,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // TextField para búsqueda, usando el estado estático
                TextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    placeholder = { Text("Buscar juegos...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Últimos lanzamientos",
                    style = MaterialTheme.typography.titleMedium,
                    color = hueso
                )

                LazyRow {
                    items(juegos) { juego ->
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .padding(8.dp)
                                .background(Color.Gray, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = juego,
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    color = hueso
                )
            }
        }
    }
}

data class User(val displayName: String)
