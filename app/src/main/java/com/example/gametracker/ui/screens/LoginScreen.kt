package com.example.gametracker.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gametracker.R
import com.example.gametracker.ui.navigation.Routes
import com.example.gametracker.viewmodel.AuthViewModel


@Composable
fun LoginScreenContent(navController: NavController, viewModel: AuthViewModel) {

    val naranja = Color(0xFFF3701E)
    val azul = Color(0xFF262b4f)
    val hueso = Color(0xFFE8D8C9)
    val colorFondo = Color(0xFF1A1B1B)
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

    // Para Google
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            BorderedHeader(
                "GAME TRACKER",
                azul,
                hueso,
                azul)
            CustomTextField(
                email,
                onValueChange = { email = it },
                "Correo electrónico",
                false)

            CustomTextField(
                password,
                onValueChange = { password = it },
                "Contraseña",
                true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Debes rellenar todos los campos",
                            Toast.LENGTH_LONG).show()
                    } else {
                        viewModel.login(email, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = naranja
                ),
                modifier = Modifier
                    .width(300.dp)
                    .height(45.dp)
            ) {
                Text(
                    text = "Entrar",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))

            Button(
                onClick = {
                    viewModel.launchGoogleSignIn(launcher)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .width(300.dp)
                    .height(45.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = "Google icon",
                    )

                   Spacer(modifier = Modifier.width(30.dp))

                    Text(
                        text = "Continuar con Google",
                        color = colorFondo,
                        modifier = Modifier.width(250.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = hueso.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Regístrate aquí",
                    color = naranja,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Routes.REGISTER)
                        },
                    style = TextStyle(
                        textDecoration = TextDecoration.Underline,
                    )
                )
            }

        }
    }
}


@Composable
fun BorderedHeader(
    text: String,
    borderColor: Color,
    textColor: Color,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .padding(25.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
            )
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    val naranja = Color(0xFFF3701E)
    val hueso = Color(0xFFE8D8C9)
    val morado = Color(0xFF1e223d)
    val grisClaro = Color(0xFF2c2d35)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFFE8D8C9)) },
        modifier = Modifier
            .width(300.dp)
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = grisClaro,
            unfocusedContainerColor = grisClaro,
            focusedIndicatorColor = grisClaro,
            unfocusedIndicatorColor = grisClaro.copy(alpha = 0.5f),
            cursorColor = naranja
        ),
        textStyle = TextStyle(
            color = hueso,
            fontSize = 16.sp
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}

@Preview
@Composable
fun LoginScreenPreview() {



    val naranja = Color(0xFFF3701E)
    val azul = Color(0xFF262b4f)
    val hueso = Color(0xFFE8D8C9)
    val colorFondo = Color(0xFF1A1B1B)
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val navController: NavController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            BorderedHeader(
                "GAME TRACKER",
                azul,
                hueso,
                azul)
            CustomTextField(
                email,
                onValueChange = { email = it },
                "Correo electrónico",
                false)

            CustomTextField(
                password,
                onValueChange = { password = it },
                "Contraseña",
                true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Debes rellenar todos los campos",
                            Toast.LENGTH_LONG).show()
                    } else {
                    }
                    navController.navigate(Routes.HOME)
                    Toast.makeText(context, "Sesión iniciada", Toast.LENGTH_LONG).show()

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = naranja
                ),
                modifier = Modifier
                    .width(300.dp)
                    .height(45.dp)
            ) {
                Text(
                    text = "Entrar",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))

            Button(
                onClick = {
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .width(300.dp)
                    .height(45.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = "Google icon",
                    )

                    Spacer(modifier = Modifier.width(30.dp))

                    Text(
                        text = "Continuar con Google",
                        color = colorFondo,
                        modifier = Modifier.width(250.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = hueso.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Regístrate aquí",
                    color = naranja,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Routes.REGISTER)
                        },
                    style = TextStyle(
                        textDecoration = TextDecoration.Underline,
                    )
                )
            }

        }
    }
}