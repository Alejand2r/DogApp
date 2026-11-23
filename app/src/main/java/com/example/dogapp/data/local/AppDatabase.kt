package com.example.dogapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dogapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Cita::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun citaDao(): CitaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        internal val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE citas ADD COLUMN raza TEXT")
                db.execSQL("ALTER TABLE citas ADD COLUMN fotoUrl TEXT")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dogapp_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.citaDao())
                    }
                }
            }

            fun populateInitialData(citaDao: CitaDao) {
                if (citaDao.getCount() == 0) {
                    val initialList = listOf(
                        Cita(
                            nombreMascota = "Zeus",
                            sintoma = "Tiene vómito y no quiere comer desde la mañana",
                            propietario = "Carlos Mendoza",
                            telefono = "3104567890",
                            fecha = "2026-11-23",
                            hora = "10:00 AM",
                            avatarRes = R.drawable.pet1
                        ),
                        Cita(
                            nombreMascota = "Kira",
                            sintoma = "Revisión general y aplicación de vacunas anuales",
                            propietario = "Laura Gómez",
                            telefono = "3159876543",
                            fecha = "2026-11-23",
                            hora = "11:30 AM",
                            avatarRes = R.drawable.pet2
                        ),
                        Cita(
                            nombreMascota = "Max",
                            sintoma = "Cojera leve en la pata trasera derecha al correr",
                            propietario = "Andrés Restrepo",
                            telefono = "3001234567",
                            fecha = "2026-11-24",
                            hora = "02:00 PM",
                            avatarRes = R.drawable.dog_head
                        ),
                        Cita(
                            nombreMascota = "Luna",
                            sintoma = "Alergia en la piel y rascado continuo en las orejas",
                            propietario = "Sofía Herrera",
                            telefono = "3187654321",
                            fecha = "2026-11-24",
                            hora = "03:30 PM",
                            avatarRes = R.drawable.pet1
                        )
                    )
                    citaDao.insertAll(initialList)
                }
            }
        }
    }
}
