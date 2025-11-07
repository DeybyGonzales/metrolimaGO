package com.gonzales.metrolimago

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
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

        // ✅ Pedir permiso de ubicación si no está concedido
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

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
