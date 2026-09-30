package com.example.thermowear.util

import android.app.Activity
import android.content.Intent
import com.example.thermowear.R

@Suppress("DEPRECATION")
object ScreenTransitions {

    fun openSlide(
        activity: Activity,
        intent: Intent
    ) {
        activity.startActivity(intent)
        activity.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    fun openFade(
        activity: Activity,
        intent: Intent
    ) {
        activity.startActivity(intent)
        activity.overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
    }

    fun openModule(
        activity: Activity,
        intent: Intent
    ) {
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        )
        openFade(activity, intent)
    }

    fun closeSlide(
        activity: Activity
    ) {
        activity.overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }

    fun closeFade(
        activity: Activity
    ) {
        activity.overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
    }
}
