package com.example.thermowear.ui.devices

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.thermowear.R
import com.example.thermowear.data.AlertManager
import com.example.thermowear.data.DeviceManager
import com.example.thermowear.model.ThermoWearDevice
import com.example.thermowear.notifications.NotificationHelper
import com.example.thermowear.ui.adddevice.AddDeviceActivity
import com.example.thermowear.ui.alerts.AlertsActivity
import com.example.thermowear.ui.control.ControlDeviceActivity
import com.example.thermowear.ui.history.HistoryActivity
import com.example.thermowear.ui.settings.SettingsActivity
import com.example.thermowear.util.ScreenTransitions
import com.google.android.material.button.MaterialButton

class DevicesActivity : AppCompatActivity() {

    private lateinit var containerDevices:
            LinearLayout

    private lateinit var emptyState:
            LinearLayout


    /*
     * Solicitud moderna del permiso
     * de notificaciones.
     */
    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
                granted ->

            if (!granted) {

                Toast.makeText(
                    this,
                    "Puedes activar las notificaciones más adelante desde los ajustes de Android.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        setContentView(
            R.layout.activity_devices
        )


        /*
         * Crear el canal es seguro aunque
         * ya exista.
         */
        NotificationHelper
            .createNotificationChannel(
                this
            )


        requestNotificationPermissionIfNeeded()


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


        /* ============================= */
        /* AÑADIR DISPOSITIVO            */
        /* ============================= */

        btnAddDevice
            .setOnClickListener {

                openAddDevice()
            }


        btnEmptyAddDevice
            .setOnClickListener {

                openAddDevice()
            }


        /* ============================= */
        /* NAVEGACIÓN                    */
        /* ============================= */

        configureNavigation()

        configureBottomNavigation()
    }


    override fun onResume() {

        super.onResume()


        loadDevices()

        updateAlertIndicator()
    }


    /* ============================= */
    /* PERMISO DE NOTIFICACIONES     */
    /* ============================= */

    private fun requestNotificationPermissionIfNeeded() {

        if (
            Build.VERSION.SDK_INT <
            Build.VERSION_CODES.TIRAMISU
        ) {

            return
        }


        if (
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {

            return
        }


        notificationPermissionLauncher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }


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

        containerDevices
            .removeAllViews()


        val devices =
            DeviceManager.getDevices(
                this
            )


        if (devices.isEmpty()) {

            emptyState.visibility =
                View.VISIBLE


            containerDevices.visibility =
                View.GONE

        } else {

            emptyState.visibility =
                View.GONE


            containerDevices.visibility =
                View.VISIBLE


            devices.forEach {
                    device ->

                addDeviceCard(
                    device
                )
            }
        }
    }


    /* ============================= */
    /* CREAR TARJETA                 */
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


        /* ============================= */
        /* DATOS                         */
        /* ============================= */

        val versionName =
            device.version.name


        val displayName =
            "Chamarra Forge $versionName"


        txtDeviceName.text =
            displayName


        val zoneText =
            if (device.zoneCount == 1) {

                "1 zona"

            } else {

                "${device.zoneCount} zonas"
            }


        txtDeviceVersion.text =
            "Forge $versionName · $zoneText"


        /* ============================= */
        /* CONEXIÓN                      */
        /* ============================= */

        if (device.connected) {

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


        /* ============================= */
        /* ABRIR CONTROL                 */
        /* ============================= */

        view.setOnClickListener {

            if (device.connected) {

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


        /* ============================= */
        /* CONECTAR / DESCONECTAR        */
        /* ============================= */

        btnConnectionAction
            .setOnClickListener {

                val newConnectionState =
                    !device.connected


                DeviceManager
                    .setConnection(
                        this,
                        device.id,
                        newConnectionState
                    )


                val message =
                    if (newConnectionState) {

                        "$displayName conectada"

                    } else {

                        "$displayName desconectada"
                    }


                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_SHORT
                ).show()


                loadDevices()
            }


        /* ============================= */
        /* ELIMINAR                      */
        /* ============================= */

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
    /* CONTROL DEL DISPOSITIVO       */
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


        /*
         * Nombre visual correcto para
         * la pantalla de Control.
         */
        intent.putExtra(
            "DEVICE_NAME",
            displayName
        )


        intent.putExtra(
            "DEVICE_VERSION",
            device.version.name
        )


        /*
         * Se mantiene temporalmente por
         * compatibilidad con ControlDeviceActivity.
         *
         * Ya no se muestra en Mis Dispositivos.
         */
        intent.putExtra(
            "DEVICE_BATTERY",
            device.battery
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
    /* CONFIRMAR ELIMINACIÓN         */
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
            ) {
                    _,
                    _ ->


                DeviceManager
                    .removeDevice(
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
    /* NAVEGACIÓN INFERIOR           */
    /* ============================= */

    private fun configureNavigation() {

        findViewById<LinearLayout>(
            R.id.navHistory
        ).setOnClickListener {

            ScreenTransitions.openFade(
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

            ScreenTransitions.openFade(
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

            ScreenTransitions.openFade(
                this,
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }


    /* ============================= */
    /* INICIO SELECCIONADO           */
    /* ============================= */

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


    /* ============================= */
    /* PUNTO ROJO DE ALERTAS         */
    /* ============================= */

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