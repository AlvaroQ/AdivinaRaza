package com.alvaroquintana.adivinaperro.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.preference.PreferenceManager
import com.alvaroquintana.adivinaperro.R

fun playSuccessSound(context: Context) {
    if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)) {
        MediaPlayer.create(context, R.raw.success).start()
    }
}

fun playFailSound(context: Context) {
    if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)) {
        MediaPlayer.create(context, R.raw.fail).start()
    }
}

fun playBarkSound(context: Context) {
    if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)) {
        MediaPlayer.create(context, R.raw.ladrido).start()
    }
}
