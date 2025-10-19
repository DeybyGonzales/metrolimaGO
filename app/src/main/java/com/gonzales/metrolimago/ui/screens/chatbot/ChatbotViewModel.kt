package com.gonzales.metrolimago.ui.screens.chatbot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gonzales.metrolimago.data.local.MetroDatabase
import com.gonzales.metrolimago.ui.screens.chatbot.nlp.Intent
import com.gonzales.metrolimago.ui.screens.chatbot.nlp.IntentMatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ChatbotViewModel(app: Application) : AndroidViewModel(app) {

    private val estacionDao = MetroDatabase.getDatabase(app).estacionDao()
    private val corredorDao = MetroDatabase.getDatabase(app).corredorDao()
    private val paraderoDao = MetroDatabase.getDatabase(app).paraderoDao() // ✅ usar ParaderoDao

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    fun onUserQuestion(q: String) {
        append(ChatMessage(q, true))
        viewModelScope.launch {
            val reply = handle(q)
            append(ChatMessage(reply, false))
        }
    }

    private suspend fun handle(q: String): String {
        return when (val intent = IntentMatcher.match(q)) {

            is Intent.HorarioEstacion -> {
                val est = estacionDao.searchEstaciones(intent.estacion).firstOrNull()?.firstOrNull()
                if (est != null) {
                    "La estación ${est.nombre} abre a las ${est.horarioApertura} y cierra a las ${est.horarioCierre}."
                } else {
                    "No encontré la estación \"${intent.estacion}\". ¿Puedes ser más específico?"
                }
            }

            is Intent.EstacionesPorLinea -> {
                val numero = if (intent.linea.contains("2")) "2" else "1"

                val needles = listOf("línea $numero", "linea $numero", "l$numero", "l $numero", numero)

                var list: List<com.gonzales.metrolimago.data.local.entities.Estacion> = emptyList()
                for (n in needles) {
                    val found = estacionDao.getEstacionesByLineaLike(n).first()
                    if (found.isNotEmpty()) { list = found; break }
                }

                if (list.isEmpty()) {
                    val all = estacionDao.getAllEstaciones().first()
                    list = all.filter { est ->
                        val v = est.linea.lowercase().replace("í", "i")
                        v.contains("linea $numero") || v.contains("l$numero") || v == numero
                    }
                }

                if (list.isEmpty()) {
                    val distinct = estacionDao.getDistinctLineas().first().joinToString()
                    "No tengo estaciones para esa línea.\nValores ‘linea’ en BD: $distinct"
                } else {
                    "Estaciones de la línea $numero:\n" + list.joinToString("\n") { "• ${it.nombre}" }
                }
            }

            // ✅ Ahora devuelve la LISTA DE PARADEROS del corredor (solo Azul/Rojo)
            is Intent.CorredorPorColor -> {
                val colorUsuario = intent.color.lowercase()

                // Mapeo color hablado -> ID de corredor en tu JSON/BD
                val colorToId = mapOf(
                    "azul" to "azul",
                    "rojo" to "rojo"
                )

                val corredorId = colorToId[colorUsuario]
                    ?: return "Solo tengo información de los corredores Azul y Rojo. 🚍"

                val paraderos = paraderoDao.getParaderosByCorredorId(corredorId).first()

                if (paraderos.isEmpty()) {
                    val disponibles = paraderoDao.getDistinctCorredorIds().first().joinToString()
                    "No encontré paraderos para el corredor ${colorUsuario}.\nDisponibles en BD: $disponibles"
                } else {
                    "🚌 Paraderos del corredor ${colorUsuario.replaceFirstChar { it.uppercase() }}:\n" +
                            paraderos.joinToString("\n") { p ->
                                "• ${p.nombre}"
                            }
                }
            }

            is Intent.EstacionesPorDistrito -> {
                val all = estacionDao.getAllEstaciones().firstOrNull().orEmpty()
                val list = all.filter { it.distrito.equals(intent.distrito, ignoreCase = true) }
                if (list.isEmpty()) {
                    "No encontré estaciones en el distrito de ${intent.distrito}."
                } else {
                    "En ${intent.distrito} se encuentran las estaciones: " + list.joinToString { it.nombre }
                }
            }

            is Intent.ConexionCercana -> {
                val est = estacionDao.searchEstaciones(intent.estacion).firstOrNull()?.firstOrNull()
                if (est == null) {
                    "No encontré la estación \"${intent.estacion}\"."
                } else {
                    "Cercano a la estación ${est.nombre} (en ${est.distrito}) hay varios puntos de interés y conexiones. Puedes revisar el mapa para más detalles."
                }
            }

            is Intent.RutaEstacionAEstacion -> {
                "Para saber cómo ir de ${intent.origen} a ${intent.destino}, te recomiendo usar el planificador de rutas en el mapa. Te mostrará el camino más corto y los transbordos necesarios."
            }

            Intent.Desconocido -> {
                "Puedo ayudarte con horarios de estaciones, listados por línea o distrito y rutas. " +
                        "Por ejemplo: \"¿A qué hora abre la estación Angamos?\" o \"dime las estaciones de la línea 1\""
            }
        }
    }

    private fun append(m: ChatMessage) {
        _messages.value = _messages.value + m
    }
}
