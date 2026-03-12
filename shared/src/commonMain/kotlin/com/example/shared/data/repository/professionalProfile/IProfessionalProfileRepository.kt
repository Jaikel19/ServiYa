package com.example.shared.data.repository.professionalProfile

import com.example.shared.domain.entity.ProfessionalProfileData
import kotlinx.coroutines.flow.Flow

interface IProfessionalProfileRepository {
    suspend fun getProfessionalProfile(workerId: String): Flow<ProfessionalProfileData?>
}