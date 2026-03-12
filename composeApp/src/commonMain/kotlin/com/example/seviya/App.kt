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
import com.example.seviya.UI.CategoriesCatalogRoute
import com.example.seviya.UI.LandingScreen
import com.example.seviya.UI.MonthlyCalendarScreen
import com.example.seviya.UI.ProfessionalProfileRoute
import com.example.seviya.UI.RoleAdmissionCatalogScreen
import com.example.seviya.UI.RoleCatalogScreen
import com.example.seviya.UI.TravelTimeConfigScreen
import com.example.seviya.UI.WorkersListRoute
import com.example.seviya.ui.ServicesScreen
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.shared.presentation.categories.CategoriesViewModel
import com.example.shared.presentation.professionalProfile.ProfessionalProfileViewModel
import com.example.shared.presentation.services.ServicesViewModel
import com.example.shared.presentation.workersList.WorkersListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf("landing") }

    // categoría seleccionada
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }

    // worker seleccionado
    var selectedWorkerId by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        "landing" -> LandingScreen(
            onGoToServices = { currentScreen = "services" },
            onLogin = { currentScreen = "roleAdmissionCatalog" },
            onRegister = { currentScreen = "roleCatalog" }
        )

        "services" -> {
            val viewModel: ServicesViewModel = koinViewModel()
            ServicesScreen(viewModel)
        }

        "travelTimeConfig" -> TravelTimeConfigScreen(
            initialMinutes = 30,
            onBack = { currentScreen = "landing" },
            onSave = { _ ->
                currentScreen = "landing"
            },
            onGoHome = { currentScreen = "landing" },
            onGoServices = { currentScreen = "services" },
            onGoRegister = { currentScreen = "roleCatalog" }
        )

        "professionalProfile" -> {
            val viewModel: ProfessionalProfileViewModel = koinViewModel()

            ProfessionalProfileRoute(
                workerId = selectedWorkerId ?: "worker_demo_001",
                viewModel = viewModel,
                onBack = { currentScreen = "workersList" },
                onBottomServices = { currentScreen = "categoriesCatalog" },
                onBottomMap = { currentScreen = "map" },
                onBottomSearch = { currentScreen = "search" },
                onBottomNotifications = { currentScreen = "alerts" },
                onBottomMenu = { currentScreen = "clientDashboard" }
            )
        }

        "roleCatalog" -> RoleCatalogScreen(
            onGoHome = { currentScreen = "landing" },
            onGoLogin = { currentScreen = "travelTimeConfig" },
            onGoRegister = { },
            onPickClient = {
                // TODO
            },
            onPickWorker = {
                // TODO
            }
        )

        "roleAdmissionCatalog" -> RoleAdmissionCatalogScreen(
            onGoHome = { currentScreen = "landing" },
            onGoLogin = { },
            onGoRegister = { currentScreen = "roleCatalog" },
            onPickClient = {
                currentScreen = "clientDashboard"
            },
            onPickWorker = {
                currentScreen = "workerDashboard"
            }
        )

        "categoriesCatalog" -> {
            val viewModel: CategoriesViewModel = koinViewModel()

            CategoriesCatalogRoute(
                viewModel = viewModel,
                selectedCategoryId = selectedCategoryId,
                onGoServices = { currentScreen = "categoriesCatalog" },
                onGoMap = { currentScreen = "map" },
                onGoSearch = { currentScreen = "search" },
                onGoAlerts = { currentScreen = "alerts" },
                onGoAgenda = { currentScreen = "monthlyCalendar" },
                onGoProfile = { currentScreen = "profile" },
                onGoConfiguration = { currentScreen = "configuration" },
                onGoMessages = { currentScreen = "messages" },
                onGoDashboard = { currentScreen = "dashboard" },
                onGoSettings = { currentScreen = "settings" },

                onCategoryClick = { category ->
                    selectedCategoryId = category.id
                    selectedCategoryName = category.name
                },

                onContinueWithSelectedCategory = {
                    if (!selectedCategoryId.isNullOrBlank()) {
                        currentScreen = "workersList"
                    }
                }
            )
        }

        "workersList" -> {
            val viewModel: WorkersListViewModel = koinViewModel()

            WorkersListRoute(
                viewModel = viewModel,
                selectedCategoryId = selectedCategoryId,
                selectedCategoryName = selectedCategoryName,
                onWorkerClick = { workerId ->
                    selectedWorkerId = workerId
                    currentScreen = "professionalProfile"
                },
                onThemeClick = { },
                onBottomServices = { currentScreen = "categoriesCatalog" },
                onBottomMap = { currentScreen = "map" },
                onBottomSearch = { currentScreen = "search" },
                onBottomNotifications = { currentScreen = "alerts" },
                onBottomMenu = { currentScreen = "clientDashboard" }
            )
        }

        "clientDashboard" -> ClientDashboardPlaceholder(
            onBackToLanding = { currentScreen = "landing" },
            onGoToProfessionalProfile = {
                selectedWorkerId = "worker_demo_001"
                currentScreen = "professionalProfile"
            },
            onGoToCategories = { currentScreen = "categoriesCatalog" },
            onGoToServices = { currentScreen = "services" }
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
                Text("Ir a la agenda")
            }
        }
    }
}

@Composable
private fun ClientDashboardPlaceholder(
    onGoToProfessionalProfile: () -> Unit,
    onBackToLanding: () -> Unit,
    onGoToCategories: () -> Unit,
    onGoToServices: () -> Unit
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

            Button(onClick = onBackToLanding) {
                Text("Volver al landing")
            }

            Button(onClick = onGoToProfessionalProfile) {
                Text("Ir al perfil profesional")
            }

            Button(
                onClick = onGoToCategories,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text("Ir a categorías")
            }

            Button(
                onClick = onGoToServices,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text("Ir a servicios")
            }
        }
    }
}