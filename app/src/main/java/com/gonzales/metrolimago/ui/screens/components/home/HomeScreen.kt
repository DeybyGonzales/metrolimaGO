package com.gonzales.metrolimago.ui.screens.components.home

import com.gonzales.metrolimago.ui.screens.components.QuickActionCard
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gonzales.metrolimago.R
import com.gonzales.metrolimago.home.navigation.Screen
import com.gonzales.metrolimago.util.LanguageManager

import androidx.compose.material.icons.filled.ChatBubbleOutline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEstaciones: () -> Unit,
    onNavigateToPlanificador: () -> Unit,
    onNavigateToPlanificadorCorredores: () -> Unit = {},  // ✅ NUEVO PARÁMETRO
    onNavigateToFavoritos: () -> Unit = {},
    navToConfiguracion: ((String) -> Unit)? = null,
    onNavigateToChat: () -> Unit, // 👈 nuevo




) {
    val context = LocalContext.current
    val gradientColors = listOf(Color(0xFF6A1B9A), Color(0xFF9C27B0))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navToConfiguracion?.invoke(Screen.Configuracion.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.configuracion),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A1B9A)
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                // Header con gradiente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(gradientColors)
                        )
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.titulo_principal),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.subtitulo_principal),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ✅ 1. Card: Planifica tu viaje (Línea 1 y 2)
                    MenuCard(
                        icon = Icons.Default.Train,
                        title = "Planifica tu viaje",
                        subtitle = "Línea 1 y Línea 2 del Metro",
                        backgroundColor = Color(0xFFE3F2FD),
                        iconColor = Color(0xFF1976D2),
                        onClick = onNavigateToPlanificador
                    )

                    // ✅ 2. Card: Planifica tu viaje (Corredores) - NUEVO
                    MenuCard(
                        icon = Icons.Default.DirectionsBus,
                        title = "Planifica tu viaje",
                        subtitle = "Corredores Complementarios",
                        backgroundColor = Color(0xFFFFF3E0),
                        iconColor = Color(0xFFFF6F00),
                        onClick = onNavigateToPlanificadorCorredores
                    )

                    // ✅ 3. Card: Explorar estaciones
                    MenuCard(
                        icon = Icons.Default.LocationOn,
                        title = stringResource(R.string.explorar_estaciones),
                        subtitle = stringResource(R.string.explorar_estaciones_desc),
                        backgroundColor = Color(0xFFE8F5E9),
                        iconColor = Color(0xFF2E7D32),
                        onClick = onNavigateToEstaciones
                    )

                    // ✅ 4. Card: Favoritos
                    MenuCard(
                        icon = Icons.Default.Favorite,
                        title = stringResource(R.string.favoritos),
                        subtitle = stringResource(R.string.mis_favoritos_desc),
                        backgroundColor = Color(0xFFFFEBEE),
                        iconColor = Color(0xFFD32F2F),
                        onClick = onNavigateToFavoritos
                    )
                    QuickActionCard(
                        icon = Icons.Default.ChatBubbleOutline,
                        title = "Chat",
                        subtitle = "Pregúntame por estaciones",
                        onClick = onNavigateToChat // 👈 aquí
                    )



                    // Card: Estado del servicio
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.servicio_operativo),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = stringResource(R.string.servicio_operativo_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF558B2F)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Idioma actual
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.idioma_actual),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Text(
                                    text = LanguageManager.getCurrentLanguageName(context),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6A1B9A)
                                )
                            }
                            Text(
                                text = if (LanguageManager.getLanguage(context) == "es") "🇪🇸" else "🇺🇸",
                                fontSize = 32.sp
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun MenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.2f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}