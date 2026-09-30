package com.example.thermowear.ui.settings

import android.os.Bundle
import android.widget.ImageView
import com.example.thermowear.R
import com.example.thermowear.util.ScreenTransitions
import com.example.thermowear.ui.ThermoWearActivity

class AboutActivity : ThermoWearActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)

        com.example.thermowear.util.WindowInsetsHelper.apply(this)

        val btnBack =
            findViewById<ImageView>(
                R.id.btnBack
            )

        btnBack.setOnClickListener {
            finish()
        }
    }
    override fun finish() {
        super.finish()
        ScreenTransitions.closeSlide(this)
    }
}
