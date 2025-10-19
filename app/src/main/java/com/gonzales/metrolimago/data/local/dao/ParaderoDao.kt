package com.gonzales.metrolimago.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gonzales.metrolimago.data.local.entities.Paradero
import kotlinx.coroutines.flow.Flow

@Dao
interface ParaderoDao {

    // -------- Lecturas básicas --------
    @Query("SELECT * FROM paraderos ORDER BY orden ASC")
    fun getAllParaderos(): Flow<List<Paradero>>

    @Query("SELECT * FROM paraderos WHERE id = :paraderoId")
    suspend fun getParaderoById(paraderoId: String): Paradero?

    @Query("SELECT * FROM paraderos WHERE nombre LIKE '%' || :query || '%' ORDER BY orden ASC")
    fun searchParaderos(query: String): Flow<List<Paradero>>

    // -------- Favoritos --------
    @Query("SELECT * FROM paraderos WHERE esFavorito = 1 ORDER BY orden ASC")
    fun getFavoritos(): Flow<List<Paradero>>

    @Query("UPDATE paraderos SET esFavorito = :esFavorito WHERE id = :paraderoId")
    suspend fun updateFavorito(paraderoId: String, esFavorito: Boolean)

    // -------- Corredores --------
    /** Recomendado: exacto por ID de corredor (tu JSON usa 'azul' / 'rojo') */
    @Query("""
        SELECT * FROM paraderos
        WHERE LOWER(corredor) = LOWER(:corredorId)
        ORDER BY orden ASC
    """)
    fun getParaderosByCorredorId(corredorId: String): Flow<List<Paradero>>

    /** Compatibilidad: alias del anterior (evita romper llamadas existentes). */
    @Deprecated(
        message = "Usa getParaderosByCorredorId(corredorId) para mayor claridad.",
        replaceWith = ReplaceWith("getParaderosByCorredorId(corredorId)")
    )
    @Query("""
        SELECT * FROM paraderos
        WHERE LOWER(corredor) = LOWER(:corredorId)
        ORDER BY orden ASC
    """)
    fun getParaderosByCorredor(corredorId: String): Flow<List<Paradero>>

    /** Búsqueda flexible por texto/alias (LIKE) */
    @Query("""
        SELECT * FROM paraderos
        WHERE LOWER(corredor) LIKE '%' || LOWER(:needle) || '%'
        ORDER BY orden ASC
    """)
    fun getParaderosByCorredorLike(needle: String): Flow<List<Paradero>>

    /** IDs de corredores disponibles en la BD (útil para fallback o debug) */
    @Query("SELECT DISTINCT corredor FROM paraderos")
    fun getDistinctCorredorIds(): Flow<List<String>>

    // -------- Escrituras --------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(paraderos: List<Paradero>)

    @Delete
    suspend fun delete(paradero: Paradero)
}
