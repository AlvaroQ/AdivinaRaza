package com.alvaroquintana.adivinaperro.ui.info

import android.os.Bundle
import android.view.View
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.base.BaseActivity
import com.alvaroquintana.adivinaperro.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.app_bar_layout.*

class InfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerInfo, InfoFragment.newInstance())
                .commitNow()
        }

        btnBack.setSafeOnClickListener { finishAfterTransition() }
        toolbarTitle.text = getString(R.string.info_title)
        layoutLife.visibility = View.GONE
    }
}