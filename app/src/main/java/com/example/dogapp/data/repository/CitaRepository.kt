package com.example.dogapp.data.repository

import com.example.dogapp.data.local.Cita
import kotlinx.coroutines.flow.Flow

interface CitaRepository {
    fun getAllCitas(): Flow<List<Cita>>
    suspend fun getCitaById(id: Long): Cita?
    suspend fun insertCita(cita: Cita): Long
    suspend fun insertAll(citas: List<Cita>): List<Long>
    suspend fun updateCita(cita: Cita): Int
    suspend fun deleteCita(cita: Cita): Int
}
