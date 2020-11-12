package com.platinumlist.sample.remote.data.response

data class ServiceResponse(
    val id: Long?,
    val name: String?,
    val price: PriceResponse?,
    val currency: String?
)