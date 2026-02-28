package com.example.shared.data.remote

import com.example.shared.config.AppConfig
import com.example.shared.domain.entity.Service
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteServicesDataSource(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val useFake: Boolean = true
) : IRemoteServicesDataSource {

    override fun getServices(): Flow<List<Service>> =
        flow {
            if (useFake) {
                println("REMOTE -> FAKE")
                delay(600L)

                emit(
                    listOf(
                        Service(
                            id = 1L,
                            title = "Corte de pelo",
                            description = "Corte clásico a domicilio",
                            price = 8000.0
                        ),
                        Service(
                            id = 2L,
                            title = "Grooming de mascotas",
                            description = "Baño + corte + secado",
                            price = 12000.0
                        )
                    )
                )
            } else {
                println("REMOTE -> REAL API")
                val services = httpClient
                    .get("${AppConfig.BASE_URL}/api/services")
                    .body<List<Service>>()

                emit(services)
            }
        }.flowOn(ioDispatcher)
}


/*
class RemoteServicesDataSource(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : IRemoteServicesDataSource {

    override fun getServices(): Flow<List<Service>> =
        flow {
            // cuando haya una API real, se cambia el URL
            val services = httpClient
                .get("https://example.com/api/services")
                .body<List<Service>>()

            emit(services)
        }.flowOn(ioDispatcher)
}

*/