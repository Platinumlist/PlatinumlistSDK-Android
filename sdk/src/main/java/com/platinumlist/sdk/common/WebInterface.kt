package com.platinumlist.sdk.common

import android.webkit.JavascriptInterface

internal class WebInterface(
    private val mCheckOut: (String) -> Unit,
    private val mGoBack: () -> Unit,
    private val mOrderStatusResolver: (Int, String) -> Unit
) {

    @JavascriptInterface
    fun checkOut(link: String) {
        mCheckOut(link)
    }

    @JavascriptInterface
    fun orderStatusResolver(orderId: Int, status: String) {
        mOrderStatusResolver(orderId, status)
    }

    @JavascriptInterface
    fun goBack() {
        mGoBack()
    }
}