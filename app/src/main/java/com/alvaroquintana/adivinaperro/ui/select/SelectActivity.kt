package com.alvaroquintana.adivinaperro.ui.select

import android.os.Bundle
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.base.BaseActivity

class SelectActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerSelect, SelectFragment.newInstance())
                .commitNow()
        }
    }
}