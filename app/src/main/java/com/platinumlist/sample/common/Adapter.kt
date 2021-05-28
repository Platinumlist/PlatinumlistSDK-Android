package com.platinumlist.sample.common

import android.content.Intent
import com.platinumlist.sample.remote.data.common.Storage
import com.platinumlist.sdk.PlatinumView
import com.platinumlist.sdk.common.SdkType
import com.platinumlist.sdk.models.UserRequest

class Adapter(
    private val mGoBack: () -> Unit,
    private val show: (String) -> Unit,
    private val startActivity: (Intent, Int) -> Unit
) : PlatinumView.Adapter() {

    override fun startActivity(intent: Intent, requestCode: Int) {
        startActivity.invoke(intent, requestCode)
    }

    override fun checkOutError(error: Exception) {
        show("checkOut error: ${error.message}")
    }

    override fun goBack() {
        mGoBack()
    }

    override suspend fun provideToken(): String {
        return Storage.token
    }

    override suspend fun provideIdEvent(): Long {
        return Storage.showId
    }

    override suspend fun provideEventShowSetting(): Int {
        return Storage.isEventShowSetting
    }

    override suspend fun provideIdEventShow(): Long {
        return Storage.eventShowId
    }

    override suspend fun provideUser(): UserRequest? {
        return UserRequest(
            name = "User Userovich",
            cityId = 1,
            email = "email@gmail.com",
            nationalityId = 254,
            phone = if (Storage.userPhoneNumber.isEmpty()) "+971501234567" else Storage.userPhoneNumber
        )
    }

    override suspend fun provideLanguage(): String {
        return Storage.lang
    }

    override fun orderStatusResolver(orderId: Long, status: String) {
        show("orderStatusResolver(orderId: $orderId, status: $status)")
    }

    override fun closeWebview() {
        show("closeWebview()")
    }

    override fun getSdkType(): SdkType {
        return Storage.sdkType
    }

    override fun triggerError(message: String, code: Int) {
        show("triggerError(message: $message, code: $code)")
    }

    override fun setOrderId(orderId: Long) {
        Storage.orderId = orderId
    }

    override fun setOrderAmount(amount: Float) {
        Storage.amount = amount
    }

    override fun registerUser() {
        show("registerUser()")
    }
}