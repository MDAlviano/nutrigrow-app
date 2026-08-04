package com.nutrigrow.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nutrigrow.app.ui.ViewModelFactory
import com.nutrigrow.app.ui.screen.auth.AuthViewModel
import com.nutrigrow.app.ui.screen.auth.LoginScreen
import com.nutrigrow.app.ui.screen.auth.RegisterScreen
import com.nutrigrow.app.ui.screen.main.MainScreen
import com.nutrigrow.app.ui.screen.onboarding.OnboardingScreen
import com.nutrigrow.app.ui.screen.planner.AddProjectScreen
import com.nutrigrow.app.ui.screen.planner.PlannerViewModel
import com.nutrigrow.app.ui.screen.splash.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.OnBoarding.route,
    nextDestinationAfterSplash: String,
    onSaveOnBoardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(nextDestinationAfterSplash) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.OnBoarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    onSaveOnBoardingCompleted()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.OnBoarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(rootNavController = navController)
        }

        composable(Screen.AddProject.route) {
            val plannerViewModel: PlannerViewModel = viewModel(factory = factory)
            AddProjectScreen(
                viewModel = plannerViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}