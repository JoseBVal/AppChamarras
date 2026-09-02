package com.example.thermowear.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.ui.devices.DevicesActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        window.decorView.postDelayed({
            val intent = Intent(this, DevicesActivity::class.java)
            startActivity(intent)
            finish()
        }, 2500)
    }
}