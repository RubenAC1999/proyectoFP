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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gametracker.ui.navigation.Routes
import com.example.gametracker.ui.theme.darkGray
import com.example.gametracker.ui.theme.grisClaro
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.example.gametracker.viewmodel.GameListViewModel
import com.example.gametracker.viewmodel.UserViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenContent(
    userViewModel: UserViewModel,
    gameListViewModel: GameListViewModel,
    userRole: String?,
    navController: NavController) {

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    val user by userViewModel.user.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val userGameList by gameListViewModel.userGameList.collectAsState()

    val completedCount = userGameList.count { it.status == "completado" }
    val pendingCount = userGameList.count { it.status == "pendiente" }
    val droppedCount = userGameList.count { it.status == "dropeado" }

    LaunchedEffect(user?.uid) {
        user?.uid.let { uid ->
            if (uid != null) {
                gameListViewModel.loadGamesForUser(uid)
                gameListViewModel.loadUserStats(uid)
            }
        }
    }

    val groupedGames = userGameList.groupBy { it.status.lowercase() }

    val statusOrder = listOf("jugando", "completado", "dropeado", "wishlist")
    val statusTitles = mapOf(
        "jugando" to "Jugando actualmente",
        "completado" to "Completados",
        "dropeado" to "Abandonados",
        "wishlist" to "Wishlist"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGray)
            .padding(16.dp)
    ) {
        item {
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
                                model = user?.profilePicUrl ?: "",
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(darkGray)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = user?.displayName ?: "Usuario",
                                style = MaterialTheme.typography.headlineSmall,
                                color = naranja
                            )
                        }

                        if (userRole == "admin") {
                            Button(
                                onClick = { showDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFD32F2F
                                    )
                                )
                            ) {
                                Text("Admin", color = hueso)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                }
            }
        }

        statusOrder.forEach { statusKey ->
            val gamesInGroup = groupedGames[statusKey].orEmpty()

            if (gamesInGroup.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = statusTitles[statusKey] ?: statusKey.capitalize(),
                        style = MaterialTheme.typography.titleMedium,
                        color = hueso,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(gamesInGroup) { game ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = grisClaro),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = game.imageUrl,
                                    contentDescription = game.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = game.name,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "${game.hoursPlayed}h jugadas",
                                        color = Color.LightGray,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Text(
                                text = game.rating?.toString() ?: "-",
                                color = naranja,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }
        }
        }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                password = ""
                errorText = ""
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentUser != null && !password.isBlank()) {
                            val email = currentUser.email

                            if (email != null) {
                                val credential =
                                    EmailAuthProvider.getCredential(email, password)
                                currentUser.reauthenticate(credential)
                                    .addOnSuccessListener {
                                        showDialog = false
                                        password = ""
                                        errorText = ""
                                        navController.navigate(Routes.ADMIN)
                                    }
                                    .addOnFailureListener {
                                        errorText = "Contraseña incorrecta"
                                    }
                            }
                        } else {
                            errorText = "La contraseña no puede estar vacía"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = naranja)
                ) {
                    Text("Confirmar", color = hueso)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    password = ""
                    errorText = ""
                }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            title = { Text("Confirmación de seguridad", color = hueso) },
            text = {
                Column {
                    Text("Introduce tu contraseña para continuar", color = hueso)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", color = hueso) },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = hueso),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = naranja,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = naranja,
                            focusedLabelColor = naranja,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    if (errorText.isNotEmpty()) {
                        Text(text = errorText, color = Color.Red)
                    }
                }
            },
            containerColor = darkGray,
            tonalElevation = 4.dp
        )
    }
    }



