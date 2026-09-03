package com.example.thermowear.data

import android.content.Context
import com.example.thermowear.model.ThermoWearDevice
import com.example.thermowear.model.ThermoWearVersion
import org.json.JSONArray
import org.json.JSONObject

object DeviceManager {

    private const val PREFS_NAME = "thermowear_devices"
    private const val KEY_DEVICES = "devices"

    fun getDevices(
        context: Context
    ): MutableList<ThermoWearDevice> {

        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val json = preferences.getString(
            KEY_DEVICES,
            null
        ) ?: return mutableListOf()

        val devices = mutableListOf<ThermoWearDevice>()

        try {

            val array = JSONArray(json)

            for (i in 0 until array.length()) {

                val item = array.getJSONObject(i)

                devices.add(
                    ThermoWearDevice(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        version = ThermoWearVersion.valueOf(
                            item.getString("version")
                        ),
                        battery = item.getInt("battery"),
                        connected = item.getBoolean("connected")
                    )
                )
            }

        } catch (_: Exception) {

            return mutableListOf()
        }

        return devices
    }


    private fun saveDevices(
        context: Context,
        devices: List<ThermoWearDevice>
    ) {

        val array = JSONArray()

        devices.forEach { device ->

            val item = JSONObject()

            item.put(
                "id",
                device.id
            )

            item.put(
                "name",
                device.name
            )

            item.put(
                "version",
                device.version.name
            )

            item.put(
                "battery",
                device.battery
            )

            item.put(
                "connected",
                device.connected
            )

            array.put(item)
        }

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                KEY_DEVICES,
                array.toString()
            )
            .apply()
    }


    fun addOrUpdateDevice(
        context: Context,
        device: ThermoWearDevice
    ) {

        val devices = getDevices(context)

        val index = devices.indexOfFirst {
            it.id == device.id
        }

        if (index >= 0) {

            devices[index] = device

        } else {

            devices.add(device)
        }

        saveDevices(
            context,
            devices
        )
    }


    fun removeDevice(
        context: Context,
        deviceId: String
    ) {

        val devices = getDevices(context)

        devices.removeAll {
            it.id == deviceId
        }

        saveDevices(
            context,
            devices
        )
    }


    fun setConnection(
        context: Context,
        deviceId: String,
        connected: Boolean
    ) {

        val devices = getDevices(context)

        val updatedDevices = devices.map { device ->

            if (device.id == deviceId) {

                device.copy(
                    connected = connected
                )

            } else {

                device
            }
        }

        saveDevices(
            context,
            updatedDevices
        )
    }


    fun isDeviceAdded(
        context: Context,
        deviceId: String
    ): Boolean {

        return getDevices(context).any {
            it.id == deviceId
        }
    }
}