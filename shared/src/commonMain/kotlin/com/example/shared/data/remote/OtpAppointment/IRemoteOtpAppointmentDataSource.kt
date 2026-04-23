package com.example.shared.data.remote.OtpAppointment

import com.example.shared.domain.entity.OtpAppointment
import kotlinx.coroutines.flow.Flow

interface IRemoteOtpAppointmentDataSource {
  suspend fun getOtpByAppointment(appointmentId: String): Flow<OtpAppointment?>

  suspend fun createOtp(appointmentId: String, otp: OtpAppointment): String

  suspend fun markOtpAsUsed(appointmentId: String, otpId: String, usedAt: String)

  suspend fun deleteOtp(appointmentId: String, otpId: String)
}
