package com.nutrigrow.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateProjectRequest(
    @SerializedName("plant_name") val plantName: String,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("target_harvest_days") val targetHarvestDays: Int
)

data class ProjectData(
    @SerializedName("id") val id: String,
    @SerializedName("plant_name") val plantName: String,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("target_harvest_days") val targetHarvestDays: Int,
    @SerializedName("status") val status: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("logs") val logs: List<LogData>? = emptyList()
)

data class LogData(
    @SerializedName("id") val id: String,
    @SerializedName("notes") val notes: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("ai_insight_status") val aiInsightStatus: String?,
    @SerializedName("ai_insight_message") val aiInsightMessage: String?,
    @SerializedName("created_at") val createdAt: String
)