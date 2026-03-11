package com.example.seviya

import androidx.compose.runtime.*
import com.example.seviya.UI.LandingScreen
import com.example.seviya.UI.RoleCatalogScreen
import com.example.seviya.UI.TravelTimeConfigScreen
import com.example.shared.presentation.services.ServicesViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.example.seviya.ui.ServicesScreen

@Composable
fun App() {

    var currentScreen by remember { mutableStateOf("landing") }

    when (currentScreen) {
        "landing" -> LandingScreen(
            onGoToServices = { currentScreen = "services" },
            onLogin = { currentScreen = "travelTimeConfig" },     // según tu flujo actual
            onRegister = { currentScreen = "roleCatalog" }        // ✅ ahora abre catálogo de roles
        )

        "services" -> {
            val viewModel: ServicesViewModel = koinViewModel()
            ServicesScreen(viewModel)
        }

        "travelTimeConfig" -> TravelTimeConfigScreen(
            initialMinutes = 30,
            onBack = { currentScreen = "landing" },
            onSave = { minutes ->
                // TODO guardar minutes
                currentScreen = "landing"
            },
            onGoHome = { currentScreen = "landing" },
            onGoServices = { currentScreen = "services" },
            onGoRegister = { currentScreen = "roleCatalog" }
        )

        "roleCatalog" -> RoleCatalogScreen(
            onGoHome = { currentScreen = "landing" },
            onGoLogin = { currentScreen = "travelTimeConfig" },
            onGoRegister = { /* ya estás aquí */ },
            onPickClient = {
                // TODO: ir a registro de cliente
                // currentScreen = "registerClient"
            },
            onPickWorker = {
                // TODO: ir a registro de trabajador
                // currentScreen = "registerWorker"
            }
        )
    }
}