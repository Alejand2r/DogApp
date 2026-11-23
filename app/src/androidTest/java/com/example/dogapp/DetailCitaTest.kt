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
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailCitaTest {
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
                telefono = "3159876543"
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
