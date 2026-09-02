package com.example.thermowear.ui.alerts

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.ui.devices.DevicesActivity
import com.example.thermowear.ui.history.HistoryActivity

class AlertsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_alerts)

        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navHistory = findViewById<LinearLayout>(R.id.navHistory)

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

        navHistory.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    HistoryActivity::class.java
                )
            )
        }
        /* Marcar Seleccion de Navbar */
        val bgNavHistory = findViewById<FrameLayout>(R.id.bgNavAlerts)
        val iconNavHistory = findViewById<ImageView>(R.id.iconNavAlerts)
        val txtNavHistory = findViewById<TextView>(R.id.txtNavAlerts)

        bgNavHistory.setBackgroundResource(R.drawable.bg_nav_selected)

        iconNavHistory.setColorFilter(
            getColor(R.color.tw_text)
        )

        txtNavHistory.setTextColor(
            getColor(R.color.tw_text)
        )
    }
}