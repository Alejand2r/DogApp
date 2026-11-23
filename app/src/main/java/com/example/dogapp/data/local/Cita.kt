package com.example.dogapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class Cita(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreMascota: String,
    val sintoma: String,
    val propietario: String = "",
    val telefono: String = "",
    val fecha: String = "",
    val hora: String = "",
    val avatarRes: Int? = null
)
