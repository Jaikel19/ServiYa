package com.example.shared.di.modules

import com.example.shared.presentation.WorkerPaymentDetail.WorkerPaymentDetailViewModel
import com.example.shared.presentation.WorkerRequest.WorkerRequestsViewModel
import com.example.shared.presentation.WorkerRequestDetailViewModel.WorkerRequestDetailViewModel
import com.example.shared.presentation.calendar.MonthlyCalendarViewModel
import com.example.shared.presentation.categories.CategoriesViewModel
import com.example.shared.presentation.clientAppointmentDetail.ClientAppointmentDetailViewModel
import com.example.shared.presentation.clientDashboard.ClientDashboardViewModel
import com.example.shared.presentation.professionalProfile.ProfessionalProfileViewModel
import com.example.shared.presentation.requestAppointment.RequestAppointmentViewModel
import com.example.shared.presentation.services.ServicesViewModel
import com.example.shared.presentation.workerAppointmentDetail.WorkerAppointmentDetailViewModel
import com.example.shared.presentation.workerDashboard.WorkerDashboardViewModel
import com.example.shared.presentation.workersList.WorkersListViewModel
import org.koin.dsl.module

val presentationModule = module {
    // Define aquí las dependencias de tu capa de presentación
    // ViewModels: LoginViewModel, RegisterViewModel, etc...

    factory { ServicesViewModel(get()) }
    factory { MonthlyCalendarViewModel(get()) }
    factory { ProfessionalProfileViewModel(get()) }
    factory { CategoriesViewModel(get()) }
    factory { WorkersListViewModel(get()) }
    factory { ClientAppointmentDetailViewModel(get()) }
    factory { WorkerDashboardViewModel(get(), get()) }
    factory { ClientDashboardViewModel(get()) }
    factory { RequestAppointmentViewModel(get(), get()) }
    factory { WorkerRequestsViewModel(get(), get()) }
    factory { WorkerRequestDetailViewModel(get()) }
    factory { WorkerPaymentDetailViewModel(get()) }
    factory { WorkerAppointmentDetailViewModel(get()) }
}