package com.platinumlist.sample.di

import com.platinumlist.sample.remote.Api
import com.platinumlist.sample.remote.RemoteInterceptor
import com.platinumlist.sample.ui.App
import com.platinumlist.sdk.BuildConfig
import com.vrgsoft.retrofit.RetrofitModule
import com.vrgsoft.retrofit.common.RetrofitConfig
import org.kodein.di.Kodein
import org.kodein.di.android.androidModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit

object AppModule {

    fun get(application: App) = Kodein.Module("App") {
        import(androidModule(application))

        configureRetrofit()
        import(RetrofitModule.get())

        bind() from singleton {
            instance<Retrofit>().create(Api::class.java)
        }
    }

    private fun configureRetrofit() {
        RetrofitConfig.apply {
            baseUrl = BuildConfig.BASE_URL

            useCustomAuthInterceptor(RemoteInterceptor())

            enableLogging()
        }
    }
}