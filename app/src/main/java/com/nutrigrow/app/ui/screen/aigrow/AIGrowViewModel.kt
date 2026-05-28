package com.nutrigrow.app.ui.screen.aigrow

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.DiagnosisData
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

sealed class AIGrowState {
    object Idle : AIGrowState()
    object Loading : AIGrowState()
    data class Success(val data: DiagnosisData) : AIGrowState()
    data class Error(val message: String) : AIGrowState()
}

class AIGrowViewModel(
    private val api: NutrigrowApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _aigrowState = MutableStateFlow<AIGrowState>(AIGrowState.Idle)
    val aigrowState: StateFlow<AIGrowState> = _aigrowState.asStateFlow()

    fun analyzeImage(context: Context, imageUri: Uri, plantType: String = "Tanaman Umum") {
        viewModelScope.launch {
            _aigrowState.value = AIGrowState.Loading

            try {
                val token = tokenManager.getToken.firstOrNull()
                if (token.isNullOrEmpty()) {
                    _aigrowState.value = AIGrowState.Error("Sesi tidak valid.")
                    return@launch
                }

                val inputStream = context.contentResolver.openInputStream(imageUri)
                if (inputStream == null) {
                    _aigrowState.value = AIGrowState.Error("Gagal membaca gambar.")
                    return@launch
                }

                val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
                tempFile.outputStream().use { output ->
                    inputStream.copyTo(output)
                }

                val reqFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", tempFile.name, reqFile)
                val typePart = plantType.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = api.diagnoseImage("Bearer $token", imagePart, typePart)

                tempFile.delete()

                if (response.isSuccessful && response.body()?.success == true) {
                    val resultData = response.body()?.data
                    if (resultData != null) {
                        _aigrowState.value = AIGrowState.Success(resultData)
                    } else {
                        _aigrowState.value = AIGrowState.Error("Data AI kosong.")
                    }
                } else {
                    _aigrowState.value = AIGrowState.Error(response.body()?.message ?: "Gagal menganalisis gambar.")
                }
            } catch (e: Exception) {
                _aigrowState.value = AIGrowState.Error("Kesalahan jaringan: ${e.message}")
            }
        }
    }

    fun resetState() {
        _aigrowState.value = AIGrowState.Idle
    }
}