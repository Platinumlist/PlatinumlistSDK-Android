package com.platinumlist.sdk

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import com.platinumlist.sdk.di.OrderResultModule
import com.platinumlist.sdk.remote.Api
import com.platinumlist.sdk.request.OrderCompletedRequest
import com.platinumlist.sdk.request.OrderFailedRequest
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.generic.instance
import java.lang.Exception

class OrderResultView(
    context: Context
) : ConstraintLayout(context, null, 0), KodeinAware {
    // TODO need refactor

    override val kodein: Kodein = Kodein.lazy {
        import(OrderResultModule.get(this@OrderResultView))
    }
    override val kodeinTrigger = KodeinTrigger()
    private val api: Api by instance()

    fun updateToCompleted(orderId: Long, amount: Float): Boolean = runBlocking {
        try {
            api.completedOrderAsync(
                id = orderId,
                request = OrderCompletedRequest(
                    scope = "partner-sale.receive-payments",
                    operation = "pay",
                    amount = amount
                )
            ).await().let {
                it.body() != null
            }
        } catch (e: Exception) {
            true
        }
    }

    fun updateToFailed(orderId: Long): Boolean = runBlocking {
        try {
            api.failedOrderAsync(
                id = orderId,
                request = OrderFailedRequest(
                    scope = "partner-sale.receive-payments",
                    operation = "failed"
                )
            ).await().let {
                it.body() != null
            }
        } catch (e: Exception) {
            true
        }
    }
}