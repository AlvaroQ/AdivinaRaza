package com.alvaroquintana.adivinaperro.managers

import android.content.Context
import android.media.MediaPlayer
import androidx.preference.PreferenceManager
import com.alvaroquintana.adivinaperro.R

class AndroidSoundPlayer(private val context: Context) : SoundPlayer {
    private fun isSoundEnabled(): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound", true)

    override fun playSuccess() {
        if (isSoundEnabled()) MediaPlayer.create(context, R.raw.success)?.start()
    }

    override fun playFail() {
        if (isSoundEnabled()) MediaPlayer.create(context, R.raw.fail)?.start()
    }

    override fun playBark() {
        if (isSoundEnabled()) MediaPlayer.create(context, R.raw.ladrido)?.start()
    }
}
