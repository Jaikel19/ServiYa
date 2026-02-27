package com.example.shared.di.modules

import com.example.shared.data.remote.IRemoteServicesDataSource
import com.example.shared.data.remote.RemoteServicesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val dataModule = module {
    // Define aquí las dependencias de tu capa de datos
    // repositories, local/remote datasources, SQLDelight, etc...
    single<IRemoteServicesDataSource> { RemoteServicesDataSource(get(), Dispatchers.IO) } //el get() le pasa el HttpClient que ya registraste en NetworkModule
}