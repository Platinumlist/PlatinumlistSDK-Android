package com.platinumlist.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.platinumlist.sample.ui.login.LoginFragment
import com.platinumlist.sample.ui.result.ResultFragment
import com.platinumlist.sample.ui.sdk.SdkFragment
import com.platinumlist.sample.ui.showlist.ShowListFragment
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

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .commit()
    }

    override fun navigateToShowList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ShowListFragment())
            .commit()
    }

    override fun navigateToSdk(id: Long) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SdkFragment.create(id))
            .addToBackStack("SdkFragment")
            .commit()
    }

    override fun navigateToResult(result: Boolean) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ResultFragment.create(result))
            .commit()
    }
}