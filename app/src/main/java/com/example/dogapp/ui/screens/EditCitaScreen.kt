package com.example.dogapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dogapp.ui.components.BreedField
import com.example.dogapp.ui.theme.AccentPink
import com.example.dogapp.ui.theme.TextPrimary
import com.example.dogapp.ui.theme.TextSecondary
import com.example.dogapp.ui.theme.White
import com.example.dogapp.ui.viewmodels.EditCitaViewModel
import com.example.dogapp.ui.viewmodels.NewCitaViewModel

// ============================================================================
// HU 5.0: VENTANA EDITAR CITA
// ============================================================================

@Composable
fun EditCitaScreen(
    citaId: Long,
    viewModel: EditCitaViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var mascota by rememberSaveable { mutableStateOf("") }
    var raza by rememberSaveable { mutableStateOf("") }
    var propietario by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var precargado by rememberSaveable { mutableStateOf(false) }

    val cita by viewModel.cita.collectAsState()
    val estado by viewModel.estado.collectAsState()
    val catalogo by viewModel.catalogo.collectAsState()
    val razas = remember(catalogo) {
        (catalogo as? NewCitaViewModel.Catalogo.Disponible)?.razas?.map { it.name }.orEmpty()
    }
    val guardando = estado == NewCitaViewModel.Estado.Guardando

    // Criterio 6: el botón se habilita solo si los 4 campos están diligenciados
    val camposCompletos = mascota.isNotBlank() && raza.isNotBlank() &&
            propietario.isNotBlank() && telefono.isNotBlank()

    // Criterios 2 a 5: hint gris que sube al enfocar y borde morado
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedBorderColor = Color(0xFF6550A4),
        focusedLabelColor = Color(0xFF6550A4),
        unfocusedLabelColor = TextSecondary,
        unfocusedBorderColor = TextSecondary
    )

    // Pide la cita a Room
    LaunchedEffect(citaId) { viewModel.cargarCita(citaId) }

    // Criterio 2: precarga los campos (solo la primera vez, para no pisar lo que se esté editando)
    LaunchedEffect(cita) {
        val actual = cita
        if (!precargado && actual != null) {
            mascota = actual.nombreMascota
            raza = actual.raza.orEmpty()
            propietario = actual.propietario
            telefono = actual.telefono
            precargado = true
        }
    }

    // Criterio 8: al terminar de guardar, navega al Home
    LaunchedEffect(estado) {
        if (estado == NewCitaViewModel.Estado.Guardada) onSaved()
    }

    BackHandler(enabled = guardando) {}
    CitaScreen(title = "Editar Cita", onBack = onBack, canGoBack = !guardando) {
        if (!precargado) {
            CircularProgressIndicator()
            return@CitaScreen
        }

        // Criterio 2: máximo 15 caracteres
        OutlinedTextField(
            value = mascota,
            onValueChange = { mascota = it.take(15) },
            label = { Text("Nombre de la Mascota") },
            enabled = !guardando,
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        // Criterio 3: AutoComplete de razas con la lista de la API
        BreedField(
            value = raza,
            onValueChange = {
                raza = it
                viewModel.limpiarErrorDeRaza()
            },
            names = razas,
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth()
        )
        when (catalogo) {
            NewCitaViewModel.Catalogo.Cargando -> Text("Cargando razas…")
            NewCitaViewModel.Catalogo.Error -> {
                Text("No se pudo cargar la lista de razas.")
                TextButton(onClick = viewModel::cargarRazas) { Text("Reintentar") }
            }
            is NewCitaViewModel.Catalogo.Disponible -> Unit
        }

        // Criterio 4: máximo 30 caracteres
        OutlinedTextField(
            value = propietario,
            onValueChange = { propietario = it.take(30) },
            label = { Text("Nombre del Propietario") },
            enabled = !guardando,
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        // Criterio 5: solo números, máximo 10
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it.filter { c -> c in '0'..'9' }.take(10) },
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = !guardando,
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        val error = when (estado) {
            NewCitaViewModel.Estado.RazaInvalida -> "Selecciona una raza de la lista."
            NewCitaViewModel.Estado.ErrorFoto -> "No se pudo obtener la foto de la raza. Intente nuevamente."
            NewCitaViewModel.Estado.ErrorGuardado -> "No se pudo guardar la cita. Intente nuevamente."
            else -> null
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        // Criterios 6 y 7: botón rosado, ícono a la izquierda, texto blanco bold al habilitarse
        Button(
            onClick = { viewModel.guardar(mascota, raza, propietario, telefono) },
            enabled = !guardando && camposCompletos,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPink,
                contentColor = White,
                disabledContainerColor = AccentPink.copy(alpha = 0.4f),
                disabledContentColor = TextPrimary // texto legible mientras el botón está inactivo
            )
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(
                text = if (guardando) "Guardando…" else "Editar Cita",
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = if (camposCompletos && !guardando) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
