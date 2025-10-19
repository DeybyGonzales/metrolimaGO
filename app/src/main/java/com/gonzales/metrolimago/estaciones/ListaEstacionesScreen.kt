package com.gonzales.metrolimago.estaciones

// ⚠️ Asegúrate de que todos estos imports están (ya los tenías)
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gonzales.metrolimago.R
import com.gonzales.metrolimago.data.local.entities.Estacion
import com.gonzales.metrolimago.data.local.entities.Paradero
import com.gonzales.metrolimago.ui.theme.MetroColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaEstacionesScreen(
    navController: NavController, // <--- AÑADE EL PARÁMETRO AQUÍ
    onEstacionClick: (String) -> Unit,
    onParaderoClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: EstacionesViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val lineas by viewModel.lineas.collectAsState()
    val corredores by viewModel.corredores.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.transporte), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.volver))
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
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { viewModel.onFilterSelected(null) },
                        label = { Text(stringResource(R.string.todas)) }
                    )
                }
                items(lineas) { linea ->
                    FilterChip(
                        selected = selectedFilter == linea.id,
                        onClick = { viewModel.onFilterSelected(linea.id) },
                        label = { Text(linea.nombre) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        Color(android.graphics.Color.parseColor(linea.color)),
                                        CircleShape
                                    )
                            )
                        }
                    )
                }
                items(corredores) { corredor ->
                    FilterChip(
                        selected = selectedFilter == corredor.id,
                        onClick = { viewModel.onFilterSelected(corredor.id) },
                        label = { Text(corredor.nombre) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        Color(android.graphics.Color.parseColor(corredor.color)),
                                        CircleShape
                                    )
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_resultados))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) { item ->
                        when (item) {
                            is TransportItem.EstacionItem -> {
                                EstacionCard(
                                    estacion = item.estacion,
                                    onClick = { onEstacionClick(item.estacion.id) },
                                    onFavoritoClick = {
                                        viewModel.toggleFavorita(item.estacion.id, !item.estacion.esFavorita)
                                    }
                                )
                            }
                            is TransportItem.ParaderoItem -> {
                                // Aquí se llama a la función corregida
                                ParaderoCard(
                                    paradero = item.paradero,
                                    onClick = { onParaderoClick(item.paradero.id) },
                                    onFavoritoClick = {
                                        viewModel.toggleFavoritoParadero(item.paradero.id, !item.paradero.esFavorito)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.buscar)) },
        leadingIcon = {
            Icon(Icons.Default.Search, stringResource(R.string.buscar))
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, stringResource(R.string.limpiar))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstacionCard(
    estacion: Estacion,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = estacion.imagenPrincipalResId),
                contentDescription = estacion.nombre,
                modifier = Modifier
                    .size(60.dp) // Tamaño rectangular
                    .clip(RoundedCornerShape(8.dp)), // Con bordes redondeados
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(8.dp, 48.dp)
                    .background(
                        if (estacion.linea == "linea1") MetroColors.Linea1 else MetroColors.Linea2,
                        RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    estacion.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        estacion.distrito,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            IconButton(onClick = onFavoritoClick) {
                Icon(
                    if (estacion.esFavorita) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorito),
                    tint = if (estacion.esFavorita) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// -----------------------------------------------------------------
//  ✅ ¡AQUÍ ESTÁ LA FUNCIÓN CORREGIDA! ✅
// -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParaderoCard(
    paradero: Paradero,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ CAMBIO:
            // Reemplazamos el Surface con Icon por un Image,
            // usando la 'imagenPrincipalResId' del paradero
            Image(
                painter = painterResource(id = paradero.imagenPrincipalResId),
                contentDescription = paradero.nombre,
                modifier = Modifier
                    .size(60.dp) // Mismo tamaño que EstacionCard
                    .clip(RoundedCornerShape(8.dp)), // Misma forma
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // ✅ NUEVO:
            // Añadimos una barra de color para el corredor
            Box(
                modifier = Modifier
                    .size(8.dp, 48.dp)
                    .background(
                        color = when(paradero.corredor) { // Usa el color del corredor
                            "azul" -> Color(0xFF2196F3)
                            "rojo" -> Color(0xFFF44336)
                            else -> Color.Gray
                        },
                        RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    paradero.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        paradero.zona, // Mostramos la 'zona'
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            IconButton(onClick = onFavoritoClick) {
                Icon(
                    if (paradero.esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorito),
                    tint = if (paradero.esFavorito) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}