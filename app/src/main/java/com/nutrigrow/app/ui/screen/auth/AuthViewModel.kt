package com.nutrigrow.app.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigrow.app.data.repository.AuthRepository
import com.nutrigrow.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email dan password wajib diisi")
            return
        }

        viewModelScope.launch {
            repository.login(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = AuthUiState.Loading
                    is Resource.Success -> _uiState.value =
                        AuthUiState.Success(result.data ?: "Login Berhasil")

                    is Resource.Error -> _uiState.value =
                        AuthUiState.Error(result.message ?: "Login Gagal")
                }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Semua kolom wajib diisi")
            return
        }

        viewModelScope.launch {
            repository.register(name, email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = AuthUiState.Loading
                    is Resource.Success -> _uiState.value =
                        AuthUiState.Success(result.data ?: "Registrasi Berhasil")

                    is Resource.Error -> _uiState.value =
                        AuthUiState.Error(result.message ?: "Registrasi Gagal")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}