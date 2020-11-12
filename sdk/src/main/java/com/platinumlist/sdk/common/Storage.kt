package com.platinumlist.sdk.common

import com.platinumlist.sdk.models.UserRequest

internal object Storage {
    var token: String = ""
    var showId: Long = -1L
    var user: UserRequest? = null
}