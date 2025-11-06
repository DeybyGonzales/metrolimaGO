package com.gonzales.metrolimago.ui.screens.mapa

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.gonzales.metrolimago.data.local.entities.Estacion
import com.gonzales.metrolimago.estaciones.RutaResult
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaRutaScreen(
    ruta: RutaResult,
    onBack: () -> Unit
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
                        text = "Ruta: ${ruta.origen.nombre} → ${ruta.destino.nombre}",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    mapView?.let { map ->
                        val allPoints = ruta.estaciones.map {
                            GeoPoint(it.latitud, it.longitud)
                        }
                        if (allPoints.isNotEmpty()) {
                            val bounds = org.osmdroid.util.BoundingBox.fromGeoPoints(allPoints)

                            // Expandir bounds con padding
                            val expandedBounds = org.osmdroid.util.BoundingBox(
                                bounds.latNorth + 0.02,
                                bounds.lonEast + 0.02,
                                bounds.latSouth - 0.02,
                                bounds.lonWest - 0.02
                            )

                            map.zoomToBoundingBox(expandedBounds, true)
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Ver ruta completa"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Mapa
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    createMapViewWithRoute(ctx, ruta).also { mapView = it }
                }
            )

            // Card de información
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🟢 ${ruta.origen.nombre}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            Text(
                                text = "🔴 ${ruta.destino.nombre}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${ruta.tiempoEstimado} min",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${ruta.numeroEstaciones} estaciones",
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
private fun createMapViewWithRoute(context: Context, ruta: RutaResult): MapView {
    return MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
        minZoomLevel = 10.0  // ← Cambiar de 12 a 10 (más zoom out)
        maxZoomLevel = 19.0

        val puntos = ruta.estaciones.map {
            GeoPoint(it.latitud, it.longitud)
        }

        val polyline = Polyline().apply {
            outlinePaint.color = Color.RED
            outlinePaint.strokeWidth = 12f
            setPoints(puntos)
        }
        overlays.add(polyline)

        ruta.estaciones.forEachIndexed { index, estacion ->
            val marker = Marker(this).apply {
                position = GeoPoint(estacion.latitud, estacion.longitud)
                title = estacion.nombre

                snippet = when {
                    index == 0 -> "🟢 Origen - ${estacion.distrito}"
                    index == ruta.estaciones.size - 1 -> "🔴 Destino - ${estacion.distrito}"
                    else -> "Estación ${index + 1} - ${estacion.distrito}"
                }

                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            overlays.add(marker)
        }

        // MEJORADO: Ajustar zoom con más padding y delay
        postDelayed({
            val bounds = org.osmdroid.util.BoundingBox.fromGeoPoints(puntos)

            // Expandir el bounding box para más padding
            val expandedBounds = org.osmdroid.util.BoundingBox(
                bounds.latNorth + 0.02,  // Agregar padding arriba
                bounds.lonEast + 0.02,   // Agregar padding derecha
                bounds.latSouth - 0.02,  // Agregar padding abajo
                bounds.lonWest - 0.02    // Agregar padding izquierda
            )

            zoomToBoundingBox(expandedBounds, true)
        }, 300) // Delay de 300ms para que el mapa esté listo
    }
}