package com.example.shared.data.repository.professionalProfile

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.ProfessionalProfileData
import kotlinx.coroutines.flow.Flow

interface IProfessionalProfileRepository {
    suspend fun getProfessionalProfile(workerId: String): Flow<ProfessionalProfileData?>
    suspend fun getWorkerAppointments(workerId: String): Flow<List<Appointment>>

    suspend fun getFavoriteWorkerIds(clientId: String): Flow<Set<String>>
    suspend fun addFavorite(clientId: String, workerId: String)
    suspend fun removeFavorite(clientId: String, workerId: String)
    suspend fun getWorkerCategoryIds(workerId: String): Flow<List<String>>
    suspend fun updateWorkerCategories(workerId: String, categoryIds: List<String>)
    suspend fun getWorkerTravelTime(workerId: String): Flow<Int>
    suspend fun updateTravelTime(workerId: String, minutes: Int)
}