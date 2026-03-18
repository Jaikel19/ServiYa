package com.example.shared.data.repository.OtpAppointment

import com.example.shared.data.remote.OtpAppointment.IRemoteOtpAppointmentDataSource
import com.example.shared.domain.entity.OtpAppointment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class OtpAppointmentRepository(
    private val remote: IRemoteOtpAppointmentDataSource
) : IOtpAppointmentRepository {

    override suspend fun getOtpByAppointment(appointmentId: String): Flow<OtpAppointment?> =
        remote.getOtpByAppointment(appointmentId)
            .catch { e ->
                println("ERROR fetching otp: ${e.message}")
                emit(null)
            }

    override suspend fun createOtp(appointmentId: String, otp: OtpAppointment): String =
        try {
            remote.createOtp(appointmentId, otp)
        } catch (e: Exception) {
            println("ERROR createOtp: ${e.message}")
            ""
        }

    override suspend fun markOtpAsUsed(appointmentId: String, otpId: String, usedAt: String) =
        try {
            remote.markOtpAsUsed(appointmentId, otpId, usedAt)
        } catch (e: Exception) {
            println("ERROR markOtpAsUsed: ${e.message}")
        }

    override suspend fun deleteOtp(appointmentId: String, otpId: String) =
        try {
            remote.deleteOtp(appointmentId, otpId)
        } catch (e: Exception) {
            println("ERROR deleteOtp: ${e.message}")
        }
}