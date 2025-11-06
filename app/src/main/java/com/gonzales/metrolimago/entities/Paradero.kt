package com.gonzales.metrolimago.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paraderos")
data class Paradero(
    @PrimaryKey val id: String,
    val nombre: String,
    val corredor: String,
    val orden: Int,
    val zona: String,
    val latitud: Double,              // ✅ NUEVO
    val longitud: Double,             // ✅ NUEVO
    val horarioApertura: String,      // ✅ NUEVO
    val horarioCierre: String,        // ✅ NUEVO
    val esFavorito: Boolean = false
)