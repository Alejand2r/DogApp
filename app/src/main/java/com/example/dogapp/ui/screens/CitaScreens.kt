package com.example.dogapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dogapp.data.local.Cita
import com.example.dogapp.ui.components.BreedField
import com.example.dogapp.ui.components.CitaImage
import com.example.dogapp.ui.theme.AccentPink
import com.example.dogapp.ui.theme.DarkGrayHeader
import com.example.dogapp.ui.theme.DogAppTheme
import com.example.dogapp.ui.theme.FredokaFontFamily
import com.example.dogapp.ui.theme.TextPrimary
import com.example.dogapp.ui.theme.TextSecondary
import com.example.dogapp.ui.theme.White
import com.example.dogapp.ui.viewmodels.DetailCitaViewModel
import com.example.dogapp.ui.viewmodels.NewCitaViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FabPosition

// ============================================================================
// HU 4.0: VENTANA DETALLE DE LA CITA
// ============================================================================

@Composable
fun DetailCitaScreen(
    citaId: Long,
    viewModel: DetailCitaViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit
) {
    val detalle by remember(citaId) { viewModel.observeCita(citaId) }
        .collectAsState(initial = DetailCitaViewModel.Detalle.Cargando)
    val eliminada by viewModel.eliminada.collectAsState()
    val cita = (detalle as? DetailCitaViewModel.Detalle.Disponible)?.cita

    // Criterio 4: al eliminar, vuelve al Home
    LaunchedEffect(eliminada) {
        if (eliminada) onDeleted()
    }

    DetailCitaContent(
        detalle = detalle,
        eliminada = eliminada,
        onBack = onBack,
        onEdit = onEdit,
        onDelete = { cita?.let { viewModel.eliminar(it) } }
    )
}

@Composable
private fun DetailCitaContent(
    detalle: DetailCitaViewModel.Detalle,
    eliminada: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val cita = (detalle as? DetailCitaViewModel.Detalle.Disponible)?.cita

    Scaffold(
        containerColor = White,
        contentColor = TextPrimary,
        topBar = {
            CitaTopBar(
                title = cita?.nombreMascota ?: "Detalle de la Cita",
                onBack = onBack
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Criterio 4: eliminar (rosado, abajo izquierda)
                FloatingActionButton(
                    onClick = onDelete,
                    containerColor = AccentPink,
                    contentColor = White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar cita")
                }

                // Criterio 5: editar (azul oscuro, abajo derecha)
                FloatingActionButton(
                    onClick = onEdit,
                    containerColor = DarkGrayHeader,
                    contentColor = White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar cita")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                detalle == DetailCitaViewModel.Detalle.Cargando -> CircularProgressIndicator()
                cita != null -> CitaDetails(cita)
                !eliminada -> Text("La cita no está disponible.")
            }
        }
    }
}

@Composable
private fun CitaDetails(cita: Cita) {
    Box(Modifier.fillMaxWidth().padding(top = 16.dp)) {

        // Criterio 3: tarjeta blanca con bordes redondeados
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 96.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Turno (id de Room), arriba a la derecha
                Text(
                    text = "#${cita.id}",
                    color = AccentPink,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )
                Spacer(Modifier.height(50.dp)) // espacio de la mitad inferior de la imagen

                // Raza en negro y bold
                cita.raza?.let { raza ->
                    Text(
                        text = raza,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                // Síntoma
                Text(
                    text = cita.sintoma,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                // Línea delgada rosada
                HorizontalDivider(color = AccentPink)
                // Propietario y teléfono
                Text("Propietario: ${cita.propietario}")
                Text("Teléfono: ${cita.telefono}")
            }
        }

        // Criterio 2: tarjeta con elevación y esquinas redondeadas que contiene la imagen
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 184.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
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

// ============================================================================
// HU 3.0: VENTANA NUEVA CITA (sin cambios)
// ============================================================================

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
            TextButton(
                onClick = { menuAbierto = true },
                enabled = !guardando,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = TextPrimary,
                    disabledContentColor = TextSecondary
                )
            ) {
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
                disabledContainerColor = AccentPink.copy(alpha = 0.4f),
                disabledContentColor = TextPrimary // texto legible mientras el botón está inactivo
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

// ============================================================================
// ESTRUCTURA COMÚN: toolbar curva + contenido + capa flotante (HU 3, 4 y 5)
// ============================================================================

@Composable
internal fun CitaScreen(
    title: String,
    onBack: () -> Unit,
    canGoBack: Boolean = true,
    floatingContent: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = White,
        contentColor = TextPrimary,
        topBar = {
            // Toolbar gris oscuro con esquinas inferiores curvas
            Surface(
                color = DarkGrayHeader,
                shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(Modifier.statusBarsPadding().padding(vertical = 12.dp)) {
                    // Flecha atrás rosada
                    IconButton(
                        onClick = onBack,
                        enabled = canGoBack,
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar", tint = AccentPink)
                    }
                    // Título blanco bold centrado
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            // Capa 1: contenido con scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content
            )
            // Capa 2: botones flotantes, encima del contenido
            floatingContent()
        }
    }
}

/** Toolbar gris oscuro con esquinas inferiores curvas, título blanco bold y flecha rosada. */
@Composable
private fun CitaTopBar(title: String, onBack: () -> Unit) {
    Surface(
        color = DarkGrayHeader,
        shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(Modifier.statusBarsPadding().padding(vertical = 12.dp)) {
            IconButton(
                onClick = onBack,
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
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailCitaPreview() {
    DogAppTheme {
        DetailCitaContent(
            detalle = DetailCitaViewModel.Detalle.Disponible(
                Cita(
                    id = 2,
                    nombreMascota = "Kira",
                    sintoma = "Tiene garrapatas",
                    propietario = "Laura Gómez",
                    telefono = "3159876543",
                    raza = "Pitbull"
                )
            ),
            eliminada = false,
            onBack = {},
            onEdit = {},
            onDelete = {}
        )
    }
}