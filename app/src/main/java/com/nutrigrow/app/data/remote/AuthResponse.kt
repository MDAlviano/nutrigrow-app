package com.nutrigrow.app.data.remote

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T? = null
)

data class UserDto(
    @SerializedName("user_id") val userId: String,
    @SerializedName("email") val email: String,
    @SerializedName("display_name") val displayName: String
)

data class LoginData(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserDto
)

data class PlantingProject(
    @SerializedName("id") val id: String,
    @SerializedName("plant_name") val plantName: String,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("target_harvest_days") val targetHarvestDays: Int,
    @SerializedName("status") val status: String,
    @SerializedName("start_date") val startDate: String
)

data class DiagnosisData(
    @SerializedName("disease_name") val diseaseName: String,
    @SerializedName("confidence_score") val confidenceScore: Double,
    @SerializedName("ai_reasoning") val aiReasoning: String,
    @SerializedName("organic_solutions") val organicSolutions: List<String>,
    @SerializedName("image_url") val imageUrl: String?
)