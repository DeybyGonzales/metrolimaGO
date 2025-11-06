package com.gonzales.metrolimago.home.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gonzales.metrolimago.data.local.entities.Estacion
import com.gonzales.metrolimago.data.local.entities.Paradero
import com.gonzales.metrolimago.estaciones.EstacionesViewModel
import com.gonzales.metrolimago.estaciones.ListaEstacionesScreen
import com.gonzales.metrolimago.estaciones.RutaResult
import com.gonzales.metrolimago.ui.screens.components.home.HomeScreen
import com.gonzales.metrolimago.estaciones.DetalleParaderoScreen
import com.gonzales.metrolimago.ui.screens.mapa.MapaEstacionScreen
import com.gonzales.metrolimago.ui.screens.mapa.MapaParaderoScreen
import com.gonzales.metrolimago.ui.screens.mapa.MapaRutaScreen
import com.gonzales.metrolimago.ui.screens.planificador.PlanificadorScreen
import com.gonzales.metrolimago.ui.screens.estaciones.DetalleEstacionScreen
import com.gonzales.metrolimago.ui.screens.components.ConfiguracionScreen


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ListaEstaciones : Screen("estaciones")
    object DetalleEstacion : Screen("estacion/{estacionId}") {
        fun createRoute(estacionId: String) = "estacion/$estacionId"
    }
    object DetalleParadero : Screen("paradero/{paraderoId}") {
        fun createRoute(paraderoId: String) = "paradero/$paraderoId"
    }
    object Planificador : Screen("planificador")
    object Configuracion : Screen("configuracion")

    object MapaEstacion : Screen("mapa/{estacionId}") {
        fun createRoute(estacionId: String) = "mapa/$estacionId"
    }

    object MapaParadero : Screen("mapa-paradero/{paraderoId}") {
        fun createRoute(paraderoId: String) = "mapa-paradero/$paraderoId"
    }

    object MapaRuta : Screen("mapa_ruta")
}

@Composable
fun MetroNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToEstaciones = {
                    navController.navigate(Screen.ListaEstaciones.route)
                },
                onNavigateToPlanificador = {
                    navController.navigate(Screen.Planificador.route)
                },
                navToConfiguracion = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.ListaEstaciones.route) {
            ListaEstacionesScreen(
                onEstacionClick = { estacionId ->
                    navController.navigate(Screen.DetalleEstacion.createRoute(estacionId))
                },
                onParaderoClick = { paraderoId ->
                    navController.navigate(Screen.DetalleParadero.createRoute(paraderoId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.DetalleEstacion.route,
            arguments = listOf(
                navArgument("estacionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val estacionId = backStackEntry.arguments?.getString("estacionId") ?: ""
            DetalleEstacionScreen(
                estacionId = estacionId,
                onBackClick = {
                    navController.popBackStack()
                },
                onMapClick = {
                    navController.navigate(Screen.MapaEstacion.createRoute(estacionId))
                }
            )
        }

        composable(
            route = Screen.DetalleParadero.route,
            arguments = listOf(
                navArgument("paraderoId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val paraderoId = backStackEntry.arguments?.getString("paraderoId") ?: ""
            DetalleParaderoScreen(
                paraderoId = paraderoId,
                onBackClick = {
                    navController.popBackStack()
                },
                onMapClick = {
                    navController.navigate(Screen.MapaParadero.createRoute(paraderoId))
                }
            )
        }

        composable(Screen.Planificador.route) {
            PlanificadorScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onMapaRutaClick = { rutaResult ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("ruta_result", rutaResult)
                    navController.navigate(Screen.MapaRuta.route)
                }
            )
        }

        // ✅ NUEVA RUTA - Configuración
        composable(Screen.Configuracion.route) {
            ConfiguracionScreen(navController = navController)
        }

        composable(
            route = Screen.MapaEstacion.route,
            arguments = listOf(
                navArgument("estacionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val estacionId = backStackEntry.arguments?.getString("estacionId") ?: ""
            val viewModel: EstacionesViewModel = viewModel()

            var estacion by remember { mutableStateOf<Estacion?>(null) }

            LaunchedEffect(estacionId) {
                estacion = viewModel.getEstacionById(estacionId)
            }

            estacion?.let {
                MapaEstacionScreen(
                    estacion = it,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(
            route = Screen.MapaParadero.route,
            arguments = listOf(
                navArgument("paraderoId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val paraderoId = backStackEntry.arguments?.getString("paraderoId") ?: ""
            val viewModel: EstacionesViewModel = viewModel()

            var paradero by remember { mutableStateOf<Paradero?>(null) }

            LaunchedEffect(paraderoId) {
                paradero = viewModel.getParaderoById(paraderoId)
            }

            paradero?.let {
                MapaParaderoScreen(
                    paradero = it,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(Screen.MapaRuta.route) {
            val rutaResult = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<RutaResult>("ruta_result")

            rutaResult?.let {
                MapaRutaScreen(
                    ruta = it,
                    onBack = { navController.popBackStack() }
                )
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}