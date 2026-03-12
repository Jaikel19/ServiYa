package com.example.shared.di.modules

import com.example.shared.data.local.AppDatabase
import com.example.shared.data.local.DriverFactory
import com.example.shared.data.local.ILocalServicesDataSource
import com.example.shared.data.local.LocalServicesDataSource
import com.example.shared.data.remote.IRemoteBookingDataSource
import com.example.shared.data.remote.Service.IRemoteServicesDataSource
import com.example.shared.data.remote.RemoteBookingDataSource
import com.example.shared.data.remote.Service.RemoteServicesDataSource
import com.example.shared.data.remote.cancellationPolicy.IRemoteCancellationPolicyDataSource
import com.example.shared.data.remote.cancellationPolicy.RemoteCancellationPolicyDataSource
import com.example.shared.data.remote.categories.IRemoteCategoriesDataSource
import com.example.shared.data.remote.categories.RemoteCategoriesDataSource
import com.example.shared.data.repository.BookingRepository
import com.example.shared.data.repository.IBookingRepository
import com.example.shared.data.remote.professionalProfile.IRemoteProfessionalProfileDataSource
import com.example.shared.data.remote.professionalProfile.RemoteProfessionalProfileDataSource

import com.example.shared.data.repository.Service.IServiceRepository
import com.example.shared.data.repository.Service.ServiceRepository


import com.example.shared.data.repository.categories.CategoryRepository
import com.example.shared.data.repository.categories.ICategoryRepository

import com.example.shared.data.repository.professionalProfile.IProfessionalProfileRepository
import com.example.shared.data.repository.professionalProfile.ProfessionalProfileRepository
import org.koin.dsl.module

import com.example.shared.data.remote.workersList.IRemoteWorkersListDataSource
import com.example.shared.data.remote.workersList.RemoteWorkersListDataSource
import com.example.shared.data.repository.workersList.IWorkersListRepository
import com.example.shared.data.repository.workersList.WorkersListRepository

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

    single<IRemoteCategoriesDataSource> { RemoteCategoriesDataSource() }
    single<ICategoryRepository> { CategoryRepository(get()) }

    single<IRemoteWorkersListDataSource> { RemoteWorkersListDataSource() }
    single<IWorkersListRepository> { WorkersListRepository(get(), get()) }

}