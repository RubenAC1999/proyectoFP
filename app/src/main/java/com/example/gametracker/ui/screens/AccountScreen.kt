package com.example.gametracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gametracker.ui.navigation.Routes
import com.example.gametracker.ui.theme.darkGray
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenContent(navController: NavController, userRole: String?) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    val currentUser = FirebaseAuth.getInstance().currentUser
    Box(
        modifier = Modifier.fillMaxSize()
            .background(darkGray),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Mi cuenta",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = naranja
            )

            Spacer(Modifier.padding(5.dp))

            Button(
                onClick = {
                    navController.navigate(Routes.LIST)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = naranja
                )
            ) {
                Text(
                    text = "Mi lista",
                    color = hueso
                )
            }

        Spacer(modifier = Modifier.padding(5.dp))

        Button(
            onClick = {
                navController.navigate(Routes.HOME)
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
            Spacer(modifier = Modifier.padding(10.dp))

            if (userRole == "admin") {
                Button(
                    onClick = {
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F))
                ) {
                    Text(
                        text = "Panel de administración",
                        color = hueso
                    )
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
                                val credential = EmailAuthProvider.getCredential(email, password)
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
            title = {
                Text("Confirmación de seguridad", color = hueso)
            },
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


