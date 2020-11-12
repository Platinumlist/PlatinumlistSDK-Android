package com.platinumlist.sample.ui.showlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.platinumlist.sample.remote.Api
import com.platinumlist.sample.ui.Router
import com.platinumlist.sample.ui.showlist.common.ShowAdapter
import com.platinumlist.sample.ui.showlist.common.ShowUI
import com.platinumlist.sdk.R
import kotlinx.android.synthetic.main.fragment_show_list.*
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class ShowListFragment : Fragment(), KodeinAware {

    private val _parentKodein: Kodein by closestKodein()
    override val kodein: Kodein = Kodein.lazy {
        extend(_parentKodein)
    }
    override val kodeinTrigger = KodeinTrigger()

    private lateinit var router: Router

    private val api: Api by instance()

    private val adapter: ShowAdapter by lazy {
        ShowAdapter(this::onItemClicked)
    }

    private var searchJob: Job? = null

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
        return inflater.inflate(R.layout.fragment_show_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.adapter = adapter

        search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchJob?.cancel()
                searchJob = GlobalScope.launch {
                    api.searchAsync(
                        scope = "partner-sale.event-tickets",
                        hasTickets = 1,
                        perPage = 100,
                        search = v.text.toString()
                    ).await().takeIf {
                        it.code() == 200
                    }?.let {
                        it.body()?.data
                    }?.map {
                        ShowUI(it.id ?: -1L)
                    }?.let {
                        withContext(Dispatchers.Main) {
                            adapter.data = it
                        }
                    }
                }
                true
            } else {
                false
            }
        }

        GlobalScope.launch {
            api.getShowListAsync(
                scope = "partner-sale.event-tickets",
                hasTickets = 1,
                perPage = 100
            ).await().takeIf {
                it.code() == 200
            }?.let {
                it.body()?.data
            }?.map {
                ShowUI(it.id ?: -1L)
            }?.let {
                withContext(Dispatchers.Main) {
                    adapter.data = it
                }
            }
        }
    }

    private fun onItemClicked(item: ShowUI) {
        router.navigateToSdk(item.id)
    }
}