package com.example.dogapp.data.repository

import com.example.dogapp.data.local.Cita
import com.example.dogapp.data.local.CitaDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CitaRepositoryImpl(
    private val citaDao: CitaDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CitaRepository {

    override fun getAllCitas(): Flow<List<Cita>> {
        return citaDao.getAllCitas()
    }

    override suspend fun getCitaById(id: Long): Cita? = withContext(ioDispatcher) {
        citaDao.getCitaById(id)
    }

    override suspend fun insertCita(cita: Cita): Long = withContext(ioDispatcher) {
        citaDao.insertCita(cita)
    }

    override suspend fun insertAll(citas: List<Cita>): List<Long> = withContext(ioDispatcher) {
        citaDao.insertAll(citas)
    }

    override suspend fun updateCita(cita: Cita): Int = withContext(ioDispatcher) {
        citaDao.updateCita(cita)
    }

    override suspend fun deleteCita(cita: Cita): Int = withContext(ioDispatcher) {
        citaDao.deleteCita(cita)
    }
}
