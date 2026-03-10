package com.example.shared.di.modules

import com.example.shared.presentation.professionalProfile.ProfessionalProfileViewModel
import com.example.shared.presentation.services.ServicesViewModel
import org.koin.dsl.module

val presentationModule = module {
    // Define aquí las dependencias de tu capa de presentación
    // ViewModels: LoginViewModel, RegisterViewModel, etc...

    factory { ServicesViewModel(get()) }
    factory { ProfessionalProfileViewModel(get()) }
}