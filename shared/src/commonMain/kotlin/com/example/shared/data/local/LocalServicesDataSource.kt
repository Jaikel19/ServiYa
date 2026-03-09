package com.example.shared.data.local

import com.example.shared.domain.entity.Service

class LocalServicesDataSource(database: AppDatabase) : ILocalServicesDataSource {

    private val dbQuery = database.appDatabaseQueries

    override fun getAllServices(): List<Service> {
        return dbQuery.selectAllServices(mapper = ::mapService).executeAsList()
    }

    private fun mapService(
        id: String,
        name: String,
        cost: Double,
        duration: String,
        description: String
    ): Service {
        return Service(
            id = id,
            name = name,
            cost = cost,
            duration = duration,
            description = description
        )
    }

    override fun clearAndCreateServices(services: List<Service>) {
        dbQuery.transaction {
            dbQuery.deleteAllServices()
            services.forEach { s ->
                dbQuery.insertService(
                    id = s.id,
                    name = s.name,
                    cost = s.cost,
                    duration = s.duration,
                    description = s.description
                )
            }
        }
    }
}