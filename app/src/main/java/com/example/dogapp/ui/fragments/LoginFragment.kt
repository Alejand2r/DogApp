package com.example.dogapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dogapp.R
import com.example.dogapp.ui.screens.LoginScreen

/** HU 1.0: Ventana Login. Si la huella es correcta navega a HU 2.0 (Home). */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeView {
        LoginScreen(
            onLoginSuccess = { findNavController().navigate(R.id.action_login_to_home) }
        )
    }
}
