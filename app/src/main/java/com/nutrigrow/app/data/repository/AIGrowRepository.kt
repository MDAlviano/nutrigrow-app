package com.nutrigrow.app.data.repository

import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.api.NutrigrowApi
import com.nutrigrow.app.data.remote.dto.DiagnosisData
import com.nutrigrow.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AIGrowRepository(
    private val api: NutrigrowApi,
    private val tokenManager: TokenManager
) {
    fun diagnoseLeaf(imageFile: File, plantType: String): Flow<Resource<DiagnosisData>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken.first()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("Token tidak ditemukan, silakan login kembali"))
                return@flow
            }

            val authHeader = "Bearer $token"
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            val plantTypeBody = plantType.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.diagnoseLeaf(authHeader, imagePart, plantTypeBody)
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                emit(Resource.Success(body.data))
            } else {
                emit(Resource.Error(body?.message ?: "Gagal menganalisis gambar daun"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }
}