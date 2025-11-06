package com.gonzales.metrolimago.data.local.dao

import androidx.room.*
import com.gonzales.metrolimago.data.local.entities.Estacion
import kotlinx.coroutines.flow.Flow

@Dao
interface EstacionDao {
    @Query("SELECT * FROM estaciones ORDER BY orden ASC")
    fun getAllEstaciones(): Flow<List<Estacion>>

    @Query("SELECT * FROM estaciones WHERE linea = :lineaId ORDER BY orden ASC")
    fun getEstacionesByLinea(lineaId: String): Flow<List<Estacion>>

    @Query("SELECT * FROM estaciones WHERE id = :estacionId")
    suspend fun getEstacionById(estacionId: String): Estacion?

    @Query("SELECT * FROM estaciones WHERE nombre LIKE '%' || :query || '%' ORDER BY orden ASC")
    fun searchEstaciones(query: String): Flow<List<Estacion>>

    @Query("SELECT * FROM estaciones WHERE esFavorita = 1 ORDER BY orden ASC")
    fun getFavoritas(): Flow<List<Estacion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(estaciones: List<Estacion>)

    @Update
    suspend fun update(estacion: Estacion)

    @Query("UPDATE estaciones SET esFavorita = :esFavorita WHERE id = :estacionId")
    suspend fun updateFavorita(estacionId: String, esFavorita: Boolean)

    @Delete
    suspend fun delete(estacion: Estacion)

    @Query("DELETE FROM estaciones")
    suspend fun deleteAll()
}