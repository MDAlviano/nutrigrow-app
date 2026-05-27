package com.nutrigrow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.NutrigrowApi
import com.nutrigrow.app.ui.navigation.AppNavigation
import com.nutrigrow.app.ui.screen.auth.AuthViewModel
import com.nutrigrow.app.ui.theme.NutrigrowTheme
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private val authViewModelFactory = object : ViewModelProvider.Factory {
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

            val tokenManager = TokenManager(applicationContext)

            return AuthViewModel(api, tokenManager) as T
        }
    }

    private val authViewModel: AuthViewModel by viewModels { authViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutrigrowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(authViewModel = authViewModel)
                }
            }
        }
    }
}