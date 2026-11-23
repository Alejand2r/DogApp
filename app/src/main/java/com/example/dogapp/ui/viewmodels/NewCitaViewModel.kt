package com.example.dogapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogapp.data.local.Cita
import com.example.dogapp.data.repository.CitaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewCitaViewModel(private val repository: CitaRepository) : ViewModel() {
    enum class Estado { Inicial, Guardando, Guardada, Error }

    private val _estado = MutableStateFlow(Estado.Inicial)
    val estado = _estado.asStateFlow()

    fun guardar(cita: Cita) {
        if (_estado.value == Estado.Guardando || _estado.value == Estado.Guardada) return

        _estado.value = Estado.Guardando
        viewModelScope.launch {
            try {
                repository.insertCita(cita)
                _estado.value = Estado.Guardada
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _estado.value = Estado.Error
            }
        }
    }
}
