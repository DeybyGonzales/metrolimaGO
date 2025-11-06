package com.gonzales.metrolimago.repository

import com.gonzales.metrolimago.data.local.dao.CorredorDao
import com.gonzales.metrolimago.data.local.dao.EstacionDao
import com.gonzales.metrolimago.data.local.dao.LineaDao
import com.gonzales.metrolimago.data.local.dao.ParaderoDao
import com.gonzales.metrolimago.data.local.entities.Corredor
import com.gonzales.metrolimago.data.local.entities.Estacion
import com.gonzales.metrolimago.data.local.entities.Linea
import com.gonzales.metrolimago.data.local.entities.Paradero
import kotlinx.coroutines.flow.Flow

class EstacionRepository(
    private val estacionDao: EstacionDao,
    private val lineaDao: LineaDao,
    private val corredorDao: CorredorDao,
    private val paraderoDao: ParaderoDao
) {
    fun getAllEstaciones(): Flow<List<Estacion>> = estacionDao.getAllEstaciones()

    fun getEstacionesByLinea(lineaId: String): Flow<List<Estacion>> =
        estacionDao.getEstacionesByLinea(lineaId)

    suspend fun getEstacionById(estacionId: String): Estacion? =
        estacionDao.getEstacionById(estacionId)

    fun searchEstaciones(query: String): Flow<List<Estacion>> =
        estacionDao.searchEstaciones(query)

    fun getFavoritas(): Flow<List<Estacion>> = estacionDao.getFavoritas()

    suspend fun toggleFavorita(estacionId: String, esFavorita: Boolean) {
        estacionDao.updateFavorita(estacionId, esFavorita)
    }

    fun getAllLineas(): Flow<List<Linea>> = lineaDao.getAllLineas()

    suspend fun getLineaById(lineaId: String): Linea? = lineaDao.getLineaById(lineaId)

    fun getAllCorredores(): Flow<List<Corredor>> = corredorDao.getAllCorredores()

    suspend fun getCorredorById(corredorId: String): Corredor? = corredorDao.getCorredorById(corredorId)

    fun getAllParaderos(): Flow<List<Paradero>> = paraderoDao.getAllParaderos()

    fun getParaderosByCorredor(corredorId: String): Flow<List<Paradero>> =
        paraderoDao.getParaderosByCorredor(corredorId)

    suspend fun getParaderoById(paraderoId: String): Paradero? =
        paraderoDao.getParaderoById(paraderoId)

    fun searchParaderos(query: String): Flow<List<Paradero>> =
        paraderoDao.searchParaderos(query)

    suspend fun toggleFavoritoParadero(paraderoId: String, esFavorito: Boolean) {
        paraderoDao.updateFavorito(paraderoId, esFavorito)
    }
}