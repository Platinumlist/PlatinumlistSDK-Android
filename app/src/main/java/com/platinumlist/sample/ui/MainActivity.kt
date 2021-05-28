package com.platinumlist.sample.ui

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.platinumlist.sample.ui.login.LoginFragment
import com.platinumlist.sample.ui.result.ResultFragment
import com.platinumlist.sample.ui.sdk.SdkFragment
import com.platinumlist.sample.ui.settings.SettingsListFragment
import com.platinumlist.sample.ui.showlist.ShowListFragment
import com.platinumlist.sample.ui.updorder.UpdateOrderFragment
import com.platinumlist.sdk.R
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.closestKodein

class MainActivity : AppCompatActivity(), Router, KodeinAware {

    private val _parentKodein: Kodein by closestKodein()
    override val kodein: Kodein = Kodein.lazy {
        extend(_parentKodein)
    }
    override val kodeinTrigger = KodeinTrigger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kodeinTrigger.trigger()

        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .commit()
    }

    override fun navigateToShowList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ShowListFragment())
            .commit()
    }

    override fun navigateToSettingsList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SettingsListFragment())
            .commit()
    }

    override fun navigateToSdk(eventId: Long, eventShowId: Long) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SdkFragment.create(eventId, eventShowId))
            .addToBackStack("SdkFragment")
            .commit()
    }

    override fun navigateToResult(result: Boolean) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ResultFragment.create(result))
            .commit()
    }

    override fun navigateToUpdateOrder() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, UpdateOrderFragment())
            .commit()
    }
}