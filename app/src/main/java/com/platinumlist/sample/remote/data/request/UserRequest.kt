package com.platinumlist.sample.remote.data.request

import com.google.gson.annotations.SerializedName

data class UserRequest(
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("nationality.id")
    val country: String,
    @SerializedName("city.id")
    val city: String,
    val birthday: String,
    val gender: String
)