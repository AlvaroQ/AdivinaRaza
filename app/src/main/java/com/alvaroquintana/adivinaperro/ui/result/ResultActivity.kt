package com.alvaroquintana.adivinaperro.ui.result

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
import kotlinx.android.synthetic.main.app_bar_layout.*

class ResultActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerResult, ResultFragment.newInstance())
                .commitNow()
        }

        btnBack.setSafeOnClickListener {
            startActivity<SelectActivity> {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        toolbarTitle.text = getString(R.string.resultado_screen_title)
        imageShare.visibility = View.VISIBLE
        imageShare.setSafeOnClickListener {
            shareApp()
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareMessage = getString(R.string.share_message)
            shareMessage =
                """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one)))
        } catch (e: Exception) {
            log(getString(R.string.share), e.toString())
        }
    }
}