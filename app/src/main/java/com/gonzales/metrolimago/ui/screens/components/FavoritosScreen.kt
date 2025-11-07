package com.gonzales.metrolimago.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gonzales.metrolimago.R
import com.gonzales.metrolimago.estaciones.EstacionCard
import com.gonzales.metrolimago.estaciones.EstacionesViewModel
import com.gonzales.metrolimago.estaciones.ParaderoCard
import com.gonzales.metrolimago.estaciones.TransportItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    onEstacionClick: (String) -> Unit,
    onParaderoClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: EstacionesViewModel = viewModel()
) {
    val favoritos by viewModel.favoritos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.favoritos),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            stringResource(R.string.volver)
                        )
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
        if (favoritos.isEmpty()) {
            // Estado vacío
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Text(
                        stringResource(R.string.sin_favoritos),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        stringResource(R.string.sin_favoritos_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            // Lista de favoritos
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Información superior
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                stringResource(R.string.total_favoritos, favoritos.size),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                stringResource(R.string.toca_corazon_quitar),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Lista de favoritos
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoritos) { item ->
                        when (item) {
                            is TransportItem.EstacionItem -> {
                                EstacionCard(
                                    estacion = item.estacion,
                                    onClick = { onEstacionClick(item.estacion.id) },
                                    onFavoritoClick = {
                                        viewModel.toggleFavorita(
                                            item.estacion.id,
                                            !item.estacion.esFavorita
                                        )
                                    }
                                )
                            }
                            is TransportItem.ParaderoItem -> {
                                ParaderoCard(
                                    paradero = item.paradero,
                                    onClick = { onParaderoClick(item.paradero.id) },
                                    onFavoritoClick = {
                                        viewModel.toggleFavoritoParadero(
                                            item.paradero.id,
                                            !item.paradero.esFavorito
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}