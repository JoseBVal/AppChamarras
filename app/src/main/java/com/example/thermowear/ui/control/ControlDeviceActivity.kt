package com.example.thermowear.ui.control

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import android.widget.TextView

class ControlDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_control_device)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)

        val switchZone = findViewById<SwitchMaterial>(R.id.switchZone)
        val sliderTemperature = findViewById<Slider>(R.id.sliderTemperature)

        val btnAutomatic = findViewById<MaterialButton>(R.id.btnAutomatic)
        val btnPowerOff = findViewById<MaterialButton>(R.id.btnPowerOff)
        val txtHeatLevel = findViewById<TextView>(R.id.txtHeatLevel)

        btnBack.setOnClickListener {
            finish()
        }

        btnSettings.setOnClickListener {
            Toast.makeText(
                this,
                "Configuración del dispositivo",
                Toast.LENGTH_SHORT
            ).show()
        }

        switchZone.setOnCheckedChangeListener { _, isChecked ->

            val message = if (isChecked) {
                "Zona Pecho encendida"
            } else {
                "Zona Pecho apagada"
            }

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }

        sliderTemperature.addOnChangeListener { _, value, fromUser ->

            if (fromUser) {

                val nivel = when (value.toInt()) {
                    1 -> "Bajo"
                    2 -> "Medio"
                    3 -> "Alto"
                    else -> "Medio"
                }

                txtHeatLevel.text = "Nivel: $nivel"
            }
        }

        btnAutomatic.setOnClickListener {
            Toast.makeText(
                this,
                "Modo automático activado",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnPowerOff.setOnClickListener {
            Toast.makeText(
                this,
                "Todas las zonas apagadas",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}