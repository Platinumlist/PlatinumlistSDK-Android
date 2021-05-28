package com.platinumlist.sdk.request

data class OrderCompletedRequest(
    val scope: String,
    val operation: String,
    val amount: Float
)