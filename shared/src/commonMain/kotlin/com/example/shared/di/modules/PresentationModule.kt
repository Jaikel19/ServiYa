package com.example.shared.di.modules

import com.example.shared.presentation.categories.CategoriesViewModel
import com.example.shared.presentation.professionalProfile.ProfessionalProfileViewModel
import com.example.shared.presentation.services.ServicesViewModel
import org.koin.dsl.module
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import com.example.shared.presentation.workersList.WorkersListViewModel
import com.example.shared.presentation.clientAppointmentDetail.ClientAppointmentDetailViewModel
import com.example.shared.presentation.clientDashboard.ClientDashboardViewModel
import com.example.shared.presentation.workerDashboard.WorkerDashboardViewModel

val presentationModule = module {
    // Define aquí las dependencias de tu capa de presentación
    // ViewModels: LoginViewModel, RegisterViewModel, etc...

    factory { ServicesViewModel(get()) }
    viewModel { MonthlyCalendarViewModel(get()) }
    factory { ProfessionalProfileViewModel(get()) }
    factory { CategoriesViewModel(get()) }
    factory { WorkersListViewModel(get()) }
    viewModel { ClientAppointmentDetailViewModel(get()) }
    factory { WorkerDashboardViewModel(get(), get()) }
    factory { ClientDashboardViewModel(get()) }
}