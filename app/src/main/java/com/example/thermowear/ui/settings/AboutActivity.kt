package com.example.thermowear.ui.settings

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.util.ScreenTransitions

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)

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
