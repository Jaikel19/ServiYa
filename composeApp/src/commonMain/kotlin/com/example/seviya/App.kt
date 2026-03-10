package com.example.seviya

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seviya.UI.LandingScreen
import com.example.seviya.UI.MonthlyCalendarScreen
import com.example.seviya.UI.ProfessionalProfileRoute
import com.example.seviya.UI.ProfessionalProfileScreen
import com.example.seviya.UI.RoleAdmissionCatalogScreen
import com.example.seviya.UI.RoleCatalogScreen
import com.example.seviya.UI.TravelTimeConfigScreen
import com.example.shared.presentation.services.ServicesViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.example.seviya.ui.ServicesScreen
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.shared.presentation.professionalProfile.ProfessionalProfileViewModel

@Composable
fun App() {

    var currentScreen by remember { mutableStateOf("landing") }

    when (currentScreen) {
        "landing" -> LandingScreen(
            onGoToServices = { currentScreen = "services" },
            onLogin = { currentScreen = "roleAdmissionCatalog" },     // según tu flujo actual
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

        "professionalProfile" -> {
            val viewModel: ProfessionalProfileViewModel = koinViewModel()

            ProfessionalProfileRoute(
                workerId = "worker_demo_001",
                viewModel = viewModel,
                onBack = { currentScreen = "landing" },
                onOpenChat = { },
                onBottomServices = { },
                onBottomMap = { },
                onBottomSearch = { },
                onBottomNotifications = { },
                onBottomMenu = { }
            )
        }

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

        "roleAdmissionCatalog" -> RoleAdmissionCatalogScreen(
            onGoHome = { currentScreen = "landing" },
            onGoLogin = { /* ya estás aquí */ },
            onGoRegister = { currentScreen = "roleCatalog" },
            onPickClient = {
                currentScreen = "clientDashboard"
            },
            onPickWorker = {
                currentScreen = "workerDashboard"
            }
        )

        "clientDashboard" -> ClientDashboardPlaceholder(
            onGoToProfessionalProfile = { currentScreen = "professionalProfile" }
        )

        "workerDashboard" -> WorkerDashboardPlaceholder(
            onBackToLanding = { currentScreen = "landing" },
            onGoToMonthlyCalendar = { currentScreen = "monthlyCalendar" }
        )

        "monthlyCalendar" -> {

            val viewModel: MonthlyCalendarViewModel = koinViewModel()

            MonthlyCalendarScreen(
                viewModel = viewModel,
                onBack = { currentScreen = "landing" }
            )
        }
    }
}

@Composable
private fun WorkerDashboardPlaceholder(
    onBackToLanding: () -> Unit,
    onGoToMonthlyCalendar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dashboard Trabajador",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Pantalla temporal para pruebas del flujo de ingreso.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
            )

            Button(onClick = onBackToLanding) {
                Text("Volver al landing")
            }

            Button(onClick = onGoToMonthlyCalendar) {
                Text("ir a la agenda")
            }
        }
    }
}

@Composable
private fun ClientDashboardPlaceholder(
    onGoToProfessionalProfile: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dashboard Cliente",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Pantalla temporal para pruebas del flujo de ingreso.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
            )

            Button(onClick = onGoToProfessionalProfile) {
                Text("Ir al perfil profesional")
            }
        }
    }
}