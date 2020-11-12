package com.platinumlist.sdk.common

class InvalidDataException(
    val data: Data
) : Exception() {

    enum class Data {
        TOKEN, ID_EVENT_SHOW
    }
}