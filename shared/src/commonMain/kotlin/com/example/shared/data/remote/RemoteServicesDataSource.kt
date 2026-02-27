package com.example.shared.data.remote

import com.example.shared.domain.entity.Service
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

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