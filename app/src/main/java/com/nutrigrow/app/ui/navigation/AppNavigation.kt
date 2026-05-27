package com.nutrigrow.app.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.ui.screen.auth.AuthState
import com.nutrigrow.app.ui.screen.auth.AuthViewModel
import com.nutrigrow.app.ui.screen.auth.LoginScreen
import com.nutrigrow.app.ui.screen.auth.RegisterScreen
import com.nutrigrow.app.ui.screen.home.HomeScreen
import com.nutrigrow.app.ui.screen.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    tokenManager: TokenManager
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val tokenState = tokenManager.getToken.collectAsState(initial = "LOADING")

    if (tokenState.value == "LOADING") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startRoute = if (tokenState.value.isNullOrEmpty()) {
        Screen.Login.route
    } else {
        Screen.Home.route
    }

    NavHost(navController = navController, startDestination = startRoute) {

        composable(Screen.Login.route) {
            val authState by authViewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    val message = (authState as AuthState.Success).message
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    authViewModel.resetState()

                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                } else if (authState is AuthState.Error) {
                    val error = (authState as AuthState.Error).message
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    authViewModel.resetState()
                }
            }

            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginClick = { email, password -> authViewModel.login(email, password) }
            )
        }

        composable(Screen.Register.route) {
            val authState by authViewModel.authState.collectAsState()
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    Toast.makeText(
                        context,
                        (authState as AuthState.Success).message,
                        Toast.LENGTH_SHORT
                    ).show()
                    authViewModel.resetState()
                    navController.popBackStack()
                } else if (authState is AuthState.Error) {
                    Toast.makeText(
                        context,
                        (authState as AuthState.Error).message,
                        Toast.LENGTH_LONG
                    ).show()
                    authViewModel.resetState()
                }
            }

            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterClick = { name, email, pass -> authViewModel.register(name, email, pass) }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onLogoutClick = {
                    coroutineScope.launch {
                        tokenManager.clearToken()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                },
                viewModel = homeViewModel,
                onNavigateToAIGrow = { /* Nanti kita isi rute ke Scanner */ },
                onNavigateToPlanner = { /* Nanti kita isi rute ke Planner */ }
            )
        }
    }
}