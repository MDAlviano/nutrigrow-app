package com.nutrigrow.app.ui.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nutrigrow.app.ui.ViewModelFactory
import com.nutrigrow.app.ui.navigation.BottomNavItem
import com.nutrigrow.app.ui.navigation.Screen
import com.nutrigrow.app.ui.screen.aigrow.AIGrowScreen
import com.nutrigrow.app.ui.screen.aigrow.AIGrowViewModel
import com.nutrigrow.app.ui.screen.home.HomeScreen
import com.nutrigrow.app.ui.screen.planner.AddProjectScreen
import com.nutrigrow.app.ui.screen.planner.PlannerScreen
import com.nutrigrow.app.ui.screen.planner.PlannerViewModel
import com.nutrigrow.app.ui.screen.profile.ProfileScreen
import com.nutrigrow.app.ui.screen.profile.ProfileViewModel

@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)

    val bottomNavController = rememberNavController()
    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.AIGrow,
        BottomNavItem.Planner,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = {
                            Text(
                                text = item.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF059669),
                            selectedTextColor = Color(0xFF059669),
                            indicatorColor = Color(0xFFD1FAE5),
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                val plannerViewModel: PlannerViewModel = viewModel(factory = factory)
                val profileViewModel: ProfileViewModel = viewModel(factory = factory)
                HomeScreen(
                    plannerViewModel = plannerViewModel,
                    profileViewModel = profileViewModel,
                    onNavigateToAIGrow = { bottomNavController.navigate(BottomNavItem.AIGrow.route) },
                    onNavigateToPlanner = { bottomNavController.navigate(BottomNavItem.Planner.route) }
                )
            }
            composable(BottomNavItem.AIGrow.route) {
                val aiGrowViewModel: AIGrowViewModel = viewModel(factory = factory)
                AIGrowScreen(viewModel = aiGrowViewModel)
            }
            composable(BottomNavItem.Planner.route) {
                val plannerViewModel: PlannerViewModel = viewModel(factory = factory)
                PlannerScreen(
                    viewModel = plannerViewModel,
                    onNavigateToAddProject = {
                        rootNavController.navigate(Screen.AddProject.route)
                    },
                    onNavigateToProjectDetail = { project ->
                        rootNavController.currentBackStackEntry?.savedStateHandle?.set("selected_project", project)
                        rootNavController.navigate(Screen.ProjectDetail.route)
                    }
                )
            }
            composable(Screen.AddProject.route) {
                val plannerViewModel: PlannerViewModel = viewModel(factory = factory)
                AddProjectScreen(
                    viewModel = plannerViewModel,
                    onNavigateBack = { rootNavController.popBackStack() }
                )
            }
            composable(BottomNavItem.Profile.route) {
                val profileViewModel: ProfileViewModel = viewModel(factory = factory)
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogoutFinished = {
                        rootNavController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}