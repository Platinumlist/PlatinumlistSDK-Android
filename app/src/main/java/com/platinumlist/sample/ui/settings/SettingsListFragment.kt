package com.platinumlist.sample.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.platinumlist.sample.ui.Router
import com.platinumlist.sdk.R
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.closestKodein
import com.platinumlist.sample.remote.Api
import com.platinumlist.sample.remote.data.common.Storage
import kotlinx.android.synthetic.main.fragment_settings_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.platinumlist.sdk.common.Language
import com.platinumlist.sdk.common.SdkType
import org.kodein.di.generic.instance

class SettingsListFragment : Fragment(), KodeinAware {

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
        return inflater.inflate(R.layout.fragment_settings_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sdkTypeList = listOf(SdkType.PURCHASE.typeName, SdkType.TICKET_OFFICE.typeName, SdkType.SELECT_TICKETS.typeName)
        val sdkTypeAdapter = ArrayAdapter(activity?.applicationContext, R.layout.support_simple_spinner_dropdown_item, sdkTypeList)
        sSdkType.adapter = sdkTypeAdapter

        etEventId.setText("11634")

        val languageList = listOf(Language.ENGLISH.fullName, Language.ARABIC.fullName)
        val languageAdapter = ArrayAdapter(activity?.applicationContext, R.layout.support_simple_spinner_dropdown_item, languageList)
        sLanguage.adapter = languageAdapter

        btnGo.setOnClickListener{
            GlobalScope.launch {
                if (etEventId.text.isNotEmpty() || etEventShowId.text.isNotEmpty()) {
                    Storage.sdkType = when (sSdkType.selectedItem) {
                        SdkType.PURCHASE.typeName -> {
                            SdkType.PURCHASE
                        }
                        SdkType.TICKET_OFFICE.typeName -> {
                            SdkType.TICKET_OFFICE
                        }
                        else -> {
                            SdkType.SELECT_TICKETS
                        }
                    }

                    var eventId: Long = if (etEventId.text.isNotEmpty()) etEventId.text.toString().toLong() else -1L
                    var eventShowId: Long = if (etEventShowId.text.isNotEmpty()) etEventShowId.text.toString().toLong() else -1L

                    Storage.userPhoneNumber = etUserPhoneNumber.text.toString()

                    Storage.isEventShowSetting = if (rbEventShowSetting.isChecked) 1 else 0

                    Storage.lang = if (sLanguage.selectedItem == Language.ENGLISH.fullName) Language.ENGLISH.shortName else Language.ARABIC.shortName

                    router.navigateToSdk(eventId, eventShowId)

                    println(1)
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireActivity(), "Incorrect event id", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}