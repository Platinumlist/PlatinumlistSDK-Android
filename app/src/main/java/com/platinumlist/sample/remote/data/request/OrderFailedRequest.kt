package com.platinumlist.sample.remote.data.request

data class OrderFailedRequest(
    val scope: String,
    val operation: String
)