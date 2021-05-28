package com.platinumlist.sdk.request

data class OrderFailedRequest(
    val scope: String,
    val operation: String
)