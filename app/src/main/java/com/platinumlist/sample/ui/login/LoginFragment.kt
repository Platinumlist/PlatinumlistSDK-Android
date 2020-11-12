package com.platinumlist.sample.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.platinumlist.sample.remote.Api
import com.platinumlist.sample.remote.data.common.Storage
import com.platinumlist.sample.remote.data.request.TokenRequest
import com.platinumlist.sample.ui.Router
import com.platinumlist.sdk.R
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class LoginFragment : Fragment(), KodeinAware {

    private val _parentKodein: Kodein by closestKodein()
    override val kodein: Kodein = Kodein.lazy {
        extend(_parentKodein)
    }
    override val kodeinTrigger = KodeinTrigger()

    private lateinit var router: Router

    private val api: Api by instance()

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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGo.setOnClickListener {
            val login = etLogin.text.toString()
            val pass = etPassword.text.toString()

            TokenRequest(
                login, pass
            ).let {
                GlobalScope.launch {
                    api.getTokenAsync(it).await().let {
                        if (it.code() == 201) {
                            it.body()?.data?.accessToken?.let {
                                Storage.token = it

                                router.navigateToShowList()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireActivity(),
                                    "Invalid data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}