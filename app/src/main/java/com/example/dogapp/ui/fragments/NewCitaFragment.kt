package com.example.dogapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dogapp.ui.screens.NewCitaScreen
import com.example.dogapp.ui.viewmodels.NewCitaViewModel

/** HU 3.0: Ventana Nueva Cita. Flecha atrás y guardado exitoso regresan al Home. */
class NewCitaFragment : Fragment() {

    private val viewModel: NewCitaViewModel by viewModels { appViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeView {
        NewCitaScreen(
            viewModel = viewModel,
            onBack = { findNavController().popBackStack() }
        )
    }
}
