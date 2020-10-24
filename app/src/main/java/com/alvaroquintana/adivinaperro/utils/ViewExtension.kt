package com.alvaroquintana.adivinaperro.utils

import android.view.View
import com.alvaroquintana.adivinaperro.utils.listener.SafeClickListener

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}