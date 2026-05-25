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