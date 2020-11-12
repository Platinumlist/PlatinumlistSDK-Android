package com.platinumlist.sample.remote

import com.platinumlist.sample.remote.data.common.DataWrapper
import com.platinumlist.sample.remote.data.request.BasketRequest
import com.platinumlist.sample.remote.data.request.TokenRequest
import com.platinumlist.sample.remote.data.response.BasketResponse
import com.platinumlist.sample.remote.data.response.ServiceResponse
import com.platinumlist.sample.remote.data.response.ShowResponse
import com.platinumlist.sample.remote.data.response.TokenResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface Api {

    @POST(TOKEN)
    fun getTokenAsync(
        @Body
        request: TokenRequest
    ): Deferred<Response<DataWrapper<TokenResponse>>>

    @GET(SHOWS)
    fun getShowListAsync(
        @Query("scope")
        scope: String,
        @Query("has_tickets")
        hasTickets: Int,
        @Query("per_page")
        perPage: Int
    ): Deferred<Response<DataWrapper<List<ShowResponse>>>>

    @GET(SHOWS)
    fun searchAsync(
        @Query("scope")
        scope: String,
        @Query("has_tickets")
        hasTickets: Int,
        @Query("event.search")
        search: String,
        @Query("per_page")
        perPage: Int
    ): Deferred<Response<DataWrapper<List<ShowResponse>>>>

    @GET(BASKET)
    fun getBasketAsync(
        @Path("id")
        id: Long,
        @Query("scope")
        scope: String,
        @Query("include")
        include: String
    ): Deferred<Response<DataWrapper<BasketResponse>>>

    @GET(SERVICES)
    fun getServiceListAsync(
        @Path("id")
        id: Long,
        @Query("scope")
        scope: String
    ): Deferred<Response<DataWrapper<List<ServiceResponse>>>>

    @PATCH(BASKET)
    fun modifyBasketAsync(
        @Path("id")
        id: Long,
        @Body
        request: BasketRequest
    ): Deferred<Response<Any>>

    companion object {
        private const val TOKEN = "access-token"
        private const val SHOWS = "event-shows"
        private const val BASKET = "baskets/{id}"
        private const val SERVICES = "event-shows/{id}/services"
    }
}