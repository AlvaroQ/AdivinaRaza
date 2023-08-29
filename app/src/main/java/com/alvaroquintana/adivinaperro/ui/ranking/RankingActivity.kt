package com.alvaroquintana.adivinaperro.ui.ranking

import android.os.Bundle
import android.view.View
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.base.BaseActivity
import com.alvaroquintana.adivinaperro.common.viewBinding
import com.alvaroquintana.adivinaperro.databinding.RankingActivityBinding
import com.alvaroquintana.adivinaperro.utils.setSafeOnClickListener
import com.alvaroquintana.adivinaperro.utils.showBanner
import com.google.android.gms.ads.MobileAds

class RankingActivity : BaseActivity() {
    private val binding by viewBinding(RankingActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerResult, RankingFragment.newInstance())
                .commitNow()
        }

        binding.appBar.btnBack.setSafeOnClickListener { finish() }
        binding.appBar.toolbarTitle.text = getString(R.string.ranking_screen_title)
        binding.appBar.layoutLife.visibility = View.GONE

        MobileAds.initialize(this)
        showBanner(true, binding.adViewRanking)
    }
}