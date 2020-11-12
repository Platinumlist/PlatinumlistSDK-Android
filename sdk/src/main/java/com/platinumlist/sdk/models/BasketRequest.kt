package com.platinumlist.sdk.models

data class BasketRequest(
    val scope: String,
    val operation: String,
    val include: String,
    val user: UserRequest
)