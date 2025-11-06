package com.gonzales.metrolimago.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lineas")
data class Linea(
    @PrimaryKey val id: String,
    val nombre: String,
    val color: String,
    val estado: String
)