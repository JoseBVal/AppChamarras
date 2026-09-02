package com.example.thermowear.ui.adddevice

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.google.android.material.button.MaterialButton

class AddDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_device)

        val btnBack = findViewById<android.widget.ImageButton>(R.id.btnBack)
        val btnConnect1 = findViewById<MaterialButton>(R.id.btnConnect1)
        val btnConnect2 = findViewById<MaterialButton>(R.id.btnConnect2)

        btnBack.setOnClickListener {
            finish()
        }

        btnConnect1.setOnClickListener {
            Toast.makeText(
                this,
                "Conectando Chamarra Térmica 001...",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnConnect2.setOnClickListener {
            Toast.makeText(
                this,
                "Conectando Chamarra Térmica 002...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}