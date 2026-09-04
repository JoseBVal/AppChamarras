package com.example.thermowear.data

import android.content.Context
import com.example.thermowear.model.HistorySession
import com.example.thermowear.model.HistoryZoneUsage
import com.example.thermowear.model.IntensityPeriod
import com.example.thermowear.model.ThermoWearDevice
import com.example.thermowear.model.ThermoWearVersion
import com.example.thermowear.model.ThermoWearZone
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID


object HistoryManager {

    private const val PREFS_NAME =
        "thermowear_history"

    private const val KEY_HISTORY =
        "history_sessions"

    private const val KEY_DEMO_CREATED =
        "demo_history_created"


    fun getSessions(
        context: Context
    ): MutableList<HistorySession> {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val json =
            preferences.getString(
                KEY_HISTORY,
                null
            ) ?: return mutableListOf()


        val sessions =
            mutableListOf<HistorySession>()


        try {

            val array =
                JSONArray(json)


            for (i in 0 until array.length()) {

                val item =
                    array.getJSONObject(i)


                val sessionStart =
                    item.getLong(
                        "startTimeMillis"
                    )


                val sessionEnd =
                    item.getLong(
                        "endTimeMillis"
                    )


                val zonesArray =
                    item.getJSONArray(
                        "zones"
                    )


                val zones =
                    mutableListOf<HistoryZoneUsage>()


                for (
                j in 0 until zonesArray.length()
                ) {

                    val zoneItem =
                        zonesArray.getJSONObject(j)


                    val periods =
                        mutableListOf<IntensityPeriod>()


                    /*
                     * FORMATO NUEVO
                     *
                     * Cada cambio de intensidad
                     * tiene su propio periodo.
                     */
                    if (
                        zoneItem.has(
                            "intensityPeriods"
                        )
                    ) {

                        val periodsArray =
                            zoneItem.getJSONArray(
                                "intensityPeriods"
                            )


                        for (
                        k in 0 until periodsArray.length()
                        ) {

                            val periodItem =
                                periodsArray
                                    .getJSONObject(k)


                            periods.add(

                                IntensityPeriod(

                                    intensity =
                                        periodItem.getInt(
                                            "intensity"
                                        ),

                                    startTimeMillis =
                                        periodItem.getLong(
                                            "startTimeMillis"
                                        ),

                                    endTimeMillis =
                                        periodItem.getLong(
                                            "endTimeMillis"
                                        )
                                )
                            )
                        }

                    } else if (
                        zoneItem.has("intensity") &&
                        zoneItem.has(
                            "activeDurationMillis"
                        )
                    ) {

                        /*
                         * Compatibilidad con el modelo
                         * anterior que hicimos.
                         *
                         * Si ya ejecutaste la versión vieja,
                         * sus datos no harán fallar la app.
                         */

                        val duration =
                            zoneItem.getLong(
                                "activeDurationMillis"
                            )


                        val oldEnd =
                            (
                                    sessionStart + duration
                                    ).coerceAtMost(
                                    sessionEnd
                                )


                        if (
                            oldEnd > sessionStart
                        ) {

                            periods.add(

                                IntensityPeriod(

                                    intensity =
                                        zoneItem.getInt(
                                            "intensity"
                                        ),

                                    startTimeMillis =
                                        sessionStart,

                                    endTimeMillis =
                                        oldEnd
                                )
                            )
                        }
                    }


                    val temperature =
                        if (
                            zoneItem.has(
                                "averageTemperatureC"
                            ) &&
                            !zoneItem.isNull(
                                "averageTemperatureC"
                            )
                        ) {

                            zoneItem.getDouble(
                                "averageTemperatureC"
                            )

                        } else {

                            null
                        }


                    zones.add(

                        HistoryZoneUsage(

                            zone =
                                ThermoWearZone.valueOf(
                                    zoneItem.getString(
                                        "zone"
                                    )
                                ),

                            intensityPeriods =
                                periods,

                            averageTemperatureC =
                                temperature
                        )
                    )
                }


                sessions.add(

                    HistorySession(

                        id =
                            item.getString("id"),

                        deviceId =
                            item.getString(
                                "deviceId"
                            ),

                        version =
                            ThermoWearVersion.valueOf(
                                item.getString(
                                    "version"
                                )
                            ),

                        startTimeMillis =
                            sessionStart,

                        endTimeMillis =
                            sessionEnd,

                        zones =
                            zones
                    )
                )
            }

        } catch (_: Exception) {

            return mutableListOf()
        }


        return sessions
            .sortedByDescending {
                it.startTimeMillis
            }
            .toMutableList()
    }


    fun getSessionsForDevice(
        context: Context,
        deviceId: String
    ): List<HistorySession> {

        return getSessions(context)
            .filter {

                it.deviceId == deviceId
            }
            .sortedByDescending {

                it.startTimeMillis
            }
    }


    /**
     * Más adelante Bluetooth utilizará
     * este método para almacenar
     * una sesión real terminada.
     */
    fun addSession(
        context: Context,
        session: HistorySession
    ) {

        val sessions =
            getSessions(context)


        sessions.add(
            session
        )


        saveSessions(
            context,
            sessions
        )
    }


    fun removeHistoryForDevice(
        context: Context,
        deviceId: String
    ) {

        val sessions =
            getSessions(context)


        sessions.removeAll {

            it.deviceId == deviceId
        }


        saveSessions(
            context,
            sessions
        )
    }


    fun clearHistory(
        context: Context
    ) {

        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .remove(
                KEY_HISTORY
            )
            .remove(
                KEY_DEMO_CREATED
            )
            .apply()
    }


    /**
     * DATOS DE DEMOSTRACIÓN
     *
     * Sirven para probar Historial antes
     * de implementar Bluetooth.
     *
     * Incluyen cambios reales de intensidad
     * dentro de una misma sesión.
     */
    fun createDemoHistoryIfNeeded(
        context: Context,
        devices: List<ThermoWearDevice>
    ) {

        if (devices.isEmpty()) {
            return
        }


        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )


        if (
            preferences.getBoolean(
                KEY_DEMO_CREATED,
                false
            )
        ) {

            return
        }


        /*
         * Si ya existen registros,
         * no añadimos demostraciones encima.
         */
        if (
            getSessions(context)
                .isNotEmpty()
        ) {

            preferences
                .edit()
                .putBoolean(
                    KEY_DEMO_CREATED,
                    true
                )
                .apply()

            return
        }


        val now =
            System.currentTimeMillis()


        val demoSessions =
            mutableListOf<HistorySession>()


        devices.forEach { device ->

            when (device.version) {

                /*
                 * ========================
                 * THERMOWEAR V1
                 * ========================
                 */
                ThermoWearVersion.V1 -> {

                    /*
                     * Sesión de 50 minutos.
                     *
                     * 15 min Bajo
                     * 25 min Medio
                     * 10 min Alto
                     */

                    val start1 =
                        now -
                                hoursToMillis(2)


                    val end1 =
                        start1 +
                                minutesToMillis(50)


                    demoSessions.add(

                        HistorySession(

                            id =
                                UUID.randomUUID()
                                    .toString(),

                            deviceId =
                                device.id,

                            version =
                                device.version,

                            startTimeMillis =
                                start1,

                            endTimeMillis =
                                end1,

                            zones =
                                listOf(

                                    HistoryZoneUsage(

                                        zone =
                                            ThermoWearZone.BACK,

                                        intensityPeriods =
                                            listOf(

                                                createPeriod(
                                                    intensity = 1,
                                                    start = start1,
                                                    durationMinutes = 15
                                                ),

                                                createPeriod(
                                                    intensity = 2,
                                                    start =
                                                        start1 +
                                                                minutesToMillis(
                                                                    15
                                                                ),
                                                    durationMinutes = 25
                                                ),

                                                createPeriod(
                                                    intensity = 3,
                                                    start =
                                                        start1 +
                                                                minutesToMillis(
                                                                    40
                                                                ),
                                                    durationMinutes = 10
                                                )
                                            )
                                    )
                                )
                        )
                    )


                    /*
                     * Segunda sesión V1:
                     * 40 minutos en intensidad Medio.
                     */

                    val start2 =
                        now -
                                hoursToMillis(5)


                    val end2 =
                        start2 +
                                minutesToMillis(40)


                    demoSessions.add(

                        HistorySession(

                            id =
                                UUID.randomUUID()
                                    .toString(),

                            deviceId =
                                device.id,

                            version =
                                device.version,

                            startTimeMillis =
                                start2,

                            endTimeMillis =
                                end2,

                            zones =
                                listOf(

                                    HistoryZoneUsage(

                                        zone =
                                            ThermoWearZone.BACK,

                                        intensityPeriods =
                                            listOf(

                                                createPeriod(
                                                    intensity = 2,
                                                    start = start2,
                                                    durationMinutes = 40
                                                )
                                            )
                                    )
                                )
                        )
                    )
                }


                /*
                 * ========================
                 * THERMOWEAR V2
                 * ========================
                 */
                ThermoWearVersion.V2 -> {

                    /*
                     * Sesión de 55 minutos.
                     *
                     * AMBAS ZONAS están lógicamente
                     * encendidas durante los 55 minutos.
                     *
                     * Aunque Arduino alterne físicamente:
                     *
                     * alta 10 s
                     * baja 10 s
                     * alta 10 s
                     * baja 10 s
                     *
                     * para el usuario ambas estuvieron
                     * activadas los 55 minutos.
                     */

                    val start1 =
                        now -
                                hoursToMillis(2)


                    val end1 =
                        start1 +
                                minutesToMillis(55)


                    demoSessions.add(

                        HistorySession(

                            id =
                                UUID.randomUUID()
                                    .toString(),

                            deviceId =
                                device.id,

                            version =
                                device.version,

                            startTimeMillis =
                                start1,

                            endTimeMillis =
                                end1,

                            zones =
                                listOf(

                                    /*
                                     * ESPALDA ALTA
                                     *
                                     * Bajo 15 min
                                     * Medio 25 min
                                     * Alto 15 min
                                     */

                                    HistoryZoneUsage(

                                        zone =
                                            ThermoWearZone.UPPER_BACK,

                                        intensityPeriods =
                                            listOf(

                                                createPeriod(
                                                    intensity = 1,
                                                    start = start1,
                                                    durationMinutes = 15
                                                ),

                                                createPeriod(
                                                    intensity = 2,
                                                    start =
                                                        start1 +
                                                                minutesToMillis(
                                                                    15
                                                                ),
                                                    durationMinutes = 25
                                                ),

                                                createPeriod(
                                                    intensity = 3,
                                                    start =
                                                        start1 +
                                                                minutesToMillis(
                                                                    40
                                                                ),
                                                    durationMinutes = 15
                                                )
                                            )
                                    ),


                                    /*
                                     * ESPALDA BAJA
                                     *
                                     * Medio 20 min
                                     * Alto 20 min
                                     * Medio 15 min
                                     */

                                    HistoryZoneUsage(

                                        zone =
                                            ThermoWearZone.LOWER_BACK,

                                        intensityPeriods =
                                            listOf(

                                                createPeriod(
                                                    intensity = 2,
                                                    start = start1,
                                                    durationMinutes = 20
                                                ),

                                                createPeriod(
                                                    intensity = 3,
                                                    start =
                                                        start1 +
                                                                minutesToMillis(
                                                                    20
                                                                ),
                                                    durationMinutes = 20
                                                ),

                                                createPeriod(
                                                    intensity = 2,
                                                    start =
                                                        start1 +
                                                                minutesToMillis(
                                                                    40
                                                                ),
                                                    durationMinutes = 15
                                                )
                                            )
                                    )
                                )
                        )
                    )


                    /*
                     * Segunda sesión V2.
                     *
                     * La sesión dura 45 minutos.
                     *
                     * Espalda alta:
                     * activa los 45 min.
                     *
                     * Espalda baja:
                     * estuvo apagada los primeros 10 min,
                     * luego activa durante 35 min.
                     */

                    val start2 =
                        now -
                                hoursToMillis(5)


                    val end2 =
                        start2 +
                                minutesToMillis(45)


                    demoSessions.add(

                        HistorySession(

                            id =
                                UUID.randomUUID()
                                    .toString(),

                            deviceId =
                                device.id,

                            version =
                                device.version,

                            startTimeMillis =
                                start2,

                            endTimeMillis =
                                end2,

                            zones =
                                listOf(

                                    HistoryZoneUsage(

                                        zone =
                                            ThermoWearZone.UPPER_BACK,

                                        intensityPeriods =
                                            listOf(

                                                createPeriod(
                                                    intensity = 2,
                                                    start = start2,
                                                    durationMinutes = 20
                                                ),

                                                createPeriod(
                                                    intensity = 3,
                                                    start =
                                                        start2 +
                                                                minutesToMillis(
                                                                    20
                                                                ),
                                                    durationMinutes = 25
                                                )
                                            )
                                    ),

                                    HistoryZoneUsage(

                                        zone =
                                            ThermoWearZone.LOWER_BACK,

                                        intensityPeriods =
                                            listOf(

                                                createPeriod(
                                                    intensity = 1,
                                                    start =
                                                        start2 +
                                                                minutesToMillis(
                                                                    10
                                                                ),
                                                    durationMinutes = 15
                                                ),

                                                createPeriod(
                                                    intensity = 2,
                                                    start =
                                                        start2 +
                                                                minutesToMillis(
                                                                    25
                                                                ),
                                                    durationMinutes = 20
                                                )
                                            )
                                    )
                                )
                        )
                    )
                }
            }
        }


        saveSessions(
            context,
            demoSessions
        )


        preferences
            .edit()
            .putBoolean(
                KEY_DEMO_CREATED,
                true
            )
            .apply()
    }


    /**
     * Convierte las sesiones a JSON
     * y las guarda en SharedPreferences.
     */
    private fun saveSessions(
        context: Context,
        sessions: List<HistorySession>
    ) {

        val sessionsArray =
            JSONArray()


        sessions.forEach { session ->

            val sessionItem =
                JSONObject()


            sessionItem.put(
                "id",
                session.id
            )

            sessionItem.put(
                "deviceId",
                session.deviceId
            )

            sessionItem.put(
                "version",
                session.version.name
            )

            sessionItem.put(
                "startTimeMillis",
                session.startTimeMillis
            )

            sessionItem.put(
                "endTimeMillis",
                session.endTimeMillis
            )


            val zonesArray =
                JSONArray()


            session.zones.forEach { zone ->

                val zoneItem =
                    JSONObject()


                zoneItem.put(
                    "zone",
                    zone.zone.name
                )


                val periodsArray =
                    JSONArray()


                zone.intensityPeriods.forEach {
                        period ->


                    val periodItem =
                        JSONObject()


                    periodItem.put(
                        "intensity",
                        period.intensity
                    )

                    periodItem.put(
                        "startTimeMillis",
                        period.startTimeMillis
                    )

                    periodItem.put(
                        "endTimeMillis",
                        period.endTimeMillis
                    )


                    periodsArray.put(
                        periodItem
                    )
                }


                zoneItem.put(
                    "intensityPeriods",
                    periodsArray
                )


                if (
                    zone.averageTemperatureC != null
                ) {

                    zoneItem.put(
                        "averageTemperatureC",
                        zone.averageTemperatureC
                    )
                }


                zonesArray.put(
                    zoneItem
                )
            }


            sessionItem.put(
                "zones",
                zonesArray
            )


            sessionsArray.put(
                sessionItem
            )
        }


        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                KEY_HISTORY,
                sessionsArray.toString()
            )
            .apply()
    }


    private fun createPeriod(
        intensity: Int,
        start: Long,
        durationMinutes: Int
    ): IntensityPeriod {

        return IntensityPeriod(

            intensity =
                intensity,

            startTimeMillis =
                start,

            endTimeMillis =
                start +
                        minutesToMillis(
                            durationMinutes
                        )
        )
    }


    private fun minutesToMillis(
        minutes: Int
    ): Long {

        return minutes *
                60L *
                1000L
    }


    private fun hoursToMillis(
        hours: Int
    ): Long {

        return hours *
                60L *
                60L *
                1000L
    }
}