package com.example.thermowear.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.data.AlertManager
import com.example.thermowear.data.SettingsManager
import com.example.thermowear.ui.alerts.AlertsActivity
import com.example.thermowear.ui.devices.DevicesActivity
import com.example.thermowear.ui.history.HistoryActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var switchSound: SwitchMaterial
    private lateinit var switchVibration: SwitchMaterial


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        configureViews()
        loadSettings()
        configureSettings()
        configureNavigation()
        configureBottomNavigation()
    }


    override fun onResume() {
        super.onResume()

        updateAlertIndicator()
    }


    private fun configureViews() {

        switchNotifications =
            findViewById(
                R.id.switchNotifications
            )

        switchSound =
            findViewById(
                R.id.switchSound
            )

        switchVibration =
            findViewById(
                R.id.switchVibration
            )
    }


    private fun loadSettings() {

        switchNotifications.isChecked =
            SettingsManager.areNotificationsEnabled(
                this
            )

        switchSound.isChecked =
            SettingsManager.isSoundEnabled(
                this
            )

        switchVibration.isChecked =
            SettingsManager.isVibrationEnabled(
                this
            )

        updateDependentSwitches()
    }


    private fun configureSettings() {

        switchNotifications.setOnCheckedChangeListener {
                _,
                enabled ->

            SettingsManager.setNotificationsEnabled(
                this,
                enabled
            )

            updateDependentSwitches()

            Toast.makeText(
                this,
                if (enabled) {
                    "Notificaciones ThermoWear activadas"
                } else {
                    "Notificaciones ThermoWear desactivadas"
                },
                Toast.LENGTH_SHORT
            ).show()
        }


        switchSound.setOnCheckedChangeListener {
                _,
                enabled ->

            SettingsManager.setSoundEnabled(
                this,
                enabled
            )

            Toast.makeText(
                this,
                if (enabled) {
                    "Sonido activado"
                } else {
                    "Sonido desactivado"
                },
                Toast.LENGTH_SHORT
            ).show()
        }


        switchVibration.setOnCheckedChangeListener {
                _,
                enabled ->

            SettingsManager.setVibrationEnabled(
                this,
                enabled
            )

            Toast.makeText(
                this,
                if (enabled) {
                    "Vibración activada"
                } else {
                    "Vibración desactivada"
                },
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun updateDependentSwitches() {

        val notificationsEnabled =
            switchNotifications.isChecked

        switchSound.isEnabled =
            notificationsEnabled

        switchVibration.isEnabled =
            notificationsEnabled

        switchSound.alpha =
            if (notificationsEnabled) {
                1.0f
            } else {
                0.45f
            }

        switchVibration.alpha =
            if (notificationsEnabled) {
                1.0f
            } else {
                0.45f
            }
    }


    private fun configureNavigation() {

        val navHome =
            findViewById<LinearLayout>(
                R.id.navHome
            )

        val navHistory =
            findViewById<LinearLayout>(
                R.id.navHistory
            )

        val navAlerts =
            findViewById<LinearLayout>(
                R.id.navAlerts
            )

        val optionAbout =
            findViewById<LinearLayout>(
                R.id.optionAbout
            )


        navHome.setOnClickListener {

            val intent =
                Intent(
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


        optionAbout.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AboutActivity::class.java
                )
            )
        }
    }


    private fun configureBottomNavigation() {

        val bgNavSettings =
            findViewById<LinearLayout>(
                R.id.bgNavSettings
            )

        val iconNavSettings =
            findViewById<ImageView>(
                R.id.iconNavSettings
            )

        val txtNavSettings =
            findViewById<TextView>(
                R.id.txtNavSettings
            )


        bgNavSettings.setBackgroundResource(
            R.drawable.bg_nav_selected
        )

        iconNavSettings.setColorFilter(
            getColor(
                R.color.tw_text
            )
        )

        txtNavSettings.setTextColor(
            getColor(
                R.color.tw_text
            )
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