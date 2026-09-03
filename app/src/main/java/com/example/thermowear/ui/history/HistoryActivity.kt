package com.example.thermowear.ui.history

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.ui.devices.DevicesActivity
import com.example.thermowear.ui.alerts.AlertsActivity
import com.example.thermowear.ui.settings.SettingsActivity

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
        val navAlerts = findViewById<LinearLayout>(R.id.navAlerts)
        navAlerts.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AlertsActivity::class.java
                )
            )
        }
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)
        navSettings.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }

        /* Marcar Seleccion de Navbar */
        val bgNavHistory = findViewById<FrameLayout>(R.id.bgNavHistory)
        val iconNavHistory = findViewById<ImageView>(R.id.iconNavHistory)
        val txtNavHistory = findViewById<TextView>(R.id.txtNavHistory)

        bgNavHistory.setBackgroundResource(R.drawable.bg_nav_selected)

        iconNavHistory.setColorFilter(
            getColor(R.color.tw_text)
        )

        txtNavHistory.setTextColor(
            getColor(R.color.tw_text)
        )
    }
}