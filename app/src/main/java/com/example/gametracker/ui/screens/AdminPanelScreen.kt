package com.example.gametracker.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gametracker.model.UserModel
import com.example.gametracker.ui.theme.darkGray
import com.example.gametracker.ui.theme.hueso
import com.example.gametracker.ui.theme.naranja
import com.example.gametracker.viewmodel.UserViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreenContent(userViewModel: UserViewModel = viewModel()) {
    val users by userViewModel.allUsers.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userViewModel.loadAllUsers()
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredUsers = users.filter {
        it.role == "casual" && (
                it.displayName.contains(searchQuery, ignoreCase = true) ||
                        it.email.contains(searchQuery, ignoreCase = true)
                )
    }

    var showWarningDialog by remember { mutableStateOf(false) }
    var warningMessage by remember { mutableStateOf("") }
    var userToWarn by remember { mutableStateOf<UserModel?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedUserToBan by remember { mutableStateOf<UserModel?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGray)
            .padding(16.dp)
    ) {
        Text(
            text = "Panel de administración",
            style = MaterialTheme.typography.headlineMedium,
            color = naranja
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(user.displayName, color = hueso, style = MaterialTheme.typography.bodyLarge)
                                Text(user.email, color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
                                if (user.isBanned == true && user.bannedUntil != null) {
                                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        .format(user.bannedUntil.toDate())
                                    Text("Suspendido hasta: $formattedDate", color = Color.Red, style = MaterialTheme.typography.labelSmall)
                                }
                                if (!user.warningMessage.isNullOrBlank()) {
                                    Text("Aviso: ${user.warningMessage}", color = Color.Yellow, style = MaterialTheme.typography.labelSmall)
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

                                IconButton(onClick = {
                                    selectedUserToBan = user
                                    showDatePicker = true
                                }) {
                                    Icon(Icons.Default.Lock, contentDescription = "Suspender", tint = Color.Red)
                                }
                            }
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
                title = { Text("Enviar aviso", color = naranja) },
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
                    TextButton(onClick = {
                        userToWarn?.let {
                            userViewModel.sendWarningToUser(it.uid, warningMessage)
                        }
                        showWarningDialog = false
                    }) {
                        Text("Enviar", color = naranja)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showWarningDialog = false
                    }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF2C2C2C),
                titleContentColor = naranja,
                textContentColor = hueso
            )
        }

        if (showDatePicker && selectedUserToBan != null) {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    calendar.set(year, month, day, 23, 59, 59)
                    val date = calendar.time

                    selectedUserToBan?.let {
                        userViewModel.reportUser(
                            userId = it.uid,
                            untilDate = Timestamp(date),
                            message = "Tu cuenta ha sido suspendida hasta $date."
                        )
                    }

                    showDatePicker = false
                    selectedUserToBan = null
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}
