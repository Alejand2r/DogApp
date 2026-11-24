package com.example.dogapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dogapp.data.local.AppDatabase
import com.example.dogapp.data.local.Cita
import com.example.dogapp.data.repository.CitaRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/** HU 5.0: editar una cita actualiza Room y conserva el turno (id) y el síntoma. */
@RunWith(AndroidJUnit4::class)
class EditCitaTest {

    @Test
    fun editingACitaUpdatesRoomAndKeepsIdAndSymptom() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val repository = CitaRepositoryImpl(database.citaDao())
            val id = repository.insertCita(
                Cita(
                    nombreMascota = "Cory",
                    sintoma = "Tiene garrapatas",
                    propietario = "Walter",
                    telefono = "3045675518",
                    raza = "Pitbull",
                    fotoUrl = "https://images.dog.ceo/breeds/pitbull/a.jpg"
                )
            )
            val original = repository.getCitaById(id)!!

            val editada = original.copy(
                nombreMascota = "Rocky",
                raza = "Pug",
                propietario = "Laura",
                telefono = "3001112233",
                fotoUrl = "https://images.dog.ceo/breeds/pug/b.jpg"
            )
            repository.updateCita(editada)

            val guardada = repository.getCitaById(id)!!
            assertEquals(editada, guardada)
            assertEquals(id, guardada.id)
            assertEquals("Tiene garrapatas", guardada.sintoma)
            assertEquals(editada, repository.observeCitaById(id).first())
        } finally {
            database.close()
        }
    }
}
