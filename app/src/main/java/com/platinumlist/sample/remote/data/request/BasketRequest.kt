package com.platinumlist.sample.remote.data.request

import com.google.gson.annotations.SerializedName

data class BasketRequest(
    val scope: String,
    val operation: String,
    val amount: Float,
    @SerializedName("event_service.id")
    val serviceList: List<Long>,
    val user: UserRequest
)