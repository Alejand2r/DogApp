package com.example.dogapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogapp.data.local.Cita
import com.example.dogapp.data.repository.CitaRepository
import com.example.dogapp.data.repository.DogBreed
import com.example.dogapp.data.repository.DogBreedRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewCitaViewModel(
    private val repository: CitaRepository,
    private val dogBreedRepository: DogBreedRepository
) : ViewModel() {
    enum class Estado { Inicial, Guardando, Guardada, ErrorFoto, ErrorGuardado, RazaInvalida }

    sealed interface Catalogo {
        data object Cargando : Catalogo
        data object Error : Catalogo
        data class Disponible(val razas: List<DogBreed>) : Catalogo
    }

    private val _estado = MutableStateFlow(Estado.Inicial)
    val estado = _estado.asStateFlow()

    private val _catalogo = MutableStateFlow<Catalogo>(Catalogo.Cargando)
    val catalogo = _catalogo.asStateFlow()

    init {
        cargarRazas()
    }

    fun cargarRazas() {
        _catalogo.value = Catalogo.Cargando
        viewModelScope.launch {
            _catalogo.value = try {
                Catalogo.Disponible(dogBreedRepository.getBreeds())
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                Catalogo.Error
            }
        }
    }

    fun limpiarErrorDeRaza() {
        if (_estado.value == Estado.RazaInvalida) _estado.value = Estado.Inicial
    }

    fun guardar(cita: Cita, nombreRaza: String) {
        if (_estado.value == Estado.Guardando || _estado.value == Estado.Guardada) return

        val razas = (_catalogo.value as? Catalogo.Disponible)?.razas
        val raza = razas?.firstOrNull { it.name.equals(nombreRaza.trim(), ignoreCase = true) }
        if (raza == null) {
            _estado.value = Estado.RazaInvalida
            return
        }

        _estado.value = Estado.Guardando
        viewModelScope.launch {
            val fotoUrl = try {
                dogBreedRepository.getImageUrl(raza)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _estado.value = Estado.ErrorFoto
                return@launch
            }

            try {
                repository.insertCita(cita.copy(raza = raza.name, fotoUrl = fotoUrl))
                _estado.value = Estado.Guardada
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _estado.value = Estado.ErrorGuardado
            }
        }
    }
}
