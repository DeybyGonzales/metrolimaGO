package com.gonzales.metrolimago.ui.screens.chatbot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController // <-- 1. IMPORTA NavController

@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar
@Composable
fun ChatbotScreen(
    navController: NavController, // <-- 2. AÑADE EL PARÁMETRO AQUÍ
    vm: ChatbotViewModel = viewModel()
) {
    val messages by vm.messages.collectAsState()

    // 3. USA Scaffold para añadir una barra superior con botón de retroceso
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistente Virtual") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // <-- 4. Usa el navController para volver atrás
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues) // Usa el padding del Scaffold
                .padding(horizontal = 16.dp) // Añade padding horizontal
                .padding(bottom = 16.dp) // Y un poco en la parte inferior
        ) {
            // La lista de mensajes ocupa el espacio restante
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                items(messages.reversed()) { m ->
                    AssistBubble(m)
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            // Campo de entrada y botón de envío
            var input by remember { mutableStateOf("") }
            Row {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Pregúntame sobre estaciones…") }
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            vm.onUserQuestion(input)
                            input = ""
                        }
                    }
                ) { Icon(Icons.Default.Send, contentDescription = "Enviar") }
            }
        }
    }
}

@Composable
private fun AssistBubble(m: ChatMessage) {
    val bg = if (m.fromUser) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.secondaryContainer
    val align = if (m.fromUser) Arrangement.End else Arrangement.Start

    Row(Modifier.fillMaxWidth(), horizontalArrangement = align) {
        Surface(
            tonalElevation = 1.dp,
            color = bg,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                m.text,
                Modifier.padding(horizontal = 16.dp, vertical = 10.dp), // Padding ajustado
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

data class ChatMessage(val text: String, val fromUser: Boolean)
