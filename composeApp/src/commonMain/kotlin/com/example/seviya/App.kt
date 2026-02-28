package com.example.seviya

import androidx.compose.runtime.*
import com.example.seviya.UI.LandingScreen
import com.example.shared.presentation.services.ServicesViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.example.seviya.ui.ServicesScreen

@Composable
fun App() {

    var currentScreen by remember { mutableStateOf("landing") }

    when (currentScreen) {
        "landing" -> LandingScreen(
            onGoToServices = {
                currentScreen = "services"
            }
        )

        "services" -> {
            val viewModel: ServicesViewModel = koinViewModel()
            ServicesScreen(viewModel)
        }
    }
}