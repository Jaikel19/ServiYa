package com.example.shared.di.modules

import com.example.shared.data.local.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    // Dependencias específicas de iOS
    single { DriverFactory() }
}