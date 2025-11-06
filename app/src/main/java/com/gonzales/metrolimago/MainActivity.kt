package com.gonzales.metrolimago

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.gonzales.metrolimago.data.local.DataLoader
import com.gonzales.metrolimago.data.local.MetroDatabase
import com.gonzales.metrolimago.home.navigation.MetroNavigation
import com.gonzales.metrolimago.ui.theme.MetroLimaGOTheme
import com.gonzales.metrolimago.util.LanguageManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aplicar idioma guardado al iniciar la app
        val savedLanguage = LanguageManager.getLanguage(this)
        LanguageManager.applyLanguage(this, savedLanguage)

        // Cargar datos iniciales
        lifecycleScope.launch {
            val database = MetroDatabase.getDatabase(applicationContext)
            val dataLoader = DataLoader(applicationContext, database)
            dataLoader.loadInitialData()
        }

        setContent {
            MetroLimaGOTheme {
                MetroNavigation()
            }
        }
    }
}