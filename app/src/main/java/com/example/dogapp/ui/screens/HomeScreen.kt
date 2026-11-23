package com.example.dogapp.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dogapp.R
import com.example.dogapp.data.local.Cita
import com.example.dogapp.ui.components.HeaderCurvedShape
import com.example.dogapp.ui.theme.AccentPink
import com.example.dogapp.ui.theme.DarkGrayHeader
import com.example.dogapp.ui.theme.DogAppTheme
import com.example.dogapp.ui.theme.FredokaFontFamily
import com.example.dogapp.ui.theme.TextPrimary
import com.example.dogapp.ui.theme.TextSecondary
import com.example.dogapp.ui.theme.White
import com.example.dogapp.ui.viewmodels.HomeViewModel

/**
 * HU 2.0: Home Administrador de Citas (Arquitectura MVVM)
 * - View (HomeScreen): Observa el estado desde HomeViewModel.
 * - ViewModel (HomeViewModel): Gestiona el StateFlow de las citas.
 * - Repository (CitaRepository): Fuente de verdad conectada a Room.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToNewCita: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Criterio 1: Al presionar Atrás, enviar al escritorio del celular
    BackHandler(enabled = true) {
        activity?.moveTaskToBack(true)
    }

    // Criterio 6: Citas observadas desde el ViewModel (MVVM)
    val citas by viewModel.citas.collectAsState()

    HomeScreenContent(
        citas = citas,
        onNavigateToNewCita = onNavigateToNewCita,
        onNavigateToDetail = onNavigateToDetail,
    )
}

@Composable
fun HomeScreenContent(
    citas: List<Cita>,
    onNavigateToNewCita: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    Scaffold(
        containerColor = White,
        floatingActionButton = {
            // Criterio 4: Ícono flotante de color rosado #F05861 ubicado en la parte inferior derecha
            FloatingActionButton(
                onClick = onNavigateToNewCita,
                containerColor = AccentPink,
                contentColor = White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva Cita",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            // Criterio 2: Header de fondo gris oscuro con la parte inferior izquierda curvada
            HomeHeader()

            // Criterio 3 & 6: Lista de citas obtenida de Room
            if (citas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay citas programadas actualmente.",
                        fontFamily = FredokaFontFamily,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 94.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(items = citas, key = { it.id }) { cita ->
                        // Criterio 3 & 5: Ítem de cita con elevación, bordes redondeados y clic a detalle
                        CitaListItem(
                            cita = cita,
                            onClick = { onNavigateToDetail(cita.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Header según Criterio 2:
 * Header gris oscuro, borde inferior izquierdo curvado,
 * título "Administrador de Citas" blanco bold a la izquierda,
 * a la derecha imagen del perro con "DogApp" debajo.
 */
@Composable
fun HomeHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = HeaderCurvedShape(cornerRadius = 55.dp),
        color = DarkGrayHeader,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            // Izquierda: Título "Administrador de Citas" de color blanco bold, centrado verticalmente
            Text(
                text = "Administrador de Citas",
                fontFamily = FredokaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                color = White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.52f)
            )

            // Derecha: Imagen del perro descrita en el login, con su respectivo título "DogApp" ubicado debajo
            Column(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.dog_brand),
                    contentDescription = "Logo Perro DogApp",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(84.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "DogApp",
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = White
                )
            }
        }
    }
}

/**
 * Criterio 3: Cada ítem de la lista:
 * - Fondo blanco con bordes redondeados y elevación
 * - Izquierda: imagen circular de la mascota
 * - Centro: nombre de la mascota y descripción del síntoma
 * - Superior derecha: turno de atención (ID autogenerado de Room)
 */
@Composable
fun CitaListItem(
    cita: Cita,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Izquierda: imagen circular de la mascota
            Image(
                painter = painterResource(citaAvatar(cita)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6))
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Centro: nombre de la mascota y descripción del síntoma
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cita.nombreMascota,
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cita.sintoma,
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "#${cita.id}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = AccentPink,
                modifier = Modifier.align(Alignment.Top)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DogAppTheme {
        HomeScreenContent(
            citas = listOf(
                Cita(
                    id = 1,
                    nombreMascota = "Zeus",
                    sintoma = "Tiene vómito y no quiere comer desde la mañana",
                    avatarRes = R.drawable.pet1
                ),
                Cita(
                    id = 2,
                    nombreMascota = "Kira",
                    sintoma = "Revisión general y aplicación de vacunas anuales",
                    avatarRes = R.drawable.pet2
                )
            ),
            onNavigateToNewCita = {},
            onNavigateToDetail = {}
        )
    }
}
