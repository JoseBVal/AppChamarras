package com.example.thermowear.ui.control

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.util.ScreenTransitions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider

class ControlDeviceActivity : AppCompatActivity() {

    private lateinit var txtDeviceName: TextView
    private lateinit var txtDeviceStatus: TextView
    private lateinit var txtDeviceVersion: TextView

    private lateinit var cardZonePrimary: MaterialCardView
    private lateinit var cardZoneSecondary: MaterialCardView

    private lateinit var txtPrimaryZoneName: TextView
    private lateinit var txtPrimaryZoneInfo: TextView

    private lateinit var txtSecondaryZoneName: TextView
    private lateinit var txtSecondaryZoneInfo: TextView

    private lateinit var txtSelectedZone: TextView

    private lateinit var containerTemperature: LinearLayout
    private lateinit var txtCurrentTemperature: TextView

    private lateinit var txtHeatLevel: TextView
    private lateinit var txtZoneStatus: TextView

    private lateinit var sliderIntensity: Slider

    private lateinit var btnApplyIntensity: MaterialButton
    private lateinit var btnZonePower: MaterialButton
    private lateinit var btnAllZonesPower: MaterialButton


    /* ============================= */
    /* DATOS DEL DISPOSITIVO         */
    /* ============================= */

    private var deviceId: String = ""
    private var deviceName: String = "ThermoWear"
    private var deviceVersion: String = "V1"
    private var deviceBattery: Int = 0
    private var deviceConnected: Boolean = false


    /* ============================= */
    /* ZONAS                         */
    /* ============================= */

    private enum class Zone {
        BACK,
        UPPER_BACK,
        LOWER_BACK
    }

    private var selectedZone: Zone = Zone.BACK


    /* ============================= */
    /* ESTADO DE ENCENDIDO           */
    /* ============================= */

    private var backEnabled = false
    private var upperBackEnabled = false
    private var lowerBackEnabled = false


    /* ============================= */
    /* INTENSIDAD APLICADA           */
    /* ============================= */

    private var backIntensity = 2
    private var upperBackIntensity = 2
    private var lowerBackIntensity = 2


    /* ============================= */
    /* INTENSIDAD SELECCIONADA       */
    /* ============================= */

    private var pendingIntensity = 2


    /* ============================= */
    /* CICLO DE VIDA                 */
    /* ============================= */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_control_device)

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
    /* OBTENER DATOS                 */
    /* ============================= */

    private fun getDeviceData() {

        deviceId =
            intent.getStringExtra("DEVICE_ID")
                ?: ""

        deviceName =
            intent.getStringExtra("DEVICE_NAME")
                ?: "ThermoWear"

        deviceVersion =
            intent.getStringExtra("DEVICE_VERSION")
                ?: "V1"

        deviceBattery =
            intent.getIntExtra(
                "DEVICE_BATTERY",
                0
            )

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
            findViewById(R.id.txtDeviceName)

        txtDeviceStatus =
            findViewById(R.id.txtDeviceStatus)

        txtDeviceVersion =
            findViewById(R.id.txtDeviceVersion)


        cardZonePrimary =
            findViewById(R.id.cardZonePrimary)

        cardZoneSecondary =
            findViewById(R.id.cardZoneSecondary)


        txtPrimaryZoneName =
            findViewById(R.id.txtPrimaryZoneName)

        txtPrimaryZoneInfo =
            findViewById(R.id.txtPrimaryZoneInfo)

        txtSecondaryZoneName =
            findViewById(R.id.txtSecondaryZoneName)

        txtSecondaryZoneInfo =
            findViewById(R.id.txtSecondaryZoneInfo)


        txtSelectedZone =
            findViewById(R.id.txtSelectedZone)


        containerTemperature =
            findViewById(R.id.containerTemperature)

        txtCurrentTemperature =
            findViewById(R.id.txtCurrentTemperature)


        txtHeatLevel =
            findViewById(R.id.txtHeatLevel)

        txtZoneStatus =
            findViewById(R.id.txtZoneStatus)


        sliderIntensity =
            findViewById(R.id.sliderIntensity)


        btnApplyIntensity =
            findViewById(R.id.btnApplyIntensity)

        btnZonePower =
            findViewById(R.id.btnZonePower)

        btnAllZonesPower =
            findViewById(R.id.btnAllZonesPower)
    }


    /* ============================= */
    /* BARRA SUPERIOR                */
    /* ============================= */

    private fun configureTopBar() {

        val btnBack =
            findViewById<ImageButton>(
                R.id.btnBack
            )

        val btnSettings =
            findViewById<ImageButton>(
                R.id.btnSettings
            )


        btnBack.setOnClickListener {
            finish()
        }


        btnSettings.setOnClickListener {

            Toast.makeText(
                this,
                "Configuración del dispositivo disponible próximamente.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    /* ============================= */
    /* CONFIGURAR MODELO             */
    /* ============================= */

    private fun configureDeviceInterface() {

        txtDeviceName.text =
            deviceName


        txtDeviceStatus.text =
            if (deviceConnected) {

                "● Conectado · 🔋 $deviceBattery%"

            } else {

                "Desconectado · 🔋 $deviceBattery%"
            }


        txtDeviceStatus.setTextColor(
            getColor(
                if (deviceConnected) {
                    R.color.tw_green
                } else {
                    R.color.tw_text_light
                }
            )
        )


        when (deviceVersion.uppercase()) {

            "V2" -> configureV2()

            else -> configureV1()
        }
    }


    /* ============================= */
    /* THERMOWEAR V1                 */
    /* ============================= */

    private fun configureV1() {

        selectedZone =
            Zone.BACK

        pendingIntensity =
            backIntensity


        txtDeviceVersion.text =
            "ThermoWear V1 · 1 zona"


        txtPrimaryZoneName.text =
            "Espalda"

        txtPrimaryZoneInfo.text =
            "Apagada"


        /*
         * V1 solamente tiene una zona.
         */

        cardZoneSecondary.visibility =
            View.GONE


        /*
         * No existe sensor de temperatura.
         */

        containerTemperature.visibility =
            View.GONE


        /*
         * En V1 "todas las zonas" sería exactamente
         * la misma acción que "zona".
         * Se oculta para evitar duplicidad.
         */

        btnAllZonesPower.visibility =
            View.GONE
    }


    /* ============================= */
    /* THERMOWEAR V2                 */
    /* ============================= */

    private fun configureV2() {

        selectedZone =
            Zone.UPPER_BACK

        pendingIntensity =
            upperBackIntensity


        txtDeviceVersion.text =
            "ThermoWear V2 · 2 zonas"


        txtPrimaryZoneName.text =
            "Espalda alta"

        txtPrimaryZoneInfo.text =
            "38 °C"


        txtSecondaryZoneName.text =
            "Espalda baja"

        txtSecondaryZoneInfo.text =
            "Apagada"


        cardZoneSecondary.visibility =
            View.VISIBLE

        btnAllZonesPower.visibility =
            View.VISIBLE


        /*
         * El sensor solamente existe
         * en Espalda alta.
         */

        containerTemperature.visibility =
            View.VISIBLE
    }


    /* ============================= */
    /* SELECCIÓN DE ZONAS            */
    /* ============================= */

    private fun configureZoneSelection() {

        cardZonePrimary.setOnClickListener {

            selectedZone =
                if (isV2()) {
                    Zone.UPPER_BACK
                } else {
                    Zone.BACK
                }


            /*
             * Si había un cambio de Slider sin aplicar,
             * al seleccionar otra zona recuperamos
             * la intensidad aplicada de esa zona.
             */

            pendingIntensity =
                getSelectedIntensity()


            updateInterface()
        }


        cardZoneSecondary.setOnClickListener {

            if (!isV2()) {
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
    /* SLIDER DE INTENSIDAD          */
    /* ============================= */

    private fun configureSlider() {

        sliderIntensity.addOnChangeListener {
                _,
                value,
                fromUser ->

            if (!fromUser) {
                return@addOnChangeListener
            }


            /*
             * Mover el Slider solamente selecciona
             * una intensidad.
             *
             * NO la aplica todavía.
             */

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


        /* ============================= */
        /* APLICAR INTENSIDAD             */
        /* ============================= */

        btnApplyIntensity.setOnClickListener {

            if (!deviceConnected) {

                showDisconnectedMessage()
                return@setOnClickListener
            }


            setSelectedIntensity(
                pendingIntensity
            )


            val intensityName =
                getIntensityName(
                    pendingIntensity
                )


            val message =
                if (isSelectedZoneEnabled()) {

                    "Intensidad $intensityName aplicada a ${getSelectedZoneName()}"

                } else {

                    "Intensidad $intensityName configurada para ${getSelectedZoneName()}"
                }


            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()


            updateInterface()
        }


        /* ============================= */
        /* ENCENDER / APAGAR ZONA         */
        /* ============================= */

        btnZonePower.setOnClickListener {

            if (!deviceConnected) {

                showDisconnectedMessage()
                return@setOnClickListener
            }


            val newState =
                !isSelectedZoneEnabled()


            setSelectedZoneEnabled(
                newState
            )


            val action =
                if (newState) {
                    "encendida"
                } else {
                    "apagada"
                }


            Toast.makeText(
                this,
                "${getSelectedZoneName()} $action",
                Toast.LENGTH_SHORT
            ).show()


            updateInterface()
        }


        /* ============================= */
        /* ENCENDER / APAGAR TODO         */
        /* ============================= */

        btnAllZonesPower.setOnClickListener {

            if (!deviceConnected) {

                showDisconnectedMessage()
                return@setOnClickListener
            }


            val shouldTurnOn =
                !areAllZonesEnabled()


            if (isV2()) {

                upperBackEnabled =
                    shouldTurnOn

                lowerBackEnabled =
                    shouldTurnOn

            } else {

                backEnabled =
                    shouldTurnOn
            }


            val message =
                if (shouldTurnOn) {

                    "Todas las zonas encendidas"

                } else {

                    "Todas las zonas apagadas"
                }


            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()


            updateInterface()
        }
    }


    /* ============================= */
    /* ACTUALIZAR INTERFAZ           */
    /* ============================= */

    private fun updateInterface() {

        updateSelectedZone()

        updateZoneCards()

        updatePowerButtons()

        updateSelectedCardStyle()
    }


    /* ============================= */
    /* ZONA SELECCIONADA             */
    /* ============================= */

    private fun updateSelectedZone() {

        txtSelectedZone.text =
            getSelectedZoneName()


        /*
         * Temperatura solamente en
         * Espalda alta de V2.
         */

        if (
            isV2() &&
            selectedZone == Zone.UPPER_BACK
        ) {

            containerTemperature.visibility =
                View.VISIBLE


            /*
             * Simulación hasta conectar
             * el sensor físico.
             */

            txtCurrentTemperature.text =
                "38 °C"

        } else {

            containerTemperature.visibility =
                View.GONE
        }


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
            if (enabled) {

                "Estado: Encendida"

            } else {

                "Estado: Apagada"
            }


        txtZoneStatus.setTextColor(
            getColor(
                if (enabled) {
                    R.color.tw_green
                } else {
                    R.color.tw_text_secondary
                }
            )
        )
    }


    /* ============================= */
    /* TARJETAS DE ZONAS             */
    /* ============================= */

    private fun updateZoneCards() {

        if (isV2()) {

            txtPrimaryZoneInfo.text =
                if (upperBackEnabled) {

                    "38 °C · ${getIntensityName(upperBackIntensity)}"

                } else {

                    "38 °C · Apagada"
                }


            txtSecondaryZoneInfo.text =
                if (lowerBackEnabled) {

                    getIntensityName(
                        lowerBackIntensity
                    )

                } else {

                    "Apagada"
                }

        } else {

            txtPrimaryZoneInfo.text =
                if (backEnabled) {

                    getIntensityName(
                        backIntensity
                    )

                } else {

                    "Apagada"
                }
        }
    }


    /* ============================= */
    /* ESTILO DE SELECCIÓN           */
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


        if (isV2()) {

            if (
                selectedZone ==
                Zone.UPPER_BACK
            ) {

                cardZonePrimary.strokeWidth =
                    dpToPx(2)

                cardZonePrimary.strokeColor =
                    selectedStroke


                cardZoneSecondary.strokeWidth =
                    dpToPx(1)

                cardZoneSecondary.strokeColor =
                    normalStroke

            } else {

                cardZonePrimary.strokeWidth =
                    dpToPx(1)

                cardZonePrimary.strokeColor =
                    normalStroke


                cardZoneSecondary.strokeWidth =
                    dpToPx(2)

                cardZoneSecondary.strokeColor =
                    selectedStroke
            }

        } else {

            cardZonePrimary.strokeWidth =
                dpToPx(2)

            cardZonePrimary.strokeColor =
                selectedStroke
        }
    }


    /* ============================= */
    /* BOTONES DE ENCENDIDO          */
    /* ============================= */

    private fun updatePowerButtons() {

        val selectedZoneEnabled =
            isSelectedZoneEnabled()


        btnZonePower.text =
            if (selectedZoneEnabled) {

                "Apagar zona"

            } else {

                "Encender zona"
            }


        if (isV2()) {

            btnAllZonesPower.visibility =
                View.VISIBLE


            btnAllZonesPower.text =
                if (areAllZonesEnabled()) {

                    "Apagar todas las zonas"

                } else {

                    "Encender todas las zonas"
                }

        } else {

            btnAllZonesPower.visibility =
                View.GONE
        }


        /*
         * Sin conexión no se pueden
         * enviar acciones al dispositivo.
         */

        sliderIntensity.isEnabled =
            deviceConnected

        btnApplyIntensity.isEnabled =
            deviceConnected

        btnZonePower.isEnabled =
            deviceConnected

        btnAllZonesPower.isEnabled =
            deviceConnected
    }


    /* ============================= */
    /* NIVEL DE CALOR                */
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

        return when (intensity) {

            1 -> "Bajo"

            2 -> "Medio"

            3 -> "Alto"

            else -> "Medio"
        }
    }


    /* ============================= */
    /* COLOR DEL SLIDER              */
    /* ============================= */

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

        return when (intensity) {

            /*
             * BAJO
             */

            1 -> Color.rgb(
                255,
                183,
                77
            )


            /*
             * MEDIO
             */

            2 -> Color.rgb(
                255,
                128,
                54
            )


            /*
             * ALTO
             */

            3 -> Color.rgb(
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
    /* ESTADO DE ZONA                */
    /* ============================= */

    private fun isSelectedZoneEnabled(): Boolean {

        return when (selectedZone) {

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

        when (selectedZone) {

            Zone.BACK -> {
                backEnabled =
                    enabled
            }

            Zone.UPPER_BACK -> {
                upperBackEnabled =
                    enabled
            }

            Zone.LOWER_BACK -> {
                lowerBackEnabled =
                    enabled
            }
        }
    }


    /* ============================= */
    /* INTENSIDAD APLICADA           */
    /* ============================= */

    private fun getSelectedIntensity(): Int {

        return when (selectedZone) {

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

        when (selectedZone) {

            Zone.BACK -> {
                backIntensity =
                    intensity
            }

            Zone.UPPER_BACK -> {
                upperBackIntensity =
                    intensity
            }

            Zone.LOWER_BACK -> {
                lowerBackIntensity =
                    intensity
            }
        }
    }


    /* ============================= */
    /* NOMBRE DE ZONA                */
    /* ============================= */

    private fun getSelectedZoneName(): String {

        return when (selectedZone) {

            Zone.BACK ->
                "Espalda"

            Zone.UPPER_BACK ->
                "Espalda alta"

            Zone.LOWER_BACK ->
                "Espalda baja"
        }
    }


    /* ============================= */
    /* TODAS LAS ZONAS              */
    /* ============================= */

    private fun areAllZonesEnabled(): Boolean {

        return if (isV2()) {

            upperBackEnabled &&
                    lowerBackEnabled

        } else {

            backEnabled
        }
    }


    /* ============================= */
    /* VERIFICAR VERSIÓN             */
    /* ============================= */

    private fun isV2(): Boolean {

        return deviceVersion
            .uppercase() == "V2"
    }


    /* ============================= */
    /* DISPOSITIVO DESCONECTADO      */
    /* ============================= */

    private fun showDisconnectedMessage() {

        Toast.makeText(
            this,
            "El dispositivo está desconectado.",
            Toast.LENGTH_SHORT
        ).show()
    }


    override fun finish() {
        super.finish()
        ScreenTransitions.closeSlide(this)
    }


    /* ============================= */
    /* CONVERSIÓN DP                 */
    /* ============================= */

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }
}