package com.nutrigrow.app.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigrow.app.data.local.TokenManager
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    fun logout(onLogoutFinished: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearAuthData()
            onLogoutFinished()
        }
    }
}