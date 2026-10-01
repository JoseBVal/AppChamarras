package com.example.thermowear.ui.control

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.thermowear.R
import com.example.thermowear.bluetooth.BleManager
import com.example.thermowear.data.DeviceManager
import com.example.thermowear.data.HistorySessionTracker
import com.example.thermowear.model.ThermoWearVersion
import com.example.thermowear.model.ThermoWearZone
import com.example.thermowear.util.ScreenTransitions
import com.example.thermowear.ui.ThermoWearActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import java.util.Locale


class ControlDeviceActivity :
    ThermoWearActivity(),
    BleManager.Listener {


    private lateinit var txtDeviceName:
            TextView

    private lateinit var txtDeviceStatus:
            TextView

    private lateinit var txtDeviceVersion:
            TextView


    private lateinit var cardZonePrimary:
            MaterialCardView

    private lateinit var cardZoneSecondary:
            MaterialCardView


    private lateinit var txtPrimaryZoneName:
            TextView

    private lateinit var txtPrimaryZoneInfo:
            TextView

    private lateinit var txtSecondaryZoneName:
            TextView

    private lateinit var txtSecondaryZoneInfo:
            TextView


    private lateinit var txtSelectedZone:
            TextView


    private lateinit var containerTemperature:
            LinearLayout

    private lateinit var txtCurrentTemperature:
            TextView

    private var currentTemperatureLabel =
        "-- °C"

    private var sensorFaultNoticeShown = false


    private lateinit var txtHeatLevel:
            TextView

    private lateinit var txtZoneStatus:
            TextView


    private lateinit var sliderIntensity:
            Slider


    private lateinit var btnApplyIntensity:
            MaterialButton

    private lateinit var btnZonePower:
            MaterialButton
    private lateinit var btnAllZonesPower:
            MaterialButton


    private lateinit var heatBack:
            View

    private lateinit var heatUpperBack:
            View

    private lateinit var heatLowerBack:
            View


    /* ============================= */
    /* DISPOSITIVO                   */
    /* ============================= */

    private var deviceId =
        ""

    private var deviceName =
        "Chamarra Forge"

    private var deviceVersion =
        "V1"

    private var deviceConnected =
        false


    /* ============================= */
    /* ZONAS                         */
    /* ============================= */

    private enum class Zone {

        BACK,

        UPPER_BACK,

        LOWER_BACK
    }


    private var pendingZonePowerZone: Zone? = null
    private var pendingZonePowerTarget: Boolean? = null
    private var pendingZonePowerLevel = 2
    private var waitingForZoneLevelAck = false
    private val controlHandler = Handler(Looper.getMainLooper())
    private val zonePowerTimeout = Runnable {
        if (pendingZonePowerZone != null) {
            clearPendingZonePower()
            Toast.makeText(this, "El ESP32 no confirmó la acción. Inténtalo de nuevo.", Toast.LENGTH_LONG).show()
            updateInterface()
        }
    }

    private var selectedZone =
        Zone.BACK


    /* ============================= */
    /* ESTADOS                       */
    /* ============================= */

    private var backEnabled =
        false

    private var upperBackEnabled =
        false

    private var lowerBackEnabled =
        false


    /* ============================= */
    /* INTENSIDADES                  */
    /* ============================= */

    private var backIntensity =
        2

    private var upperBackIntensity =
        2

    private var lowerBackIntensity =
        2


    private var pendingIntensity =
        2


    /* ============================= */
    /* ON CREATE                     */
    /* ============================= */

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        setContentView(
            R.layout.activity_control_device
        )

        com.example.thermowear.util.WindowInsetsHelper.apply(this)


        getDeviceData()

        bindViews()

        configureTopBar()

        configureDeviceInterface()

        configureZoneSelection()

        configureSlider()

        configureButtons()

        updateInterface()
    }


    /* ============================= */
    /* ON RESUME                     */
    /* ============================= */

    override fun onResume() {

        super.onResume()


        BleManager.listener =
            this
    }


    /* ============================= */
    /* ON PAUSE                      */
    /* ============================= */

    override fun onPause() {

        if (
            BleManager.listener ===
            this
        ) {

            BleManager.listener =
                null
        }


        super.onPause()
    }


    /* ============================= */
    /* DATOS                         */
    /* ============================= */

    private fun getDeviceData() {

        deviceId =
            intent.getStringExtra(
                "DEVICE_ID"
            ) ?: ""


        deviceName =
            intent.getStringExtra(
                "DEVICE_NAME"
            ) ?: "Chamarra Forge"


        deviceVersion =
            intent.getStringExtra(
                "DEVICE_VERSION"
            ) ?: "V1"


        deviceConnected =
            intent.getBooleanExtra(
                "DEVICE_CONNECTED",
                false
            )
    }


    /* ============================= */
    /* VISTAS                        */
    /* ============================= */

    private fun bindViews() {

        txtDeviceName =
            findViewById(
                R.id.txtDeviceName
            )


        txtDeviceStatus =
            findViewById(
                R.id.txtDeviceStatus
            )


        txtDeviceVersion =
            findViewById(
                R.id.txtDeviceVersion
            )


        cardZonePrimary =
            findViewById(
                R.id.cardZonePrimary
            )


        cardZoneSecondary =
            findViewById(
                R.id.cardZoneSecondary
            )


        txtPrimaryZoneName =
            findViewById(
                R.id.txtPrimaryZoneName
            )


        txtPrimaryZoneInfo =
            findViewById(
                R.id.txtPrimaryZoneInfo
            )


        txtSecondaryZoneName =
            findViewById(
                R.id.txtSecondaryZoneName
            )


        txtSecondaryZoneInfo =
            findViewById(
                R.id.txtSecondaryZoneInfo
            )


        txtSelectedZone =
            findViewById(
                R.id.txtSelectedZone
            )


        containerTemperature =
            findViewById(
                R.id.containerTemperature
            )

        txtCurrentTemperature =
            findViewById(
                R.id.txtCurrentTemperature
            )


        txtHeatLevel =
            findViewById(
                R.id.txtHeatLevel
            )


        txtZoneStatus =
            findViewById(
                R.id.txtZoneStatus
            )


        sliderIntensity =
            findViewById(
                R.id.sliderIntensity
            )


        btnApplyIntensity =
            findViewById(
                R.id.btnApplyIntensity
            )


        btnZonePower =
            findViewById(
                R.id.btnZonePower
            )


        btnAllZonesPower =
            findViewById(
                R.id.btnAllZonesPower
            )
        heatBack =
            findViewById(
                R.id.heatBack
            )


        heatUpperBack =
            findViewById(
                R.id.heatUpperBack
            )


        heatLowerBack =
            findViewById(
                R.id.heatLowerBack
            )
    }


    /* ============================= */
    /* TOP BAR                       */
    /* ============================= */

    private fun configureTopBar() {

        findViewById<ImageButton>(
            R.id.btnBack
        ).setOnClickListener {
            finish()
        }


        findViewById<ImageButton>(
            R.id.btnSettings
        ).setOnClickListener {

            Toast.makeText(
                this,
                "Configuración del dispositivo disponible próximamente.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    /* ============================= */
    /* INTERFAZ MODELO               */
    /* ============================= */

    private fun configureDeviceInterface() {

        txtDeviceName.text =
            deviceName


        /*
         * El prototipo actual no tiene
         * temperatura.
         */
        containerTemperature.visibility =
            View.GONE


        when (
            deviceVersion.uppercase()
        ) {

            "V2" ->
                configureV2()

            else ->
                configureV1()
        }


        updateConnectionStatus()
    }


    private fun configureV1() {

        selectedZone =
            Zone.BACK


        pendingIntensity =
            backIntensity


        txtDeviceVersion.text =
            "Forge V1 · 1 zona"


        txtPrimaryZoneName.text =
            "Espalda"


        cardZoneSecondary.visibility =
            View.GONE


        btnAllZonesPower.visibility =
            View.GONE

        btnZonePower.visibility = View.VISIBLE
        containerTemperature.visibility =
            View.GONE
    }


    private fun configureV2() {

        selectedZone =
            Zone.UPPER_BACK


        pendingIntensity =
            upperBackIntensity


        txtDeviceVersion.text =
            "Forge $deviceVersion · 2 zonas"


        txtPrimaryZoneName.text =
            "Espalda alta"


        txtSecondaryZoneName.text =
            "Espalda baja"


        cardZoneSecondary.visibility =
            View.VISIBLE


        btnAllZonesPower.visibility =
            View.VISIBLE

        btnZonePower.visibility = View.VISIBLE
        containerTemperature.visibility =
            View.GONE
    }


    /* ============================= */
    /* SELECCIONAR ZONA              */
    /* ============================= */

    private fun configureZoneSelection() {

        cardZonePrimary.setOnClickListener {
            if (pendingZonePowerZone != null) return@setOnClickListener

            selectedZone =
                if (
                    isTwoZoneModel()
                ) {

                    Zone.UPPER_BACK

                } else {

                    Zone.BACK
                }


            pendingIntensity =
                getSelectedIntensity()


            updateInterface()
        }


        cardZoneSecondary.setOnClickListener {
            if (pendingZonePowerZone != null) return@setOnClickListener

            if (
                !isTwoZoneModel()
            ) {

                return@setOnClickListener
            }


            selectedZone =
                Zone.LOWER_BACK


            pendingIntensity =
                getSelectedIntensity()


            updateInterface()
        }
    }


    /* ============================= */
    /* SLIDER                        */
    /* ============================= */

    private fun configureSlider() {

        sliderIntensity
            .addOnChangeListener {
                    _,
                    value,
                    fromUser ->


                if (
                    !fromUser
                ) {

                    return@addOnChangeListener
                }


                pendingIntensity =
                    value.toInt()


                updateHeatLevel(
                    pendingIntensity
                )


                updateSliderColor(
                    pendingIntensity
                )
            }
    }


    /* ============================= */
    /* BOTONES                       */
    /* ============================= */

    private fun configureButtons() {

        /* ------------------------- */
        /* INTENSIDAD                */
        /* ------------------------- */

        btnApplyIntensity
            .setOnClickListener {

                if (
                    !deviceConnected
                ) {

                    showDisconnectedMessage()

                    return@setOnClickListener
                }


                val command =
                    when (
                        selectedZone
                    ) {

                        Zone.UPPER_BACK ->
                            "UPPER_LEVEL:$pendingIntensity"

                        Zone.LOWER_BACK ->
                            "LOWER_LEVEL:$pendingIntensity"

                        Zone.BACK ->
                            null
                    }


                if (
                    command == null
                ) {

                    return@setOnClickListener
                }


                if (
                    BleManager.sendCommand(
                        command
                    )
                ) {

                    setSelectedIntensity(
                        pendingIntensity
                    )

                    getHistoryZone(selectedZone)?.let { zone ->
                        HistorySessionTracker.changeIntensity(
                            context = this,
                            deviceId = deviceId,
                            zone = zone,
                            intensity = pendingIntensity
                        )
                    }


                    Toast.makeText(
                        this,
                        "Intensidad ${getIntensityName(pendingIntensity)} enviada",
                        Toast.LENGTH_SHORT
                    ).show()


                    updateInterface()

                } else {

                    showBluetoothError()
                }
            }


        /* ------------------------- */
        /* ENCENDIDO POR ZONA        */
        /* ------------------------- */

        btnZonePower.setOnClickListener {
            toggleZonePower(selectedZone)
        }

        /* ------------------------- */
        /* TODAS                     */
        /* ------------------------- */

        btnAllZonesPower
            .setOnClickListener {

                if (
                    !deviceConnected
                ) {

                    showDisconnectedMessage()

                    return@setOnClickListener
                }


                val shouldTurnOn =
                    !areAllZonesEnabled()


                val command =
                    if (
                        shouldTurnOn
                    ) {

                        "ALL_ON"

                    } else {

                        "ALL_OFF"
                    }


                if (
                    BleManager.sendCommand(
                        command
                    )
                ) {

                    upperBackEnabled =
                        shouldTurnOn


                    lowerBackEnabled =
                        shouldTurnOn

                    val historyTimestamp = System.currentTimeMillis()
                    listOf(
                        Triple(ThermoWearZone.UPPER_BACK, upperBackEnabled, upperBackIntensity),
                        Triple(ThermoWearZone.LOWER_BACK, lowerBackEnabled, lowerBackIntensity)
                    ).forEach { (zone, enabled, intensity) ->
                        HistorySessionTracker.setZoneEnabled(
                            context = this,
                            deviceId = deviceId,
                            version = getHistoryVersion(),
                            zone = zone,
                            enabled = enabled,
                            intensity = intensity,
                            timestampMillis = historyTimestamp
                        )
                    }


                    Toast.makeText(
                        this,
                        if (
                            shouldTurnOn
                        ) {

                            "Todas las zonas encendidas"

                        } else {

                            "Todas las zonas apagadas"
                        },
                        Toast.LENGTH_SHORT
                    ).show()


                    updateInterface()

                } else {

                    showBluetoothError()
                }
            }
    }


    /* ============================= */
    /* ACTUALIZAR                    */
    /* ============================= */

    private fun updateInterface() {

        updateSelectedZone()

        updateZoneCards()

        updatePowerButtons()

        updateSelectedCardStyle()

        updateHeatMap()
    }


    /* ============================= */
    /* CONEXIÓN                      */
    /* ============================= */

    private fun updateConnectionStatus() {

        txtDeviceStatus.text =
            if (
                deviceConnected
            ) {

                "● Conectado"

            } else {

                "● Desconectado"
            }


        txtDeviceStatus.setTextColor(
            getColor(
                if (
                    deviceConnected
                ) {

                    R.color.tw_green

                } else {

                    R.color.tw_text_light
                }
            )
        )
    }


    /* ============================= */
    /* MAPA DE CALOR                 */
    /* ============================= */

    private fun updateHeatMap() {

        if (
            isTwoZoneModel()
        ) {

            heatBack.visibility =
                View.GONE


            heatUpperBack.visibility =
                if (
                    upperBackEnabled
                ) {

                    View.VISIBLE

                } else {

                    View.GONE
                }


            heatLowerBack.visibility =
                if (
                    lowerBackEnabled
                ) {

                    View.VISIBLE

                } else {

                    View.GONE
                }

        } else {

            heatUpperBack.visibility =
                View.GONE


            heatLowerBack.visibility =
                View.GONE


            heatBack.visibility =
                if (
                    backEnabled
                ) {

                    View.VISIBLE

                } else {

                    View.GONE
                }
        }
    }


    /* ============================= */
    /* ZONA ACTUAL                   */
    /* ============================= */

    private fun updateSelectedZone() {

        txtSelectedZone.text =
            getSelectedZoneName()


        containerTemperature.visibility =
            if (isTwoZoneModel()) View.VISIBLE else View.GONE
        txtCurrentTemperature.text = currentTemperatureLabel


        sliderIntensity.value =
            pendingIntensity.toFloat()


        updateHeatLevel(
            pendingIntensity
        )


        updateSliderColor(
            pendingIntensity
        )


        val enabled =
            isSelectedZoneEnabled()


        txtZoneStatus.text = when {
            pendingZonePowerZone == selectedZone && waitingForZoneLevelAck ->
                "Aplicando intensidad antes de encender…"
            pendingZonePowerZone == selectedZone && pendingZonePowerTarget == false ->
                "Apagando la zona…"
            pendingZonePowerZone == selectedZone ->
                "Esperando confirmación del ESP32…"
            enabled -> "Estado: Encendida"
            else -> "Estado: Apagada"
        }


        txtZoneStatus.setTextColor(
            getColor(
                if (
                    enabled
                ) {

                    R.color.tw_green

                } else {

                    R.color.tw_text_secondary
                }
            )
        )
    }


    /* ============================= */
    /* TARJETAS                      */
    /* ============================= */

    private fun updateZoneCards() {

        if (
            isTwoZoneModel()
        ) {

            val upperState = if (upperBackEnabled) "Encendida" else "Apagada"
            val lowerState = if (lowerBackEnabled) "Encendida" else "Apagada"

            txtPrimaryZoneInfo.text = if (selectedZone == Zone.UPPER_BACK) {
                "Seleccionada · $upperState"
            } else {
                "Toca para elegir · $upperState"
            }


            txtSecondaryZoneInfo.text = if (selectedZone == Zone.LOWER_BACK) {
                "Seleccionada · $lowerState"
            } else {
                "Toca para elegir · $lowerState"
            }

        } else {

            txtPrimaryZoneInfo.text =
                if (
                    backEnabled
                ) {

                    getIntensityName(
                        backIntensity
                    )

                } else {

                    "Apagada"
                }
        }
    }


    /* ============================= */
    /* ESTILO TARJETAS               */
    /* ============================= */

    private fun updateSelectedCardStyle() {

        val selectedStroke =
            getColor(R.color.tw_primary)

        val normalStroke =
            Color.parseColor("#E5E5EC")

        val selectedBackground =
            Color.parseColor("#EEE8FA")

        val normalBackground =
            Color.parseColor("#FFFFFF")

        if (isTwoZoneModel()) {
            val upperSelected = selectedZone == Zone.UPPER_BACK

            cardZonePrimary.strokeWidth = dpToPx(if (upperSelected) 2 else 1)
            cardZonePrimary.strokeColor = if (upperSelected) selectedStroke else normalStroke
            cardZonePrimary.setCardBackgroundColor(if (upperSelected) selectedBackground else normalBackground)

            cardZoneSecondary.strokeWidth = dpToPx(if (upperSelected) 1 else 2)
            cardZoneSecondary.strokeColor = if (upperSelected) normalStroke else selectedStroke
            cardZoneSecondary.setCardBackgroundColor(if (upperSelected) normalBackground else selectedBackground)
        } else {
            cardZonePrimary.strokeWidth = dpToPx(2)
            cardZonePrimary.strokeColor = selectedStroke
            cardZonePrimary.setCardBackgroundColor(selectedBackground)
        }
    }

    /* ============================= */
    /* BOTONES ESTADO                */
    /* ============================= */

    private fun updatePowerButtons() {
        btnZonePower.text = when {
            pendingZonePowerZone == selectedZone && waitingForZoneLevelAck -> "Aplicando intensidad…"
            pendingZonePowerZone == selectedZone && pendingZonePowerTarget == false -> "Apagando…"
            pendingZonePowerZone == selectedZone -> "Encendiendo…"
            isSelectedZoneEnabled() -> "Apagar ${getSelectedZoneName()}"
            else -> "Encender · ${getIntensityName(pendingIntensity)}"
        }

        if (isTwoZoneModel()) {
            btnAllZonesPower.visibility = View.VISIBLE
            btnAllZonesPower.text = if (areAllZonesEnabled()) "Apagar ambas" else "Encender ambas"
        } else {
            btnAllZonesPower.visibility = View.GONE
        }

        val idle = pendingZonePowerZone == null
        sliderIntensity.isEnabled = deviceConnected && isTwoZoneModel() && idle
        btnApplyIntensity.isEnabled = deviceConnected && isTwoZoneModel() && idle
        btnZonePower.isEnabled = deviceConnected && isTwoZoneModel() && idle
        btnAllZonesPower.isEnabled = deviceConnected && isTwoZoneModel() && idle
    }

    /* ============================= */
    /* INTENSIDAD                    */
    /* ============================= */

    private fun updateHeatLevel(
        intensity: Int
    ) {

        txtHeatLevel.text =
            getIntensityName(
                intensity
            )


        txtHeatLevel.setTextColor(
            getIntensityColor(
                intensity
            )
        )
    }


    private fun getIntensityName(
        intensity: Int
    ): String {

        return when (
            intensity
        ) {

            1 ->
                "Bajo"

            2 ->
                "Medio"

            3 ->
                "Alto"

            else ->
                "Medio"
        }
    }


    private fun updateSliderColor(
        intensity: Int
    ) {

        val color =
            getIntensityColor(
                intensity
            )


        val inactiveColor =
            Color.parseColor(
                "#E3E3E8"
            )


        sliderIntensity.thumbTintList =
            ColorStateList.valueOf(
                color
            )


        sliderIntensity.trackActiveTintList =
            ColorStateList.valueOf(
                color
            )


        sliderIntensity.trackInactiveTintList =
            ColorStateList.valueOf(
                inactiveColor
            )
    }


    private fun getIntensityColor(
        intensity: Int
    ): Int {

        return when (
            intensity
        ) {

            1 ->
                Color.rgb(
                    255,
                    183,
                    77
                )

            2 ->
                Color.rgb(
                    255,
                    128,
                    54
                )

            3 ->
                Color.rgb(
                    229,
                    72,
                    72
                )

            else ->
                getColor(
                    R.color.tw_primary
                )
        }
    }


    private fun toggleZonePower(zone: Zone) {
        if (!deviceConnected) { showDisconnectedMessage(); return }
        if (pendingZonePowerZone != null) return

        val enable = !isZoneEnabled(zone)
        pendingZonePowerZone = zone
        pendingZonePowerTarget = enable
        waitingForZoneLevelAck = enable
        pendingZonePowerLevel = if (zone == selectedZone) pendingIntensity else getZoneIntensity(zone)

        val command = when (zone) {
            Zone.UPPER_BACK -> if (enable) "UPPER_LEVEL:$pendingZonePowerLevel" else "UPPER_OFF"
            Zone.LOWER_BACK -> if (enable) "LOWER_LEVEL:$pendingZonePowerLevel" else "LOWER_OFF"
            Zone.BACK -> null
        }
        if (command == null || !BleManager.sendCommand(command)) {
            clearPendingZonePower()
            showBluetoothError()
            updateInterface()
            return
        }
        controlHandler.postDelayed(zonePowerTimeout, 5_000L)
        updatePowerButtons()
        updateSelectedZone()
    }

    private fun handleZonePowerAcknowledgement(message: String): Boolean {
        val zone = pendingZonePowerZone ?: return false
        val enable = pendingZonePowerTarget ?: return false

        if (enable && waitingForZoneLevelAck) {
            val prefix = when (zone) {
                Zone.UPPER_BACK -> "OK:UPPER_LEVEL:"
                Zone.LOWER_BACK -> "OK:LOWER_LEVEL:"
                Zone.BACK -> return false
            }
            if (!message.startsWith(prefix)) return false
            val confirmedLevel = message.substringAfterLast(':').toIntOrNull()
            if (confirmedLevel != pendingZonePowerLevel) return false

            waitingForZoneLevelAck = false
            controlHandler.removeCallbacks(zonePowerTimeout)
            setZoneIntensity(zone, confirmedLevel)
            getHistoryZone(zone)?.let { historyZone ->
                HistorySessionTracker.changeIntensity(
                    context = this,
                    deviceId = deviceId,
                    zone = historyZone,
                    intensity = confirmedLevel
                )
            }
            val onCommand = if (zone == Zone.UPPER_BACK) "UPPER_ON" else "LOWER_ON"
            if (!BleManager.sendCommand(onCommand)) {
                clearPendingZonePower()
                showBluetoothError()
                updateInterface()
            } else {
                controlHandler.postDelayed(zonePowerTimeout, 5_000L)
                updatePowerButtons()
                updateSelectedZone()
            }
            return true
        }

        val expected = when (zone) {
            Zone.UPPER_BACK -> if (enable) "OK:UPPER_ON" else "OK:UPPER_OFF"
            Zone.LOWER_BACK -> if (enable) "OK:LOWER_ON" else "OK:LOWER_OFF"
            Zone.BACK -> return false
        }
        if (message != expected) return false

        setZoneEnabled(zone, enable)
        getHistoryZone(zone)?.let { historyZone ->
            HistorySessionTracker.setZoneEnabled(
                context = this,
                deviceId = deviceId,
                version = getHistoryVersion(),
                zone = historyZone,
                enabled = enable,
                intensity = getZoneIntensity(zone)
            )
        }
        clearPendingZonePower()
        Toast.makeText(
            this,
            "${getZoneName(zone)} ${if (enable) "encendida" else "apagada"}",
            Toast.LENGTH_SHORT
        ).show()
        updateInterface()
        return true
    }

    private fun clearPendingZonePower() {
        controlHandler.removeCallbacks(zonePowerTimeout)
        pendingZonePowerZone = null
        pendingZonePowerTarget = null
        waitingForZoneLevelAck = false
    }

    private fun setZoneIntensity(zone: Zone, intensity: Int) {
        when (zone) {
            Zone.BACK -> backIntensity = intensity
            Zone.UPPER_BACK -> upperBackIntensity = intensity
            Zone.LOWER_BACK -> lowerBackIntensity = intensity
        }
    }

    private fun isZoneEnabled(zone: Zone): Boolean = when (zone) {
        Zone.BACK -> backEnabled
        Zone.UPPER_BACK -> upperBackEnabled
        Zone.LOWER_BACK -> lowerBackEnabled
    }

    private fun setZoneEnabled(zone: Zone, enabled: Boolean) {
        when (zone) {
            Zone.BACK -> backEnabled = enabled
            Zone.UPPER_BACK -> upperBackEnabled = enabled
            Zone.LOWER_BACK -> lowerBackEnabled = enabled
        }
    }

    private fun getZoneName(zone: Zone): String = when (zone) {
        Zone.BACK -> "Espalda"
        Zone.UPPER_BACK -> "Espalda alta"
        Zone.LOWER_BACK -> "Espalda baja"
    }

    private fun getZoneIntensity(zone: Zone): Int = when (zone) {
        Zone.BACK -> backIntensity
        Zone.UPPER_BACK -> upperBackIntensity
        Zone.LOWER_BACK -> lowerBackIntensity
    }

    /* ============================= */
    /* ESTADOS DE ZONA               */
    /* ============================= */

    private fun isSelectedZoneEnabled():
            Boolean {

        return when (
            selectedZone
        ) {

            Zone.BACK ->
                backEnabled

            Zone.UPPER_BACK ->
                upperBackEnabled

            Zone.LOWER_BACK ->
                lowerBackEnabled
        }
    }


    private fun setSelectedZoneEnabled(
        enabled: Boolean
    ) {

        when (
            selectedZone
        ) {

            Zone.BACK ->
                backEnabled =
                    enabled

            Zone.UPPER_BACK ->
                upperBackEnabled =
                    enabled

            Zone.LOWER_BACK ->
                lowerBackEnabled =
                    enabled
        }
    }


    private fun getSelectedIntensity():
            Int {

        return when (
            selectedZone
        ) {

            Zone.BACK ->
                backIntensity

            Zone.UPPER_BACK ->
                upperBackIntensity

            Zone.LOWER_BACK ->
                lowerBackIntensity
        }
    }


    private fun getHistoryVersion(): ThermoWearVersion =
        runCatching {
            ThermoWearVersion.valueOf(deviceVersion)
        }.getOrDefault(ThermoWearVersion.V1)


    private fun getHistoryZone(zone: Zone): ThermoWearZone? =
        when (zone) {
            Zone.BACK -> ThermoWearZone.BACK
            Zone.UPPER_BACK -> ThermoWearZone.UPPER_BACK
            Zone.LOWER_BACK -> ThermoWearZone.LOWER_BACK
        }


    private fun setSelectedIntensity(
        intensity: Int
    ) {

        when (
            selectedZone
        ) {

            Zone.BACK ->
                backIntensity =
                    intensity

            Zone.UPPER_BACK ->
                upperBackIntensity =
                    intensity

            Zone.LOWER_BACK ->
                lowerBackIntensity =
                    intensity
        }
    }


    private fun getSelectedZoneName():
            String {

        return when (
            selectedZone
        ) {

            Zone.BACK ->
                "Espalda"

            Zone.UPPER_BACK ->
                "Espalda alta"

            Zone.LOWER_BACK ->
                "Espalda baja"
        }
    }


    private fun areAllZonesEnabled():
            Boolean {

        return if (
            isTwoZoneModel()
        ) {

            upperBackEnabled &&
                    lowerBackEnabled

        } else {

            backEnabled
        }
    }


    private fun isTwoZoneModel(): Boolean =
        deviceVersion.equals("V2", ignoreCase = true)


    /* ============================= */
    /* BLE CALLBACKS                 */
    /* ============================= */

    override fun onScanning() {
    }


    override fun onDeviceFound(
        name: String,
        address: String
    ) {
    }


    override fun onConnecting() {
    }


    override fun onConnected(
        address: String
    ) {

        runOnUiThread {

            deviceConnected =
                true


            updateConnectionStatus()

            updatePowerButtons()
        }
    }


    override fun onReady() {

        runOnUiThread {

            deviceConnected =
                true


            DeviceManager.setConnection(
                this,
                deviceId,
                true
            )


            updateConnectionStatus()

            updatePowerButtons()
        }
    }


    override fun onMessageReceived(message: String) {
        val received = message.trim()
        runOnUiThread {
            if (handleZonePowerAcknowledgement(received)) {
                return@runOnUiThread
            }
            when {
                received.startsWith("MAX_TEMP:") -> {
                    clearPendingZonePower()
                    val temperature = received.substringAfter("MAX_TEMP:").toDoubleOrNull()
                    if (temperature != null && temperature.isFinite()) {
                        currentTemperatureLabel =
                            String.format(Locale.US, "%.1f °C", temperature)
                        txtCurrentTemperature.text = currentTemperatureLabel

                        upperBackEnabled = false
                        lowerBackEnabled = false
                        backEnabled = false
                        BleManager.sendCommand("ALL_OFF")
                        updatePowerButtons()
                        updateZoneCards()
                        updateInterface()

                        Toast.makeText(
                            this,
                            "Temperatura máxima del prototipo alcanzada. Calefacción apagada.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                received.startsWith("TEMP:") -> {
                    val temperature = received.substringAfter("TEMP:").toDoubleOrNull()
                    if (temperature != null && temperature.isFinite()) {
                        sensorFaultNoticeShown = false
                    }
                    currentTemperatureLabel =
                        if (temperature != null && temperature.isFinite() &&
                            temperature in -55.0..125.0
                        ) {
                            String.format(Locale.US, "%.1f °C", temperature)
                        } else {
                            "Sin lectura"
                        }
                    txtCurrentTemperature.text = currentTemperatureLabel
                    updateZoneCards()
                }
                received == "ERROR:TEMP_SENSOR" -> {
                    clearPendingZonePower()
                    currentTemperatureLabel = "Sensor sin lectura"
                    txtCurrentTemperature.text = currentTemperatureLabel
                    upperBackEnabled = false
                    lowerBackEnabled = false
                    backEnabled = false
                    updatePowerButtons()
                    updateZoneCards()
                    updateInterface()
                    if (!sensorFaultNoticeShown) {
                        sensorFaultNoticeShown = true
                        Toast.makeText(
                            this,
                            "Sin lectura del sensor. Calefacción apagada.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                received == "ERROR:THERMAL_LOCKOUT" -> {
                    clearPendingZonePower()
                    upperBackEnabled = false
                    lowerBackEnabled = false
                    backEnabled = false
                    updateInterface()
                    Toast.makeText(
                        this,
                        "La chamarra sigue bloqueada hasta que se enfríe.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                received.startsWith("ERROR") -> {
                    if (pendingZonePowerZone != null) {
                        clearPendingZonePower()
                        updateInterface()
                    }
                    Toast.makeText(this, "ESP32: $received", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDisconnected() {

        runOnUiThread {

            clearPendingZonePower()

            deviceConnected =
                false

            currentTemperatureLabel = "-- °C"


            upperBackEnabled =
                false


            lowerBackEnabled =
                false


            backEnabled =
                false


            DeviceManager.setConnection(
                this,
                deviceId,
                false
            )


            updateConnectionStatus()

            updateInterface()


            Toast.makeText(
                this,
                "Se perdió la conexión con $deviceName.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    override fun onError(
        message: String
    ) {

        runOnUiThread {

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }


    /* ============================= */
    /* MENSAJES                      */
    /* ============================= */

    private fun showDisconnectedMessage() {

        Toast.makeText(
            this,
            "$deviceName está desconectada.",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun showBluetoothError() {

        Toast.makeText(
            this,
            "No se pudo enviar el comando Bluetooth.",
            Toast.LENGTH_SHORT
        ).show()
    }


    /* ============================= */
    /* FINISH                        */
    /* ============================= */

    override fun finish() {

        super.finish()


        ScreenTransitions.closeSlide(
            this
        )
    }


    /* ============================= */
    /* DP                            */
    /* ============================= */

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
                dp *
                        resources
                            .displayMetrics
                            .density
                ).toInt()
    }
}
