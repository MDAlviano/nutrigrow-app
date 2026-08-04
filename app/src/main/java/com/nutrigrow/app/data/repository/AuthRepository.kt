package com.nutrigrow.app.data.repository

import com.nutrigrow.app.data.local.TokenManager
import com.nutrigrow.app.data.remote.api.NutrigrowApi
import com.nutrigrow.app.data.remote.dto.LoginRequest
import com.nutrigrow.app.data.remote.dto.RegisterRequest
import com.nutrigrow.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(
    private val api: NutrigrowApi,
    private val tokenManager: TokenManager
) {
    fun register(displayName: String, email: String, password: String): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val response = api.register(RegisterRequest(displayName, email, password))
                if (response.isSuccessful && response.body()?.success == true) {
                    emit(Resource.Success(response.body()?.message ?: "Registrasi berhasil"))
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Registrasi gagal"
                    emit(Resource.Error(errorMsg))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
            }
        }

    fun login(email: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.login(LoginRequest(email, password))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                // Simpan Token ke DataStore
                tokenManager.saveAuthData(
                    token = body.data.token,
                    userId = ""
                )
                emit(Resource.Success(body.message))
            } else {
                emit(Resource.Error(body?.message ?: "Login gagal, periksa email/password"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }
}