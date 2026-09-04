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

    /*
     * Representación visual del calor.
     */
    private lateinit var heatBack: View
    private lateinit var heatUpperBack: View
    private lateinit var heatLowerBack: View


    /* ============================= */
    /* DATOS DEL DISPOSITIVO         */
    /* ============================= */

    private var deviceId: String = ""
    private var deviceName: String = "Chamarra Forge"
    private var deviceVersion: String = "V1"
    private var deviceConnected: Boolean = false


    /* ============================= */
    /* ZONAS                         */
    /* ============================= */

    private enum class Zone {
        BACK,
        UPPER_BACK,
        LOWER_BACK
    }

    private var selectedZone: Zone =
        Zone.BACK


    /* ============================= */
    /* ESTADO DE ENCENDIDO           */
    /* ============================= */

    private var backEnabled =
        false

    private var upperBackEnabled =
        false

    private var lowerBackEnabled =
        false


    /* ============================= */
    /* INTENSIDAD APLICADA           */
    /* ============================= */

    private var backIntensity =
        2

    private var upperBackIntensity =
        2

    private var lowerBackIntensity =
        2


    /* ============================= */
    /* INTENSIDAD SELECCIONADA       */
    /* ============================= */

    private var pendingIntensity =
        2


    /* ============================= */
    /* CICLO DE VIDA                 */
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
    /* TRANSICIÓN AL VOLVER          */
    /* ============================= */

    override fun finish() {

        super.finish()

        ScreenTransitions.closeSlide(
            this
        )
    }


    /* ============================= */
    /* OBTENER DATOS                 */
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

                "● Conectado"

            } else {

                "● Desconectado"
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


        when (
            deviceVersion.uppercase()
        ) {

            "V2" ->
                configureV2()

            else ->
                configureV1()
        }
    }


    /* ============================= */
    /* FORGE V1                      */
    /* ============================= */

    private fun configureV1() {

        selectedZone =
            Zone.BACK


        pendingIntensity =
            backIntensity


        txtDeviceVersion.text =
            "Forge V1 · 1 zona"


        txtPrimaryZoneName.text =
            "Espalda"


        txtPrimaryZoneInfo.text =
            "Apagada"


        cardZoneSecondary.visibility =
            View.GONE


        containerTemperature.visibility =
            View.GONE


        btnAllZonesPower.visibility =
            View.GONE
    }


    /* ============================= */
    /* FORGE V2                      */
    /* ============================= */

    private fun configureV2() {

        selectedZone =
            Zone.UPPER_BACK


        pendingIntensity =
            upperBackIntensity


        txtDeviceVersion.text =
            "Forge V2 · 2 zonas"


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
         * Sensor solamente en espalda alta.
         */
        containerTemperature.visibility =
            View.VISIBLE
    }


    /* ============================= */
    /* SELECCIÓN DE ZONAS            */
    /* ============================= */

    private fun configureZoneSelection() {

        cardZonePrimary
            .setOnClickListener {

                selectedZone =
                    if (isV2()) {

                        Zone.UPPER_BACK

                    } else {

                        Zone.BACK
                    }


                pendingIntensity =
                    getSelectedIntensity()


                updateInterface()
            }


        cardZoneSecondary
            .setOnClickListener {

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

        sliderIntensity
            .addOnChangeListener {
                    _,
                    value,
                    fromUser ->


                if (!fromUser) {

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

        /* ============================= */
        /* APLICAR INTENSIDAD             */
        /* ============================= */

        btnApplyIntensity
            .setOnClickListener {

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
                    if (
                        isSelectedZoneEnabled()
                    ) {

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

        btnZonePower
            .setOnClickListener {

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

        btnAllZonesPower
            .setOnClickListener {

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

        updateHeatMap()
    }


    /* ============================= */
    /* MAPA DE CALOR                 */
    /* ============================= */

    private fun updateHeatMap() {

        if (isV2()) {

            /*
             * En Forge V2 existen dos
             * zonas independientes.
             */
            heatBack.visibility =
                View.GONE


            heatUpperBack.visibility =
                if (upperBackEnabled) {

                    View.VISIBLE

                } else {

                    View.GONE
                }


            heatLowerBack.visibility =
                if (lowerBackEnabled) {

                    View.VISIBLE

                } else {

                    View.GONE
                }

        } else {

            /*
             * Forge V1 tiene una sola
             * zona general en la espalda.
             */
            heatUpperBack.visibility =
                View.GONE


            heatLowerBack.visibility =
                View.GONE


            heatBack.visibility =
                if (backEnabled) {

                    View.VISIBLE

                } else {

                    View.GONE
                }
        }
    }


    /* ============================= */
    /* ZONA SELECCIONADA             */
    /* ============================= */

    private fun updateSelectedZone() {

        txtSelectedZone.text =
            getSelectedZoneName()


        if (
            isV2() &&
            selectedZone ==
            Zone.UPPER_BACK
        ) {

            containerTemperature.visibility =
                View.VISIBLE


            /*
             * Simulación hasta conectar
             * el sensor real del ESP32.
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
    /* ESTADO DE ZONA                */
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


    /* ============================= */
    /* TODAS LAS ZONAS              */
    /* ============================= */

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


    /* ============================= */
    /* VERIFICAR VERSIÓN             */
    /* ============================= */

    private fun isV2():
            Boolean {

        return deviceVersion
            .uppercase() ==
                "V2"
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


    /* ============================= */
    /* CONVERSIÓN DP                 */
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