package com.nutrigrow.app.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object OnBoarding : Screen("onboarding_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Main : Screen("main_screen")

    // sub routes
    object AddProject : Screen("add_project_screen")
    object ProjectDetail : Screen("project_detail_screen/{projectId}") {
        fun createRoute(projectId: String) = "project_detail_screen/$projectId"
    }
}