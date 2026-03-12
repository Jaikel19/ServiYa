package com.example.shared.data.local

import com.example.shared.domain.entity.Service

interface ILocalServicesDataSource {
    fun getAllServices(): List<Service>
    fun clearAndCreateServices(services: List<Service>)
}