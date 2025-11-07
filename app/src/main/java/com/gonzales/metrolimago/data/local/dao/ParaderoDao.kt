package com.gonzales.metrolimago.data.local.dao

import androidx.room.*
import com.gonzales.metrolimago.data.local.entities.Paradero
import kotlinx.coroutines.flow.Flow

@Dao
interface ParaderoDao {
    @Query("SELECT * FROM paraderos ORDER BY orden ASC")
    fun getAllParaderos(): Flow<List<Paradero>>

    @Query("SELECT * FROM paraderos WHERE corredor = :corredorId ORDER BY orden ASC")
    fun getParaderosByCorredor(corredorId: String): Flow<List<Paradero>>

    @Query("SELECT * FROM paraderos WHERE id = :paraderoId")
    suspend fun getParaderoById(paraderoId: String): Paradero?

    @Query("SELECT * FROM paraderos WHERE nombre LIKE '%' || :query || '%' ORDER BY orden ASC")
    fun searchParaderos(query: String): Flow<List<Paradero>>

    // ✅ AGREGAR ESTA CONSULTA PARA OBTENER FAVORITOS
    @Query("SELECT * FROM paraderos WHERE esFavorito = 1 ORDER BY orden ASC")
    fun getFavoritos(): Flow<List<Paradero>>

    @Query("UPDATE paraderos SET esFavorito = :esFavorito WHERE id = :paraderoId")
    suspend fun updateFavorito(paraderoId: String, esFavorito: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(paraderos: List<Paradero>)

    @Delete
    suspend fun delete(paradero: Paradero)
}