package com.example.shared.data.repository.OtpCode

import com.example.shared.data.remote.OtpCode.IRemoteOtpCodeDataSource
import com.example.shared.domain.entity.OtpCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class OtpCodeRepository(
    private val remote: IRemoteOtpCodeDataSource
) : IOtpCodeRepository {

    override suspend fun getOtpCodesByUser(userId: String): Flow<List<OtpCode>> =
        remote.getOtpCodesByUser(userId)
            .catch { e ->
                println("ERROR fetching otp codes: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getOtpCodeById(userId: String, otpId: String): OtpCode? =
        try {
            remote.getOtpCodeById(userId, otpId)
        } catch (e: Exception) {
            println("ERROR getOtpCodeById: ${e.message}")
            null
        }

    override suspend fun createOtpCode(userId: String, otp: OtpCode): String =
        try {
            remote.createOtpCode(userId, otp)
        } catch (e: Exception) {
            println("ERROR createOtpCode: ${e.message}")
            ""
        }

    override suspend fun markOtpCodeAsUsed(userId: String, otpId: String, usedAt: Long) =
        try {
            remote.markOtpCodeAsUsed(userId, otpId, usedAt)
        } catch (e: Exception) {
            println("ERROR markOtpCodeAsUsed: ${e.message}")
        }

    override suspend fun deleteOtpCode(userId: String, otpId: String) =
        try {
            remote.deleteOtpCode(userId, otpId)
        } catch (e: Exception) {
            println("ERROR deleteOtpCode: ${e.message}")
        }
}