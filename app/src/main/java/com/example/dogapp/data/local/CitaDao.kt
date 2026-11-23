package com.example.dogapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {

    @Query("SELECT * FROM citas ORDER BY id ASC")
    fun getAllCitas(): Flow<List<Cita>>

    @Query("SELECT * FROM citas WHERE id = :id LIMIT 1")
    fun getCitaById(id: Long): Cita?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCita(cita: Cita): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(citas: List<Cita>): List<Long>

    @Update
    fun updateCita(cita: Cita): Int

    @Delete
    fun deleteCita(cita: Cita): Int

    @Query("SELECT COUNT(*) FROM citas")
    fun getCount(): Int
}
