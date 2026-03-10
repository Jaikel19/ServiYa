package com.example.shared.di.modules

import com.example.shared.data.local.AppDatabase
import com.example.shared.data.local.DriverFactory
import com.example.shared.data.local.ILocalServicesDataSource
import com.example.shared.data.local.LocalServicesDataSource
import com.example.shared.data.remote.IRemoteServicesDataSource
import com.example.shared.data.remote.RemoteServicesDataSource
import com.example.shared.data.remote.professionalProfile.IRemoteProfessionalProfileDataSource
import com.example.shared.data.remote.professionalProfile.RemoteProfessionalProfileDataSource
import com.example.shared.data.repository.IServiceRepository
import com.example.shared.data.repository.ServiceRepository
import com.example.shared.data.repository.professionalProfile.IProfessionalProfileRepository
import com.example.shared.data.repository.professionalProfile.ProfessionalProfileRepository
import org.koin.dsl.module

val dataModule = module {

    // Remote
    single<IRemoteServicesDataSource> { RemoteServicesDataSource() }

    // Local (SQLDelight)
    single { get<DriverFactory>().createDriver() }
    single { AppDatabase(get()) }
    single { get<AppDatabase>().appDatabaseQueries }
    single<ILocalServicesDataSource> { LocalServicesDataSource(get()) }

    // Repository
    single<IServiceRepository> { ServiceRepository(get()) }

    single<IRemoteProfessionalProfileDataSource> { RemoteProfessionalProfileDataSource() }
    single<IProfessionalProfileRepository> { ProfessionalProfileRepository(get(), get()) }
}