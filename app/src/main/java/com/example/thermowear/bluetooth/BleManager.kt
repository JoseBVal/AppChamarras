package com.example.thermowear.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.thermowear.data.AlertManager
import com.example.thermowear.data.DeviceManager
import com.example.thermowear.data.HistorySessionTracker
import java.util.UUID
import kotlin.math.roundToInt


object BleManager {

    /* ============================= */
    /* NOMBRE DEL ESP32              */
    /* ============================= */

    private val DEVICE_NAMES = setOf("Forge V2")


    /* ============================= */
    /* UUID DEL ESP32                */
    /* ============================= */

    private val SERVICE_UUID =
        UUID.fromString(
            "7c2c0001-6f4e-4f52-4745-563200000001"
        )


    private val COMMAND_UUID =
        UUID.fromString(
            "7c2c0002-6f4e-4f52-4745-563200000002"
        )


    private val STATUS_UUID =
        UUID.fromString(
            "7c2c0003-6f4e-4f52-4745-563200000003"
        )


    private val CCCD_UUID =
        UUID.fromString(
            "00002902-0000-1000-8000-00805f9b34fb"
        )


    /* ============================= */
    /* CALLBACK HACIA LA ACTIVITY    */
    /* ============================= */

    interface Listener {

        fun onScanning()

        fun onDeviceFound(
            name: String,
            address: String
        )

        fun onConnecting()

        fun onConnected(
            address: String
        )

        fun onReady()

        fun onMessageReceived(
            message: String
        )

        fun onDisconnected()

        fun onError(
            message: String
        )
    }


    var listener: Listener? =
        null


    /* ============================= */
    /* VARIABLES BLE                 */
    /* ============================= */

    private var bluetoothGatt:
            BluetoothGatt? =
        null


    private var commandCharacteristic:
            BluetoothGattCharacteristic? =
        null


    private var statusCharacteristic:
            BluetoothGattCharacteristic? =
        null


    private var applicationContext:
            Context? =
        null


    private var scanning =
        false

    private var temperaturePolling = false
    private var lastMaximumTemperatureAlertKey: String? = null
    private var lastMaximumTemperatureAlertAt = 0L
    private val temperaturePollRunnable = object : Runnable {
        override fun run() {
            if (!temperaturePolling) return
            sendCommand("TEMP_REQUEST")
            mainHandler.postDelayed(this, 3000)
        }
    }


    private val mainHandler =
        Handler(
            Looper.getMainLooper()
        )


    /* ============================= */
    /* ADAPTADOR BLUETOOTH           */
    /* ============================= */

    private fun getBluetoothAdapter(
        context: Context
    ): BluetoothAdapter? {

        val manager =
            context.getSystemService(
                Context.BLUETOOTH_SERVICE
            ) as BluetoothManager


        return manager.adapter
    }


    /* ============================= */
    /* PERMISOS                      */
    /* ============================= */

    fun hasPermissions(
        context: Context
    ): Boolean {

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) ==
                    PackageManager.PERMISSION_GRANTED &&

                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) ==
                    PackageManager.PERMISSION_GRANTED

        } else {

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }


    /* ============================= */
    /* BLUETOOTH ENCENDIDO           */
    /* ============================= */

    @SuppressLint("MissingPermission")
    fun isBluetoothEnabled(
        context: Context
    ): Boolean {

        return getBluetoothAdapter(
            context
        )?.isEnabled == true
    }


    /* ============================= */
    /* ESCANEAR                      */
    /* ============================= */

    @SuppressLint("MissingPermission")
    fun startScan(
        context: Context
    ) {

        applicationContext =
            context.applicationContext


        if (
            !hasPermissions(
                context
            )
        ) {

            listener?.onError(
                "Faltan permisos de Bluetooth."
            )

            return
        }


        val adapter =
            getBluetoothAdapter(
                context
            )


        if (
            adapter == null
        ) {

            listener?.onError(
                "Este dispositivo no tiene Bluetooth."
            )

            return
        }


        if (
            !adapter.isEnabled
        ) {

            listener?.onError(
                "Activa Bluetooth antes de buscar una chamarra Forge."
            )

            return
        }


        val scanner =
            adapter.bluetoothLeScanner


        if (
            scanner == null
        ) {

            listener?.onError(
                "No se pudo iniciar el escáner BLE."
            )

            return
        }


        stopScan()


        scanning =
            true


        listener?.onScanning()


        scanner.startScan(
            scanCallback
        )


        /*
         * Máximo 12 segundos de búsqueda.
         */
        mainHandler.postDelayed(
            {

                if (
                    scanning
                ) {

                    stopScan()


                    listener?.onError(
                        "No se encontró una chamarra Forge V2."
                    )
                }

            },
            12_000
        )
    }


    /* ============================= */
    /* DETENER ESCANEO               */
    /* ============================= */

    @SuppressLint("MissingPermission")
    fun stopScan() {

        val context =
            applicationContext
                ?: return


        val adapter =
            getBluetoothAdapter(
                context
            )
                ?: return


        val scanner =
            adapter.bluetoothLeScanner
                ?: return


        if (
            scanning
        ) {

            scanner.stopScan(
                scanCallback
            )
        }


        scanning =
            false
    }


    /* ============================= */
    /* CALLBACK ESCANEO              */
    /* ============================= */

    private val scanCallback =
        object :
            ScanCallback() {

            @SuppressLint("MissingPermission")
            override fun onScanResult(
                callbackType: Int,
                result: ScanResult
            ) {

                val device =
                    result.device


                val name =
                    try {

                        device.name

                    } catch (
                        exception: SecurityException
                    ) {

                        null
                    }
                        ?: result
                            .scanRecord
                            ?.deviceName
                        ?: return


                if (DEVICE_NAMES.any { name.equals(it, ignoreCase = true) }) {

                    val address =
                        device.address


                    listener?.onDeviceFound(
                        name,
                        address
                    )


                    stopScan()


                    connect(
                        device
                    )
                }
            }


            override fun onScanFailed(
                errorCode: Int
            ) {

                scanning =
                    false


                listener?.onError(
                    "Error al buscar BLE: $errorCode"
                )
            }
        }


    /* ============================= */
    /* CONECTAR                      */
    /* ============================= */

    @SuppressLint("MissingPermission")
    private fun connect(
        device: android.bluetooth.BluetoothDevice
    ) {

        val context =
            applicationContext
                ?: return


        listener?.onConnecting()


        bluetoothGatt
            ?.close()


        bluetoothGatt =
            device.connectGatt(
                context,
                false,
                gattCallback,
                android.bluetooth.BluetoothDevice.TRANSPORT_LE
            )
    }


    /* ============================= */
    /* CALLBACK GATT                 */
    /* ============================= */

    private val gattCallback =
        object :
            BluetoothGattCallback() {


            @SuppressLint("MissingPermission")
            override fun onConnectionStateChange(
                gatt: BluetoothGatt,
                status: Int,
                newState: Int
            ) {

                if (
                    newState ==
                    BluetoothProfile.STATE_CONNECTED
                ) {

                    listener?.onConnected(
                        gatt.device.address
                    )


                    gatt.discoverServices()

                } else if (
                    newState ==
                    BluetoothProfile.STATE_DISCONNECTED
                ) {

                    commandCharacteristic =
                        null


                    statusCharacteristic =
                        null

                    stopTemperaturePolling()
                    applicationContext?.let { context ->
                        HistorySessionTracker.finishActiveSession(
                            context,
                            gatt.device.address
                        )
                    }
                    listener?.onDisconnected()
                }
            }


            @SuppressLint("MissingPermission")
            override fun onServicesDiscovered(
                gatt: BluetoothGatt,
                status: Int
            ) {

                if (
                    status !=
                    BluetoothGatt.GATT_SUCCESS
                ) {

                    listener?.onError(
                        "No se pudieron descubrir los servicios BLE."
                    )

                    return
                }


                val service:
                        BluetoothGattService? =
                    gatt.getService(
                        SERVICE_UUID
                    )


                if (
                    service == null
                ) {

                    listener?.onError(
                        "El dispositivo no tiene el servicio BLE de ThermoWear."
                    )

                    return
                }


                commandCharacteristic =
                    service.getCharacteristic(
                        COMMAND_UUID
                    )


                statusCharacteristic =
                    service.getCharacteristic(
                        STATUS_UUID
                    )


                if (
                    commandCharacteristic == null
                ) {

                    listener?.onError(
                        "No se encontró la característica de comandos."
                    )

                    return
                }


                if (
                    statusCharacteristic == null
                ) {

                    listener?.onError(
                        "No se encontró la característica de estado."
                    )

                    return
                }


                enableNotifications(
                    gatt
                )
            }


            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic:
                BluetoothGattCharacteristic
            ) {

                if (
                    characteristic.uuid !=
                    STATUS_UUID
                ) {

                    return
                }


                val message =
                    characteristic.value
                        ?.toString(
                            Charsets.UTF_8
                        )
                        ?: return


                dispatchStatusMessage(gatt, message)
            }


            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic:
                BluetoothGattCharacteristic,
                value: ByteArray
            ) {

                if (
                    characteristic.uuid !=
                    STATUS_UUID
                ) {

                    return
                }


                val message =
                    value.toString(
                        Charsets.UTF_8
                    )


                dispatchStatusMessage(gatt, message)
            }


            @SuppressLint("MissingPermission")
            override fun onDescriptorWrite(
                gatt: BluetoothGatt,
                descriptor:
                BluetoothGattDescriptor,
                status: Int
            ) {

                if (
                    descriptor.uuid ==
                    CCCD_UUID
                ) {

                    listener?.onReady()


                    /*
                     * PRUEBA AUTOMÁTICA.
                     */
                    sendCommand(
                        "PING"
                    )
                }
            }
        }


    @SuppressLint("MissingPermission")
    private fun dispatchStatusMessage(
        gatt: BluetoothGatt,
        message: String
    ) {
        val received = message.trim()
        applicationContext?.let { context ->
            when {
                received.startsWith("TEMP:") ->
                    HistorySessionTracker.touchActiveSession(
                        context,
                        gatt.device.address
                    )

                received.startsWith("MAX_TEMP:") ||
                        received == "ERROR:TEMP_SENSOR" ||
                        received == "ERROR:THERMAL_LOCKOUT" ->
                    HistorySessionTracker.finishActiveSession(
                        context,
                        gatt.device.address
                    )
            }
        }

        if (received.startsWith("MAX_TEMP:")) {
            val temperature =
                received.substringAfter("MAX_TEMP:").toDoubleOrNull()

            if (temperature != null && temperature.isFinite()) {
                val deviceAddress = gatt.device.address
                val alertKey = "$deviceAddress:${temperature.roundToInt()}"
                val now = System.currentTimeMillis()

                if (alertKey != lastMaximumTemperatureAlertKey ||
                    now - lastMaximumTemperatureAlertAt > 2_000L
                ) {
                    applicationContext?.let { context ->
                        val device = DeviceManager.getDevices(context)
                            .firstOrNull { it.id.equals(deviceAddress, ignoreCase = true) }

                        AlertManager.addHighTemperatureAlert(
                            context = context,
                            deviceId = device?.id ?: deviceAddress,
                            deviceName = device?.name?.takeIf { it.isNotBlank() }
                                ?: "Chamarra Forge",
                            temperatureC = temperature.roundToInt()
                        )
                    }

                    lastMaximumTemperatureAlertKey = alertKey
                    lastMaximumTemperatureAlertAt = now
                }
            }
        }

        listener?.onMessageReceived(message)
        if (received == "PONG") {
            startTemperaturePolling()
        }
    }


    /* ============================= */
    /* ACTIVAR NOTIFICACIONES GATT   */
    /* ============================= */

    @SuppressLint("MissingPermission")
    private fun enableNotifications(
        gatt: BluetoothGatt
    ) {

        val characteristic =
            statusCharacteristic
                ?: return


        val enabled =
            gatt.setCharacteristicNotification(
                characteristic,
                true
            )


        if (
            !enabled
        ) {

            listener?.onError(
                "No se pudieron activar las respuestas BLE."
            )

            return
        }


        val descriptor =
            characteristic.getDescriptor(
                CCCD_UUID
            )


        if (
            descriptor == null
        ) {

            listener?.onError(
                "No se encontró el descriptor BLE."
            )

            return
        }


        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            gatt.writeDescriptor(
                descriptor,
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            )

        } else {

            @Suppress("DEPRECATION")

            descriptor.value =
                BluetoothGattDescriptor
                    .ENABLE_NOTIFICATION_VALUE


            @Suppress("DEPRECATION")

            gatt.writeDescriptor(
                descriptor
            )
        }
    }


    private fun startTemperaturePolling() {
        if (temperaturePolling) return
        temperaturePolling = true
        sendCommand("TEMP_REQUEST")
        mainHandler.postDelayed(temperaturePollRunnable, 3000)
    }

    private fun stopTemperaturePolling() {
        temperaturePolling = false
        mainHandler.removeCallbacks(temperaturePollRunnable)
    }


    /* ============================= */
    /* ENVIAR COMANDO                */
    /* ============================= */

    @SuppressLint("MissingPermission")
    fun sendCommand(
        command: String
    ): Boolean {

        val gatt =
            bluetoothGatt
                ?: return false


        val characteristic =
            commandCharacteristic
                ?: return false


        val bytes =
            command.toByteArray(
                Charsets.UTF_8
            )


        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            gatt.writeCharacteristic(

                characteristic,

                bytes,

                BluetoothGattCharacteristic
                    .WRITE_TYPE_DEFAULT

            ) ==
                    android.bluetooth.BluetoothStatusCodes.SUCCESS

        } else {

            @Suppress("DEPRECATION")

            characteristic.writeType =
                BluetoothGattCharacteristic
                    .WRITE_TYPE_DEFAULT


            @Suppress("DEPRECATION")

            characteristic.value =
                bytes


            @Suppress("DEPRECATION")

            gatt.writeCharacteristic(
                characteristic
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToAddress(
        context: Context,
        address: String
    ) {

        applicationContext =
            context.applicationContext


        if (
            !hasPermissions(
                context
            )
        ) {

            listener?.onError(
                "Faltan permisos Bluetooth."
            )


            return
        }


        val adapter =
            getBluetoothAdapter(
                context
            )


        if (
            adapter == null
        ) {

            listener?.onError(
                "Bluetooth no disponible."
            )


            return
        }


        try {

            val device =
                adapter.getRemoteDevice(
                    address
                )


            connect(
                device
            )

        } catch (
            exception: Exception
        ) {

            listener?.onError(
                "No se pudo encontrar la chamarra Forge guardada."
            )
        }
    }

    /* ============================= */
    /* DESCONECTAR                   */
    /* ============================= */

    @SuppressLint("MissingPermission")
    fun disconnect() {

        stopScan()


        bluetoothGatt
            ?.disconnect()
    }


    /* ============================= */
    /* CERRAR                        */
    /* ============================= */

    @SuppressLint("MissingPermission")
    fun close() {

        stopScan()
        stopTemperaturePolling()


        bluetoothGatt
            ?.close()


        bluetoothGatt =
            null


        commandCharacteristic =
            null


        statusCharacteristic =
            null
    }
}
