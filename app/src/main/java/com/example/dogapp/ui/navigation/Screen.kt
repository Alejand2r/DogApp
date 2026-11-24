package com.example.dogapp.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object NewCita : Screen("new_cita") // HU 3.0
    object DetailCita : Screen("detail_cita/{citaId}") { // HU 4.0
        fun createRoute(citaId: Long) = "detail_cita/$citaId"
    }
    object EditCita : Screen("edit_cita/{citaId}") { // HU 5.0
        fun createRoute(citaId: Long) = "edit_cita/$citaId"
    }
}
