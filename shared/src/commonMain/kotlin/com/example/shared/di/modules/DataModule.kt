package com.example.shared.di.modules

import com.example.shared.data.local.AppDatabase
import com.example.shared.data.local.DriverFactory
import com.example.shared.data.local.ILocalServicesDataSource
import com.example.shared.data.local.LocalServicesDataSource

import com.example.shared.data.remote.IRemoteBookingDataSource     
import com.example.shared.data.remote.RemoteBookingDataSource
import com.example.shared.data.repository.BookingRepository
import com.example.shared.data.repository.IBookingRepository
import com.example.shared.data.remote.professionalProfile.IRemoteProfessionalProfileDataSource
import com.example.shared.data.remote.professionalProfile.RemoteProfessionalProfileDataSource

import com.example.shared.data.repository.professionalProfile.IProfessionalProfileRepository
import com.example.shared.data.repository.professionalProfile.ProfessionalProfileRepository

import com.example.shared.data.remote.Services.IRemoteServicesDataSource
import com.example.shared.data.remote.Services.RemoteServicesDataSource
import com.example.shared.data.repository.Services.IServiceRepository
import com.example.shared.data.repository.Services.ServiceRepository
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

    // Remote Bookings
    single<IRemoteBookingDataSource> { RemoteBookingDataSource() }
    // Repository Bookings
    single<IBookingRepository> { BookingRepository(get()) }
    single<IRemoteProfessionalProfileDataSource> { RemoteProfessionalProfileDataSource() }
    single<IProfessionalProfileRepository> { ProfessionalProfileRepository(get(), get()) }
}