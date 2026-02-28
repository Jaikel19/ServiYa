package com.example.shared.data.local

import com.example.shared.domain.entity.Service

class LocalServicesDataSource(database: AppDatabase) : ILocalServicesDataSource {

    private val dbQuery = database.appDatabaseQueries

    override fun getAllServices(): List<Service> {
        return dbQuery.selectAllServices(mapper = ::mapServiceSelecting).executeAsList()
    }

    private fun mapServiceSelecting(
        id: Long,
        title: String,
        description: String?,
        category: String?,
        price: Double?,
        isActive: Boolean?
    ): Service {
        return Service(
            id = id,
            title = title,
            description = description,
            category = category,
            price = price,
            isActive = isActive ?: true
        )
    }

    override fun clearAndCreateServices(services: List<Service>) {
        dbQuery.transaction {
            dbQuery.deleteAllServices()
            services.forEach { s ->
                dbQuery.insertService(
                    title = s.title,
                    description = s.description,
                    category = s.category,
                    price = s.price,
                    isActive = s.isActive
                )
            }
        }
    }
}
