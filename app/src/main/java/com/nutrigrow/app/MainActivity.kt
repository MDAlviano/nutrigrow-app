package com.nutrigrow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.NutrigrowApi
import com.nutrigrow.app.ui.navigation.AppNavigation
import com.nutrigrow.app.ui.screen.auth.AuthViewModel
import com.nutrigrow.app.ui.screen.home.HomeViewModel
import com.nutrigrow.app.ui.screen.planner.PlannerViewModel
import com.nutrigrow.app.ui.theme.NutrigrowTheme
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private val tokenManager by lazy {
        TokenManager(applicationContext)
    }

    private val appViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/api/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(NutrigrowApi::class.java)

            return when {
                modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                    AuthViewModel(api, tokenManager) as T
                }

                modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(api, tokenManager) as T
                }

                modelClass.isAssignableFrom(PlannerViewModel::class.java) -> {
                    PlannerViewModel(api, tokenManager) as T
                }

                else -> throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
    private val authViewModel: AuthViewModel by viewModels { appViewModelFactory }
    private val homeViewModel: HomeViewModel by viewModels { appViewModelFactory }
    private val plannerViewModel: PlannerViewModel by viewModels { appViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutrigrowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        authViewModel = authViewModel,
                        homeViewModel = homeViewModel,
                        plannerViewModel = plannerViewModel,
                        tokenManager = tokenManager
                    )
                }
            }
        }
    }
}