package com.nutrigrow.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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

    @GET("planner/projects")
    suspend fun getProjects(
        @Header("Authorization") token: String
    ): Response<BaseResponse<List<PlantingProject>>>

    @POST("planner/projects")
    suspend fun createProject(
        @Header("Authorization") token: String,
        @Body request: Map<String, Any>
    ): Response<BaseResponse<PlantingProject>>

}