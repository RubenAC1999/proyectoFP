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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gametracker.model.UserModel
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.example.gametracker.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreenContent(userViewModel: UserViewModel = viewModel()) {
    val users by userViewModel.allUsers.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadAllUsers()
    }

    var searchQuery by remember { mutableStateOf("") }


    val filteredUsers = users.filter {
        (it.role == "casual") && (
        it.displayName.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
                )
    }


    var showWarningDialog by remember { mutableStateOf(false) }
    var warningMessage by remember { mutableStateOf("") }
    var userToWarn by remember { mutableStateOf<UserModel?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .padding(16.dp)
    ) {
        Text(
            text = "Panel de administración",
            style = MaterialTheme.typography.headlineMedium,
            color = naranja
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar usuario", color = hueso) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = hueso),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = naranja,
                unfocusedBorderColor = Color.Gray,
                cursorColor = naranja,
                focusedLabelColor = naranja,
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(filteredUsers) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(user.displayName, color = hueso)
                            Text(
                                user.email,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (user.isBanned == true) {
                            val date = user.bannedUntil?.toDate()
                            val formatter =
                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                            Text(
                                text = "Usuario reportado hasta ${formatter.format(date)}",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (!user.warningMessage.isNullOrEmpty()) {
                            Text(
                                text = "Aviso: ${user.warningMessage}",
                                color = Color.Yellow,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Row {
                        IconButton(onClick = {
                            userToWarn = user
                            warningMessage = ""
                            showWarningDialog = true
                        }) {
                            Icon(Icons.Default.Warning, contentDescription = "Avisar", tint = Color.Yellow)
                        }

                        IconButton(onClick = {     // TODO: Implementar lógica para reportar/bloquear usuario
                        }) {
                            Icon(Icons.Default.Lock, contentDescription = "Reportar", tint = Color.Red )
                        }
                    }
                }
            }
        }

        if (showWarningDialog && userToWarn != null) {
            AlertDialog(
                onDismissRequest = {
                    showWarningDialog = false
                    userToWarn = null
                    warningMessage = ""
                },
                title = {
                    Text("Enviar aviso", color = naranja)
                },
                text = {
                    OutlinedTextField(
                        value = warningMessage,
                        onValueChange = { warningMessage = it },
                        label = { Text("Mensaje de aviso", color = hueso) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = hueso),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = naranja,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = naranja,
                            focusedLabelColor = naranja,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        userToWarn?.let {
                            userViewModel.sendWarningToUser(it.uid, warningMessage)
                        }
                        showWarningDialog = false
                        warningMessage = ""
                        userToWarn = null
                    }) {
                        Text("Enviar", color = naranja)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        showWarningDialog = false
                        warningMessage = ""
                        userToWarn = null
                    }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },

                containerColor = Color(0xFF2C2C2C)
            )
        }
    }
}
