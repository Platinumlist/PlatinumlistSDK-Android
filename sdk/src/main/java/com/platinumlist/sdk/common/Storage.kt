package com.platinumlist.sdk.common

import com.platinumlist.sdk.models.UserRequest

internal object Storage {
    var token: String = ""
    var showId: Long = -1L
    var user: UserRequest? = null
    var lang: String = Language.ENGLISH.fullName
    var eventShowId: Long = -1L
    var eventShowSetting: Int = 1
    var orderId: Long = -1L
    var amount: Float = 0f
}