package com.example.shared.di.modules

import com.example.shared.data.local.AppDatabase
import com.example.shared.data.local.DriverFactory
import com.example.shared.data.local.ILocalServicesDataSource
import com.example.shared.data.local.LocalServicesDataSource
import com.example.shared.data.remote.IRemoteBookingDataSource
import com.example.shared.data.remote.RemoteBookingDataSource
import com.example.shared.data.remote.Service.IRemoteServicesDataSource
import com.example.shared.data.remote.Service.RemoteServicesDataSource
import com.example.shared.data.remote.Address.IRemoteAddressDataSource
import com.example.shared.data.remote.Address.RemoteAddressDataSource
import com.example.shared.data.remote.appointment.IRemoteAppointmentDataSource
import com.example.shared.data.remote.appointment.RemoteAppointmentDataSource
import com.example.shared.data.remote.cancellationPolicy.IRemoteCancellationPolicyDataSource
import com.example.shared.data.remote.cancellationPolicy.RemoteCancellationPolicyDataSource
import com.example.shared.data.remote.categories.IRemoteCategoriesDataSource
import com.example.shared.data.remote.categories.RemoteCategoriesDataSource
import com.example.shared.data.remote.professionalProfile.IRemoteProfessionalProfileDataSource
import com.example.shared.data.remote.professionalProfile.RemoteProfessionalProfileDataSource
import com.example.shared.data.remote.workersList.IRemoteWorkersListDataSource
import com.example.shared.data.remote.workersList.RemoteWorkersListDataSource
import com.example.shared.data.repository.BookingRepository
import com.example.shared.data.repository.IBookingRepository
import com.example.shared.data.repository.Service.IServiceRepository
import com.example.shared.data.repository.Service.ServiceRepository
import com.example.shared.data.repository.Address.AddressRepository
import com.example.shared.data.repository.Address.IAddressRepository
import com.example.shared.data.repository.Appointment.AppointmentRepository
import com.example.shared.data.repository.Appointment.IAppointmentRepository
import com.example.shared.data.repository.categories.CategoryRepository
import com.example.shared.data.repository.categories.ICategoryRepository
import com.example.shared.data.repository.professionalProfile.IProfessionalProfileRepository
import com.example.shared.data.repository.professionalProfile.ProfessionalProfileRepository
import com.example.shared.data.repository.workersList.IWorkersListRepository
import com.example.shared.data.repository.workersList.WorkersListRepository
import org.koin.dsl.module

val dataModule = module {

    // Remote Services
    single<IRemoteServicesDataSource> { RemoteServicesDataSource() }

    // Local
    single { get<DriverFactory>().createDriver() }
    single { AppDatabase(get()) }
    single { get<AppDatabase>().appDatabaseQueries }
    single<ILocalServicesDataSource> { LocalServicesDataSource(get()) }

    // Services Repository
    single<IServiceRepository> { ServiceRepository(get()) }

    // Booking
    single<IRemoteBookingDataSource> { RemoteBookingDataSource() }
    single<IBookingRepository> { BookingRepository(get()) }

    // Professional Profile
    single<IRemoteProfessionalProfileDataSource> { RemoteProfessionalProfileDataSource() }
    single<IProfessionalProfileRepository> { ProfessionalProfileRepository(get(), get()) }

    // Categories
    single<IRemoteCategoriesDataSource> { RemoteCategoriesDataSource() }
    single<ICategoryRepository> { CategoryRepository(get()) }

    // Workers List
    single<IRemoteWorkersListDataSource> { RemoteWorkersListDataSource() }
    single<IWorkersListRepository> { WorkersListRepository(get(), get()) }

    // Appointments
    single<IRemoteAppointmentDataSource> { RemoteAppointmentDataSource() }
    single<IAppointmentRepository> { AppointmentRepository(get()) }

    // Addresses
    single<IRemoteAddressDataSource> { RemoteAddressDataSource() }
    single<IAddressRepository> { AddressRepository(get()) }

    // Cancellation policy
    single<IRemoteCancellationPolicyDataSource> { RemoteCancellationPolicyDataSource() }
}