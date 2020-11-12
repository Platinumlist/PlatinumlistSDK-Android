package com.platinumlist.sample.remote.data.request

import com.google.gson.annotations.SerializedName

data class TokenRequest(
    val credential: String,
    val password: String,
    @SerializedName("is_partner_login")
    val isPartnerLogin: Boolean = true
)