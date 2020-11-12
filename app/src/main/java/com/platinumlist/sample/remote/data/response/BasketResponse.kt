package com.platinumlist.sample.remote.data.response

import com.google.gson.annotations.SerializedName
import com.platinumlist.sample.remote.data.common.DataWrapper

class BasketResponse(
    val amount: Float?,
    @SerializedName("flat_price")
    val flatPrice: Float?,
    val fee: List<Any>?,
    val currency: String?,
    @SerializedName("promo_code")
    val promoCode: String?,
    val expires: Long?,
    @SerializedName("basket_items")
    val items: DataWrapper<List<Any>>?
)