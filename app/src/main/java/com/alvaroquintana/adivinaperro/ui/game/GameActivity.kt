package com.alvaroquintana.adivinaperro.ui.game

import android.content.Intent
import android.os.Bundle
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.base.BaseActivity
import com.alvaroquintana.adivinaperro.common.startActivity
import com.alvaroquintana.adivinaperro.ui.select.SelectActivity
import kotlinx.android.synthetic.main.app_bar_layout.*


class GameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerGame, GameFragment.newInstance())
                .commitNow()
        }

        btnBack.setOnClickListener {
            startActivity<SelectActivity> {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        toolbarTitle.text = getString(R.string.game_screen_title)
    }
}