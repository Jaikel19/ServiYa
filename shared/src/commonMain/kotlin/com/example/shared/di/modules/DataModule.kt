package com.example.shared.di.modules

import com.example.shared.config.AppConfig
import com.example.shared.data.local.AppDatabase
import com.example.shared.data.local.DriverFactory
import com.example.shared.data.local.ILocalServicesDataSource
import com.example.shared.data.local.LocalServicesDataSource
import com.example.shared.data.remote.IRemoteServicesDataSource
import com.example.shared.data.remote.RemoteServicesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import com.example.shared.data.repository.IServiceRepository
import com.example.shared.data.repository.ServiceRepository

val dataModule = module {
    // Define aqui las dependencias de tu capa de datos
    // repositories, local/remote datasources, SQLDelight, etc...

    //el get() le pasa el HttpClient que ya registraste en NetworkModule

    // Remote
    //single<IRemoteServicesDataSource> { RemoteServicesDataSource(get(), Dispatchers.IO) }
    //single<IRemoteServicesDataSource> { RemoteServicesDataSource(ioDispatcher = Dispatchers.IO) }
    single<IRemoteServicesDataSource> {
        RemoteServicesDataSource(
            httpClient = get(),
            ioDispatcher = Dispatchers.IO,
            useFake = AppConfig.USE_FAKE_REMOTE
        )
    }

    // Local (SQLDelight)
    single { get<DriverFactory>().createDriver() }
    single { AppDatabase(get()) }
    single { get<AppDatabase>().appDatabaseQueries }
    single<ILocalServicesDataSource> { LocalServicesDataSource(get()) }

    single<IServiceRepository> { ServiceRepository(get(), get(), Dispatchers.Default) }

}