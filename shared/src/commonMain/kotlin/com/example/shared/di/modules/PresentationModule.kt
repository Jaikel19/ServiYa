package com.example.shared.di.modules

import com.example.shared.presentation.services.ServicesViewModel
import org.koin.dsl.module
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import org.koin.compose.viewmodel.dsl.viewModel

val presentationModule = module {
    // Define aquí las dependencias de tu capa de presentación
    // ViewModels: LoginViewModel, RegisterViewModel, etc...

    factory { ServicesViewModel(get()) }
    viewModel { MonthlyCalendarViewModel() }
}