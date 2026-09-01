package com.example.thermowear.ui.devices

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DevicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_devices)

        val btnAdd = findViewById<FloatingActionButton>(R.id.btnAddDevice)
        val btnControl = findViewById<MaterialButton>(R.id.btnControl)

        btnAdd.setOnClickListener {
            Toast.makeText(
                this,
                "Añadir dispositivo",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnControl.setOnClickListener {
            Toast.makeText(
                this,
                "Control del dispositivo",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}