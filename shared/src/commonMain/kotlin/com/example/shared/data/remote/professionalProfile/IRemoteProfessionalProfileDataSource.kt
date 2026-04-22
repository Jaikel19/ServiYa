package com.example.shared.data.remote.professionalProfile

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.domain.entity.PortfolioItem
import com.example.shared.domain.entity.WorkerProfile
import com.example.shared.domain.entity.WorkerReviewItem
import com.example.shared.domain.entity.WorkerSchedule
import kotlinx.coroutines.flow.Flow

interface IRemoteProfessionalProfileDataSource {
  suspend fun getWorkerProfile(workerId: String): Flow<WorkerProfile?>

  suspend fun getCategoryNames(categoryIds: List<String>): List<String>

  suspend fun getWorkerProvinceFromAddresses(workerId: String): String?

  suspend fun getWorkerCancellationPolicy(workerId: String): CancellationPolicy?

  suspend fun getWorkerSchedule(workerId: String): List<WorkerSchedule>

  suspend fun getWorkerPortfolios(workerId: String): List<PortfolioItem>

  suspend fun getWorkerReviews(workerId: String): List<WorkerReviewItem>

  suspend fun getWorkerAppointments(workerId: String): Flow<List<Appointment>>

  suspend fun getFavoriteWorkerIds(clientId: String): Flow<Set<String>>

  suspend fun addFavorite(clientId: String, workerId: String)

  suspend fun removeFavorite(clientId: String, workerId: String)

  suspend fun updateWorkerCategories(workerId: String, categoryIds: List<String>)

  suspend fun getWorkerTravelTime(workerId: String): Flow<Int>

  suspend fun updateTravelTime(workerId: String, minutes: Int)

  suspend fun updateStars(userId: String, stars: Double)
}
