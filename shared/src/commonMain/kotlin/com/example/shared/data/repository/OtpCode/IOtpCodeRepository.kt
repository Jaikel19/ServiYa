package com.example.shared.data.repository.OtpCode

import com.example.shared.domain.entity.OtpCode
import kotlinx.coroutines.flow.Flow

interface IOtpCodeRepository {
  suspend fun getOtpCodesByUser(userId: String): Flow<List<OtpCode>>

  suspend fun getOtpCodeById(userId: String, otpId: String): OtpCode?

  suspend fun createOtpCode(userId: String, otp: OtpCode): String

  suspend fun markOtpCodeAsUsed(userId: String, otpId: String, usedAt: Long)

  suspend fun deleteOtpCode(userId: String, otpId: String)
}
