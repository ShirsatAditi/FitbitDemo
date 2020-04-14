package com.example.fitbitdemo.Model

import com.google.gson.annotations.SerializedName

/**
 *
 *
 * @author Aditi Shirsat
 */

data class AccessToken(
    @SerializedName("access_token") val access_token: String,
    @SerializedName("expires_in") val expires_in: String,
    @SerializedName("refresh_token") val refresh_token: String,
    @SerializedName("token_type") val token_type: String,
    @SerializedName("user_id") val user_id: String
)