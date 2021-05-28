package com.platinumlist.sdk.common

import android.webkit.JavascriptInterface

internal class WebInterface(
    private val mCheckOut: (String, Long) -> Unit,
    private val mGoBack: () -> Unit,
    private val mOrderStatusResolver: (Long, String) -> Unit,
    private val mCloseWebview: () -> Unit,
    private val mTriggerError: (String, Int) -> Unit,
    private val mProcessTicketDownload: (String, String) -> Unit

) {

    @JavascriptInterface
    fun checkOut(link: String, showId: Long) {
        mCheckOut(link, showId)
    }

    @JavascriptInterface
    fun orderStatusResolver(orderId: Long, status: String) {
        mOrderStatusResolver(orderId, status)
    }

    @JavascriptInterface
    fun goBack() {
        mGoBack()
    }

    @JavascriptInterface
    fun closeWebview() {
        mCloseWebview()
    }

    @JavascriptInterface
    fun triggerError(message: String, code: Int) {
        mTriggerError(message, code)
    }

    @JavascriptInterface
    fun processTicketDownload(link: String, type: String) {
        mProcessTicketDownload(link, type)
    }
}