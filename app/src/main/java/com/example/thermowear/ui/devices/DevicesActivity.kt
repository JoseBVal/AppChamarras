package com.example.thermowear.ui.devices

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.thermowear.R
import com.example.thermowear.bluetooth.BleManager
import com.example.thermowear.data.AlertManager
import com.example.thermowear.data.DeviceManager
import com.example.thermowear.data.HistoryManager
import com.example.thermowear.data.HistorySessionTracker
import com.example.thermowear.model.ThermoWearDevice
import com.example.thermowear.model.ThermoWearVersion
import com.example.thermowear.notifications.NotificationHelper
import com.example.thermowear.ui.adddevice.AddDeviceActivity
import com.example.thermowear.ui.alerts.AlertsActivity
import com.example.thermowear.ui.control.ControlDeviceActivity
import com.example.thermowear.ui.history.HistoryActivity
import com.example.thermowear.ui.settings.SettingsActivity
import com.example.thermowear.ui.ThermoWearActivity
import com.example.thermowear.util.ScreenTransitions
import com.google.android.material.button.MaterialButton


class DevicesActivity :
    ThermoWearActivity(),
    BleManager.Listener {

    override val isNavigationMenu = true

    private lateinit var containerDevices:
            LinearLayout

    private lateinit var emptyState:
            LinearLayout


    private var reconnectingDeviceId:
            String? =
        null


    /* ============================= */
    /* BLUETOOTH                     */
    /* ============================= */

    private val bluetoothPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted =
                permissions.values.all {
                    it
                }


            if (!granted) {

                Toast.makeText(
                    this,
                    "ThermoWear necesita permiso Bluetooth.",
                    Toast.LENGTH_LONG
                ).show()
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

        HistoryManager.removeLegacyDemoHistoryIfPresent(this)
        HistorySessionTracker.recoverInterruptedSession(this)


        setContentView(
            R.layout.activity_devices
        )

        com.example.thermowear.util.WindowInsetsHelper.apply(this)


        NotificationHelper
            .createNotificationChannel(
                this
            )


        containerDevices =
            findViewById(
                R.id.containerDevices
            )


        emptyState =
            findViewById(
                R.id.emptyState
            )


        val btnAddDevice =
            findViewById<MaterialButton>(
                R.id.btnAddDevice
            )


        val btnEmptyAddDevice =
            findViewById<MaterialButton>(
                R.id.btnEmptyAddDevice
            )


        btnAddDevice.setOnClickListener {

            openAddDevice()
        }


        btnEmptyAddDevice.setOnClickListener {

            openAddDevice()
        }


        configureNavigation()

        configureBottomNavigation()
    }


    /* ============================= */
    /* ON RESUME                     */
    /* ============================= */

    override fun onResume() {

        super.onResume()


        BleManager.listener =
            this


        loadDevices()

        updateAlertIndicator()
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
    /* ABRIR AÑADIR                  */
    /* ============================= */

    private fun openAddDevice() {

        ScreenTransitions.openSlide(
            this,
            Intent(
                this,
                AddDeviceActivity::class.java
            )
        )
    }


    /* ============================= */
    /* CARGAR DISPOSITIVOS           */
    /* ============================= */

    private fun loadDevices() {

        containerDevices.removeAllViews()


        val devices =
            DeviceManager.getDevices(
                this
            )


        if (
            devices.isEmpty()
        ) {

            emptyState.visibility =
                View.VISIBLE


            containerDevices.visibility =
                View.GONE


            return
        }


        emptyState.visibility =
            View.GONE


        containerDevices.visibility =
            View.VISIBLE


        devices.forEach { device ->

            addDeviceCard(
                device
            )
        }
    }


    /* ============================= */
    /* TARJETA                       */
    /* ============================= */

    private fun addDeviceCard(
        device: ThermoWearDevice
    ) {

        val view =
            layoutInflater.inflate(
                R.layout.item_device,
                containerDevices,
                false
            )


        val txtDeviceName =
            view.findViewById<TextView>(
                R.id.txtDeviceName
            )


        val txtDeviceVersion =
            view.findViewById<TextView>(
                R.id.txtDeviceVersion
            )


        val txtDeviceStatus =
            view.findViewById<TextView>(
                R.id.txtDeviceStatus
            )


        val btnConnectionAction =
            view.findViewById<MaterialButton>(
                R.id.btnConnectionAction
            )


        val btnDeleteDevice =
            view.findViewById<MaterialButton>(
                R.id.btnDeleteDevice
            )


        val versionName =
            device.version.name


        val displayName =
            "Chamarra Forge $versionName"


        txtDeviceName.text =
            displayName


        val zoneText =
            if (
                device.zoneCount == 1
            ) {

                "1 zona"

            } else {

                "${device.zoneCount} zonas"
            }


        txtDeviceVersion.text =
            "Forge $versionName · $zoneText"


        /* ========================= */
        /* ESTADO                    */
        /* ========================= */

        if (
            device.connected
        ) {

            txtDeviceStatus.text =
                "● Conectado"


            txtDeviceStatus.setTextColor(
                getColor(
                    R.color.tw_green
                )
            )


            btnConnectionAction.text =
                "Desconectar"

        } else {

            txtDeviceStatus.text =
                "● Desconectado"


            txtDeviceStatus.setTextColor(
                getColor(
                    R.color.tw_text_light
                )
            )


            btnConnectionAction.text =
                "Conectar"
        }


        /* ========================= */
        /* ABRIR CONTROL             */
        /* ========================= */

        view.setOnClickListener {

            if (
                device.connected
            ) {

                openDevice(
                    device,
                    displayName
                )

            } else {

                Toast.makeText(
                    this,
                    "Conecta la chamarra antes de controlarla.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        /* ========================= */
        /* CONECTAR/DESCONECTAR      */
        /* ========================= */

        btnConnectionAction
            .setOnClickListener {


                if (
                    device.version ==
                    ThermoWearVersion.V1
                ) {

                    Toast.makeText(
                        this,
                        "Forge V1 no utiliza Bluetooth.",
                        Toast.LENGTH_SHORT
                    ).show()


                    return@setOnClickListener
                }


                if (
                    device.connected
                ) {

                    BleManager.disconnect()


                    DeviceManager.setConnection(
                        this,
                        device.id,
                        false
                    )


                    loadDevices()

                } else {

                    reconnectingDeviceId =
                        device.id


                    Toast.makeText(
                        this,
                        "Conectando con Forge ${device.version.name}...",
                        Toast.LENGTH_SHORT
                    ).show()


                    BleManager.connectToAddress(
                        this,
                        device.id
                    )
                }
            }


        /* ========================= */
        /* ELIMINAR                  */
        /* ========================= */

        btnDeleteDevice
            .setOnClickListener {

                showDeleteDialog(
                    device,
                    displayName
                )
            }


        containerDevices.addView(
            view
        )
    }


    /* ============================= */
    /* ABRIR CONTROL                 */
    /* ============================= */

    private fun openDevice(
        device: ThermoWearDevice,
        displayName: String
    ) {

        val intent =
            Intent(
                this,
                ControlDeviceActivity::class.java
            )


        intent.putExtra(
            "DEVICE_ID",
            device.id
        )


        intent.putExtra(
            "DEVICE_NAME",
            displayName
        )


        intent.putExtra(
            "DEVICE_VERSION",
            device.version.name
        )


        intent.putExtra(
            "DEVICE_CONNECTED",
            device.connected
        )


        ScreenTransitions.openSlide(
            this,
            intent
        )
    }


    /* ============================= */
    /* ELIMINAR                      */
    /* ============================= */

    private fun showDeleteDialog(
        device: ThermoWearDevice,
        displayName: String
    ) {

        AlertDialog.Builder(
            this
        )
            .setTitle(
                "Eliminar dispositivo"
            )
            .setMessage(
                "¿Deseas eliminar $displayName de ThermoWear?"
            )
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Eliminar"
            ) { _, _ ->


                if (
                    device.connected &&
                    device.version != ThermoWearVersion.V1
                ) {

                    BleManager.disconnect()
                }


                DeviceManager.removeDevice(
                    this,
                    device.id
                )


                Toast.makeText(
                    this,
                    "Dispositivo eliminado",
                    Toast.LENGTH_SHORT
                ).show()


                loadDevices()
            }
            .show()
    }


    /* ============================= */
    /* BLE                           */
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

            Toast.makeText(
                this,
                "Bluetooth conectado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onReady() {

        runOnUiThread {

            reconnectingDeviceId?.let {

                DeviceManager.setConnection(
                    this,
                    it,
                    true
                )
            }


            val connectedVersion = reconnectingDeviceId
                ?.let { id -> DeviceManager.getDevices(this).firstOrNull { it.id == id }?.version?.name }

            reconnectingDeviceId =
                null


            loadDevices()

            Toast.makeText(
                this,
                connectedVersion?.let { "Forge $it conectada" } ?: "Chamarra conectada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onMessageReceived(
        message: String
    ) {
    }


    override fun onDisconnected() {

        runOnUiThread {

            DeviceManager
                .getDevices(
                    this
                )
                .filter {
                    it.version != ThermoWearVersion.V1
                }
                .forEach {

                    DeviceManager.setConnection(
                        this,
                        it.id,
                        false
                    )
                }


            loadDevices()


            Toast.makeText(
                this,
                "Chamarra desconectada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onError(
        message: String
    ) {

        runOnUiThread {

            reconnectingDeviceId =
                null


            Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            ).show()


            loadDevices()
        }
    }


    /* ============================= */
    /* NAVEGACIÓN                    */
    /* ============================= */

    private fun configureNavigation() {

        findViewById<LinearLayout>(
            R.id.navHistory
        ).setOnClickListener {

            ScreenTransitions.openModule(
                this,
                Intent(
                    this,
                    HistoryActivity::class.java
                )
            )
        }


        findViewById<LinearLayout>(
            R.id.navAlerts
        ).setOnClickListener {

            ScreenTransitions.openModule(
                this,
                Intent(
                    this,
                    AlertsActivity::class.java
                )
            )
        }


        findViewById<LinearLayout>(
            R.id.navSettings
        ).setOnClickListener {

            ScreenTransitions.openModule(
                this,
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }


    private fun configureBottomNavigation() {

        val bgNavHome =
            findViewById<LinearLayout>(
                R.id.bgNavHome
            )


        val iconNavHome =
            findViewById<ImageView>(
                R.id.iconNavHome
            )


        val txtNavHome =
            findViewById<TextView>(
                R.id.txtNavHome
            )


        bgNavHome.setBackgroundResource(
            R.drawable.bg_nav_selected
        )


        iconNavHome.setColorFilter(
            getColor(
                R.color.tw_text
            )
        )


        txtNavHome.setTextColor(
            getColor(
                R.color.tw_text
            )
        )
    }


    private fun updateAlertIndicator() {

        val alertDot =
            findViewById<View>(
                R.id.alertDot
            )


        alertDot.visibility =
            if (
                AlertManager.hasUnreadAlerts(
                    this
                )
            ) {

                View.VISIBLE

            } else {

                View.GONE
            }
    }
}
