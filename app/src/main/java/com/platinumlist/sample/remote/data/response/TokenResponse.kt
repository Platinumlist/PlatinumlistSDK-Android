package com.platinumlist.sample.remote.data.response


import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("expires")
    val expires: Int?,
    @SerializedName("lang")
    val lang: String?,
    @SerializedName("scopes")
    val scopes: List<String?>?,
    @SerializedName("token_type")
    val tokenType: String?
)