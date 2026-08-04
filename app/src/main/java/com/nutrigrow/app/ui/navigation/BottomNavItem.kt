package com.nutrigrow.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home_tab", "Beranda", Icons.Default.Home)
    object AIGrow : BottomNavItem("aigrow_tab", "AIGrpw", Icons.Default.QrCodeScanner)
    object Planner : BottomNavItem("planner_tab", "Planner", Icons.Default.CalendarMonth)
    object Profile : BottomNavItem("profile_tab", "Profil", Icons.Default.Person)
}