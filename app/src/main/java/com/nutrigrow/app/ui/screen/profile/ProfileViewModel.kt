package com.nutrigrow.app.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigrow.app.data.local.TokenManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val tokenManager: TokenManager
): ViewModel() {
    val userName: StateFlow<String?> = tokenManager.getUserName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Pengguna Nutrigrow"
    )

    val userEmail: StateFlow<String?> = tokenManager.getUserEmail.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "user@nutrigrow.app"
    )

    fun logout(onLogoutFinished: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearAuthData()
            onLogoutFinished()
        }
    }
}