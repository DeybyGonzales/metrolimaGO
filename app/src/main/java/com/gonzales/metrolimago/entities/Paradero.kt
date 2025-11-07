package com.gonzales.metrolimago.data.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.gonzales.metrolimago.R // 👈 AÑADE ESTE IMPORT

@Parcelize
@Entity(tableName = "paraderos")
data class Paradero(
    @PrimaryKey val id: String, // "azul_01", "rojo_01", etc.
    val nombre: String,
    val corredor: String,
    val orden: Int,
    val zona: String,
    val latitud: Double,
    val longitud: Double,
    val horarioApertura: String,
    val horarioCierre: String,
    val esFavorito: Boolean = false
) : Parcelable {

    val imagenesResIds: List<Int>
        get() = when (id) {
            // --- 🔵 CORREDOR AZUL ---
            "azul_01" -> listOf(R.drawable.estacion28dejulio, R.drawable.corredorazul28)
            "azul_02" -> listOf(R.drawable.estacionangamosazull, R.drawable.angamos_corredor_azul)
            "azul_03" -> listOf(R.drawable.domingo_orue_estacion, R.drawable.orue_domingo_estacion)
            "azul_04" -> listOf(R.drawable.javier_padro_azul, R.drawable.javier_padros)
            "azul_05" -> listOf(R.drawable.arenales, R.drawable.arenales_paradero)
            "azul_06" -> listOf(R.drawable.paradero_risso, R.drawable.paradero_risso) // ✅ Repetido
            "azul_07" -> listOf(R.drawable.estacion_mexico, R.drawable.estacion_mexico) // ✅ Repetido
            "azul_08" -> listOf(R.drawable.quilcaestacion, R.drawable.quilcaestacion) // ✅ Repetido
            "azul_09" -> listOf(R.drawable.tacnaestacion, R.drawable.tacnaestacion) // ✅ Repetido
            "azul_10" -> listOf(R.drawable.achoparadero, R.drawable.achoparadero) // ✅ Repetido

            // --- 🔴 CORREDOR ROJO ---
            "rojo_01" -> listOf(R.drawable.estacion_fauceet, R.drawable.estacion_fauceet) // ✅ Repetido
            "rojo_02" -> listOf(R.drawable.av_universitaria, R.drawable.av_universitaria) // ✅ Repetido
            "rojo_03" -> listOf(R.drawable.la_marina_paradero, R.drawable.la_marina_paradero) // ✅ Repetido
            "rojo_04" -> listOf(R.drawable.av_san_miguel, R.drawable.av_san_miguel) // ✅ Repetido
            "rojo_05" -> listOf(R.drawable.la_mar_paradero, R.drawable.la_mar_paradero) // ✅ Repetido
            "rojo_06" -> listOf(R.drawable.salaverry_foto, R.drawable.salaverry_foto) // ✅ Repetido
            "rojo_07" -> listOf(R.drawable.paradero_javier_prado, R.drawable.paradero_javier_prado) // ✅ Repetido
            "rojo_08" -> listOf(R.drawable.laramblacorredorrojo, R.drawable.laramblacorredorrojo) // ✅ Repetido
            "rojo_09" -> listOf(R.drawable.aterojo, R.drawable.aterojo) // ✅ Repetido

            // --- Fallback (Imagen por defecto) ---
            else -> listOf(R.drawable.estacion_villa_el_salvador_1, R.drawable.estacion_villa_el_salvador_2)
        }

    /**
     * Imagen principal (la primera de la lista)
     */
    val imagenPrincipalResId: Int
        get() = imagenesResIds.firstOrNull() ?: R.drawable.estacion_mexico
}