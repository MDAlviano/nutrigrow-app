package com.nutrigrow.app.ui.screen.aigrow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigrow.app.data.remote.dto.DiagnosisData
import com.nutrigrow.app.data.repository.AIGrowRepository
import com.nutrigrow.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class AIGrowUiState {
    object Idle : AIGrowUiState()
    object Loading : AIGrowUiState()
    data class Success(val result: DiagnosisData) : AIGrowUiState()
    data class Error(val message: String) : AIGrowUiState()
}

class AIGrowViewModel(
    private val repository: AIGrowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AIGrowUiState>(AIGrowUiState.Idle)
    val uiState: StateFlow<AIGrowUiState> = _uiState

    fun diagnose(imageFile: File, plantType: String) {
        viewModelScope.launch {
            repository.diagnoseLeaf(imageFile, plantType).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.value = AIGrowUiState.Loading
                    is Resource.Success -> _uiState.value = AIGrowUiState.Success(resource.data!!)
                    is Resource.Error -> _uiState.value =
                        AIGrowUiState.Error(resource.message ?: "Terjadi kesalahan")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = AIGrowUiState.Idle
    }
}