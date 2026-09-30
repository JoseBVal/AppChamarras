package com.example.thermowear.ui.splash

import android.content.Intent
import android.os.Bundle
import com.example.thermowear.R
import com.example.thermowear.ui.devices.DevicesActivity
import com.example.thermowear.ui.ThermoWearActivity

class SplashActivity : ThermoWearActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        com.example.thermowear.util.WindowInsetsHelper.apply(this)

        window.decorView.postDelayed({
            val intent = Intent(this, DevicesActivity::class.java)
            startActivity(intent)
            finish()
        }, 2500)
    }
}
