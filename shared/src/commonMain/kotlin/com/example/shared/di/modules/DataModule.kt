package com.example.shared.di.modules

import com.example.shared.data.local.AppDatabase
import com.example.shared.data.local.DriverFactory
import com.example.shared.data.local.ILocalServicesDataSource
import com.example.shared.data.local.LocalServicesDataSource
import com.example.shared.data.remote.IRemoteServicesDataSource
import com.example.shared.data.remote.RemoteServicesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val dataModule = module {
    // Define aquí las dependencias de tu capa de datos
    // repositories, local/remote datasources, SQLDelight, etc...

    //el get() le pasa el HttpClient que ya registraste en NetworkModule
    // Remote
    single<IRemoteServicesDataSource> { RemoteServicesDataSource(get(), Dispatchers.IO) }

    // Local (SQLDelight)
    single { get<DriverFactory>().createDriver() }
    single { AppDatabase(get()) }
    single { get<AppDatabase>().appDatabaseQueries }
    single<ILocalServicesDataSource> { LocalServicesDataSource(get()) }

}