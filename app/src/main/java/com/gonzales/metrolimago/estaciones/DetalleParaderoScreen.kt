package com.gonzales.metrolimago.estaciones

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gonzales.metrolimago.data.local.entities.Paradero

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleParaderoScreen(
    paraderoId: String,
    onBackClick: () -> Unit,
    onMapClick: () -> Unit,  // ✅ NUEVO parámetro
    viewModel: EstacionesViewModel = viewModel()
) {
    var paradero by remember { mutableStateOf<Paradero?>(null) }

    LaunchedEffect(paraderoId) {
        paradero = viewModel.getParaderoById(paraderoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Paradero", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    paradero?.let { par ->
                        IconButton(onClick = {
                            viewModel.toggleFavoritoParadero(par.id, !par.esFavorito)
                            paradero = par.copy(esFavorito = !par.esFavorito)
                        }) {
                            Icon(
                                if (par.esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (par.esFavorito) Color.Red else Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        paradero?.let { par ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con nombre y corredor
                HeaderParaderoSection(paradero = par)

                // Información general
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRowParadero(
                            icon = Icons.Default.LocationCity,
                            label = "Zona",
                            value = par.zona
                        )
                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        InfoRowParadero(
                            icon = Icons.Default.DirectionsBus,
                            label = "Corredor",
                            value = when(par.corredor) {
                                "azul" -> "Corredor Azul"
                                "rojo" -> "Corredor Rojo"
                                "verde" -> "Corredor Verde"
                                "amarillo" -> "Corredor Amarillo"
                                else -> "Corredor"
                            },
                            color = getCorredorColor(par.corredor)
                        )
                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        InfoRowParadero(
                            icon = Icons.Default.Numbers,
                            label = "Orden",
                            value = "Paradero #${par.orden}"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ BOTONES DE ACCIÓN (Mapa y Compartir)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onMapClick,  // ✅ Llamar al mapa
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver en mapa")
                    }

                    OutlinedButton(
                        onClick = { /* TODO: Compartir */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compartir")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun HeaderParaderoSection(paradero: Paradero) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(getCorredorColor(paradero.corredor))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.DirectionsBus,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                paradero.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Text(
                    when(paradero.corredor) {
                        "azul" -> "Corredor Azul"
                        "rojo" -> "Corredor Rojo"
                        "verde" -> "Corredor Verde"
                        "amarillo" -> "Corredor Amarillo"
                        else -> "Corredor"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun InfoRowParadero(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color ?: MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun getCorredorColor(corredorId: String): Color {
    return when(corredorId) {
        "azul" -> Color(0xFF2196F3)
        "rojo" -> Color(0xFFF44336)
        "verde" -> Color(0xFF4CAF50)
        "amarillo" -> Color(0xFFFF9800)
        else -> Color(0xFF2196F3)
    }
}