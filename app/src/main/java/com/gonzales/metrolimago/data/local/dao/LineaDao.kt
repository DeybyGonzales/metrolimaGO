package com.gonzales.metrolimago.data.local.dao

import androidx.room.*
import com.gonzales.metrolimago.data.local.entities.Linea
import kotlinx.coroutines.flow.Flow

@Dao
interface LineaDao {
    @Query("SELECT * FROM lineas")
    fun getAllLineas(): Flow<List<Linea>>

    @Query("SELECT * FROM lineas WHERE id = :lineaId")
    suspend fun getLineaById(lineaId: String): Linea?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lineas: List<Linea>)

    @Delete
    suspend fun delete(linea: Linea)
}