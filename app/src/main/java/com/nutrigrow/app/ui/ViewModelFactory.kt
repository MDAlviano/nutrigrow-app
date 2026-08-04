package com.nutrigrow.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.api.ApiClient
import com.nutrigrow.app.data.repository.AIGrowRepository
import com.nutrigrow.app.data.repository.AuthRepository
import com.nutrigrow.app.data.repository.PlannerRepository
import com.nutrigrow.app.ui.screen.aigrow.AIGrowViewModel
import com.nutrigrow.app.ui.screen.auth.AuthViewModel
import com.nutrigrow.app.ui.screen.planner.PlannerViewModel
import com.nutrigrow.app.ui.screen.profile.ProfileViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val tokenManager = TokenManager(context)
        val apiService = ApiClient.apiService

        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(AuthRepository(apiService, tokenManager)) as T
            }

            modelClass.isAssignableFrom(AIGrowViewModel::class.java) -> {
                AIGrowViewModel(AIGrowRepository(apiService, tokenManager)) as T
            }

            modelClass.isAssignableFrom(PlannerViewModel::class.java) -> {
                PlannerViewModel(PlannerRepository(apiService, tokenManager)) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(tokenManager) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}