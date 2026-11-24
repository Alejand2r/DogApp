package com.example.dogapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogapp.data.local.Cita
import com.example.dogapp.data.repository.CitaRepository
import com.example.dogapp.data.repository.DogBreedRepository
import com.example.dogapp.ui.viewmodels.NewCitaViewModel.Catalogo
import com.example.dogapp.ui.viewmodels.NewCitaViewModel.Estado
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditCitaViewModel(
    private val repository: CitaRepository,
    private val dogBreedRepository: DogBreedRepository
) : ViewModel() {

    private val _cita = MutableStateFlow<Cita?>(null)
    val cita: StateFlow<Cita?> = _cita.asStateFlow()

    private val _estado = MutableStateFlow(Estado.Inicial)
    val estado = _estado.asStateFlow()

    private val _catalogo = MutableStateFlow<Catalogo>(Catalogo.Cargando)
    val catalogo = _catalogo.asStateFlow()

    init {
        cargarRazas()
    }

    // HU 5.0 - Criterio 3: lista de razas desde https://dog.ceo/api/breeds/list/all
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

    // HU 5.0 - Criterio 2: trae los datos de la cita para precargar los campos
    fun cargarCita(id: Long) {
        if (_cita.value != null) return
        viewModelScope.launch {
            try {
                _cita.value = repository.getCitaById(id)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) { }
        }
    }

    fun limpiarErrorDeRaza() {
        if (_estado.value == Estado.RazaInvalida) _estado.value = Estado.Inicial
    }

    // HU 5.0 - Criterio 8: modifica la cita en Room
    fun guardar(nombre: String, nombreRaza: String, propietario: String, telefono: String) {
        val original = _cita.value ?: return
        // Evita doble clic mientras ya se está guardando
        if (_estado.value == Estado.Guardando || _estado.value == Estado.Guardada) return

        // HU 5.0 - Criterio 3: la raza escrita debe existir en la lista de la API
        val razas = (_catalogo.value as? Catalogo.Disponible)?.razas
        val raza = razas?.firstOrNull { it.name.equals(nombreRaza.trim(), ignoreCase = true) }
        if (raza == null) {
            _estado.value = Estado.RazaInvalida
            return
        }

        _estado.value = Estado.Guardando
        viewModelScope.launch {
            // Misma raza -> conserva la foto. Raza nueva -> pide una foto nueva a la API.
            val fotoUrl = if (raza.name.equals(original.raza, ignoreCase = true) &&
                !original.fotoUrl.isNullOrBlank()
            ) {
                original.fotoUrl
            } else {
                try {
                    dogBreedRepository.getImageUrl(raza)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _estado.value = Estado.ErrorFoto
                    return@launch
                }
            }

            // HU 5.0 - Criterio 8: modifica la cita en Room (se conservan id, síntoma, fecha, hora)
            try {
                repository.updateCita(
                    original.copy(
                        nombreMascota = nombre.trim(),
                        propietario = propietario.trim(),
                        telefono = telefono,
                        raza = raza.name,
                        fotoUrl = fotoUrl
                    )
                )
                _estado.value = Estado.Guardada   // la UI navega al Home al ver este estado
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _estado.value = Estado.ErrorGuardado
            }
        }
    }
    }