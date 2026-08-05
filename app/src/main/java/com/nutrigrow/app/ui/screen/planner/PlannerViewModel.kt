package com.nutrigrow.app.ui.screen.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigrow.app.data.remote.dto.ProjectData
import com.nutrigrow.app.data.repository.PlannerRepository
import com.nutrigrow.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class PlannerUiState {
    object Loading : PlannerUiState()
    data class Success(val projects: List<ProjectData>) : PlannerUiState()
    data class Error(val message: String) : PlannerUiState()
}

class PlannerViewModel(
    private val repository: PlannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlannerUiState>(PlannerUiState.Loading)
    val uiState: StateFlow<PlannerUiState> = _uiState

    init {
        fetchProjects()
    }

    fun fetchProjects() {
        viewModelScope.launch {
            repository.getProjects().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.value = PlannerUiState.Loading
                    is Resource.Success -> _uiState.value =
                        PlannerUiState.Success(resource.data ?: emptyList())

                    is Resource.Error -> _uiState.value =
                        PlannerUiState.Error(resource.message ?: "Gagal mengambil data")
                }
            }
        }
    }

    fun createProject(
        name: String,
        media: String,
        targetDays: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            repository.createProject(name, media, targetDays).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        fetchProjects()
                        onResult(true, "Proyek berhasil dibuat")
                    }

                    is Resource.Error -> onResult(false, resource.message ?: "Gagal membuat proyek")
                    else -> {}
                }
            }
        }
    }

    fun addProgressLog(
        projectId: String,
        imageFile: File,
        notes: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            repository.addProgressLog(projectId, imageFile, notes).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        fetchProjects()
                        onResult(true, "Log perkembangan berhasil ditambahkan")
                    }

                    is Resource.Error -> onResult(false, resource.message ?: "Gagal menambah log")
                    else -> {}
                }
            }
        }
    }
}