package com.example.thermowear.ui.alerts

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.data.AlertManager
import com.example.thermowear.data.DeviceManager
import com.example.thermowear.model.AlertType
import com.example.thermowear.model.ThermoWearAlert
import com.example.thermowear.model.ThermoWearDevice
import com.example.thermowear.model.ThermoWearVersion
import com.example.thermowear.ui.devices.DevicesActivity
import com.example.thermowear.ui.history.HistoryActivity
import com.example.thermowear.ui.settings.SettingsActivity
import com.example.thermowear.util.ScreenTransitions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlertsActivity : AppCompatActivity() {

    private lateinit var alertsContainer:
            LinearLayout

    private lateinit var emptyAlertsState:
            LinearLayout

    private lateinit var btnClearAlerts:
            MaterialButton


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        setContentView(
            R.layout.activity_alerts
        )


        bindViews()

        configureNavigation()

        configureBottomNavigation()

        configureDemoButtons()

        configureClearButton()
    }


    override fun onResume() {

        super.onResume()


        /*
         * Al entrar en esta pantalla,
         * las alertas se consideran vistas.
         */
        AlertManager.markAlertsAsRead(
            this
        )


        updateAlertIndicator()

        renderAlerts()
    }


    /* ============================ */
    /* VISTAS                       */
    /* ============================ */

    private fun bindViews() {

        alertsContainer =
            findViewById(
                R.id.alertsContainer
            )


        emptyAlertsState =
            findViewById(
                R.id.emptyAlertsState
            )


        btnClearAlerts =
            findViewById(
                R.id.btnClearAlerts
            )
    }


    /* ============================ */
    /* MOSTRAR ALERTAS              */
    /* ============================ */

    private fun renderAlerts() {

        alertsContainer
            .removeAllViews()


        val alerts =
            AlertManager.getAlerts(
                this
            )


        if (
            alerts.isEmpty()
        ) {

            emptyAlertsState.visibility =
                View.VISIBLE


            alertsContainer.visibility =
                View.GONE


            btnClearAlerts.visibility =
                View.GONE


            return
        }


        emptyAlertsState.visibility =
            View.GONE


        alertsContainer.visibility =
            View.VISIBLE


        btnClearAlerts.visibility =
            View.VISIBLE


        alerts.forEach {
                alert ->

            alertsContainer.addView(
                createAlertCard(
                    alert
                )
            )
        }
    }


    /* ============================ */
    /* CREAR TARJETA                */
    /* ============================ */

    private fun createAlertCard(
        alert: ThermoWearAlert
    ): MaterialCardView {

        val card =
            MaterialCardView(
                this
            )


        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        cardParams.bottomMargin =
            14.dp


        card.layoutParams =
            cardParams


        card.radius =
            16.dp.toFloat()


        card.cardElevation =
            0f


        card.strokeWidth =
            1.dp


        card.setStrokeColor(
            when (
                alert.type
            ) {

                AlertType.HIGH_TEMPERATURE ->

                    getColor(
                        R.color.tw_warning_red_light
                    )


                AlertType.DEVICE_DISCONNECTED ->

                    getColor(
                        R.color.tw_warning_blue_light
                    )
            }
        )


        card.setCardBackgroundColor(
            getColor(
                R.color.white
            )
        )


        val content =
            LinearLayout(
                this
            )


        content.orientation =
            LinearLayout.HORIZONTAL


        content.setPadding(
            18.dp,
            18.dp,
            18.dp,
            18.dp
        )


        /* ======================== */
        /* ICONO                    */
        /* ======================== */

        val iconCard =
            MaterialCardView(
                this
            )


        val iconCardParams =
            LinearLayout.LayoutParams(
                52.dp,
                52.dp
            )


        iconCard.layoutParams =
            iconCardParams


        iconCard.radius =
            14.dp.toFloat()


        iconCard.cardElevation =
            0f


        iconCard.setCardBackgroundColor(

            when (
                alert.type
            ) {

                AlertType.HIGH_TEMPERATURE ->

                    getColor(
                        R.color.tw_warning_red_light
                    )


                AlertType.DEVICE_DISCONNECTED ->

                    getColor(
                        R.color.tw_warning_blue_light
                    )
            }
        )


        val icon =
            ImageView(
                this
            )


        icon.setPadding(
            13.dp,
            13.dp,
            13.dp,
            13.dp
        )


        when (
            alert.type
        ) {

            AlertType.HIGH_TEMPERATURE -> {

                icon.setImageResource(
                    R.drawable.ic_thermostat
                )


                icon.setColorFilter(
                    getColor(
                        R.color.tw_warning_red
                    )
                )
            }


            AlertType.DEVICE_DISCONNECTED -> {

                icon.setImageResource(
                    R.drawable.ic_bluetooth_off
                )


                icon.setColorFilter(
                    getColor(
                        R.color.tw_warning_blue
                    )
                )
            }
        }


        iconCard.addView(
            icon
        )


        content.addView(
            iconCard
        )


        /* ======================== */
        /* INFORMACIÓN              */
        /* ======================== */

        val information =
            LinearLayout(
                this
            )


        information.orientation =
            LinearLayout.VERTICAL


        val informationParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )


        informationParams.marginStart =
            16.dp


        information.layoutParams =
            informationParams


        val title =
            TextView(
                this
            )


        title.text =
            alert.title


        title.textSize =
            18f


        title.setTypeface(
            title.typeface,
            Typeface.BOLD
        )


        title.setTextColor(
            getColor(
                R.color.tw_text
            )
        )


        information.addView(
            title
        )


        val message =
            TextView(
                this
            )


        message.text =
            alert.message


        message.textSize =
            13f


        message.setTextColor(
            getColor(
                R.color.tw_text_secondary
            )
        )


        val messageParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        messageParams.topMargin =
            5.dp


        message.layoutParams =
            messageParams


        information.addView(
            message
        )


        val date =
            TextView(
                this
            )


        date.text =
            formatAlertTime(
                alert.timestampMillis
            )


        date.textSize =
            11f


        date.setTextColor(
            getColor(
                R.color.tw_text_light
            )
        )


        val dateParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        dateParams.topMargin =
            10.dp


        date.layoutParams =
            dateParams


        information.addView(
            date
        )


        content.addView(
            information
        )


        card.addView(
            content
        )


        return card
    }


    /* ============================ */
    /* DEMOSTRACIÓN                 */
    /* ============================ */

    private fun configureDemoButtons() {

        val btnDemoDisconnect =
            findViewById<MaterialButton>(
                R.id.btnDemoDisconnect
            )


        val btnDemoTemperature =
            findViewById<MaterialButton>(
                R.id.btnDemoTemperature
            )


        btnDemoDisconnect
            .setOnClickListener {

                createDemoDisconnectAlert()
            }


        btnDemoTemperature
            .setOnClickListener {

                createDemoTemperatureAlert()
            }
    }


    private fun createDemoDisconnectAlert() {

        val device =
            DeviceManager
                .getDevices(
                    this
                )
                .firstOrNull()


        val deviceName =
            if (
                device != null
            ) {

                getForgeDeviceName(
                    device
                )

            } else {

                "Chamarra Forge"
            }


        AlertManager
            .addDisconnectedAlert(

                context =
                    this,

                deviceId =
                    device?.id,

                deviceName =
                    deviceName
            )


        Toast.makeText(
            this,
            "Alerta de desconexión generada",
            Toast.LENGTH_SHORT
        ).show()


        updateAlertIndicator()

        renderAlerts()
    }


    private fun createDemoTemperatureAlert() {

        val device =
            DeviceManager
                .getDevices(
                    this
                )
                .firstOrNull {

                    it.version ==
                            ThermoWearVersion.V2
                }


        if (
            device == null
        ) {

            Toast.makeText(
                this,
                "Añade una chamarra Forge V2 para probar esta alerta.",
                Toast.LENGTH_LONG
            ).show()


            return
        }


        /*
         * 42 °C se usa únicamente para
         * demostrar visualmente el evento.
         *
         * Cuando integremos el sensor real,
         * este valor llegará desde el ESP32.
         */
        AlertManager
            .addHighTemperatureAlert(

                context =
                    this,

                deviceId =
                    device.id,

                deviceName =
                    getForgeDeviceName(
                        device
                    ),

                temperatureC =
                    42
            )


        Toast.makeText(
            this,
            "Alerta de temperatura generada",
            Toast.LENGTH_SHORT
        ).show()


        updateAlertIndicator()

        renderAlerts()
    }


    /* ============================ */
    /* NOMBRE VISUAL FORGE          */
    /* ============================ */

    private fun getForgeDeviceName(
        device: ThermoWearDevice
    ): String {

        return when (
            device.version
        ) {

            ThermoWearVersion.V1 ->
                "Chamarra Forge V1"

            ThermoWearVersion.V2 ->
                "Chamarra Forge V2"
        }
    }


    /* ============================ */
    /* BORRAR HISTORIAL             */
    /* ============================ */

    private fun configureClearButton() {

        btnClearAlerts
            .setOnClickListener {

                showClearAlertDialog()
            }
    }


    private fun showClearAlertDialog() {

        AlertDialog.Builder(
            this
        )
            .setTitle(
                "Borrar alertas"
            )
            .setMessage(
                "¿Deseas eliminar el historial de alertas?"
            )
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Borrar"
            ) {
                    _,
                    _ ->


                AlertManager.clearAlerts(
                    this
                )


                updateAlertIndicator()

                renderAlerts()


                Toast.makeText(
                    this,
                    "Historial de alertas eliminado",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }


    /* ============================ */
    /* FECHA                        */
    /* ============================ */

    private fun formatAlertTime(
        timestampMillis: Long
    ): String {

        val difference =
            System.currentTimeMillis() -
                    timestampMillis


        val minute =
            60_000L


        val hour =
            60L *
                    minute


        val day =
            24L *
                    hour


        return when {

            difference <
                    minute ->

                "Ahora"


            difference <
                    hour -> {

                val minutes =
                    difference /
                            minute


                "Hace $minutes min"
            }


            difference <
                    day -> {

                val hours =
                    difference /
                            hour


                "Hace $hours h"
            }


            else -> {

                val formatter =
                    SimpleDateFormat(
                        "dd MMM, HH:mm",
                        Locale(
                            "es",
                            "BO"
                        )
                    )


                formatter.format(
                    Date(
                        timestampMillis
                    )
                )
            }
        }
    }


    /* ============================ */
    /* NAVEGACIÓN                   */
    /* ============================ */

    private fun configureNavigation() {

        val navHome =
            findViewById<LinearLayout>(
                R.id.navHome
            )


        val navHistory =
            findViewById<LinearLayout>(
                R.id.navHistory
            )


        val navSettings =
            findViewById<LinearLayout>(
                R.id.navSettings
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


            ScreenTransitions.openFade(
                this,
                intent
            )
        }


        navHistory.setOnClickListener {

            ScreenTransitions.openFade(
                this,
                Intent(
                    this,
                    HistoryActivity::class.java
                )
            )
        }


        navSettings.setOnClickListener {

            ScreenTransitions.openFade(
                this,
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }


    override fun finish() {

        super.finish()

        ScreenTransitions.closeFade(
            this
        )
    }


    /* ============================ */
    /* ALERTAS SELECCIONADO         */
    /* ============================ */

    private fun configureBottomNavigation() {

        val bgNavAlerts =
            findViewById<LinearLayout>(
                R.id.bgNavAlerts
            )


        val iconNavAlerts =
            findViewById<ImageView>(
                R.id.iconNavAlerts
            )


        val txtNavAlerts =
            findViewById<TextView>(
                R.id.txtNavAlerts
            )


        bgNavAlerts.setBackgroundResource(
            R.drawable.bg_nav_selected
        )


        iconNavAlerts.setColorFilter(
            getColor(
                R.color.tw_text
            )
        )


        txtNavAlerts.setTextColor(
            getColor(
                R.color.tw_text
            )
        )
    }


    /* ============================ */
    /* PUNTO ROJO                   */
    /* ============================ */

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


    /* ============================ */
    /* DP                           */
    /* ============================ */

    private val Int.dp: Int

        get() =
            (
                    this *
                            resources
                                .displayMetrics
                                .density
                    ).toInt()
}