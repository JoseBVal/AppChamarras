package com.example.thermowear.ui.devices

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import com.example.thermowear.ui.adddevice.AddDeviceActivity
import com.example.thermowear.ui.control.ControlDeviceActivity

class DevicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_devices)

        val btnAdd = findViewById<FloatingActionButton>(R.id.btnAddDevice)
        val btnControl = findViewById<MaterialButton>(R.id.btnControl)

        btnAdd.setOnClickListener {
            val intent = Intent(
                this,
                AddDeviceActivity::class.java
            )

            startActivity(intent)
        }

        btnControl.setOnClickListener {

            val intent = Intent(
                this,
                ControlDeviceActivity::class.java
            )

            startActivity(intent)
        }
    }
}