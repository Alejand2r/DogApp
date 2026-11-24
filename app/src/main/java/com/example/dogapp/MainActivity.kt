package com.example.dogapp

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dogapp.data.local.AppDatabase
import com.example.dogapp.data.repository.CitaRepositoryImpl
import com.example.dogapp.ui.navigation.Screen
import com.example.dogapp.ui.screens.DetailCitaScreen
import com.example.dogapp.ui.screens.EditCitaScreen
import com.example.dogapp.ui.screens.HomeScreen
import com.example.dogapp.ui.screens.LoginScreen
import com.example.dogapp.ui.screens.NewCitaScreen
import com.example.dogapp.ui.theme.DarkGrayBackground
import com.example.dogapp.ui.theme.DogAppTheme
import com.example.dogapp.ui.viewmodels.AppViewModelFactory
import com.example.dogapp.ui.viewmodels.HomeViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        setContent {
            DogAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkGrayBackground
                ) {
                    DogAppNavigation()
                }
            }
        }
    }
}

@Composable
fun DogAppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // MVVM: Room Database -> Repository -> ViewModelFactory
    val database = AppDatabase.getDatabase(context)
    val repository = remember(database) { CitaRepositoryImpl(database.citaDao()) }
    val viewModelFactory = remember(repository) { AppViewModelFactory(repository) }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // HU 1.0
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // HU 2.0
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToNewCita = { navController.navigate(Screen.NewCita.route) },
                onNavigateToDetail = { citaId ->
                    navController.navigate(Screen.DetailCita.createRoute(citaId))
                }
            )
        }

        // HU 3.0
        composable(Screen.NewCita.route) {
            NewCitaScreen(
                viewModel = viewModel(factory = viewModelFactory),
                onBack = { navController.popBackStack() }
            )
        }

        // HU 4.0: Detalle de la Cita
        composable(
            route = Screen.DetailCita.route,
            arguments = listOf(navArgument("citaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val citaId = backStackEntry.arguments?.getLong("citaId") ?: 0L
            DetailCitaScreen(
                citaId = citaId,
                viewModel = viewModel(factory = viewModelFactory),
                // Criterio 1: flecha atrás -> Home
                onBack = { navController.popBackStack() },
                // Criterio 5: editar -> HU 5.0
                onEdit = { navController.navigate(Screen.EditCita.createRoute(citaId)) },
                // Criterio 4: tras eliminar -> Home
                onDeleted = { navController.popBackStack(Screen.Home.route, inclusive = false) }
            )
        }

        // HU 5.0: Editar Cita
        composable(
            route = Screen.EditCita.route,
            arguments = listOf(navArgument("citaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val citaId = backStackEntry.arguments?.getLong("citaId") ?: 0L
            EditCitaScreen(
                citaId = citaId,
                viewModel = viewModel(factory = viewModelFactory),
                // Criterio 1: flecha atrás -> Detalle de la misma cita
                onBack = { navController.popBackStack() },
                // Criterio 8: tras guardar -> Home
                onSaved = { navController.popBackStack(Screen.Home.route, inclusive = false) }
            )
        }
    }
}