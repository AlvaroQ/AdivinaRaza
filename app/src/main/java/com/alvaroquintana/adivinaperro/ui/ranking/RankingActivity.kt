package com.alvaroquintana.adivinaperro.ui.ranking

import android.os.Bundle
import android.view.View
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.base.BaseActivity
import com.alvaroquintana.adivinaperro.utils.setSafeOnClickListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.app_bar_layout.*
import kotlinx.android.synthetic.main.ranking_activity.*

class RankingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerResult, RankingFragment.newInstance())
                .commitNow()
        }
        loadAd(adView)

        btnBack.setSafeOnClickListener {
            finish()
        }
        toolbarTitle.text = getString(R.string.ranking_screen_title)
        layoutLife.visibility = View.GONE
    }

    private fun loadAd(mAdView: AdView) {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}