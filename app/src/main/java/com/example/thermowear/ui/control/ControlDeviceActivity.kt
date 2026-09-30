package com.example.thermowear.ui.control

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.thermowear.R
import com.example.thermowear.bluetooth.BleManager
import com.example.thermowear.data.DeviceManager
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


        containerTemperature.visibility =
            View.GONE
    }


    private fun configureV2() {

        selectedZone =
            Zone.UPPER_BACK


        pendingIntensity =
            upperBackIntensity


        txtDeviceVersion.text =
            "Forge V2 · 2 zonas"


        txtPrimaryZoneName.text =
            "Espalda alta"


        txtSecondaryZoneName.text =
            "Espalda baja"


        cardZoneSecondary.visibility =
            View.VISIBLE


        btnAllZonesPower.visibility =
            View.VISIBLE


        containerTemperature.visibility =
            View.GONE
    }


    /* ============================= */
    /* SELECCIONAR ZONA              */
    /* ============================= */

    private fun configureZoneSelection() {

        cardZonePrimary.setOnClickListener {

            selectedZone =
                if (
                    isV2()
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

            if (
                !isV2()
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
        /* UNA ZONA                  */
        /* ------------------------- */

        btnZonePower
            .setOnClickListener {

                if (
                    !deviceConnected
                ) {

                    showDisconnectedMessage()

                    return@setOnClickListener
                }


                val newState =
                    !isSelectedZoneEnabled()


                val command =
                    when (
                        selectedZone
                    ) {

                        Zone.UPPER_BACK ->

                            if (
                                newState
                            ) {

                                "UPPER_ON"

                            } else {

                                "UPPER_OFF"
                            }


                        Zone.LOWER_BACK ->

                            if (
                                newState
                            ) {

                                "LOWER_ON"

                            } else {

                                "LOWER_OFF"
                            }


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

                    setSelectedZoneEnabled(
                        newState
                    )


                    Toast.makeText(
                        this,
                        if (
                            newState
                        ) {

                            "${getSelectedZoneName()} encendida"

                        } else {

                            "${getSelectedZoneName()} apagada"
                        },
                        Toast.LENGTH_SHORT
                    ).show()


                    updateInterface()

                } else {

                    showBluetoothError()
                }
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
            isV2()
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
            if (isV2() && selectedZone == Zone.UPPER_BACK) View.VISIBLE else View.GONE
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


        txtZoneStatus.text =
            if (
                enabled
            ) {

                "Estado: Encendida"

            } else {

                "Estado: Apagada"
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
            isV2()
        ) {

            txtPrimaryZoneInfo.text =
                currentTemperatureLabel


            txtSecondaryZoneInfo.text =
                if (
                    lowerBackEnabled
                ) {

                    getIntensityName(
                        lowerBackIntensity
                    )

                } else {

                    "Apagada"
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
            getColor(
                R.color.tw_primary
            )


        val normalStroke =
            Color.parseColor(
                "#E5E5EC"
            )


        if (
            isV2()
        ) {

            if (
                selectedZone ==
                Zone.UPPER_BACK
            ) {

                cardZonePrimary.strokeWidth =
                    dpToPx(
                        2
                    )


                cardZonePrimary.strokeColor =
                    selectedStroke


                cardZoneSecondary.strokeWidth =
                    dpToPx(
                        1
                    )


                cardZoneSecondary.strokeColor =
                    normalStroke

            } else {

                cardZonePrimary.strokeWidth =
                    dpToPx(
                        1
                    )


                cardZonePrimary.strokeColor =
                    normalStroke


                cardZoneSecondary.strokeWidth =
                    dpToPx(
                        2
                    )


                cardZoneSecondary.strokeColor =
                    selectedStroke
            }

        } else {

            cardZonePrimary.strokeWidth =
                dpToPx(
                    2
                )


            cardZonePrimary.strokeColor =
                selectedStroke
        }
    }


    /* ============================= */
    /* BOTONES ESTADO                */
    /* ============================= */

    private fun updatePowerButtons() {

        btnZonePower.text =
            if (
                isSelectedZoneEnabled()
            ) {

                "Apagar zona"

            } else {

                "Encender zona"
            }


        if (
            isV2()
        ) {

            btnAllZonesPower.visibility =
                View.VISIBLE


            btnAllZonesPower.text =
                if (
                    areAllZonesEnabled()
                ) {

                    "Apagar todas las zonas"

                } else {

                    "Encender todas las zonas"
                }

        } else {

            btnAllZonesPower.visibility =
                View.GONE
        }


        sliderIntensity.isEnabled =
            deviceConnected && isV2()


        btnApplyIntensity.isEnabled =
            deviceConnected && isV2()


        btnZonePower.isEnabled =
            deviceConnected && isV2()


        btnAllZonesPower.isEnabled =
            deviceConnected && isV2()
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
            isV2()
        ) {

            upperBackEnabled &&
                    lowerBackEnabled

        } else {

            backEnabled
        }
    }


    private fun isV2():
            Boolean {

        return deviceVersion.uppercase() ==
                "V2"
    }


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
            when {
                received.startsWith("TEMP:") -> {
                    val temperature = received.substringAfter("TEMP:").toDoubleOrNull()
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
                    currentTemperatureLabel = "Sensor sin lectura"
                    txtCurrentTemperature.text = currentTemperatureLabel
                    updateZoneCards()
                }
                received.startsWith("ERROR") -> {
                    Toast.makeText(this, "ESP32: $received", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDisconnected() {

        runOnUiThread {

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
                "Se perdió la conexión con Forge V2.",
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
            "Forge V2 está desconectada.",
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
