package com.gonzales.metrolimago.ui.screens.chatbot.nlp

object IntentMatcher {
    fun match(q: String): Intent {
        val t = q.lowercase().trim()

        // 1️⃣ Horario de estación
        Regex("""(hora|horario).*(abre|cierra).*([a-záéíóúñ ]{3,})""").find(t)?.let {
            val est = it.groupValues.last().trim()
            return Intent.HorarioEstacion(est)
        }

        // 2️⃣ 🚌 Corredor por color (solo Azul y Rojo, flexible con frases)
        Regex("""(estaciones|rutas?|paraderos|info|información)?.*?(del|de)?\s*(corredor|color)\s*(azul|rojo)""")
            .find(t)?.let {
                val color = it.groupValues.last().trim().lowercase()
                return Intent.CorredorPorColor(color)
            }

        // Variante más directa (por si el usuario solo dice “corredor azul” o “rutas corredor rojo”)
        Regex("""(corredor|color)\s*(azul|rojo)""")
            .find(t)?.let {
                val color = it.groupValues.last().trim().lowercase()
                return Intent.CorredorPorColor(color)
            }

        // 3️⃣ Estaciones por línea
        Regex("""(estaciones|paradas|lista|listado|rutas?).*?(de|por)?\s*(la\s*)?(línea|linea)\s*(\d+)""")
            .find(t)?.let {
                val num = it.groupValues.last().trim()
                return Intent.EstacionesPorLinea("línea $num")
            }

        // Variante tipo “dime todas las de la línea 1”
        Regex("""(todas|todos|dime|muéstrame|muestrame|listar|listado|lista).*(de|por)\s*(la\s*)?(línea|linea)\s*(\d+)""")
            .find(t)?.let {
                val num = it.groupValues.last().trim()
                return Intent.EstacionesPorLinea("línea $num")
            }

        // 4️⃣ Estaciones por distrito (evita capturar corredor/color/línea)
        Regex("""(estaciones|paradas).*(en|del)\s+(?!corredor\b|color\b|línea\b|linea\b)([a-záéíóúñ ]{3,})""")
            .find(t)?.let {
                return Intent.EstacionesPorDistrito(it.groupValues.last().trim())
            }

        // 5️⃣ Conexiones o paraderos cercanos
        Regex("""(conexiones|paraderos|corredores).*(en|de)\s+([a-záéíóúñ ]{3,})""")
            .find(t)?.let {
                return Intent.ConexionCercana(it.groupValues.last().trim())
            }

        // 6️⃣ Ruta entre estaciones
        Regex("""(cómo|como|ruta|ir).*(de)\s+([a-záéíóúñ ]{3,}).*(a)\s+([a-záéíóúñ ]{3,})""")
            .find(t)?.let {
                val (o, d) = it.groupValues[3].trim() to it.groupValues[5].trim()
                return Intent.RutaEstacionAEstacion(o, d)
            }

        // 7️⃣ Si no coincide con nada, intent desconocido
        return Intent.Desconocido
    }
}

// --- Intents ---
sealed class Intent {
    data class HorarioEstacion(val estacion: String): Intent()
    data class EstacionesPorLinea(val linea: String): Intent()
    data class EstacionesPorDistrito(val distrito: String): Intent()
    data class ConexionCercana(val estacion: String): Intent()
    data class RutaEstacionAEstacion(val origen: String, val destino: String): Intent()
    data class CorredorPorColor(val color: String): Intent()
    data object Desconocido: Intent()
}
