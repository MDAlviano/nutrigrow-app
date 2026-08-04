package com.nutrigrow.app.data.remote.api

import com.nutrigrow.app.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface NutrigrowApi {
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<BaseResponse<Any>>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<BaseResponse<LoginData>>

    @Multipart
    @POST("aigrow/diagnose")
    suspend fun diagnoseLeaf(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("plant_type") plantType: RequestBody
    ): Response<BaseResponse<DiagnosisData>>

    @GET("planner/projects")
    suspend fun getProjects(
        @Header("Authorization") token: String
    ): Response<BaseResponse<List<ProjectData>>>

    @POST("planner/projects")
    suspend fun createProject(
        @Header("Authorization") token: String,
        @Body request: CreateProjectRequest
    ): Response<BaseResponse<ProjectData>>

    @Multipart
    @POST("planner/projects/{project_id}/logs")
    suspend fun addProgressLog(
        @Header("Authorization") token: String,
        @Path("project_id") projectId: String,
        @Part image: MultipartBody.Part,
        @Part("notes") notes: RequestBody
    ): Response<BaseResponse<LogData>>
}