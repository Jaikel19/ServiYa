package com.example.shared.data.repository.OtpAppointment

import com.example.shared.domain.entity.OtpAppointment
import kotlinx.coroutines.flow.Flow

interface IOtpAppointmentRepository {
  suspend fun getOtpByAppointment(appointmentId: String): Flow<OtpAppointment?>

  suspend fun createOtp(appointmentId: String, otp: OtpAppointment): String

  suspend fun markOtpAsUsed(appointmentId: String, otpId: String, usedAt: String)

  suspend fun deleteOtp(appointmentId: String, otpId: String)
}
