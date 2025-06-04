    package com.example.gametracker.ui.screens

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
    import androidx.compose.foundation.lazy.LazyRow
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.AccountCircle
    import androidx.compose.material.icons.filled.Add
    import androidx.compose.material.icons.filled.Home
    import androidx.compose.material.icons.filled.Notifications
    import androidx.compose.material.icons.filled.Search
    import androidx.compose.material3.AlertDialog
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.Icon
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.NavigationBar
    import androidx.compose.material3.NavigationBarItem
    import androidx.compose.material3.NavigationBarItemDefaults
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.material3.TextButton
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
    import com.google.firebase.auth.ktx.auth
    import com.google.firebase.ktx.Firebase

    @Composable
    fun HomeScreenContent(navController: NavController, userViewModel: UserViewModel, gameViewModel: GameViewModel) {
        val currentUserId = AuthRepository.getCurrentUser()?.uid
        val user by userViewModel.user.collectAsState()
        val topRatedGames = gameViewModel.topRatedGames.value
        val popularGames = gameViewModel.popularGames.value
        val scrollState = rememberScrollState()


        val apiKey = "e4480f64ecde4fefb4d3cc23e566f83f"


        LaunchedEffect(currentUserId) {
            currentUserId?.let {
                userViewModel.loadUserData(it)
            }
        }

        LaunchedEffect(apiKey) {
            gameViewModel.loadPopularGames(apiKey)
            gameViewModel.loadTopRatedGames(apiKey)
        }

        var showWarningDialog by remember { mutableStateOf(false) }
        var hasShowWarning by remember { mutableStateOf(false) }
        val currentUid = Firebase.auth.currentUser?.uid


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
                        .verticalScroll(scrollState)
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Bienvenido${user?.displayName?.let { ", $it" } ?: ""}",
                        color = naranja,
                        style = MaterialTheme.typography.headlineMedium
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

                    Spacer(modifier = Modifier.height(16.dp))


                    if (currentUid == "mllTmLug6bNO8BfEy86gg5YnSbf1") {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Actividad reciente",
                            style = MaterialTheme.typography.titleMedium,
                            color = hueso,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = grisClaro),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp) )
                            {
                                Text("🎮 Invitado2 ha empezado a jugar", color = naranja)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = "https://images.igdb.com/igdb/image/upload/t_cover_big/co1wz4.webp",
                                        contentDescription = "Imagen del juego",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("The Witcher 3", style = MaterialTheme.typography.bodyLarge, color = hueso)
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = grisClaro),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🏆 Prueba ha completado", color = naranja)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = "https://images.igdb.com/igdb/image/upload/t_cover_big/co1tmu.jpg",
                                        contentDescription = "Imagen del juego",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("God of War", style = MaterialTheme.typography.bodyLarge, color = hueso)
                                        Text("Horas jugadas: 25h", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                    }
                                }
                            }
                        }
                    }
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
                    onClick = { navController.navigate(Routes.EXPLORE) },
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
                        .clickable {
                            navController.navigate("${Routes.EXPLORE}?showSearch=true")
                        },
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
                    Text(
                        text = "Bienvenido, $userDisplayName",
                        style = MaterialTheme.typography.headlineMedium,
                        color = naranja,
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
