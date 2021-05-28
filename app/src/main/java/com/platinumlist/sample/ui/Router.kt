package com.platinumlist.sample.ui

interface Router {
    fun navigateToShowList()
    fun navigateToSettingsList()
    fun navigateToSdk(eventId: Long, eventShowId: Long)
    fun navigateToResult(result: Boolean)
    fun navigateToUpdateOrder()
}