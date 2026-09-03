package com.example.thermowear.ui.devices

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.thermowear.ui.adddevice.AddDeviceActivity
import com.example.thermowear.ui.control.ControlDeviceActivity
import com.example.thermowear.ui.history.HistoryActivity
import com.example.thermowear.ui.alerts.AlertsActivity
import com.example.thermowear.ui.settings.SettingsActivity

class DevicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_devices)

        val btnAdd = findViewById<FloatingActionButton>(R.id.btnAddDevice)
        val btnControl = findViewById<MaterialButton>(R.id.btnControl)
        val navHistory = findViewById<LinearLayout>(R.id.navHistory)
        val navAlerts = findViewById<LinearLayout>(R.id.navAlerts)
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)

        btnAdd.setOnClickListener {
            val intent = Intent(
                this,
                AddDeviceActivity::class.java
            )

            startActivity(intent)
        }

        btnControl.setOnClickListener {

            val intent = Intent(
                this,
                ControlDeviceActivity::class.java
            )

            startActivity(intent)
        }

        navHistory.setOnClickListener {

            val intent = Intent(
                this,
                HistoryActivity::class.java
            )

            startActivity(intent)
        }

        navAlerts.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AlertsActivity::class.java
                )
            )
        }

        navSettings.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }


        /* Marcar Seleccion de Navbar */
        val bgNavHistory = findViewById<FrameLayout>(R.id.bgNavHome)
        val iconNavHistory = findViewById<ImageView>(R.id.iconNavHome)
        val txtNavHistory = findViewById<TextView>(R.id.txtNavHome)

        bgNavHistory.setBackgroundResource(R.drawable.bg_nav_selected)

        iconNavHistory.setColorFilter(
            getColor(R.color.tw_text)
        )

        txtNavHistory.setTextColor(
            getColor(R.color.tw_text)
        )
    }
}