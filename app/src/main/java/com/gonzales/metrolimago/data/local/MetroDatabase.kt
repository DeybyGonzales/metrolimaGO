package com.gonzales.metrolimago.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gonzales.metrolimago.data.local.dao.EstacionDao
import com.gonzales.metrolimago.data.local.dao.LineaDao
import com.gonzales.metrolimago.data.local.dao.ParaderoDao
import com.gonzales.metrolimago.data.local.dao.CorredorDao
import com.gonzales.metrolimago.data.local.entities.Estacion
import com.gonzales.metrolimago.data.local.entities.Linea
import com.gonzales.metrolimago.data.local.entities.Paradero
import com.gonzales.metrolimago.data.local.entities.Corredor

@Database(
    entities = [
        Estacion::class,
        Linea::class,
        Paradero::class,
        Corredor::class
    ],
    version = 3,  // ✅ Cambiar de 2 a 3
    exportSchema = false
)
abstract class MetroDatabase : RoomDatabase() {
    abstract fun estacionDao(): EstacionDao
    abstract fun lineaDao(): LineaDao
    abstract fun paraderoDao(): ParaderoDao
    abstract fun corredorDao(): CorredorDao

    companion object {
        @Volatile
        private var INSTANCE: MetroDatabase? = null

        fun getDatabase(context: Context): MetroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MetroDatabase::class.java,
                    "metro_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}