package com.nutrigrow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.local.UserPreferences
import com.nutrigrow.app.ui.navigation.AppNavHost
import com.nutrigrow.app.ui.navigation.Screen
import com.nutrigrow.app.ui.theme.NutrigrowTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val userPreferences = UserPreferences(this)
        val tokenManager = TokenManager(this)

        setContent {
            NutrigrowTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val navController = rememberNavController()
                    val coroutineScope = rememberCoroutineScope()

                    var nextDestinationAfterSplash by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        val isOnBoardingCompleted = userPreferences.isOnBoardingCompleted.first()
                        val jwtToken = tokenManager.getToken.first()

                        nextDestinationAfterSplash = when {
                            !isOnBoardingCompleted -> Screen.OnBoarding.route
                            jwtToken.isNullOrEmpty() -> Screen.Login.route
                            else -> Screen.Main.route
                        }
                    }

                    if (nextDestinationAfterSplash == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF059669))
                        }
                    } else {
                        AppNavHost(
                            navController = navController,
                            startDestination = Screen.Splash.route,
                            nextDestinationAfterSplash = nextDestinationAfterSplash!!,
                            onSaveOnBoardingCompleted = {
                                coroutineScope.launch {
                                    userPreferences.setOnBoardingCompleted(true)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}