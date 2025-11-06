package com.gonzales.metrolimago.data.local.dao

import androidx.room.*
import com.gonzales.metrolimago.data.local.entities.Corredor
import kotlinx.coroutines.flow.Flow

@Dao
interface CorredorDao {
    @Query("SELECT * FROM corredores")
    fun getAllCorredores(): Flow<List<Corredor>>

    @Query("SELECT * FROM corredores WHERE id = :corredorId")
    suspend fun getCorredorById(corredorId: String): Corredor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(corredores: List<Corredor>)

    @Delete
    suspend fun delete(corredor: Corredor)
}