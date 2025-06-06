package com.example.gametracker.ui


import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gametracker.data.repository.AuthRepository
import com.example.gametracker.ui.navigation.Routes
import com.example.gametracker.ui.screens.AccountScreenContent
import com.example.gametracker.ui.screens.AdminPanelScreenContent
import com.example.gametracker.ui.screens.ExploreScreenContent
import com.example.gametracker.ui.screens.GameDetailScreenContent
import com.example.gametracker.ui.screens.HomeScreenContent
import com.example.gametracker.ui.screens.ListScreenContent
import com.example.gametracker.ui.screens.LoginScreenContent
import com.example.gametracker.ui.screens.PublicProfileScreenContent
import com.example.gametracker.ui.screens.RegisterScreenContent
import com.example.gametracker.viewmodel.AuthViewModel
import com.example.gametracker.viewmodel.AuthViewModelFactory
import com.example.gametracker.viewmodel.GameListViewModel
import com.example.gametracker.viewmodel.GameViewModel
import com.example.gametracker.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                val currentUser by remember {mutableStateOf(AuthRepository.getCurrentUser()) }
                val authRepository = AuthRepository
                val apiKey = "e4480f64ecde4fefb4d3cc23e566f83f"

            val application: Application = applicationContext as Application

                val userViewModel = UserViewModel()
                val gameViewModel = GameViewModel()
                val gameListViewModel = GameListViewModel()

                val viewModel: AuthViewModel by viewModels {
                    AuthViewModelFactory(application, userViewModel)
                }
                val navController = rememberNavController()
                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        navController.navigate(Routes.LOGIN) {
                        }
                    }
                }

                NavHost(navController = navController, startDestination = Routes.LOGIN) {
                    composable(Routes.LOGIN) { LoginScreenContent(navController, viewModel) }
                    composable(Routes.REGISTER) { RegisterScreenContent(navController, viewModel) }
                    composable(Routes.HOME) { HomeScreenContent( navController, userViewModel, gameViewModel) }
                    composable(Routes.ACCOUNT) {
                        val user by userViewModel.user.collectAsState()
                        val userGameList by gameListViewModel.userGameList.collectAsState()
                        val currentUser = AuthRepository.getCurrentUser()
                        val context = LocalContext.current

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.GetContent()
                        ) { uri ->
                            uri?.let { userViewModel.uploadProfilePicture(it) }
                        }

                        AccountScreenContent(
                            user = user,
                            userRole = user?.role ?: "",
                            userGameList = userGameList,
                            navController = navController,
                            gameListViewModel = gameListViewModel,
                            onUpdateProfile = { name, bio, isPrivate ->
                                userViewModel.updateUserProfile(name, bio, isPrivate)
                            },
                            onDeleteGame = { gameEntry ->
                                gameListViewModel.deleteGameById(gameEntry.id)
                            },
                            launcher = launcher,
                            currentUser = currentUser
                        )
                    }

                    composable(Routes.LIST) { ListScreenContent(navController) }
                    composable(Routes.ADMIN) {
                        AdminPanelScreenContent(userViewModel)
                    }
                    composable(
                        route = "${Routes.GAME_DETAIL}/{gameId}",
                        arguments = listOf(navArgument("gameId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val gameId = backStackEntry.arguments?.getInt("gameId") ?: -1
                        val userId = currentUser?.uid ?: ""

                        GameDetailScreenContent(
                            gameId = gameId,
                            gameViewModel = gameViewModel,
                            gameListViewModel = GameListViewModel(),
                            apiKey = apiKey
                        )
                    }
                    composable(
                        route = "explore?showSearch={showSearch}",
                        arguments = listOf(
                            navArgument("showSearch") {
                                defaultValue = "false"
                            }
                        )
                    ) { backStackEntry ->
                        val showSearch = backStackEntry.arguments?.getString("showSearch") == "true"
                        ExploreScreenContent(
                            gameViewModel = gameViewModel,
                            userViewModel = userViewModel,
                            apiKey = apiKey,
                            navController = navController,
                            launchWithSearch = showSearch
                        )
                    }
                    composable("public_profile/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        Log.d("PublicProfile", "userId recibido: $userId") // <-- AÑADE ESTO TEMPORALMENTE
                        PublicProfileScreenContent(
                            userId = userId,
                            userViewModel = userViewModel,
                            gameListViewModel = gameListViewModel,
                            navController = navController
                        )
                    }


                }
            }
        }
    }




