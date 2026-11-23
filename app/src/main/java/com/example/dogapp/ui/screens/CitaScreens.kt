package com.example.dogapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dogapp.ui.components.BreedField
import com.example.dogapp.ui.components.CitaImage
import com.example.dogapp.data.local.Cita
import com.example.dogapp.ui.theme.AccentPink
import com.example.dogapp.ui.theme.DarkGrayHeader
import com.example.dogapp.ui.theme.DogAppTheme
import com.example.dogapp.ui.theme.FredokaFontFamily
import com.example.dogapp.ui.theme.TextPrimary
import com.example.dogapp.ui.theme.TextSecondary
import com.example.dogapp.ui.theme.White
import kotlinx.coroutines.CancellationException
import com.example.dogapp.ui.viewmodels.NewCitaViewModel

@Composable
fun DetailCitaScreen(
    citaId: Long,
    loadCita: suspend (Long) -> Cita?,
    onBack: () -> Unit
) {
    val result by produceState<Result<Cita?>?>(initialValue = null, key1 = citaId) {
        value = null
        value = try {
            Result.success(loadCita(citaId))
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
    val cita = result?.getOrNull()

    CitaScreen(title = cita?.nombreMascota ?: "Detalle de la Cita", onBack = onBack) {
        when {
            result == null -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            result?.isFailure == true -> Text("No se pudo cargar la cita.")
            cita == null -> Text("La cita no está disponible.")
            else -> CitaDetails(cita)
        }
    }
}

@Composable
private fun CitaDetails(cita: Cita) {
    Box(Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 96.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "#${cita.id}",
                    color = AccentPink,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )
                Spacer(Modifier.height(50.dp))
                cita.raza?.let { raza ->
                    Text(raza, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                Text(
                    text = cita.sintoma,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(color = AccentPink)
                if (cita.propietario.isNotBlank()) Text("Propietario: ${cita.propietario}")
                if (cita.telefono.isNotBlank()) Text("Teléfono: ${cita.telefono}")
                if (cita.fecha.isNotBlank()) Text("Fecha: ${cita.fecha}")
                if (cita.hora.isNotBlank()) Text("Hora: ${cita.hora}")
            }
        }
        Card(
            modifier = Modifier.align(Alignment.TopCenter).widthIn(max = 184.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            CitaImage(
                cita = cita,
                contentDescription = "Imagen de ${cita.nombreMascota}",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().height(184.dp).padding(8.dp)
            )
        }
    }
}

private val sintomas = listOf(
    "Solo duerme",
    "No come",
    "Fractura extremidad",
    "Tiene pulgas",
    "Tiene garrapatas",
    "Bota demasiado pelo"
)

@Composable
fun NewCitaScreen(
    viewModel: NewCitaViewModel,
    onBack: () -> Unit
) {
    var mascota by rememberSaveable { mutableStateOf("") }
    var raza by rememberSaveable { mutableStateOf("") }
    var propietario by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var sintoma by rememberSaveable { mutableStateOf("") }
    var menuAbierto by remember { mutableStateOf(false) }
    var faltaSintoma by rememberSaveable { mutableStateOf(false) }
    val estado by viewModel.estado.collectAsState()
    val catalogo by viewModel.catalogo.collectAsState()
    val razas = remember(catalogo) {
        (catalogo as? NewCitaViewModel.Catalogo.Disponible)?.razas?.map { it.name }.orEmpty()
    }
    val guardando = estado == NewCitaViewModel.Estado.Guardando
    val camposCompletos = mascota.isNotBlank() && raza.isNotBlank() &&
        propietario.isNotBlank() && telefono.isNotBlank()
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedBorderColor = Color(0xFF6550A4),
        focusedLabelColor = Color(0xFF6550A4),
        unfocusedLabelColor = TextSecondary,
        unfocusedBorderColor = TextSecondary
    )
    LaunchedEffect(estado) {
        if (estado == NewCitaViewModel.Estado.Guardada) onBack()
    }

    if (faltaSintoma) {
        AlertDialog(
            onDismissRequest = { faltaSintoma = false },
            text = { Text("Selecciona un síntoma") },
            confirmButton = {
                TextButton(onClick = { faltaSintoma = false }) { Text("Aceptar") }
            }
        )
    }

    BackHandler(enabled = guardando) {}
    CitaScreen(title = "Nueva Cita", onBack = onBack, canGoBack = !guardando) {
        OutlinedTextField(
            value = mascota,
            onValueChange = { mascota = it.take(15) },
            label = { Text("Nombre de la Mascota") },
            enabled = !guardando,
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )
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
        OutlinedTextField(
            value = propietario,
            onValueChange = { propietario = it.take(30) },
            label = { Text("Nombre del Propietario") },
            enabled = !guardando,
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it.filter { char -> char in '0'..'9' }.take(10) },
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = !guardando,
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )
        Box(Modifier.align(Alignment.Start)) {
            TextButton(onClick = { menuAbierto = true }, enabled = !guardando) {
                Text(sintoma.ifBlank { "Síntomas" })
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                sintomas.forEach { opcion ->
                    DropdownMenuItem(text = { Text(opcion) }, onClick = {
                        sintoma = opcion
                        faltaSintoma = false
                        menuAbierto = false
                    })
                }
            }
        }
        val error = when (estado) {
            NewCitaViewModel.Estado.RazaInvalida -> "Selecciona una raza de la lista."
            NewCitaViewModel.Estado.ErrorFoto -> "No se pudo obtener la foto de la raza. Intente nuevamente."
            NewCitaViewModel.Estado.ErrorGuardado -> "No se pudo guardar la cita. Intente nuevamente."
            else -> null
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = {
                if (sintoma.isBlank()) {
                    faltaSintoma = true
                } else {
                    viewModel.guardar(
                        Cita(
                            nombreMascota = mascota.trim(),
                            propietario = propietario.trim(),
                            telefono = telefono,
                            sintoma = sintoma
                        ),
                        nombreRaza = raza
                    )
                }
            },
            enabled = !guardando && camposCompletos,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPink,
                contentColor = White,
                disabledContainerColor = AccentPink.copy(alpha = 0.4f)
            )
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(
                text = if (guardando) "Guardando…" else "Guardar Cita",
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = if (camposCompletos && !guardando) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun CitaScreen(
    title: String,
    onBack: () -> Unit,
    canGoBack: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = White,
        contentColor = TextPrimary,
        topBar = {
            Surface(
                color = DarkGrayHeader,
                shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(Modifier.statusBarsPadding().padding(vertical = 12.dp)) {
                    IconButton(
                        onClick = onBack,
                        enabled = canGoBack,
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar", tint = AccentPink)
                    }
                    Text(
                        text = title,
                        color = White,
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).padding(horizontal = 64.dp)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CitaDetailsPreview() {
    DogAppTheme {
        CitaScreen(title = "Kira", onBack = {}) {
            CitaDetails(
                Cita(
                    id = 2,
                    nombreMascota = "Kira",
                    sintoma = "No come",
                    propietario = "Laura Gómez",
                    telefono = "3159876543"
                )
            )
        }
    }
}
