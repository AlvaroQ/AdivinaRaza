package com.alvaroquintana.adivinaperro.utils

import android.media.MediaPlayer
import android.view.View
import android.view.animation.AnimationUtils
import androidx.preference.PreferenceManager
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.utils.listener.SafeClickListener


fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_xy_collapse))

        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)) {
            MediaPlayer.create(context, R.raw.click).start()
        }
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}