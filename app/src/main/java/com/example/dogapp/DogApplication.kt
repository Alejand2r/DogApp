package com.example.dogapp

import android.app.Application
import com.example.dogapp.data.local.AppDatabase
import com.example.dogapp.data.repository.CitaRepository
import com.example.dogapp.data.repository.CitaRepositoryImpl
import com.example.dogapp.ui.viewmodels.AppViewModelFactory

/**
 * Punto único donde se arma la arquitectura MVVM:
 * Room (AppDatabase) -> Repository -> ViewModelFactory.
 * Los Fragments toman la factory desde aquí.
 */
class DogApplication : Application() {
    val repository: CitaRepository by lazy {
        CitaRepositoryImpl(AppDatabase.getDatabase(this).citaDao())
    }
    val viewModelFactory: AppViewModelFactory by lazy { AppViewModelFactory(repository) }
}
