package com.gonzales.metrolimago.ui.screens.planificador

import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.School
import com.gonzales.metrolimago.estaciones.RutaResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.gonzales.metrolimago.data.local.entities.Estacion
import com.gonzales.metrolimago.estaciones.EstacionesViewModel
import com.gonzales.metrolimago.ui.theme.MetroColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificadorScreen(
    onBackClick: () -> Unit,
    onMapaRutaClick: (RutaResult) -> Unit = {},
    viewModel: EstacionesViewModel = viewModel()
) {
    var estaciones by remember { mutableStateOf<List<Estacion>>(emptyList()) }
    var origenSeleccionado by remember { mutableStateOf<Estacion?>(null) }
    var destinoSeleccionado by remember { mutableStateOf<Estacion?>(null) }
    var resultado by remember { mutableStateOf<com.gonzales.metrolimago.estaciones.RutaResult?>(null) }
    var isCalculating by remember { mutableStateOf(false) }
    var expandedOrigen by remember { mutableStateOf(false) }
    var expandedDestino by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Cargar estaciones al inicio
    LaunchedEffect(Unit) {
        estaciones = viewModel.getAllEstacionesList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planifica tu Viaje", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Selecciona tu origen y destino para calcular la mejor ruta",
                        style = MaterialTheme.typography.bodyMedium
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
                            value = origenSeleccionado?.nombre ?: "Selecciona una estación",
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
                            estaciones.forEach { estacion ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(estacion.nombre)
                                            Text(
                                                if (estacion.linea == "linea1") "Línea 1" else "Línea 2",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (estacion.linea == "linea1")
                                                    MetroColors.Linea1 else MetroColors.Linea2
                                            )
                                        }
                                    },
                                    onClick = {
                                        origenSeleccionado = estacion
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
                    tint = MaterialTheme.colorScheme.primary
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
                            value = destinoSeleccionado?.nombre ?: "Selecciona una estación",
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
                            estaciones.forEach { estacion ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(estacion.nombre)
                                            Text(
                                                if (estacion.linea == "linea1") "Línea 1" else "Línea 2",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (estacion.linea == "linea1")
                                                    MetroColors.Linea1 else MetroColors.Linea2
                                            )
                                        }
                                    },
                                    onClick = {
                                        destinoSeleccionado = estacion
                                        expandedDestino = false
                                        resultado = null
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Botón Calcular
            Button(
                onClick = {
                    if (origenSeleccionado != null && destinoSeleccionado != null) {
                        scope.launch {
                            isCalculating = true
                            resultado = viewModel.calcularRuta(
                                origenSeleccionado!!.id,
                                destinoSeleccionado!!.id
                            )
                            isCalculating = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = origenSeleccionado != null && destinoSeleccionado != null && !isCalculating,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
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
                ResultadoRuta(
                    ruta = ruta,
                    onVerEnMapa = { onMapaRutaClick(ruta) }
                )
            }
        }
    }
}

@Composable
fun ResultadoRuta(
    ruta: com.gonzales.metrolimago.estaciones.RutaResult,
    onVerEnMapa: () -> Unit = {},
    context: android.content.Context = androidx.compose.ui.platform.LocalContext.current
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header del resultado
            Text(
                "🎉 Ruta Calculada",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info resumida
            Row(

                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip(
                    icon = Icons.Default.Train,
                    label = if (ruta.linea == "linea1") "Línea 1" else "Línea 2",
                    color = if (ruta.linea == "linea1") MetroColors.Linea1 else MetroColors.Linea2
                )
                InfoChip(
                    icon = Icons.Default.AccessTime,
                    label = "${ruta.tiempoEstimado} min",
                    color = Color(0xFF2196F3)
                )
                InfoChip(
                    icon = Icons.Default.Pin,
                    label = "${ruta.numeroEstaciones} estaciones",
                    color = Color(0xFF4CAF50)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

// Tarifa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip(
                    icon = Icons.Default.AccountBalance,
                    label = "S/ ${String.format("%.2f", ruta.tarifaGeneral)}",
                    color = Color(0xFFFF9800)
                )
                InfoChip(
                    icon = Icons.Default.School,
                    label = "S/ ${String.format("%.2f", ruta.tarifaUniversitaria)} Universitaria",
                    color = Color(0xFF9C27B0)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Lista de estaciones
            Text(
                "Recorrido:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ruta.estaciones.forEachIndexed { index, estacion ->
                EstacionItem(
                    estacion = estacion,
                    numero = index + 1,
                    isOrigen = index == 0,
                    isDestino = index == ruta.estaciones.size - 1,
                    showLine = index < ruta.estaciones.size - 1
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
                    containerColor = androidx.compose.ui.graphics.Color(0xFF00C853)
                )
            ) {
                Icon(Icons.Default.Map, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver ruta en mapa", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f)
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
fun EstacionItem(
    estacion: Estacion,
    numero: Int,
    isOrigen: Boolean,
    isDestino: Boolean,
    showLine: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Indicador visual
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
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
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
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Info de la estación
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (showLine) 0.dp else 8.dp)
        ) {
            Text(
                text = estacion.nombre,
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