package com.example.dogapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogapp.data.local.Cita
import com.example.dogapp.data.repository.CitaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DetailCitaViewModel(
    private val repository: CitaRepository
) : ViewModel() {

    sealed interface Detalle {
        data object Cargando : Detalle
        data object NoDisponible : Detalle
        data class Disponible(val cita: Cita) : Detalle
    }

    private val _eliminada = MutableStateFlow(false)
    val eliminada = _eliminada.asStateFlow()

    fun observeCita(id: Long): Flow<Detalle> =
        repository.observeCitaById(id).map { cita ->
            if (cita == null) Detalle.NoDisponible else Detalle.Disponible(cita)
        }

    // HU 4.0 Criterio 4: elimina de Room sin diálogo de confirmación
    fun eliminar(cita: Cita) {
        viewModelScope.launch {
            try {
                repository.deleteCita(cita)
                _eliminada.value = true
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                // Si falla, la persona sigue en el detalle
            }
        }
    }
}