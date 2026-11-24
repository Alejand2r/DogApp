package com.example.dogapp.ui.fragments

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dogapp.DogApplication
import com.example.dogapp.ui.theme.DarkGrayBackground
import com.example.dogapp.ui.theme.DogAppTheme

/** Argumento (Bundle) con el id de la cita para Detalle y Editar. Debe coincidir con nav_graph.xml. */
const val ARG_CITA_ID = "citaId"

/** Factory de ViewModels (MVVM) creada en DogApplication. */
fun Fragment.appViewModelFactory(): ViewModelProvider.Factory =
    (requireActivity().application as DogApplication).viewModelFactory

/** Devuelve la vista del Fragment: un ComposeView con el tema de la app que dibuja la pantalla. */
fun Fragment.composeView(content: @Composable () -> Unit): ComposeView =
    ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            DogAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = DarkGrayBackground) {
                    content()
                }
            }
        }
    }
