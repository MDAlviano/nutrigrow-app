package com.nutrigrow.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NutrigrowApi {

    @POST("auth/register")
    suspend fun register(
        @Body request: Map<String, String>
    ): Response<BaseResponse<UserDto>>

    @POST("auth/login")
    suspend fun login(
        @Body request: Map<String, String>
    ): Response<BaseResponse<LoginData>>
}