package com.platinumlist.sdk.di

import android.view.View
import com.platinumlist.sdk.remote.Api
import com.platinumlist.sdk.remote.RemoteInterceptor
import com.vrgsoft.retrofit.RetrofitModule
import com.vrgsoft.retrofit.common.RetrofitConfig
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import retrofit2.Retrofit

object OrderResultModule {

    fun get(view: View) = Kodein.Module("OrderResultView") {
        bind() from provider { view.context }

        bind() from singleton {
            instance<Retrofit>().create(Api::class.java)
        }

        configureRetrofit()
        import(RetrofitModule.get())
    }

    private fun configureRetrofit() {
        RetrofitConfig.apply {
            baseUrl = "https://api.platinumlist.net/v/7/"

            useCustomAuthInterceptor(RemoteInterceptor())
        }
    }

}