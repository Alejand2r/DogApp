package com.example.dogapp

import android.content.Context
import java.util.UUID
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dogapp.data.local.AppDatabase
import com.example.dogapp.data.local.Cita
import com.example.dogapp.data.repository.CitaRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailCitaTest {
    @Test
    fun migratingFromVersionOneKeepsExistingAppointments() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "migration-${UUID.randomUUID()}.db"
        context.getDatabasePath(name).parentFile?.mkdirs()
        try {
            context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use { legacy ->
                legacy.execSQL(
                    """
                    CREATE TABLE citas (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombreMascota TEXT NOT NULL,
                        sintoma TEXT NOT NULL,
                        propietario TEXT NOT NULL,
                        telefono TEXT NOT NULL,
                        fecha TEXT NOT NULL,
                        hora TEXT NOT NULL,
                        avatarRes INTEGER
                    )
                    """.trimIndent()
                )
                legacy.execSQL(
                    "INSERT INTO citas VALUES (7, 'Kira', 'No come', 'Laura', '3159876543', '', '', NULL)"
                )
                legacy.version = 1
            }

            val database = Room.databaseBuilder(context, AppDatabase::class.java, name)
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build()
            try {
                val repository = CitaRepositoryImpl(database.citaDao())
                assertEquals(
                    Cita(7, "Kira", "No come", "Laura", "3159876543"),
                    repository.getCitaById(7)
                )
            } finally {
                database.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }

    @Test
    fun loadsTheRequestedRoomRecord() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val repository = CitaRepositoryImpl(database.citaDao())
            val otherId = repository.insertCita(Cita(nombreMascota = "Zeus", sintoma = "No come"))
            val selected = Cita(
                nombreMascota = "Kira",
                sintoma = "Tiene pulgas",
                propietario = "Laura",
                telefono = "3159876543",
                raza = "Akita",
                fotoUrl = "https://images.dog.ceo/breeds/akita/example.jpg"
            )
            val selectedId = repository.insertCita(selected)

            assertEquals(listOf(otherId, selectedId), repository.getAllCitas().first().map { it.id })
            assertEquals(selected.copy(id = selectedId), repository.getCitaById(selectedId))
            assertNull(repository.getCitaById(0L))
        } finally {
            database.close()
        }
    }
}
