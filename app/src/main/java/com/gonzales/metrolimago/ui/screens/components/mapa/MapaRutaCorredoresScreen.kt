package com.gonzales.metrolimago.ui.screens.mapa

import android.content.Context
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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    mapView?.let { map ->
                        val allPoints = ruta.paraderos.map {
                            GeoPoint(it.latitud, it.longitud)
                        }
                        if (allPoints.isNotEmpty()) {
                            val bounds = org.osmdroid.util.BoundingBox.fromGeoPoints(allPoints)
                            val expandedBounds = org.osmdroid.util.BoundingBox(
                                bounds.latNorth + 0.01,
                                bounds.lonEast + 0.01,
                                bounds.latSouth - 0.01,
                                bounds.lonWest - 0.01
                            )
                            map.zoomToBoundingBox(expandedBounds, true)
                        }
                    }
                },
                containerColor = Color(0xFFFF6F00)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Ver ruta completa",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 🗺️ Mapa con ubicación en tiempo real
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    createMapViewWithRouteAndLocation(ctx, ruta).also { mapView = it }
                }
            )

            // 📋 Información de la ruta en la parte inferior
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Origen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "🟢 ${ruta.origen.nombre}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Destino
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFFF44336), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "🔴 ${ruta.destino.nombre}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // Info del viaje
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            InfoItemMapa(
                                icon = Icons.Default.DirectionsBus,
                                text = "Corredor ${ruta.corredor.capitalize()}",
                                color = Color(
                                    ruta.colorCorredor.removePrefix("#").toLong(16).toInt()
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${ruta.tiempoEstimado} min",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6F00)
                            )
                            Text(
                                text = "${ruta.numeroParaderos} paradas",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDetach()
        }
    }
}

private fun createMapViewWithRouteAndLocation(
    context: Context,
    ruta: RutaCorredorResult
): MapView {
    return MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)

        // 🔹 ACTIVAR UBICACIÓN EN TIEMPO REAL (punto azul)
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        overlays.add(locationOverlay)

        minZoomLevel = 10.0
        maxZoomLevel = 19.0

        // Crear puntos de la ruta
        val puntos = ruta.paraderos.map {
            GeoPoint(it.latitud, it.longitud)
        }

        // Dibujar línea de ruta con el color del corredor
        val polyline = Polyline().apply {
            outlinePaint.color = android.graphics.Color.parseColor(ruta.colorCorredor)
            outlinePaint.strokeWidth = 12f
            setPoints(puntos)
        }
        overlays.add(polyline)

        // Agregar marcadores para cada paradero
        ruta.paraderos.forEachIndexed { index, paradero ->
            val marker = Marker(this).apply {
                position = GeoPoint(paradero.latitud, paradero.longitud)
                title = paradero.nombre
                snippet = when {
                    index == 0 -> "🟢 Origen - ${paradero.zona}"
                    index == ruta.paraderos.size - 1 -> "🔴 Destino - ${paradero.zona}"
                    else -> "Parada ${index + 1} - ${paradero.zona}"
                }
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            overlays.add(marker)
        }

        // Ajustar zoom con padding para mostrar toda la ruta
        postDelayed({
            val bounds = org.osmdroid.util.BoundingBox.fromGeoPoints(puntos)
            val expandedBounds = org.osmdroid.util.BoundingBox(
                bounds.latNorth + 0.01,
                bounds.lonEast + 0.01,
                bounds.latSouth - 0.01,
                bounds.lonWest - 0.01
            )
            zoomToBoundingBox(expandedBounds, true)
        }, 300)
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