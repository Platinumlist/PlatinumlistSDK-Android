package com.platinumlist.sample.ui.updorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.platinumlist.sample.remote.data.common.Storage
import com.platinumlist.sample.remote.Api
import com.platinumlist.sample.remote.data.request.OrderCompletedRequest
import com.platinumlist.sample.remote.data.request.OrderFailedRequest
import com.platinumlist.sdk.OrderResultView
import com.platinumlist.sdk.R
import kotlinx.android.synthetic.main.fragment_update_order.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class UpdateOrderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderRepository = OrderResultView(requireContext())

        tvOrderID.text = "Order ID: ${Storage.orderId}"

        btnComplete.setOnClickListener {
            val isUpdate = orderRepository.updateToCompleted(Storage.orderId, Storage.amount)

            if (isUpdate) {
                Toast.makeText(context, "order status completed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        }

        btnFailed.setOnClickListener {
            val isUpdate = orderRepository.updateToFailed(Storage.orderId)

            if (isUpdate) {
                Toast.makeText(context, "order status failed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}