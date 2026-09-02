package com.example.thermowear.ui.history

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.ui.devices.DevicesActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history)

        val navHome = findViewById<LinearLayout>(R.id.navHome)

        navHome.setOnClickListener {

            val intent = Intent(
                this,
                DevicesActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)
        }
    }
}