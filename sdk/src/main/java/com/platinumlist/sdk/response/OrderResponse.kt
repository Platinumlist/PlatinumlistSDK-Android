package com.platinumlist.sdk.response

import com.google.gson.annotations.SerializedName

class OrderResponse(
    val id: Long?,
    val completed: Long?,
    val comment: String?,
    @SerializedName("is_preprinted")
    val isPreprinted: Boolean?,
    @SerializedName("extended_verification_code")
    val extendedVerificationCode: String?,
    @SerializedName("is_delayed_delivery")
    val isDelayedDelivery: Boolean?,
    @SerializedName("ticket_waiting_text")
    val ticketWaitingText: String?,
    @SerializedName("expiration_datetime")
    val expirationDatetime: Long?,
    val amount: Float?,
    val currency: String?,
    val status: String?,
    @SerializedName("is_white_label")
    val isWhiteLabel: Boolean?,
    @SerializedName("sale_integration")
    val saleIntegration: String?,
    val timezone: String?
)