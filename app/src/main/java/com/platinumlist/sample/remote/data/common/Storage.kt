package com.platinumlist.sample.remote.data.common

import com.platinumlist.sdk.common.Language
import com.platinumlist.sdk.common.SdkType

object Storage {
    var token: String = ""
    var showId: Long = 0
    var sdkType: SdkType = SdkType.PURCHASE
    var lang: String = Language.ENGLISH.fullName
    var eventShowId: Long = -1L
    var isEventShowSetting: Int = 1
    var orderId: Long = -1L
    var amount: Float = 0f
    var userPhoneNumber: String = ""
}