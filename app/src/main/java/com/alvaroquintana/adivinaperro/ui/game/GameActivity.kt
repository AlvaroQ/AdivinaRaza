package com.alvaroquintana.adivinaperro.ui.game

import android.content.Intent
import android.os.Bundle
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.base.BaseActivity
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.ui.select.SelectActivity
import com.alvaroquintana.adivinaperro.utils.setSafeOnClickListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.app_bar_layout.*
import kotlinx.android.synthetic.main.game_activity.*


class GameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerGame, GameFragment.newInstance())
                .commitNow()
        }

        loadAd(adView)
        btnBack.setSafeOnClickListener {
            startActivity<SelectActivity> {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        toolbarTitle.text = getString(R.string.game_screen_title)
    }

    private fun loadAd(mAdView: AdView) {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}