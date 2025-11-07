package com.gonzales.metrolimago.ui.screens.mapa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.gonzales.metrolimago.estaciones.RutaCorredorResult
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaRutaCorredoresScreen(
    ruta: RutaCorredorResult,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }

    // Configurar OSMDroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ruta: ${ruta.origen.nombre} → ${ruta.destino.nombre}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Mapa
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        // Configurar zoom y centro inicial
                        val puntoInicial = GeoPoint(
                            ruta.origen.latitud,
                            ruta.origen.longitud
                        )
                        controller.setZoom(14.0)
                        controller.setCenter(puntoInicial)

                        mapView = this
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { map ->
                    map.overlays.clear()

                    // Dibujar línea de ruta
                    val polyline = Polyline().apply {
                        outlinePaint.color = android.graphics.Color.parseColor(
                            ruta.colorCorredor
                        )
                        outlinePaint.strokeWidth = 12f

                        ruta.paraderos.forEach { paradero ->
                            addPoint(GeoPoint(paradero.latitud, paradero.longitud))
                        }
                    }
                    map.overlays.add(polyline)

                    // Agregar marcadores para cada paradero
                    ruta.paraderos.forEachIndexed { index, paradero ->
                        val marker = Marker(map).apply {
                            position = GeoPoint(paradero.latitud, paradero.longitud)
                            title = paradero.nombre

                            // Personalizar icono según si es origen, destino u otro
                            when {
                                index == 0 -> {
                                    // Origen - marcador verde
                                    snippet = "🟢 Origen"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                index == ruta.paraderos.size - 1 -> {
                                    // Destino - marcador rojo
                                    snippet = "🔴 Destino"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                else -> {
                                    // Paradero intermedio
                                    snippet = "Parada ${index + 1}"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                            }
                        }
                        map.overlays.add(marker)
                    }

                    map.invalidate()
                }
            )

            // Información de la ruta en la parte inferior
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Origen
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(Color(0xFF4CAF50), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        ruta.origen.nombre,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Destino
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(Color(0xFFF44336), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        ruta.destino.nombre,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Info del corredor
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoItemMapa(
                                icon = Icons.Default.DirectionsBus,
                                text = ruta.corredor,
                                color = Color(
                                    ruta.colorCorredor.removePrefix("#").toLong(16).toInt()
                                )
                            )
                            InfoItemMapa(
                                icon = Icons.Default.MyLocation,
                                text = "${ruta.numeroParaderos} paradas",
                                color = Color(0xFF2196F3)
                            )
                        }
                    }
                }
            }

            // Botón para centrar en origen
            FloatingActionButton(
                onClick = {
                    mapView?.controller?.animateTo(
                        GeoPoint(ruta.origen.latitud, ruta.origen.longitud)
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = Color.White
            ) {
                Icon(
                    Icons.Default.MyLocation,
                    contentDescription = "Centrar en origen",
                    tint = Color(0xFFFF6F00)
                )
            }
        }
    }
}

@Composable
private fun InfoItemMapa(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}
