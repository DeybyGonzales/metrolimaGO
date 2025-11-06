package com.gonzales.metrolimago.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LanguageManager {

    private const val PREF_NAME = "app_preferences"
    private const val KEY_LANGUAGE = "selected_language"

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, Locale.getDefault().language) ?: "es"
    }

    fun setLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()

        applyLanguage(context, languageCode, shouldRecreate = true)
    }

    // ✅ Parámetro nuevo para controlar cuándo reiniciar
    fun applyLanguage(context: Context, languageCode: String, shouldRecreate: Boolean = false) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Solo reinicia si cambiaste el idioma manualmente
        if (shouldRecreate && context is Activity) {
            context.recreate()
        }
    }

    fun getCurrentLanguageName(context: Context): String {
        return when (getLanguage(context)) {
            "es" -> "Español"
            "en" -> "English"
            else -> "Español"
        }
    }
}