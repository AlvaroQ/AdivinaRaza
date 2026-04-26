package com.alvaroquintana.adivinaperro.managers

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class AndroidSettings(context: Context) : Settings {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)

    override fun putInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    override fun getBoolean(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }
}
