package com.gonzales.metrolimago.ui.screens.mapa

import android.content.Context
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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaEstacionScreen(
    estacion: Estacion,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }

    // Configurar OSMDroid una sola vez
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ubicación de ${estacion.nombre}",
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
                    // Centrar en la estación
                    mapView?.controller?.animateTo(
                        GeoPoint(estacion.latitud, estacion.longitud)
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Centrar"
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
                    createMapView(ctx, estacion).also { mapView = it }
                }
            )

            // Card de información sobre el mapa
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = estacion.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${estacion.linea} • ${estacion.distrito}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Horario: ${estacion.horarioApertura} - ${estacion.horarioCierre}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Cleanup cuando se destruye la pantalla
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDetach()
        }
    }
}

private fun createMapView(context: Context, estacion: Estacion): MapView {
    return MapView(context).apply {
        // Configuración del mapa
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true) // Habilitar zoom con pellizco

        // Configurar zoom
        minZoomLevel = 12.0
        maxZoomLevel = 20.0
        controller.setZoom(17.0) // Zoom inicial para ver bien la estación

        // Centrar en la estación
        val ubicacion = GeoPoint(estacion.latitud, estacion.longitud)
        controller.setCenter(ubicacion)

        // Agregar marcador de la estación
        val marker = Marker(this).apply {
            position = ubicacion
            title = estacion.nombre
            snippet = "${estacion.linea} - ${estacion.distrito}"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            // Personalizar el marcador según la línea
            // Puedes cambiar el ícono aquí si quieres
        }
        overlays.add(marker)

        // Mostrar el info window del marcador automáticamente
        marker.showInfoWindow()
    }
}