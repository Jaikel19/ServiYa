package com.example.shared.data.repository.OtpAppointment

import com.example.shared.data.remote.OtpAppointment.IRemoteOtpAppointmentDataSource
import com.example.shared.data.repository.catchNull
import com.example.shared.data.repository.safeStringCall
import com.example.shared.data.repository.safeUnitCall
import com.example.shared.domain.entity.OtpAppointment
import kotlinx.coroutines.flow.Flow

class OtpAppointmentRepository(
    private val remote: IRemoteOtpAppointmentDataSource
) : IOtpAppointmentRepository {

    override suspend fun getOtpByAppointment(appointmentId: String): Flow<OtpAppointment?> =
        remote.getOtpByAppointment(appointmentId).catchNull("fetching otp")

    override suspend fun createOtp(appointmentId: String, otp: OtpAppointment): String =
        safeStringCall("createOtp") { remote.createOtp(appointmentId, otp) }

    override suspend fun markOtpAsUsed(appointmentId: String, otpId: String, usedAt: String) =
        safeUnitCall("markOtpAsUsed") { remote.markOtpAsUsed(appointmentId, otpId, usedAt) }

    override suspend fun deleteOtp(appointmentId: String, otpId: String) =
        safeUnitCall("deleteOtp") { remote.deleteOtp(appointmentId, otpId) }
}