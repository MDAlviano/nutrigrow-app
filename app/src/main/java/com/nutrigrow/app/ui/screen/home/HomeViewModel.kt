package com.nutrigrow.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.NutrigrowApi
import com.nutrigrow.app.data.remote.PlantingProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val projects: List<PlantingProject>) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(
    private val api: NutrigrowApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        fetchProjects()
    }

    fun fetchProjects() {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading

            try {
                val token = tokenManager.getToken.firstOrNull()

                if (token.isNullOrEmpty()) {
                    _homeState.value = HomeState.Error("Sesi telah habis. Silakan login kembali.")
                    return@launch
                }

                val bearerToken = "Bearer $token"

                val response = api.getProjects(bearerToken)

                if (response.isSuccessful && response.body()?.success == true) {
                    val projects = response.body()?.data ?: emptyList()
                    _homeState.value = HomeState.Success(projects)
                } else {
                    _homeState.value = HomeState.Error(response.body()?.message ?: "Gagal mengambil data proyek")
                }
            } catch (e: Exception) {
                _homeState.value = HomeState.Error("Terjadi kesalahan jaringan.")
            }
        }
    }
}