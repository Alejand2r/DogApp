package com.example.dogapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogapp.data.local.Cita
import com.example.dogapp.data.repository.CitaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: CitaRepository
) : ViewModel() {

    // Estado reactivo de las citas expuesto como StateFlow
    val citas: StateFlow<List<Cita>> = repository.getAllCitas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteCita(cita: Cita) {
        viewModelScope.launch {
            repository.deleteCita(cita)
        }
    }
}
