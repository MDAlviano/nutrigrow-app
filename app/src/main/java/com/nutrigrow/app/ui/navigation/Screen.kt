package com.nutrigrow.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object AddProject : Screen("add_project")
    object AIGrow : Screen("aigrow")
}