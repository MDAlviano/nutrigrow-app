package com.nutrigrow.app.ui.screen.planner

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.NutrigrowApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

sealed class LogUploadState {
    object Idle : LogUploadState()
    object Loading : LogUploadState()
    object Success : LogUploadState()
    data class Error(val message: String) : LogUploadState()
}

class ProjectDetailViewModel(
    private val api: NutrigrowApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uploadState = MutableStateFlow<LogUploadState>(LogUploadState.Idle)
    val uploadState: StateFlow<LogUploadState> = _uploadState.asStateFlow()

    fun addLog(context: Context, projectId: String, imageUri: Uri, notes: String) {
        viewModelScope.launch {
            _uploadState.value = LogUploadState.Loading

            try {
                val token = tokenManager.getToken.firstOrNull()
                if (token.isNullOrEmpty()) {
                    _uploadState.value = LogUploadState.Error("Sesi tidak valid")
                    return@launch
                }

                val inputStream = context.contentResolver.openInputStream(imageUri)
                val tempFile = File.createTempFile("log_", ".jpg", context.cacheDir)
                inputStream?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }

                val reqFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", tempFile.name, reqFile)
                val notesPart = notes.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = api.addProjectLog("Bearer $token", projectId, imagePart, notesPart)

                tempFile.delete()

                if (response.isSuccessful && response.body()?.success == true) {
                    _uploadState.value = LogUploadState.Success
                } else {
                    _uploadState.value = LogUploadState.Error(response.body()?.message ?: "Gagal upload log")
                }
            } catch (e: Exception) {
                _uploadState.value = LogUploadState.Error("Kesalahan jaringan: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uploadState.value = LogUploadState.Idle
    }
}