package com.example.dogapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dogapp.R
import com.example.dogapp.ui.screens.DetailCitaScreen
import com.example.dogapp.ui.viewmodels.DetailCitaViewModel

/** HU 4.0: Ventana Detalle de la Cita. Recibe el id de la cita como argumento. */
class DetailCitaFragment : Fragment() {

    private val viewModel: DetailCitaViewModel by viewModels { appViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val citaId = requireArguments().getLong(ARG_CITA_ID)
        return composeView {
            DetailCitaScreen(
                citaId = citaId,
                viewModel = viewModel,
                // Criterio 1: flecha atrás -> Home
                onBack = { findNavController().popBackStack() },
                // Criterio 5: botón editar -> HU 5.0
                onEdit = {
                    findNavController().navigate(
                        R.id.action_detail_to_edit,
                        bundleOf(ARG_CITA_ID to citaId)
                    )
                },
                // Criterio 4: tras eliminar -> Home (la cita ya no aparece)
                onDeleted = { findNavController().popBackStack(R.id.homeFragment, false) }
            )
        }
    }
}
