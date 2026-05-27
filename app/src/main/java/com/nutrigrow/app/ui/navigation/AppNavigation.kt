package com.nutrigrow.app.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nutrigrow.app.ui.screen.auth.AuthState
import com.nutrigrow.app.ui.screen.auth.AuthViewModel
import com.nutrigrow.app.ui.screen.auth.LoginScreen
import com.nutrigrow.app.ui.screen.auth.RegisterScreen

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            val authState by authViewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Success -> {
                        val message = (authState as AuthState.Success).message
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        authViewModel.resetState()
                    }

                    is AuthState.Error -> {
                        val error = (authState as AuthState.Error).message
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        authViewModel.resetState()
                    }

                    else -> {}
                }
            }

            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                }
            )
        }

        composable(Screen.Register.route) {
            val authState by authViewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Success -> {
                        val message = (authState as AuthState.Success).message
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        authViewModel.resetState()
                        navController.popBackStack()
                    }

                    is AuthState.Error -> {
                        val error = (authState as AuthState.Error).message
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        authViewModel.resetState()
                    }

                    else -> {}
                }
            }

            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterClick = { name, email, pass ->
                    authViewModel.register(name, email, pass)
                }
            )
        }
    }
}