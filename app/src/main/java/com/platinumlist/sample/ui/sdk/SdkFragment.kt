package com.platinumlist.sample.ui.sdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.platinumlist.sample.common.Adapter
import com.platinumlist.sample.remote.data.common.Storage
import com.platinumlist.sample.ui.Router
import com.platinumlist.sdk.R
import com.platinumlist.sdk.common.InvalidDataException
import com.platinumlist.sdk.common.SdkType
import kotlinx.android.synthetic.main.activity_sample.platinumView
import kotlinx.android.synthetic.main.fragment_sdk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.closestKodein

class SdkFragment : Fragment(), KodeinAware {

    private val _parentKodein: Kodein by closestKodein()
    override val kodein: Kodein = Kodein.lazy {
        extend(_parentKodein)
    }
    override val kodeinTrigger = KodeinTrigger()

    private lateinit var router: Router
    private var isInit = false

    private val adapter: Adapter by lazy {
        Adapter(
            mGoBack = requireActivity()::onBackPressed,
            show = this::showMessage,
            startActivity = this::startActivity
        )
    }

    private fun startActivity(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    override fun onAttach(context: Context?) {
        kodeinTrigger.trigger()
        super.onAttach(context)
        router = context as Router
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sdk, container, false)
    }

    override fun onStart() {
        super.onStart()
        if(isInit){
            return
        }
        isInit = true

        platinumView.adapter = adapter

        adapter.progress.observe(this, Observer {
            progressView.visibility = if (it) View.VISIBLE else View.GONE
        })

        GlobalScope.launch {
            try {
                //adapter.processUser("jwt", SMSocialType.DTCM)
                //adapter.setLang(Language.ENGLISH.shortName)


                Storage.showId = arguments!!.getLong(EVENT_ID, -1L)
                Storage.eventShowId = arguments!!.getLong(EVENT_SHOW_ID, -1L)
                adapter.invalidate()
            } catch (e: InvalidDataException) {
                when (e.data) {
                    InvalidDataException.Data.ID_EVENT_SHOW -> {
                        "Not valid show id"
                    }
                    InvalidDataException.Data.TOKEN -> {
                        "Not valid token"
                    }
                }.let {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        if (Storage.orderId != -1L) {
            router.navigateToUpdateOrder()
        }

        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        platinumView.adapter = adapter
        adapter.onActivityResult(requestCode, resultCode, data)
    }

    private fun showMessage(message: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        platinumView.clear()
        super.onStop()
    }

    companion object {
        private const val EVENT_ID = "eventId"
        private const val EVENT_SHOW_ID = "eventShowId"

        fun create(eventId: Long, eventShowId: Long): SdkFragment {
            return SdkFragment().apply {
                arguments = Bundle().apply {
                    putLong(EVENT_ID, eventId)
                    putLong(EVENT_SHOW_ID, eventShowId)
                }
            }
        }
    }
}