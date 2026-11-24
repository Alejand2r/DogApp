package com.example.dogapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dogapp.data.repository.CitaRepository
import com.example.dogapp.data.repository.DogBreedRepository

class AppViewModelFactory(
    private val repository: CitaRepository,
    private val dogBreedRepository: DogBreedRepository = DogBreedRepository()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NewCitaViewModel::class.java) -> {
                NewCitaViewModel(repository, dogBreedRepository) as T
            }
            modelClass.isAssignableFrom(DetailCitaViewModel::class.java) -> {
                DetailCitaViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EditCitaViewModel::class.java) -> {
                EditCitaViewModel(repository, dogBreedRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
