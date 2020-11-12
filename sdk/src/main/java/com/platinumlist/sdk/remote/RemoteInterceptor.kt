package com.platinumlist.sdk.remote

import com.platinumlist.sdk.common.Storage
import okhttp3.Interceptor
import okhttp3.Response

internal class RemoteInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        builder.addHeader("Api-Authorization", "Bearer ${Storage.token}")

        return builder.build().let {
            chain.proceed(it)
        }
    }

}