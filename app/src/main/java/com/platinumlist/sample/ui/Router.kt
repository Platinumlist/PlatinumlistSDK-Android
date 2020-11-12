package com.platinumlist.sample.ui

interface Router {
    fun navigateToShowList()
    fun navigateToSdk(id: Long)
    fun navigateToResult(result: Boolean)
}