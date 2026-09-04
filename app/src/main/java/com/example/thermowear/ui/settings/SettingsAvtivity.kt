package com.example.thermowear.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.view.View
import com.example.thermowear.data.AlertManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.ui.alerts.AlertsActivity
import com.example.thermowear.ui.devices.DevicesActivity
import com.example.thermowear.ui.history.HistoryActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navHistory = findViewById<LinearLayout>(R.id.navHistory)
        val navAlerts = findViewById<LinearLayout>(R.id.navAlerts)

        val switchNotifications =
            findViewById<SwitchMaterial>(R.id.switchNotifications)

        val switchSound =
            findViewById<SwitchMaterial>(R.id.switchSound)

        val switchVibration =
            findViewById<SwitchMaterial>(R.id.switchVibration)

        val optionDevice =
            findViewById<LinearLayout>(R.id.optionDevice)

        val optionAbout =
            findViewById<LinearLayout>(R.id.optionAbout)

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

        navAlerts.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AlertsActivity::class.java
                )
            )
        }

        optionDevice.setOnClickListener {
            Toast.makeText(
                this,
                "Configuración del dispositivo",
                Toast.LENGTH_SHORT
            ).show()
        }

        optionAbout.setOnClickListener {
            Toast.makeText(
                this,
                "ThermoWear v1.0",
                Toast.LENGTH_SHORT
            ).show()
        }

        switchNotifications.setOnCheckedChangeListener { _, enabled ->
            Toast.makeText(
                this,
                if (enabled)
                    "Notificaciones activadas"
                else
                    "Notificaciones desactivadas",
                Toast.LENGTH_SHORT
            ).show()
        }

        switchSound.setOnCheckedChangeListener { _, enabled ->
            Toast.makeText(
                this,
                if (enabled)
                    "Sonido activado"
                else
                    "Sonido desactivado",
                Toast.LENGTH_SHORT
            ).show()
        }

        switchVibration.setOnCheckedChangeListener { _, enabled ->
            Toast.makeText(
                this,
                if (enabled)
                    "Vibración activada"
                else
                    "Vibración desactivada",
                Toast.LENGTH_SHORT
            ).show()
        }
        configureBottomNavigation()
    }
    override fun onResume() {
        super.onResume()

        updateAlertIndicator()
    }

    private fun configureBottomNavigation() {

        val bgNavSettings = findViewById<LinearLayout>(
            R.id.bgNavSettings
        )

        val iconNavSettings = findViewById<ImageView>(
            R.id.iconNavSettings
        )

        val txtNavSettings = findViewById<TextView>(
            R.id.txtNavSettings
        )

        bgNavSettings.setBackgroundResource(
            R.drawable.bg_nav_selected
        )

        iconNavSettings.setColorFilter(
            getColor(R.color.tw_text)
        )

        txtNavSettings.setTextColor(
            getColor(R.color.tw_text)
        )
    }
    private fun updateAlertIndicator() {

        val alertDot =
            findViewById<View>(
                R.id.alertDot
            )

        alertDot.visibility =
            if (
                AlertManager.hasUnreadAlerts(
                    this
                )
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }
}