package com.example.thermowear.ui.adddevice

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.thermowear.R
import com.example.thermowear.bluetooth.BleManager
import com.example.thermowear.data.DeviceManager
import com.example.thermowear.model.ThermoWearDevice
import com.example.thermowear.model.ThermoWearVersion
import com.example.thermowear.util.ScreenTransitions
import com.example.thermowear.ui.ThermoWearActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView


class AddDeviceActivity :
    ThermoWearActivity(),
    BleManager.Listener {


    private lateinit var btnScan:
            MaterialButton

    private lateinit var btnConnect2:
            MaterialButton

    private lateinit var txtScanStatus:
            TextView

    private lateinit var txtFoundTitle:
            TextView

    private lateinit var txtFoundDeviceName:
            TextView

    private lateinit var txtFoundDeviceInfo:
            TextView

    private lateinit var txtFoundDeviceStatus:
            TextView

    private lateinit var cardForgeV2:
            MaterialCardView


    private var foundAddress:
            String? =
        null


    /* ============================= */
    /* PERMISOS                      */
    /* ============================= */

    private val bluetoothPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts
                .RequestMultiplePermissions()
        ) { permissions ->


            val granted =
                permissions.values.all {
                    it
                }


            if (
                granted
            ) {

                startForgeScan()

            } else {

                Toast.makeText(
                    this,
                    "ThermoWear necesita permiso de Bluetooth para encontrar Forge V2.",
                    Toast.LENGTH_LONG
                ).show()


                resetScanButton()
            }
        }


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
            R.layout.activity_add_device
        )

        com.example.thermowear.util.WindowInsetsHelper.apply(this)


        BleManager.listener =
            this


        bindViews()

        configureButtons()
    }


    /* ============================= */
    /* VISTAS                        */
    /* ============================= */

    private fun bindViews() {

        btnScan =
            findViewById(
                R.id.btnScan
            )


        btnConnect2 =
            findViewById(
                R.id.btnConnect2
            )


        txtScanStatus =
            findViewById(
                R.id.txtScanStatus
            )


        txtFoundTitle =
            findViewById(
                R.id.txtFoundTitle
            )


        txtFoundDeviceName =
            findViewById(
                R.id.txtFoundDeviceName
            )


        txtFoundDeviceInfo =
            findViewById(
                R.id.txtFoundDeviceInfo
            )


        txtFoundDeviceStatus =
            findViewById(
                R.id.txtFoundDeviceStatus
            )


        cardForgeV2 =
            findViewById(
                R.id.cardForgeV2
            )
    }


    /* ============================= */
    /* BOTONES                       */
    /* ============================= */

    private fun configureButtons() {

        val btnBack =
            findViewById<ImageButton>(
                R.id.btnBack
            )


        btnBack.setOnClickListener {
            finish()
        }


        btnScan.setOnClickListener {

            checkPermissionsAndScan()
        }


        btnConnect2.setOnClickListener {

            val address =
                foundAddress


            if (
                address == null
            ) {

                Toast.makeText(
                    this,
                    "Primero busca Forge V2.",
                    Toast.LENGTH_SHORT
                ).show()


                return@setOnClickListener
            }


            btnConnect2.text =
                "Conectando..."


            btnConnect2.isEnabled =
                false


            txtFoundDeviceStatus.text =
                "● Conectando"


            BleManager.connectToAddress(
                this,
                address
            )
        }
    }


    /* ============================= */
    /* PERMISOS                      */
    /* ============================= */

    private fun checkPermissionsAndScan() {

        if (
            BleManager.hasPermissions(
                this
            )
        ) {

            startForgeScan()

            return
        }


        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            bluetoothPermissionLauncher.launch(

                arrayOf(

                    Manifest.permission
                        .BLUETOOTH_SCAN,

                    Manifest.permission
                        .BLUETOOTH_CONNECT
                )
            )

        } else {

            bluetoothPermissionLauncher.launch(

                arrayOf(

                    Manifest.permission
                        .ACCESS_FINE_LOCATION
                )
            )
        }
    }


    /* ============================= */
    /* ESCANEAR                      */
    /* ============================= */

    private fun startForgeScan() {

        if (
            !BleManager.isBluetoothEnabled(
                this
            )
        ) {

            Toast.makeText(
                this,
                "Activa Bluetooth en tu teléfono.",
                Toast.LENGTH_LONG
            ).show()


            return
        }


        foundAddress =
            null


        cardForgeV2.visibility =
            View.GONE


        txtFoundTitle.visibility =
            View.GONE


        txtScanStatus.text =
            "Buscando Forge V2..."


        btnScan.text =
            "Buscando..."


        btnScan.isEnabled =
            false


        BleManager.startScan(
            this
        )
    }


    /* ============================= */
    /* BLE ESCANEANDO                */
    /* ============================= */

    override fun onScanning() {

        runOnUiThread {

            txtScanStatus.text =
                "Buscando dispositivos BLE cercanos..."
        }
    }


    /* ============================= */
    /* BLE ENCONTRADO                */
    /* ============================= */

    override fun onDeviceFound(
        name: String,
        address: String
    ) {

        foundAddress =
            address


        runOnUiThread {

            txtScanStatus.text =
                "Forge V2 encontrada."


            txtFoundTitle.visibility =
                View.VISIBLE


            cardForgeV2.visibility =
                View.VISIBLE


            txtFoundDeviceName.text =
                name


            txtFoundDeviceInfo.text =
                "ESP32 · BLE"


            txtFoundDeviceStatus.text =
                "● Disponible"


            btnConnect2.text =
                "Conectar"


            btnConnect2.isEnabled =
                true


            btnScan.text =
                "Buscar de nuevo"


            btnScan.isEnabled =
                true
        }
    }


    /* ============================= */
    /* BLE CONECTANDO                */
    /* ============================= */

    override fun onConnecting() {

        runOnUiThread {

            txtFoundDeviceStatus.text =
                "● Conectando..."


            btnConnect2.text =
                "Conectando..."
        }
    }


    /* ============================= */
    /* BLE CONECTADO                 */
    /* ============================= */

    override fun onConnected(
        address: String
    ) {

        foundAddress =
            address


        runOnUiThread {

            txtFoundDeviceStatus.text =
                "● Conectado"


            txtFoundDeviceStatus.setTextColor(
                getColor(
                    R.color.tw_green
                )
            )


            btnConnect2.text =
                "Preparando..."
        }
    }


    /* ============================= */
    /* GATT LISTO                    */
    /* ============================= */

    override fun onReady() {

        runOnUiThread {

            txtFoundDeviceStatus.text =
                "● Conectado"


            btnConnect2.text =
                "Conectado"


            /*
             * BleManager manda PING
             * automáticamente.
             */
            txtScanStatus.text =
                "Comprobando comunicación con Forge V2..."
        }
    }


    /* ============================= */
    /* RESPUESTA ESP32               */
    /* ============================= */

    override fun onMessageReceived(
        message: String
    ) {

        runOnUiThread {

            if (
                message.trim() ==
                "PONG"
            ) {

                saveRealForgeV2()


                txtScanStatus.text =
                    "Forge V2 conectada correctamente."


                txtFoundDeviceStatus.text =
                    "● Conectado"


                btnConnect2.text =
                    "Conectado"


                Toast.makeText(
                    this,
                    "Forge V2 conectada correctamente.",
                    Toast.LENGTH_LONG
                ).show()


                btnConnect2.postDelayed(
                    {

                        finish()

                    },
                    700
                )

            } else {

                txtScanStatus.text =
                    "ESP32: $message"
            }
        }
    }


    /* ============================= */
    /* GUARDAR DISPOSITIVO           */
    /* ============================= */

    private fun saveRealForgeV2() {

        val address =
            foundAddress
                ?: return


        val device =
            ThermoWearDevice(

                id =
                    address,

                name =
                    "Chamarra Forge V2",

                version =
                    ThermoWearVersion.V2,

                battery =
                    0,

                connected =
                    true
            )


        DeviceManager.addOrUpdateDevice(
            this,
            device
        )
    }


    /* ============================= */
    /* DESCONECTADO                  */
    /* ============================= */

    override fun onDisconnected() {

        runOnUiThread {

            txtFoundDeviceStatus.text =
                "● Desconectado"


            btnConnect2.text =
                "Conectar"


            btnConnect2.isEnabled =
                true
        }
    }


    /* ============================= */
    /* ERROR                         */
    /* ============================= */

    override fun onError(
        message: String
    ) {

        runOnUiThread {

            txtScanStatus.text =
                message


            resetScanButton()


            Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }


    /* ============================= */
    /* REINICIAR BOTÓN               */
    /* ============================= */

    private fun resetScanButton() {

        btnScan.text =
            "Buscar dispositivos"


        btnScan.isEnabled =
            true
    }


    /* ============================= */
    /* DESTROY                       */
    /* ============================= */

    override fun onDestroy() {

        if (
            BleManager.listener ===
            this
        ) {

            BleManager.listener =
                null
        }


        super.onDestroy()
    }


    /* ============================= */
    /* VOLVER                        */
    /* ============================= */

    override fun finish() {

        BleManager.stopScan()


        super.finish()


        ScreenTransitions.closeSlide(
            this
        )
    }
}
