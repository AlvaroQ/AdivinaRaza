package com.alvaroquintana.adivinaperro.ui.result

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.alvaroquintana.adivinaperro.BuildConfig
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.base.BaseActivity
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.ui.select.SelectActivity
import com.alvaroquintana.adivinaperro.utils.log
import com.alvaroquintana.adivinaperro.utils.setSafeOnClickListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.app_bar_layout.*

class ResultActivity : BaseActivity() {
    private lateinit var rewardedAd: RewardedAd
    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerResult, ResultFragment.newInstance())
                .commitNow()
        }

        activity = this
        btnBack.setSafeOnClickListener {
            startActivity<SelectActivity> {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        toolbarTitle.text = getString(R.string.resultado_screen_title)
        layoutLife.visibility = View.GONE
    }

    fun showAd(){
        rewardedAd = RewardedAd(this, getString(R.string.BONIFICADO_GAME_OVER))
        val adLoadCallback: RewardedAdLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                rewardedAd.show(activity, null)
            }

            override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                //FirebaseCrashlytics.getInstance().recordException(Throwable(adError.message))
                log("ResultActivity - loadAd", "Ad failed to load.")
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }

}