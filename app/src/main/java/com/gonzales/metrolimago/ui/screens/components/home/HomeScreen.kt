package com.gonzales.metrolimago.ui.screens.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gonzales.metrolimago.home.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEstaciones: () -> Unit,
    onNavigateToPlanificador: () -> Unit,
    navToConfiguracion: ((String) -> Unit)? = null
) {
    val appBarColor = Color(0xFF6A1B9A) // morado del header

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MetroLima GO",
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = {
                        // Navega a configuración usando la ruta de tu sistema actual
                        navToConfiguracion?.invoke(Screen.Configuracion.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = appBarColor
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Metro de Lima",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text("Conectando Lima")

                // Planifica tu viaje
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPlanificador() },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    ListItem(
                        headlineContent = { Text("Planifica tu viaje") },
                        supportingContent = { Text("Encuentra la mejor ruta para tu destino") },
                        leadingContent = {
                            Icon(Icons.Default.Train, contentDescription = null)
                        }
                    )
                }

                // Explorar estaciones
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToEstaciones() },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
                ) {
                    ListItem(
                        headlineContent = { Text("Explorar estaciones") },
                        supportingContent = { Text("Consulta información de todas las estaciones") },
                        leadingContent = {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        }
                    )
                }

                // Estado del servicio
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
                ) {
                    ListItem(
                        headlineContent = {
                            Text("Servicio Operativo", color = Color(0xFF2E7D32))
                        },
                        supportingContent = {
                            Text("Todas las líneas funcionando con normalidad")
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    )
                }

                // Idioma actual
                Text(
                    text = "Idioma actual: Español 🇪🇸",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
