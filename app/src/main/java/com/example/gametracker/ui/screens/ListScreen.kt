package com.example.gametracker.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gametracker.ui.navigation.Routes

class ListScreen: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                val navController = rememberNavController()
                ListScreenContent(navController)

        }
    }
}

@Composable
fun ListScreenContent(navController: NavController) {
    val context = LocalContext.current
    val naranja = Color(0xFFF3701E)
    val azul = Color(0xFF262b4f)
    val hueso = Color(0xFFE8D8C9)
    val colorFondo = Color(0xFF1A1B1B)

    Box(
        modifier = Modifier.fillMaxSize()
            .background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Mi lista",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = naranja
            )

            Spacer(Modifier.padding(5.dp))

            Button(
                onClick = {
                    navController.navigate(Routes.ACCOUNT)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = naranja
                )
            ) {
                Text(
                    text = "Atrás",
                    color = hueso
                )
            }
        }
    }
}