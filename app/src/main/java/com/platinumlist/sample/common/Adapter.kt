package com.platinumlist.sample.common

import android.content.Intent
import com.platinumlist.sample.remote.data.common.Storage
import com.platinumlist.sdk.PlatinumView
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

    override suspend fun provideIdEventShow(): Long {
        return Storage.showId
    }

    override suspend fun provideUser(): UserRequest {
        return UserRequest(
            name = "User Userovich",
            cityId = 1,
            email = "email@gmail.com",
            nationalityId = 254,
            phone = "+971501234567"
        )
    }

    override fun orderStatusResolver(orderId: Int, status: String) {
        show("orderStatusResolver(orderId: $orderId, status: $status)")
    }
}