package com.platinumlist.sample.remote

import com.platinumlist.sample.remote.data.common.Storage
import okhttp3.Interceptor
import okhttp3.Response

class RemoteInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        builder.addHeader("Api-Authorization", "Bearer ${Storage.token}")

        return builder.build().let {
            chain.proceed(it)
        }
    }

}