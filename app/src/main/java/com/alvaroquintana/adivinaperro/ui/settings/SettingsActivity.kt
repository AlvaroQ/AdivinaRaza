package com.alvaroquintana.adivinaperro.ui.settings

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.base.BaseActivity
import com.alvaroquintana.adivinaperro.common.viewBinding
import com.alvaroquintana.adivinaperro.databinding.SettingsActivityBinding
import com.alvaroquintana.adivinaperro.utils.setSafeOnClickListener


class SettingsActivity : BaseActivity() {
    private val binding by viewBinding(SettingsActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setupToolbar()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        binding.appBar.toolbarTitle.text = getString(R.string.settings)
        binding.appBar.layoutLife.visibility = View.GONE
        binding.appBar.btnBack.setSafeOnClickListener { finishAfterTransition() }
    }
}