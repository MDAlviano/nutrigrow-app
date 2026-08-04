package com.nutrigrow.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DiagnosisData(
    @SerializedName("id") val id: String,
    @SerializedName("disease_name") val diseaseName: String,
    @SerializedName("confidence_score") val confidenceScore: Double,
    @SerializedName("ai_reasoning") val aiReasoning: String,
    @SerializedName("organic_solutions") val organicSolutions: List<String>,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("created_at") val createdAt: String
)