package com.gonzales.metrolimago.estaciones

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gonzales.metrolimago.data.local.MetroDatabase
import com.gonzales.metrolimago.data.local.entities.Corredor
import com.gonzales.metrolimago.data.local.entities.Estacion
import com.gonzales.metrolimago.data.local.entities.Linea
import com.gonzales.metrolimago.data.local.entities.Paradero
import com.gonzales.metrolimago.repository.EstacionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


sealed class TransportItem {
    data class EstacionItem(val estacion: Estacion) : TransportItem()
    data class ParaderoItem(val paradero: Paradero) : TransportItem()
}

class EstacionesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EstacionRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow<String?>(null)
    val selectedFilter: StateFlow<String?> = _selectedFilter.asStateFlow()

    val lineas: StateFlow<List<Linea>>
    val corredores: StateFlow<List<Corredor>>
    val items: StateFlow<List<TransportItem>>

    val favoritos: StateFlow<List<TransportItem>>

    init {
        val database = MetroDatabase.getDatabase(application)
        repository = EstacionRepository(
            database.estacionDao(),
            database.lineaDao(),
            database.corredorDao(),
            database.paraderoDao()
        )

        lineas = repository.getAllLineas()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        corredores = repository.getAllCorredores()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        favoritos = combine(
            repository.getEstacionesFavoritas(),
            repository.getParaderosFavoritos()
        ) { estacionesFav, paraderosFav ->
            estacionesFav.map { TransportItem.EstacionItem(it) } +
                    paraderosFav.map { TransportItem.ParaderoItem(it) }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        items = combine(
            _searchQuery,
            _selectedFilter
        ) { query, filter ->
            Pair(query, filter)
        }.flatMapLatest { (query, filter) ->
            when {
                query.isNotBlank() -> {
                    combine(
                        repository.searchEstaciones(query),
                        repository.searchParaderos(query)
                    ) { estaciones, paraderos ->
                        estaciones.map { TransportItem.EstacionItem(it) } +
                                paraderos.map { TransportItem.ParaderoItem(it) }
                    }
                }
                filter != null && filter.startsWith("linea") -> {
                    repository.getEstacionesByLinea(filter).map { estaciones ->
                        estaciones.map { TransportItem.EstacionItem(it) }
                    }
                }
                filter != null -> {
                    repository.getParaderosByCorredor(filter).map { paraderos ->
                        paraderos.map { TransportItem.ParaderoItem(it) }
                    }
                }
                else -> {
                    combine(
                        repository.getAllEstaciones(),
                        repository.getAllParaderos()
                    ) { estaciones, paraderos ->
                        estaciones.map { TransportItem.EstacionItem(it) } +
                                paraderos.map { TransportItem.ParaderoItem(it) }
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filterId: String?) {
        _selectedFilter.value = filterId
    }

    fun toggleFavorita(estacionId: String, esFavorita: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorita(estacionId, esFavorita)
        }
    }

    fun toggleFavoritoParadero(paraderoId: String, esFavorito: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoritoParadero(paraderoId, esFavorito)
        }
    }

    suspend fun getEstacionById(estacionId: String): Estacion? {
        return repository.getEstacionById(estacionId)
    }

    suspend fun getParaderoById(paraderoId: String): Paradero? {
        return repository.getParaderoById(paraderoId)
    }

    suspend fun getCorredorById(corredorId: String): Corredor? {
        return repository.getCorredorById(corredorId)
    }

    // ============================================
    // FUNCIONES PARA EL PLANIFICADOR DE LÍNEAS
    // ============================================

    suspend fun getAllEstacionesList(): List<Estacion> {
        return repository.getAllEstaciones().first()
    }

    suspend fun calcularRuta(origenId: String, destinoId: String): RutaResult? {
        val todasEstaciones = getAllEstacionesList()

        val origen = todasEstaciones.find { it.id == origenId } ?: return null
        val destino = todasEstaciones.find { it.id == destinoId } ?: return null

        // Si son la misma estación
        if (origen.id == destino.id) {
            return RutaResult(
                origen = origen,
                destino = destino,
                estaciones = listOf(origen),
                tiempoEstimado = 0,
                numeroEstaciones = 1,
                linea = origen.linea
            )
        }

        // Si están en la misma línea
        if (origen.linea == destino.linea) {
            val estacionesLinea = todasEstaciones
                .filter { it.linea == origen.linea }
                .sortedBy { it.orden }

            val indexOrigen = estacionesLinea.indexOfFirst { it.id == origen.id }
            val indexDestino = estacionesLinea.indexOfFirst { it.id == destino.id }

            val ruta = if (indexOrigen < indexDestino) {
                estacionesLinea.subList(indexOrigen, indexDestino + 1)
            } else {
                estacionesLinea.subList(indexDestino, indexOrigen + 1).reversed()
            }

            val numeroEstaciones = ruta.size
            val tiempoEstimado = (numeroEstaciones - 1) * 2 + 5 // 2 min entre estaciones + 5 min espera

            return RutaResult(
                origen = origen,
                destino = destino,
                estaciones = ruta,
                tiempoEstimado = tiempoEstimado,
                numeroEstaciones = numeroEstaciones,
                linea = origen.linea
            )
        }

        // TODO: Si son líneas diferentes, implementar transbordo
        return null
    }

    // ============================================
    // ✅ NUEVAS FUNCIONES PARA PLANIFICADOR DE CORREDORES
    // ============================================

    suspend fun getAllParaderosList(): List<Paradero> {
        return repository.getAllParaderos().first()
    }

    suspend fun calcularRutaCorredor(origenId: String, destinoId: String): RutaCorredorResult? {
        val todosParaderos = getAllParaderosList()

        val origen = todosParaderos.find { it.id == origenId } ?: return null
        val destino = todosParaderos.find { it.id == destinoId } ?: return null

        // Si son el mismo paradero
        if (origen.id == destino.id) {
            return RutaCorredorResult(
                origen = origen,
                destino = destino,
                paraderos = listOf(origen),
                tiempoEstimado = 0,
                numeroParaderos = 1,
                corredor = origen.corredor,
                colorCorredor = getColorCorredor(origen.corredor)
            )
        }

        // Si están en el mismo corredor
        if (origen.corredor == destino.corredor) {
            val paraderosRuta = todosParaderos
                .filter { it.corredor == origen.corredor }
                .sortedBy { it.orden }

            val indexOrigen = paraderosRuta.indexOfFirst { it.id == origen.id }
            val indexDestino = paraderosRuta.indexOfFirst { it.id == destino.id }

            val ruta = if (indexOrigen < indexDestino) {
                paraderosRuta.subList(indexOrigen, indexDestino + 1)
            } else {
                paraderosRuta.subList(indexDestino, indexOrigen + 1).reversed()
            }

            val numeroParaderos = ruta.size
            val tiempoEstimado = (numeroParaderos - 1) * 3 + 5 // 3 min entre paraderos + 5 min espera

            return RutaCorredorResult(
                origen = origen,
                destino = destino,
                paraderos = ruta,
                tiempoEstimado = tiempoEstimado,
                numeroParaderos = numeroParaderos,
                corredor = origen.corredor,
                colorCorredor = getColorCorredor(origen.corredor)
            )
        }

        // Si están en corredores diferentes, retornar null (sin transferencia por ahora)
        return null
    }

    private fun getColorCorredor(corredor: String): String {
        return when (corredor.lowercase()) {
            "azul" -> "#0D47A1"
            "rojo" -> "#D32F2F"
            "morado" -> "#7B1FA2"
            "amarillo" -> "#F57F17"
            "verde" -> "#388E3C"
            "naranja" -> "#FF6F00"
            else -> "#757575"
        }
    }
}

// ============================================
// DATA CLASS PARA RESULTADO DE RUTA (LÍNEAS)
// ============================================

@Parcelize
data class RutaResult(
    val origen: Estacion,
    val destino: Estacion,
    val estaciones: List<Estacion>,
    val tiempoEstimado: Int, // en minutos
    val numeroEstaciones: Int,
    val linea: String,
    val requiereTransbordo: Boolean = false,
    val tarifaGeneral: Double = 1.50,
    val tarifaUniversitaria: Double = 0.75
) : Parcelable

// ============================================
// ✅ NUEVA DATA CLASS PARA RESULTADO DE RUTA (CORREDORES)
// ============================================

@Parcelize
data class RutaCorredorResult(
    val origen: Paradero,
    val destino: Paradero,
    val paraderos: List<Paradero>,
    val tiempoEstimado: Int, // en minutos
    val numeroParaderos: Int,
    val corredor: String,
    val colorCorredor: String,
    val requiereTransferencia: Boolean = false,
    val tarifaGeneral: Double = 2.50,
    val tarifaUniversitaria: Double = 1.25
) : Parcelable