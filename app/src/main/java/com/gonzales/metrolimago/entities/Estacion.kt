package com.gonzales.metrolimago.data.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.gonzales.metrolimago.R

@Parcelize
@Entity(tableName = "estaciones")
data class Estacion(
    @PrimaryKey val id: String,
    val nombre: String,
    val linea: String,
    val orden: Int,
    val distrito: String,
    val latitud: Double,
    val longitud: Double,
    val horarioApertura: String,
    val horarioCierre: String,
    val esFavorita: Boolean = false,
    val imagenPrincipal: String = "",
    val imagenesGaleria: String = "",
    val descripcion: String = "",
    val puntosInteres: String = ""
) : Parcelable {

    // 📸 Lista de imágenes para cada estación (2 fotos deslizables)
    val imagenesResIds: List<Int>
        get() = when (id) {
            // Línea 1 - Orden correcto según JSON
            "est_01" -> listOf(R.drawable.estacion_villa_el_salvador_1, R.drawable.estacion_villa_el_salvador_2)
            "est_02" -> listOf(R.drawable.estacion_parque_industrial_1, R.drawable.estacion_parque_industrial_2)
            "est_03" -> listOf(R.drawable.estacion_pumacahua_1, R.drawable.estacion_pumacahua_2)
            "est_04" -> listOf(R.drawable.estacion_villa_maria_1, R.drawable.estacion_villa_maria_2)
            "est_05" -> listOf(R.drawable.estacion_maria_auxiliadora_1, R.drawable.estacion_maria_auxiliadora_2)
            "est_06" -> listOf(R.drawable.estacion_san_juan_1, R.drawable.estacion_san_juan_2)
            "est_07" -> listOf(R.drawable.estacion_atocongo_1, R.drawable.estacion_atocongo_2)
            "est_08" -> listOf(R.drawable.estacion_jorge_chavez_1, R.drawable.estacion_jorge_chavez_2)
            "est_09" -> listOf(R.drawable.estacion_ayacucho_1, R.drawable.estacion_ayacucho_2)
            "est_10" -> listOf(R.drawable.estacion_cabitos_1, R.drawable.estacion_cabitos_2)
            "est_11" -> listOf(R.drawable.estacion_angamos_1, R.drawable.estacion_angamos_2) // ✅ ANGAMOS
            "est_12" -> listOf(R.drawable.estacion_san_borja_sur1, R.drawable.estacion_san_borja_sur_2) // ✅ SAN BORJA SUR
            "est_13" -> listOf(R.drawable.estacion_la_cultura_1, R.drawable.estacion_la_cultura_2) // ✅ LA CULTURA
            "est_14" -> listOf(R.drawable.estacion_arriola_1, R.drawable.estacion_arriola_2) // ✅ ARRIOLA
            "est_15" -> listOf(R.drawable.estacion_gamarra_1, R.drawable.estacion_gamarra_2) // ✅ GAMARRA (falta _1)
            "est_16" -> listOf(R.drawable.estacion_miguel_grau_1, R.drawable.estacion_miguel_grau_2) // ✅ MIGUEL GRAU
            "est_17" -> listOf(R.drawable.estacion_el_angel_1, R.drawable.estacion_el_angel_2) // ✅ EL ÁNGEL
            "est_18" -> listOf(R.drawable.estacion_presbitero_maestro_1, R.drawable.estacion_presbitero_maestro_2) // ✅ PRESBÍTERO MAESTRO
            "est_19" -> listOf(R.drawable.estacion_caja_de_agua_1, R.drawable.estacion_caja_de_agua_2) // ✅ CAJA DE AGUA
            "est_20" -> listOf(R.drawable.estacion_piramide_del_sol_1, R.drawable.estacion_piramide_del_sol_2) // ✅ PIRÁMIDE DEL SOL
            "est_21" -> listOf(R.drawable.estacion_los_jardines_1, R.drawable.estacion_los_jardines_2) // ✅ LOS JARDINES
            "est_22" -> listOf(R.drawable.estacion_los_postes_1, R.drawable.estacion_los_postes_2) // ✅ LOS POSTES
            "est_23" -> listOf(R.drawable.estacion_san_carlos_1, R.drawable.estacion_san_carlos_2) // ✅ SAN CARLOS
            "est_24" -> listOf(R.drawable.estacion_san_martin_1, R.drawable.estacion_san_martin_2) // ✅ SAN MARTÍN
            "est_25" -> listOf(R.drawable.estacion_santa_rosa_1, R.drawable.estacion_santa_rosa_2) // ✅ SANTA ROSA
            "est_26" -> listOf(R.drawable.estacion_bayovar_1, R.drawable.estacion_bayovar_2) // ✅ BAYÓVAR

            // Fallback
            else -> listOf(R.drawable.estacion_villa_el_salvador_1, R.drawable.estacion_villa_el_salvador_2)
        }

    val imagenPrincipalResId: Int
        get() = imagenesResIds.firstOrNull() ?: R.drawable.estacion_villa_el_salvador_1

    // 📍 Referencias cercanas por estación
    val referencias: List<String>
        get() = when (id) {
            "est_01" -> listOf(
                "Hospital María Auxiliadora",
                "Plaza VES",
                "Mercado Municipal",
                "Banco BCP"
            )
            "est_10" -> listOf(
                "Gamarra (Emporio comercial)",
                "Mercado Central de Lima",
                "Hospital del Niño",
                "Banco de la Nación"
            )
            "est_26" -> listOf(
                "Megaplaza Norte",
                "Universidad Privada del Norte",
                "Real Plaza",
                "Banco Interbank"
            )
            else -> listOf(
                "Comercios locales",
                "Servicios bancarios",
                "Centros de salud cercanos"
            )
        }
}