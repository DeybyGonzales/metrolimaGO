package com.gonzales.metrolimago.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gonzales.metrolimago.R
import com.gonzales.metrolimago.util.LanguageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(navController: NavController) {
    val context = LocalContext.current
    val idiomaActual = remember { mutableStateOf(LanguageManager.getLanguage(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.configuracion)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE53935),
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
                .padding(16.dp)
        ) {
            // Título de sección
            Text(
                text = stringResource(R.string.seleccionar_idioma),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Idioma actual
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE53935).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.idioma_actual),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = LanguageManager.getCurrentLanguageName(context),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFE53935)
                        )
                    }
                }
            }

            // Opciones de idioma
            Text(
                text = stringResource(R.string.cambiar_idioma),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Opción Español
            LanguageOption(
                nombreIdioma = stringResource(R.string.idioma_espanol),
                codigoIdioma = "es",
                isSelected = idiomaActual.value == "es",
                onClick = {
                    idiomaActual.value = "es"
                    LanguageManager.setLanguage(context, "es")

                    // 🔁 Regresa al Home y recarga idioma
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opción Inglés
            LanguageOption(
                nombreIdioma = stringResource(R.string.idioma_ingles),
                codigoIdioma = "en",
                isSelected = idiomaActual.value == "en",
                onClick = {
                    idiomaActual.value = "en"
                    LanguageManager.setLanguage(context, "en")

                    // 🔁 Regresa al Home y recarga idioma
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun LanguageOption(
    nombreIdioma: String,
    codigoIdioma: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE53935) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = nombreIdioma,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) Color.White else Color.Black
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = Color.White
                )
            }
        }
    }
}
