package com.example.thermowear.ui.adddevice

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.data.DeviceManager
import com.example.thermowear.model.ThermoWearDevice
import com.example.thermowear.model.ThermoWearVersion
import com.example.thermowear.util.ScreenTransitions
import com.google.android.material.button.MaterialButton

class AddDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_device)


        val btnBack =
            findViewById<ImageButton>(
                R.id.btnBack
            )


        val btnConnect1 =
            findViewById<MaterialButton>(
                R.id.btnConnect1
            )


        val btnConnect2 =
            findViewById<MaterialButton>(
                R.id.btnConnect2
            )


        /* ============================= */
        /* VOLVER                        */
        /* ============================= */

        btnBack.setOnClickListener {
            finish()
        }


        /* ============================= */
        /* FORGE V1                      */
        /* ============================= */

        btnConnect1.setOnClickListener {

            connectDevice(
                ThermoWearDevice(
                    id = "TW-V1-001",
                    name = "Chamarra Forge V1",
                    version = ThermoWearVersion.V1,
                    battery = 80,
                    connected = true
                )
            )
        }


        /* ============================= */
        /* FORGE V2                      */
        /* ============================= */

        btnConnect2.setOnClickListener {

            connectDevice(
                ThermoWearDevice(
                    id = "TW-V2-001",
                    name = "Chamarra Forge V2",
                    version = ThermoWearVersion.V2,
                    battery = 85,
                    connected = true
                )
            )
        }
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
    /* CONECTAR DISPOSITIVO          */
    /* ============================= */

    private fun connectDevice(
        device: ThermoWearDevice
    ) {

        val alreadyAdded =
            DeviceManager.isDeviceAdded(
                this,
                device.id
            )


        DeviceManager.addOrUpdateDevice(
            this,
            device
        )


        if (alreadyAdded) {

            Toast.makeText(
                this,
                "${device.name} reconectada",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            Toast.makeText(
                this,
                "${device.name} añadida correctamente",
                Toast.LENGTH_SHORT
            ).show()
        }


        finish()
    }
}