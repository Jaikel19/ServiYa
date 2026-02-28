package com.example.shared.data.repository

import com.example.shared.data.local.ILocalServicesDataSource
import com.example.shared.data.remote.IRemoteServicesDataSource
import com.example.shared.domain.entity.Service
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

class ServiceRepository(
    private val local: ILocalServicesDataSource,
    private val remote: IRemoteServicesDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : IServiceRepository {

    override val services: Flow<List<Service>> = flow {
        // emitir cache inmediato (esto evita “loading infinito”)
        emit(local.getAllServices())

        // intentar red, guardar y emitir
        emitAll(
            remote.getServices()
                .onEach { list ->
                    local.clearAndCreateServices(list)
                }
        )
    }
        .flowOn(defaultDispatcher)
        .catch { e ->
            println("ERROR REAL: ${e.message}")
            emit(emptyList())
        }
}