package com.nutrigrow.app.data.repository

import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.api.NutrigrowApi
import com.nutrigrow.app.data.remote.dto.CreateProjectRequest
import com.nutrigrow.app.data.remote.dto.LogData
import com.nutrigrow.app.data.remote.dto.ProjectData
import com.nutrigrow.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PlannerRepository(
    private val api: NutrigrowApi,
    private val tokenManager: TokenManager
) {
    fun getProjects(): Flow<Resource<List<ProjectData>>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken.first()
            val response = api.getProjects("Bearer $token")
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                emit(Resource.Success(body.data ?: emptyList()))
            } else {
                emit(Resource.Error(body?.message ?: "Gagal mengambil daftar proyek"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }

    fun createProject(name: String, media: String, targetDays: Int): Flow<Resource<ProjectData>> =
        flow {
            emit(Resource.Loading())
            try {
                val token = tokenManager.getToken.first()
                val response = api.createProject(
                    "Bearer $token",
                    CreateProjectRequest(name, media, targetDays)
                )
                val body = response.body()

                if (response.isSuccessful && body?.success == true && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal membuat proyek baru"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
            }
        }

    fun addProgressLog(projectId: String, imageFile: File, notes: String): Flow<Resource<LogData>> =
        flow {
            emit(Resource.Loading())
            try {
                val token = tokenManager.getToken.first()
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart =
                    MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
                val notesBody = notes.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = api.addProgressLog("Bearer $token", projectId, imagePart, notesBody)
                val body = response.body()

                if (response.isSuccessful && body?.success == true && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal menambahkan log progress"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
            }
        }
}