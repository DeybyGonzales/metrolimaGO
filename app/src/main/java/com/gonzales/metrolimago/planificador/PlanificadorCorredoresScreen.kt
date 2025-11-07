package com.gonzales.metrolimago.ui.screens.planificador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gonzales.metrolimago.data.local.entities.Paradero
import com.gonzales.metrolimago.estaciones.EstacionesViewModel
import com.gonzales.metrolimago.estaciones.RutaCorredorResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificadorCorredoresScreen(
    onBackClick: () -> Unit,
    onMapaRutaClick: (RutaCorredorResult) -> Unit = {},
    viewModel: EstacionesViewModel = viewModel()
) {
    var paraderos by remember { mutableStateOf<List<Paradero>>(emptyList()) }
    var origenSeleccionado by remember { mutableStateOf<Paradero?>(null) }
    var destinoSeleccionado by remember { mutableStateOf<Paradero?>(null) }
    var resultado by remember { mutableStateOf<RutaCorredorResult?>(null) }
    var isCalculating by remember { mutableStateOf(false) }
    var expandedOrigen by remember { mutableStateOf(false) }
    var expandedDestino by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Filtrar solo corredores azul y rojo
        paraderos = viewModel.getAllParaderosList()
            .filter { it.corredor.lowercase() in listOf("azul", "rojo") }
    }

    // Verificar si origen y destino son del mismo corredor
    val sonDelMismoCorredor = origenSeleccionado?.corredor == destinoSeleccionado?.corredor
    val puedeCalcular = origenSeleccionado != null &&
            destinoSeleccionado != null &&
            sonDelMismoCorredor &&
            origenSeleccionado?.id != destinoSeleccionado?.id

    // Función para calcular la ruta entre paraderos
    fun calcularRutaCorredor(origen: Paradero, destino: Paradero): RutaCorredorResult {
        // Filtrar paraderos del mismo corredor y ordenar
        val paraderosRuta = paraderos
            .filter { it.corredor == origen.corredor }
            .sortedBy { it.orden }

        val indiceOrigen = paraderosRuta.indexOfFirst { it.id == origen.id }
        val indiceDestino = paraderosRuta.indexOfFirst { it.id == destino.id }

        val paraderosEnRuta = if (indiceOrigen < indiceDestino) {
            paraderosRuta.subList(indiceOrigen, indiceDestino + 1)
        } else {
            paraderosRuta.subList(indiceDestino, indiceOrigen + 1).reversed()
        }

        val numeroParaderos = paraderosEnRuta.size
        val tiempoEstimado = numeroParaderos * 3 // 3 minutos por paradero aprox

        return RutaCorredorResult(
            origen = origen,
            destino = destino,
            corredor = origen.corredor,
            colorCorredor = getColorCorredor(origen.corredor),
            paraderos = paraderosEnRuta,
            tiempoEstimado = tiempoEstimado,
            numeroParaderos = numeroParaderos,
            requiereTransferencia = false,
            tarifaGeneral = 2.50,
            tarifaUniversitaria = 1.25
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Planifica tu viaje - Corredores",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6F00),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Instrucciones
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFFF6F00)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Selecciona tu origen y destino del mismo corredor",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Selector de Origen
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF4CAF50), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Origen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedOrigen,
                        onExpandedChange = { expandedOrigen = !expandedOrigen }
                    ) {
                        OutlinedTextField(
                            value = origenSeleccionado?.nombre ?: "Selecciona un paradero",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOrigen)
                            },
                            colors = OutlinedTextFieldDefaults.colors()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedOrigen,
                            onDismissRequest = { expandedOrigen = false }
                        ) {
                            paraderos.forEach { paradero ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // Barra de color vertical a la izquierda
                                            Box(
                                                modifier = Modifier
                                                    .width(4.dp)
                                                    .height(48.dp)
                                                    .background(
                                                        Color(getColorCorredor(paradero.corredor)
                                                            .removePrefix("#")
                                                            .toLong(16)
                                                            .toInt())
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = paradero.nombre,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    "Corredor ${paradero.corredor.capitalize()}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = when (paradero.corredor.lowercase()) {
                                                        "azul" -> Color(0xFF0D47A1)
                                                        "rojo" -> Color(0xFFD32F2F)
                                                        else -> Color.Gray
                                                    }
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        origenSeleccionado = paradero
                                        expandedOrigen = false
                                        resultado = null
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Indicador visual de dirección
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ArrowDownward,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFFFF6F00)
                )
            }

            // Selector de Destino
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFF44336), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Destino",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedDestino,
                        onExpandedChange = { expandedDestino = !expandedDestino }
                    ) {
                        OutlinedTextField(
                            value = destinoSeleccionado?.nombre ?: "Selecciona un paradero",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDestino)
                            },
                            colors = OutlinedTextFieldDefaults.colors()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedDestino,
                            onDismissRequest = { expandedDestino = false }
                        ) {
                            paraderos.forEach { paradero ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // Barra de color vertical a la izquierda
                                            Box(
                                                modifier = Modifier
                                                    .width(4.dp)
                                                    .height(48.dp)
                                                    .background(
                                                        when (paradero.corredor.lowercase()) {
                                                            "azul" -> Color(0xFF0D47A1)
                                                            "rojo" -> Color(0xFFD32F2F)
                                                            else -> Color.Gray
                                                        }
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = paradero.nombre,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    "Corredor ${paradero.corredor.capitalize()}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = when (paradero.corredor.lowercase()) {
                                                        "azul" -> Color(0xFF0D47A1)
                                                        "rojo" -> Color(0xFFD32F2F)
                                                        else -> Color.Gray
                                                    }
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        destinoSeleccionado = paradero
                                        expandedDestino = false
                                        resultado = null
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Mensaje de advertencia si son de diferentes corredores
            if (origenSeleccionado != null && destinoSeleccionado != null && !sonDelMismoCorredor) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF9C4)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF57C00)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Los paraderos deben ser del mismo corredor",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            // Botón Calcular
            Button(
                onClick = {
                    if (puedeCalcular) {
                        scope.launch {
                            isCalculating = true
                            resultado = calcularRutaCorredor(
                                origenSeleccionado!!,
                                destinoSeleccionado!!
                            )
                            isCalculating = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = puedeCalcular && !isCalculating,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6F00),
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                )
            ) {
                if (isCalculating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calculando...")
                } else {
                    Icon(Icons.Default.Route, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calcular Ruta", style = MaterialTheme.typography.titleMedium)
                }
            }

            // Resultado
            resultado?.let { ruta ->
                ResultadoRutaCorredor(
                    ruta = ruta,
                    onVerEnMapa = { onMapaRutaClick(ruta) }
                )
            }
        }
    }
}

@Composable
fun ResultadoRutaCorredor(
    ruta: RutaCorredorResult,
    onVerEnMapa: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header del resultado
            Text(
                "Ruta Calculada",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6F00)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info resumida
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChipCorredor(
                    icon = Icons.Default.DirectionsBus,
                    label = "Corredor ${ruta.corredor.capitalize()}",
                    color = Color(ruta.colorCorredor.removePrefix("#").toLong(16).toInt())
                )
                InfoChipCorredor(
                    icon = Icons.Default.AccessTime,
                    label = "${ruta.tiempoEstimado} min",
                    color = Color(0xFF2196F3)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChipCorredor(
                    icon = Icons.Default.Pin,
                    label = "${ruta.numeroParaderos} paradas",
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tarifas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChipCorredor(
                    icon = Icons.Default.AccountBalance,
                    label = "S/ ${String.format("%.2f", ruta.tarifaGeneral)}",
                    color = Color(0xFFFF9800)
                )
                InfoChipCorredor(
                    icon = Icons.Default.School,
                    label = "S/ ${String.format("%.2f", ruta.tarifaUniversitaria)}",
                    color = Color(0xFF9C27B0)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Lista de paraderos
            Text(
                "Recorrido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ruta.paraderos.forEachIndexed { index, paradero ->
                ParaderoItem(
                    paradero = paradero,
                    numero = index + 1,
                    isOrigen = index == 0,
                    isDestino = index == ruta.paraderos.size - 1,
                    showLine = index < ruta.paraderos.size - 1
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Ver en Mapa
            Button(
                onClick = onVerEnMapa,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00C853)
                )
            ) {
                Icon(Icons.Default.Map, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Ruta en Mapa", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun InfoChipCorredor(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun ParaderoItem(
    paradero: Paradero,
    numero: Int,
    isOrigen: Boolean,
    isDestino: Boolean,
    showLine: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        when {
                            isOrigen -> Color(0xFF4CAF50)
                            isDestino -> Color(0xFFF44336)
                            else -> Color(0xFFFF6F00).copy(alpha = 0.7f)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = numero.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(40.dp)
                        .background(Color(0xFFFF6F00).copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (showLine) 0.dp else 8.dp)
        ) {
            Text(
                text = paradero.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isOrigen || isDestino) FontWeight.Bold else FontWeight.Normal
            )
            if (isOrigen || isDestino) {
                Text(
                    text = if (isOrigen) "🟢 Origen" else "🔴 Destino",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// Función auxiliar para obtener colores de corredores
fun getColorCorredor(corredor: String): String {
    return when (corredor.lowercase()) {
        "azul" -> "#0D47A1"
        "rojo" -> "#D32F2F"
        else -> "#757575"
    }
}