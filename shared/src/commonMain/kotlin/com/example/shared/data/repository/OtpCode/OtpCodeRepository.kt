package com.example.shared.data.repository.OtpCode

import com.example.shared.data.remote.OtpCode.IRemoteOtpCodeDataSource
import com.example.shared.data.repository.catchEmpty
import com.example.shared.data.repository.safeNullableCall
import com.example.shared.data.repository.safeStringCall
import com.example.shared.data.repository.safeUnitCall
import com.example.shared.domain.entity.OtpCode
import kotlinx.coroutines.flow.Flow

class OtpCodeRepository(private val remote: IRemoteOtpCodeDataSource) : IOtpCodeRepository {

  override suspend fun getOtpCodesByUser(userId: String): Flow<List<OtpCode>> =
      remote.getOtpCodesByUser(userId).catchEmpty("fetching otp codes")

  override suspend fun getOtpCodeById(userId: String, otpId: String): OtpCode? =
      safeNullableCall("getOtpCodeById") { remote.getOtpCodeById(userId, otpId) }

  override suspend fun createOtpCode(userId: String, otp: OtpCode): String =
      safeStringCall("createOtpCode") { remote.createOtpCode(userId, otp) }

  override suspend fun markOtpCodeAsUsed(userId: String, otpId: String, usedAt: Long) =
      safeUnitCall("markOtpCodeAsUsed") { remote.markOtpCodeAsUsed(userId, otpId, usedAt) }

  override suspend fun deleteOtpCode(userId: String, otpId: String) =
      safeUnitCall("deleteOtpCode") { remote.deleteOtpCode(userId, otpId) }
}
