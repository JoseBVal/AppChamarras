package com.example.thermowear.ui.devices

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.ui.adddevice.AddDeviceActivity
import com.example.thermowear.ui.alerts.AlertsActivity
import com.example.thermowear.ui.control.ControlDeviceActivity
import com.example.thermowear.ui.history.HistoryActivity
import com.example.thermowear.ui.settings.SettingsActivity
import com.google.android.material.button.MaterialButton

class DevicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_devices)

        /* ============================= */
        /* BOTONES                       */
        /* ============================= */

        val btnAdd = findViewById<MaterialButton>(
            R.id.btnAddDevice
        )

        val btnControl = findViewById<MaterialButton>(
            R.id.btnControl
        )

        /* ============================= */
        /* NAVEGACIÓN INFERIOR           */
        /* ============================= */

        val navHistory = findViewById<LinearLayout>(
            R.id.navHistory
        )

        val navAlerts = findViewById<LinearLayout>(
            R.id.navAlerts
        )

        val navSettings = findViewById<LinearLayout>(
            R.id.navSettings
        )


        /* ============================= */
        /* AÑADIR DISPOSITIVO            */
        /* ============================= */

        btnAdd.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddDeviceActivity::class.java
                )
            )
        }


        /* ============================= */
        /* CONTROLAR DISPOSITIVO         */
        /* ============================= */

        btnControl.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ControlDeviceActivity::class.java
                )
            )
        }


        /* ============================= */
        /* HISTORIAL                     */
        /* ============================= */

        navHistory.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    HistoryActivity::class.java
                )
            )
        }


        /* ============================= */
        /* ALERTAS                       */
        /* ============================= */

        navAlerts.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AlertsActivity::class.java
                )
            )
        }


        /* ============================= */
        /* AJUSTES                       */
        /* ============================= */

        navSettings.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }


        /* ============================= */
        /* NAVBAR - INICIO ACTIVO        */
        /* ============================= */

        configureBottomNavigation()
    }


    private fun configureBottomNavigation() {

        val bgNavHome = findViewById<LinearLayout>(
            R.id.bgNavHome
        )

        val iconNavHome = findViewById<ImageView>(
            R.id.iconNavHome
        )

        val txtNavHome = findViewById<TextView>(
            R.id.txtNavHome
        )

        bgNavHome.setBackgroundResource(
            R.drawable.bg_nav_selected
        )

        iconNavHome.setColorFilter(
            getColor(R.color.tw_text)
        )

        txtNavHome.setTextColor(
            getColor(R.color.tw_text)
        )
    }
}