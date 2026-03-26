package com.example.shared.data.repository.Appointment

import com.example.shared.data.remote.appointment.IRemoteAppointmentDataSource
import com.example.shared.data.repository.OtpAppointment.IOtpAppointmentRepository
import com.example.shared.data.repository.catchEmpty
import com.example.shared.data.repository.catchNull
import com.example.shared.data.repository.safeStringCall
import com.example.shared.data.repository.safeUnitCall
import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.OtpAppointment
import com.example.shared.utils.DateTimeUtils
import com.example.shared.utils.OtpUtils
import kotlinx.coroutines.flow.Flow

class AppointmentRepository(
    private val remote: IRemoteAppointmentDataSource,
    private val otpAppointmentRepository: IOtpAppointmentRepository
) : IAppointmentRepository {

    override suspend fun getAppointmentsByClient(clientId: String): Flow<List<Appointment>> =
        remote.getAppointmentsByClient(clientId).catchEmpty("fetching appointments by client")

    override suspend fun getAppointmentsByWorker(workerId: String): Flow<List<Appointment>> =
        remote.getAppointmentsByWorker(workerId).catchEmpty("fetching appointments by worker")

    override suspend fun getAppointmentById(appointmentId: String): Flow<Appointment?> =
        remote.getAppointmentById(appointmentId).catchNull("fetching appointment")

    override suspend fun createAppointment(appointment: Appointment): String =
        safeStringCall("createAppointment") { remote.createAppointment(appointment) }

    override suspend fun updateAppointment(appointment: Appointment) =
        safeUnitCall("updateAppointment") { remote.updateAppointment(appointment) }

    override suspend fun deleteAppointment(appointmentId: String) =
        safeUnitCall("deleteAppointment") { remote.deleteAppointment(appointmentId) }

    override suspend fun approveAppointment(appointmentId: String) {
        remote.approveAppointment(appointmentId)
    }

    override suspend fun confirmPayment(appointmentId: String) {
        println("OTP DEBUG 0: entró a confirmPayment con id = $appointmentId")

        try {
            remote.confirmPayment(appointmentId)
            println("OTP DEBUG 1: cita confirmada = $appointmentId")

            val otpCode = OtpUtils.generateOtpCode()
            val now = DateTimeUtils.nowIsoMinute()

            println("OTP DEBUG 2: otpCode generado = $otpCode")
            println("OTP DEBUG 3: fecha actual = $now")

            val otp = OtpAppointment(
                id = "current",
                purpose = "start",
                code = otpCode,
                codeHash = OtpUtils.sha256(otpCode),
                createdAt = now,
                expiresAt = now,
                usedAt = null,
                status = "GENERATED"
            )

            println("OTP DEBUG 4: otp creado = $otp")

            val result = otpAppointmentRepository.createOtp(appointmentId, otp)
            println("OTP DEBUG 5: createOtp result = $result")
        } catch (e: Exception) {
            println("OTP DEBUG ERROR: ${e.message}")
            e.printStackTrace()
        }
    }

    override suspend fun startAppointment(appointmentId: String) {
        remote.startAppointment(appointmentId)
    }

    override suspend fun completeAppointment(appointmentId: String) {
        remote.completeAppointment(appointmentId)
    }

    override suspend fun rejectAppointmentByWorker(appointmentId: String) {
        remote.rejectAppointmentByWorker(appointmentId)
    }

    override suspend fun cancelAppointmentByWorker(appointmentId: String) {
        remote.cancelAppointmentByWorker(appointmentId)
    }

    override suspend fun cancelAppointmentByClient(appointmentId: String) {
        remote.cancelAppointmentByClient(appointmentId)
    }

    override suspend fun cancelAppointmentByClientWithRefund(
        appointmentId: String,
        cancelledAt: String,
        refundPercentage: Int,
        refundAmount: Int,
        policyLabel: String,
        warningMessage: String
    ) {
        println(
            "CANCEL DEBUG CLIENT -> appointmentId=$appointmentId, " +
                    "cancelledAt=$cancelledAt, refundPercentage=$refundPercentage, " +
                    "refundAmount=$refundAmount, policyLabel=$policyLabel"
        )

        remote.cancelAppointmentByClient(appointmentId)
    }

    override suspend fun cancelAppointmentByWorkerWithRefund(
        appointmentId: String,
        cancelledAt: String,
        refundPercentage: Int,
        refundAmount: Int,
        policyLabel: String,
        warningMessage: String
    ) {
        println(
            "CANCEL DEBUG WORKER -> appointmentId=$appointmentId, " +
                    "cancelledAt=$cancelledAt, refundPercentage=$refundPercentage, " +
                    "refundAmount=$refundAmount, policyLabel=$policyLabel"
        )

        remote.cancelAppointmentByWorker(appointmentId)
    }

    override suspend fun markPaymentPending(appointmentId: String) {
        remote.markPaymentPending(appointmentId)
    }
}