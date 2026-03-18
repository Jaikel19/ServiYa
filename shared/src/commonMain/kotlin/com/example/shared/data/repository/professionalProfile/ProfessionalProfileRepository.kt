package com.example.shared.data.repository.professionalProfile

import com.example.shared.data.remote.professionalProfile.IRemoteProfessionalProfileDataSource
import com.example.shared.data.repository.Service.IServiceRepository
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.ProfessionalProfileData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ProfessionalProfileRepository(
    private val remoteProfile: IRemoteProfessionalProfileDataSource,
    private val servicesRepository: IServiceRepository
) : IProfessionalProfileRepository {

    override suspend fun getProfessionalProfile(workerId: String): Flow<ProfessionalProfileData?> {
        val profileFlow = remoteProfile.getWorkerProfile(workerId)
        val servicesFlow = servicesRepository.getServicesByWorker(workerId)
        val schedule = remoteProfile.getWorkerSchedule(workerId)
        val portfolios = remoteProfile.getWorkerPortfolios(workerId)
        val reviews = remoteProfile.getWorkerReviews(workerId)

        return combine(profileFlow, servicesFlow) { profile, services ->
            if (profile == null) return@combine null

            val categoryNames = remoteProfile.getCategoryNames(profile.categories)
            val province = remoteProfile.getWorkerProvinceFromAddresses(workerId).orEmpty()
            val cancellationPolicy = remoteProfile.getWorkerCancellationPolicy(workerId)

            ProfessionalProfileData(
                workerId = workerId,
                uid = profile.uid,
                name = profile.name,
                email = profile.email,
                phone = profile.phone,
                profilePictureLink = profile.profilePicture,
                description = profile.description,
                role = profile.role,
                stars = profile.stars,
                status = profile.status,
                travelTime = profile.travelTime,
                trustScore = profile.trustScore,
                locationProvince = province,
                categoryNames = categoryNames,
                services = services,
                cancellationPolicy = cancellationPolicy,
                schedule = schedule,
                portfolios = portfolios,
                reviews = reviews
            )
        }
    }

    override suspend fun getWorkerAppointments(workerId: String): Flow<List<Appointment>> {
        return remoteProfile.getWorkerAppointments(workerId)
    }

    override suspend fun getFavoriteWorkerIds(clientId: String): Flow<Set<String>> {
        return remoteProfile.getFavoriteWorkerIds(clientId)
    }

    override suspend fun addFavorite(clientId: String, workerId: String) {
        remoteProfile.addFavorite(clientId, workerId)
    }

    override suspend fun removeFavorite(clientId: String, workerId: String) {
        remoteProfile.removeFavorite(clientId, workerId)
    }
}