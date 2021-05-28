package com.platinumlist.sample.remote.data.request

data class OrderCompletedRequest(
    val scope: String,
    val operation: String,
    val amount: Float
)