package com.platinumlist.sample.ui.result

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.platinumlist.sample.ui.Router
import com.platinumlist.sdk.R
import kotlinx.android.synthetic.main.fragment_result.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.closestKodein

class ResultFragment : Fragment(), KodeinAware {

    private val _parentKodein: Kodein by closestKodein()
    override val kodein: Kodein = Kodein.lazy {
        extend(_parentKodein)
    }
    override val kodeinTrigger = KodeinTrigger()

    private lateinit var router: Router

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
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments!!.getBoolean(KEY_RESULT, false)) {
            result.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            result.text = "Success"
        } else {
            result.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            result.text = "Failed"
        }

        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    companion object {
        private const val KEY_RESULT = "result"

        fun create(
            result: Boolean
        ): ResultFragment {
            return ResultFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(KEY_RESULT, result)
                }
            }
        }
    }
}
