package com.nutrigrow.app.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.NutrigrowApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val api: NutrigrowApi,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)

    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun register(displayName: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                val request = mapOf(
                    "display_name" to displayName,
                    "email" to email,
                    "password" to password
                )

                val response = api.register(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    _authState.value = AuthState.Success("Registrasi berhasil! Silakan masuk.")
                } else {
                    val errorMsg = response.body()?.message ?: "Terjadi kesalahan sistem"
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Gagal terhubung ke server. Periksa koneksi Anda.")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                val request = mapOf(
                    "email" to email,
                    "password" to password
                )

                val response = api.login(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val token = response.body()?.data?.token

                    if (token != null) {
                        tokenManager.saveToken(token)
                        _authState.value = AuthState.Success("Login berhasil!")
                    } else {
                        _authState.value = AuthState.Error("Token tidak ditemukan dari server")
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Email atau kata sandi salah"
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Gagal terhubung ke server. Periksa koneksi Anda.")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}