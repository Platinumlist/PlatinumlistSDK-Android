package com.platinumlist.sdk.remote

import com.platinumlist.sdk.common.DataWrapper
import com.platinumlist.sdk.models.BasketRequest
import com.platinumlist.sdk.request.OrderCompletedRequest
import com.platinumlist.sdk.request.OrderFailedRequest
import com.platinumlist.sdk.response.OrderResponse
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

    @PATCH(ORDER)
    fun completedOrderAsync(
        @Path("id")
        id: Long,
        @Body
        request: OrderCompletedRequest
    ): Deferred<Response<Any>>

    @PATCH(ORDER)
    fun failedOrderAsync(
        @Path("id")
        id: Long,
        @Body
        request: OrderFailedRequest
    ): Deferred<Response<Any>>

    @GET(ORDER)
    fun getOrderAsync(
        @Path("id")
        id: Long,
        @Query("scope")
        scope: String
    ): Deferred<Response<DataWrapper<OrderResponse>>>

    companion object {
        private const val CHECK = "event-shows/{id}"
        private const val BASKET = "baskets/{id}"
        private const val ORDER = "orders/{id}"
    }
}