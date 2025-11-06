package com.gonzales.metrolimago.data.local

import android.content.Context
import com.gonzales.metrolimago.data.local.entities.Corredor
import com.gonzales.metrolimago.data.local.entities.Estacion
import com.gonzales.metrolimago.data.local.entities.Linea
import com.gonzales.metrolimago.data.local.entities.Paradero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DataLoader(private val context: Context, private val database: MetroDatabase) {

    suspend fun loadInitialData() {
        withContext(Dispatchers.IO) {
            try {
                // Verificar si ya hay datos
                val estacionesCount = database.estacionDao().getAllEstaciones()

                // Leer el archivo JSON desde assets
                val jsonString = context.assets.open("estaciones.json")
                    .bufferedReader()
                    .use { it.readText() }

                val jsonObject = JSONObject(jsonString)

                // Cargar Líneas
                val lineasJson = jsonObject.getJSONArray("lineas")
                val lineas = mutableListOf<Linea>()
                for (i in 0 until lineasJson.length()) {
                    val lineaJson = lineasJson.getJSONObject(i)
                    lineas.add(
                        Linea(
                            id = lineaJson.getString("id"),
                            nombre = lineaJson.getString("nombre"),
                            color = lineaJson.getString("color"),
                            estado = lineaJson.getString("estado")
                        )
                    )
                }
                database.lineaDao().insertAll(lineas)

                // Cargar Estaciones
                val estacionesJson = jsonObject.getJSONArray("estaciones")
                val estaciones = mutableListOf<Estacion>()
                for (i in 0 until estacionesJson.length()) {
                    val estJson = estacionesJson.getJSONObject(i)
                    estaciones.add(
                        Estacion(
                            id = estJson.getString("id"),
                            nombre = estJson.getString("nombre"),
                            linea = estJson.getString("linea"),
                            orden = estJson.getInt("orden"),
                            distrito = estJson.getString("distrito"),
                            latitud = estJson.getDouble("latitud"),
                            longitud = estJson.getDouble("longitud"),
                            horarioApertura = estJson.getString("horarioApertura"),
                            horarioCierre = estJson.getString("horarioCierre"),
                            esFavorita = false,
                            imagenPrincipal = estJson.optString("imagenPrincipal", ""),
                            imagenesGaleria = estJson.optString("imagenesGaleria", ""),
                            descripcion = estJson.optString("descripcion", ""),
                            puntosInteres = estJson.optString("puntosInteres", "")
                        )
                    )
                }
                database.estacionDao().insertAll(estaciones)

                // Cargar Corredores
                val corredoresJson = jsonObject.getJSONArray("corredores")
                val corredores = mutableListOf<Corredor>()
                for (i in 0 until corredoresJson.length()) {
                    val corrJson = corredoresJson.getJSONObject(i)
                    corredores.add(
                        Corredor(
                            id = corrJson.getString("id"),
                            nombre = corrJson.getString("nombre"),
                            color = corrJson.getString("color"),
                            ruta = corrJson.getString("ruta"),
                            estado = corrJson.getString("estado")
                        )
                    )
                }
                database.corredorDao().insertAll(corredores)

                // Cargar Paraderos
                val paraderosJson = jsonObject.getJSONArray("paraderos")
                val paraderos = mutableListOf<Paradero>()
                for (i in 0 until paraderosJson.length()) {
                    val parJson = paraderosJson.getJSONObject(i)
                    paraderos.add(
                        Paradero(
                            id = parJson.getString("id"),
                            nombre = parJson.getString("nombre"),
                            corredor = parJson.getString("corredor"),
                            orden = parJson.getInt("orden"),
                            zona = parJson.getString("zona"),
                            latitud = parJson.getDouble("latitud"),              // ✅ NUEVO
                            longitud = parJson.getDouble("longitud"),            // ✅ NUEVO
                            horarioApertura = parJson.getString("horarioApertura"), // ✅ NUEVO
                            horarioCierre = parJson.getString("horarioCierre"),     // ✅ NUEVO
                            esFavorito = false
                        )
                    )
                }
                database.paraderoDao().insertAll(paraderos)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}