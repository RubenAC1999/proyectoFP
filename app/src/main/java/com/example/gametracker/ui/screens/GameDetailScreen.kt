package com.example.gametracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.gametracker.model.GameEntry
import com.example.gametracker.ui.theme.darkGray
import com.example.gametracker.ui.theme.grisClaro
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.example.gametracker.viewmodel.GameListViewModel
import com.example.gametracker.viewmodel.GameViewModel


@Composable
fun GameDetailScreenContent(
    gameId: Int,
    gameViewModel: GameViewModel,
    gameListViewModel: GameListViewModel,
    apiKey: String,
    userId: String
) {
    val gameDetail = gameViewModel.gameDetail.value
    val context = LocalContext.current
    val screenshots = gameViewModel.screenshots.value
    val imageHeight = 300.dp
    val density = LocalDensity.current

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(gameId) {
        gameViewModel.loadGameDetail(apiKey, gameId)
        gameViewModel.loadGameScreenshots(apiKey, gameId)
    }

    if (gameDetail == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(darkGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Cargando...", color = hueso)
        }
        return
    }

    val gameEntry = GameEntry(
        id = gameDetail.id.toString(),
        gameId = gameDetail.id,
        name = gameDetail.name,
        imageUrl = gameDetail.imageUrl,
        status = "wishlist",
        rating = null,
        hoursPlayed = 0
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGray)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = gameDetail.imageUrl),
                contentDescription = gameDetail.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = 0.99f }
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, darkGray),
                            startY = with(density) { imageHeight.toPx() },
                            endY = with(density) { imageHeight.toPx() + 300f }
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = gameDetail.name,
            style = MaterialTheme.typography.headlineMedium,
            color = naranja
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "(${gameDetail.released ?: "Fecha no disponible"})",
            style = MaterialTheme.typography.bodyMedium,
            color = hueso
        )

        Spacer(modifier = Modifier.height(8.dp))

        val genresText = gameDetail.genres?.joinToString(", ") { it.name } ?: "Géneros no disponibles"
        Text(text = genresText, style = MaterialTheme.typography.bodyMedium, color = hueso)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Usuarios: ${gameDetail.rating ?: "N/A"}", color = hueso)
                Text("Metacritic: ${gameDetail.metacritic ?: "N/A"}", color = hueso)
            }

            Button(onClick = { showDialog = true }) {
                Text("Añadir a lista", color = hueso)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = gameDetail.description ?: "Sin descripción",
            style = MaterialTheme.typography.bodyMedium,
            color = hueso,
            modifier = Modifier
                .fillMaxWidth()
                .background(grisClaro)
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow {
            items(screenshots) { screenshot ->
                Image(
                    painter = rememberAsyncImagePainter(model = screenshot.image),
                    contentDescription = "Captura del juego",
                    modifier = Modifier
                        .height(150.dp)
                        .width(250.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    if (showDialog) {
        AddGameDialog(
            gameEntry = gameEntry,
            onDismiss = { showDialog = false },
            onAdd = { updatedGame ->
                gameListViewModel.addGame(
                    userId = userId,
                    game = updatedGame,
                    onSuccess = {
                        Toast.makeText(context, "Juego añadido", Toast.LENGTH_SHORT).show()
                    },
                    onError = { e ->
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}


    @Composable
    fun DropdownMenuField(
        options: List<String>,
        selectedOption: String,
        onOptionSelected: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                label = { Text("Estado") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    androidx.compose.material3.IconButton(onClick = { expanded = true }) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                            contentDescription = "Expand"
                        )
                    }
                }
            )

            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

@Composable
fun AddGameDialog(
    gameEntry: GameEntry,
    onDismiss: () -> Unit,
    onAdd: (GameEntry) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("Wishlist") }
    var ratingInput by remember { mutableStateOf("") }
    var hoursInput by remember { mutableStateOf("") }

    val statusOptions = listOf("Wishlist", "Jugando", "Completado", "Dropeado")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val rating = ratingInput.toIntOrNull()
                val hours = hoursInput.toIntOrNull() ?: 0

                val updatedGame = gameEntry.copy(
                    status = selectedStatus.lowercase(),
                    rating = rating,
                    hoursPlayed = hours
                )
                onAdd(updatedGame)
                onDismiss()
            }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Añadir juego a la lista") },
        text = {
            Column {
                Text("Estado:")
                DropdownMenuField(
                    options = statusOptions,
                    selectedOption = selectedStatus,
                    onOptionSelected = { selectedStatus = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = ratingInput,
                    onValueChange = { ratingInput = it },
                    label = { Text("Puntuación (sobre 100)") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = hoursInput,
                    onValueChange = { hoursInput = it },
                    label = { Text("Horas jugadas") },
                    singleLine = true
                )
            }
        }
    )
}
