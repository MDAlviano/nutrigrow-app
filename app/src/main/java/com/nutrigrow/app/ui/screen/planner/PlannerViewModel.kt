package com.nutrigrow.app.ui.screen.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.NutrigrowApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class PlannerState {
    object Idle : PlannerState()
    object Loading : PlannerState()
    object Success : PlannerState()
    data class Error(val message: String) : PlannerState()
}

class PlannerViewModel(
    private val api: NutrigrowApi,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _plannerState = MutableStateFlow<PlannerState>(PlannerState.Idle)
    val plannerState: StateFlow<PlannerState> = _plannerState.asStateFlow()

    fun createProject(plantName: String, mediaType: String, targetDays: String) {
        viewModelScope.launch {
            _plannerState.value = PlannerState.Loading

            try {
                val daysInt = targetDays.toIntOrNull()
                if (plantName.isBlank() || mediaType.isBlank() || daysInt == null || daysInt <= 0) {
                    _plannerState.value =
                        PlannerState.Error("Mohon isi semua data dengan benar (Hari harus angka bulat).")
                    return@launch
                }

                val token = tokenManager.getToken.firstOrNull()
                if (token.isNullOrEmpty()) {
                    _plannerState.value = PlannerState.Error("Sesi tidak valid.")
                    return@launch
                }

                val request = mapOf(
                    "plant_name" to plantName,
                    "media_type" to mediaType,
                    "target_harvest_days" to daysInt
                )

                val response = api.createProject("Bearer $token", request)

                if (response.isSuccessful && response.body()?.success == true) {
                    _plannerState.value = PlannerState.Success
                } else {
                    _plannerState.value =
                        PlannerState.Error(response.body()?.message ?: "Gagal membuat proyek.")
                }
            } catch (e: Exception) {
                _plannerState.value = PlannerState.Error("Kesalahan jaringan.")
            }
        }
    }

    fun resetState() {
        _plannerState.value = PlannerState.Idle
    }
}