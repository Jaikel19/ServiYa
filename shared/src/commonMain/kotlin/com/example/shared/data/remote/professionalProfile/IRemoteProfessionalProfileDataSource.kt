package com.example.shared.data.remote.professionalProfile

import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.domain.entity.WorkerProfile
import kotlinx.coroutines.flow.Flow

interface IRemoteProfessionalProfileDataSource {
    suspend fun getWorkerProfile(workerId: String): Flow<WorkerProfile?>
    suspend fun getCategoryNames(categoryIds: List<String>): List<String>
    suspend fun getWorkerProvinceFromAddresses(workerId: String): String?
    suspend fun getWorkerCancellationPolicy(workerId: String): CancellationPolicy?
}