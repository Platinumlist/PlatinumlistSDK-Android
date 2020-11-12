package com.platinumlist.sdk.models

import com.google.gson.annotations.SerializedName

data class UserRequest(
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("nationality.id")
    val nationalityId: Long,
    @SerializedName("city.id")
    val cityId: Long
)