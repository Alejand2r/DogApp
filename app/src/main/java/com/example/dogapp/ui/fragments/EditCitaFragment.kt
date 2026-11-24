package com.example.dogapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dogapp.R
import com.example.dogapp.ui.screens.EditCitaScreen
import com.example.dogapp.ui.viewmodels.EditCitaViewModel

/** HU 5.0: Ventana Editar Cita. Recibe el id de la cita como argumento. */
class EditCitaFragment : Fragment() {

    private val viewModel: EditCitaViewModel by viewModels { appViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val citaId = requireArguments().getLong(ARG_CITA_ID)
        return composeView {
            EditCitaScreen(
                citaId = citaId,
                viewModel = viewModel,
                // Criterio 1: flecha atrás -> Detalle de la misma cita
                onBack = { findNavController().popBackStack() },
                // Criterio 8: tras guardar -> Home con la cita modificada
                onSaved = { findNavController().popBackStack(R.id.homeFragment, false) }
            )
        }
    }
}
