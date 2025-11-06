package com.gonzales.metrolimago.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "corredores")
data class Corredor(
    @PrimaryKey val id: String,
    val nombre: String,
    val color: String,
    val ruta: String,
    val estado: String
)