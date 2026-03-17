package com.example.shared.di.modules

import com.example.shared.presentation.WorkerPaymentDetail.WorkerPaymentDetailViewModel
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
import com.example.shared.presentation.WorkerRequest.WorkerRequestsViewModel
import com.example.shared.presentation.WorkerRequestDetailViewModel.WorkerRequestDetailViewModel

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
    viewModel { WorkerRequestsViewModel(get()) }
    viewModel { WorkerRequestDetailViewModel(get()) }
    viewModel { WorkerPaymentDetailViewModel(get()) }
}