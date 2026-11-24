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
import com.example.dogapp.ui.screens.HomeScreen
import com.example.dogapp.ui.viewmodels.HomeViewModel

/** HU 2.0: Home Administrador de Citas. */
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels { appViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeView {
        HomeScreen(
            viewModel = viewModel,
            // Criterio 4: botón flotante -> HU 3.0 Nueva Cita
            onNavigateToNewCita = { findNavController().navigate(R.id.action_home_to_newCita) },
            // Criterio 5: clic en un ítem -> HU 4.0 Detalle
            onNavigateToDetail = { citaId ->
                findNavController().navigate(
                    R.id.action_home_to_detail,
                    bundleOf(ARG_CITA_ID to citaId)
                )
            }
        )
    }
}
