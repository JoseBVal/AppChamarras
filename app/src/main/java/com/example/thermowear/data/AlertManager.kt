package com.example.thermowear.data

import android.content.Context
import com.example.thermowear.model.AlertType
import com.example.thermowear.model.ThermoWearAlert
import com.example.thermowear.notifications.NotificationHelper
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object AlertManager {

    private const val PREFS_NAME =
        "thermowear_alerts"

    private const val KEY_ALERTS =
        "alerts"


    /* ============================ */
    /* OBTENER ALERTAS              */
    /* ============================ */

    fun getAlerts(
        context: Context
    ): List<ThermoWearAlert> {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )


        val json =
            preferences.getString(
                KEY_ALERTS,
                null
            )
                ?: return emptyList()


        return try {

            val array =
                JSONArray(
                    json
                )


            val alerts =
                mutableListOf<ThermoWearAlert>()


            for (
            index in 0 until array.length()
            ) {

                val objectJson =
                    array.getJSONObject(
                        index
                    )


                alerts.add(
                    alertFromJson(
                        objectJson
                    )
                )
            }


            alerts.sortedByDescending {
                it.timestampMillis
            }

        } catch (
            exception: Exception
        ) {

            emptyList()
        }
    }


    /* ============================ */
    /* INDICADOR DE NO LEÍDAS       */
    /* ============================ */

    fun hasUnreadAlerts(
        context: Context
    ): Boolean {

        return getAlerts(
            context
        ).any {
            !it.read
        }
    }


    /* ============================ */
    /* ALERTA DE DESCONEXIÓN        */
    /* ============================ */

    fun addDisconnectedAlert(
        context: Context,
        deviceId: String?,
        deviceName: String
    ) {

        val alert =
            ThermoWearAlert(

                id =
                    UUID.randomUUID()
                        .toString(),

                type =
                    AlertType.DEVICE_DISCONNECTED,

                deviceId =
                    deviceId,

                deviceName =
                    deviceName,

                title =
                    "Dispositivo desconectado",

                message =
                    "Se perdió la conexión con $deviceName.",

                timestampMillis =
                    System.currentTimeMillis(),

                read =
                    false
            )


        addAlert(
            context,
            alert
        )
    }


    /* ============================ */
    /* ALERTA DE TEMPERATURA V2     */
    /* ============================ */

    fun addHighTemperatureAlert(
        context: Context,
        deviceId: String?,
        deviceName: String,
        temperatureC: Int
    ) {

        val alert =
            ThermoWearAlert(

                id =
                    UUID.randomUUID()
                        .toString(),

                type =
                    AlertType.HIGH_TEMPERATURE,

                deviceId =
                    deviceId,

                deviceName =
                    deviceName,

                title =
                    "Temperatura elevada",

                message =
                    "Espalda alta de $deviceName alcanzó $temperatureC °C.",

                timestampMillis =
                    System.currentTimeMillis(),

                read =
                    false
            )


        addAlert(
            context,
            alert
        )
    }


    /* ============================ */
    /* GUARDAR ALERTA               */
    /* ============================ */

    private fun addAlert(
        context: Context,
        alert: ThermoWearAlert
    ) {

        val alerts =
            getAlerts(
                context
            ).toMutableList()


        alerts.add(
            alert
        )


        /*
         * Para esta versión no necesitamos
         * conservar cientos de alertas.
         *
         * Guardamos como máximo las 50
         * más recientes.
         */
        val limitedAlerts =
            alerts
                .sortedByDescending {
                    it.timestampMillis
                }
                .take(
                    50
                )


        saveAlerts(
            context,
            limitedAlerts
        )


        /*
         * La misma alerta se envía también
         * al sistema Android.
         */
        NotificationHelper
            .showAlertNotification(
                context,
                alert
            )
    }


    /* ============================ */
    /* MARCAR COMO LEÍDAS           */
    /* ============================ */

    fun markAlertsAsRead(
        context: Context
    ) {

        val alerts =
            getAlerts(
                context
            ).map {

                it.copy(
                    read = true
                )
            }


        saveAlerts(
            context,
            alerts
        )
    }


    /* ============================ */
    /* BORRAR HISTORIAL             */
    /* ============================ */

    fun clearAlerts(
        context: Context
    ) {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )


        preferences.edit()
            .remove(
                KEY_ALERTS
            )
            .apply()
    }


    /* ============================ */
    /* GUARDAR JSON                 */
    /* ============================ */

    private fun saveAlerts(
        context: Context,
        alerts: List<ThermoWearAlert>
    ) {

        val array =
            JSONArray()


        alerts.forEach {
                alert ->

            array.put(
                alertToJson(
                    alert
                )
            )
        }


        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )


        preferences.edit()
            .putString(
                KEY_ALERTS,
                array.toString()
            )
            .apply()
    }


    /* ============================ */
    /* ALERTA -> JSON               */
    /* ============================ */

    private fun alertToJson(
        alert: ThermoWearAlert
    ): JSONObject {

        return JSONObject()
            .put(
                "id",
                alert.id
            )
            .put(
                "type",
                alert.type.name
            )
            .put(
                "deviceId",
                alert.deviceId
            )
            .put(
                "deviceName",
                alert.deviceName
            )
            .put(
                "title",
                alert.title
            )
            .put(
                "message",
                alert.message
            )
            .put(
                "timestampMillis",
                alert.timestampMillis
            )
            .put(
                "read",
                alert.read
            )
    }


    /* ============================ */
    /* JSON -> ALERTA               */
    /* ============================ */

    private fun alertFromJson(
        json: JSONObject
    ): ThermoWearAlert {

        val deviceId =
            if (
                json.isNull(
                    "deviceId"
                )
            ) {

                null

            } else {

                json.optString(
                    "deviceId"
                )
            }


        return ThermoWearAlert(

            id =
                json.getString(
                    "id"
                ),

            type =
                AlertType.valueOf(
                    json.getString(
                        "type"
                    )
                ),

            deviceId =
                deviceId,

            deviceName =
                json.getString(
                    "deviceName"
                ),

            title =
                json.getString(
                    "title"
                ),

            message =
                json.getString(
                    "message"
                ),

            timestampMillis =
                json.getLong(
                    "timestampMillis"
                ),

            read =
                json.optBoolean(
                    "read",
                    false
                )
        )
    }
}