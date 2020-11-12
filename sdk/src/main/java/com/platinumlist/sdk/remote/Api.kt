package com.platinumlist.sdk.remote

import com.platinumlist.sdk.models.BasketRequest
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

internal interface Api {

    @GET(CHECK)
    fun checkAsync(
        @Path("id")
        id: Long,
        @Query("scope")
        scope: String
    ): Deferred<Response<Any>>

    @DELETE(BASKET)
    fun clearBasketAsync(
        @Path("id")
        id: Long,
        @Query("scope")
        scope: String
    ): Deferred<Response<Any>>

    @PATCH(BASKET)
    fun changeBasketAsync(
        @Path("id")
        id: Long,
        @Body
        request: BasketRequest
    ): Deferred<Response<Any>>

    companion object {
        private const val CHECK = "event-shows/{id}"
        private const val BASKET = "baskets/{id}"
    }
}