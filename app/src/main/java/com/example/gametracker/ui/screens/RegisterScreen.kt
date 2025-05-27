package com.example.gametracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gametracker.ui.navigation.Routes
import com.example.gametracker.ui.theme.darkGray
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.example.gametracker.viewmodel.AuthViewModel


@Composable
fun RegisterScreenContent(navController: NavController, viewModel: AuthViewModel) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estados del viewmodel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Para mostrar errores
    LaunchedEffect(errorMessage) {
        errorMessage?.let{
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.errorMessage.value = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is AuthViewModel.NavigationEvent.NavigateToHome -> {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
                is AuthViewModel.NavigationEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGray),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Text(
                text = "REGISTRO",
                style = TextStyle(
                    color = naranja,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.height(3.dp))

            CustomTextField(
                value = username,
                onValueChange = { username = it },
                label = "Usuario"
            )

            Spacer(modifier = Modifier.padding(3.dp))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )

            Spacer(modifier = Modifier.padding(3.dp))

            CustomTextField(
                value = password,
                onValueChange = { password = it},
                label = "Contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.padding(3.dp))

            CustomTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirma la contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Button(
                onClick = {
                    if (username.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Debes rellenar todos los campos.",
                            Toast.LENGTH_LONG).show()
                    } else if (confirmPassword != password) {
                        Toast.makeText(context, "Las contraseñas no coinciden.",
                            Toast.LENGTH_LONG).show()
                    } else {
                        viewModel.register(email, password, username)
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = naranja
                ),
                modifier = Modifier.width(300.dp).height(45.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Registrarse", color = Color.White)
                }
            }

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = hueso.copy(alpha = 0.8f)
                )

                Text(
                    text = "Inicia sesión aquí",
                    color = naranja.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable {
                            navController.popBackStack()
                        },
                    style = TextStyle(
                        textDecoration = TextDecoration.Underline
                    )
                )
            }


        }
    }
}

@Preview
@Composable
fun RegisterScreenContentPreview() {
    val context = LocalContext.current
    val naranja = Color(0xFFF3701E)
    val darkGray = Color(0XFF1A1B1B)
    val hueso = Color(0xFFE8D8C9)

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGray),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "REGISTRO",
                style = TextStyle(
                    color = naranja,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.height(3.dp))

            CustomTextField(
                value = username,
                onValueChange = { username = it },
                label = "Usuario"
            )

            Spacer(modifier = Modifier.padding(3.dp))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )

            Spacer(modifier = Modifier.padding(3.dp))

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.padding(3.dp))

            CustomTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirma la contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Button(
                onClick = {
                    if (username.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(
                            context, "Debes rellenar todos los campos.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (confirmPassword != password) {
                        Toast.makeText(
                            context, "Las contraseñas no coinciden.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = naranja
                ),
                modifier = Modifier.width(300.dp).height(45.dp)
            ) {


                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        color = hueso.copy(alpha = 0.8f)
                    )

                    Text(
                        text = "Inicia sesión aquí",
                        color = naranja.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable {
                            },
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }


            }
        }
    }
}


