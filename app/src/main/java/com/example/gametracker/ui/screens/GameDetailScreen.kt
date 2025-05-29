package com.example.gametracker.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.gametracker.ui.theme.darkGray
import com.example.gametracker.ui.theme.grisClaro
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.example.gametracker.viewmodel.GameViewModel


@Composable
fun GameDetailScreenContent(
    gameId: Int,
    gameViewModel: GameViewModel,
    apiKey: String) {

    val gameDetail = gameViewModel.gameDetail.value
    val screenshots = gameViewModel.screenshots.value

    val imageHeight = 300.dp
    val density = LocalDensity.current

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
                           endY = with(density) { imageHeight.toPx() + 300f}
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
        Text(
            text = genresText,
            style = MaterialTheme.typography.bodyMedium,
            color = hueso
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Usuarios: ${gameDetail.rating ?: "N/A"}", color = hueso)
                Text("Metacritic: ${gameDetail.metacritic ?: "N/A"}", color = hueso)
            }

            Row {
                Text(
                    text = "Wishlist",
                    color = naranja,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Text(text = "Lista", color = hueso)
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
}