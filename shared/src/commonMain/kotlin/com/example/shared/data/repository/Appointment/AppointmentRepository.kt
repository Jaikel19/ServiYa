package com.example.shared.data.repository.Appointment

import com.example.shared.data.remote.appointment.IRemoteAppointmentDataSource
import com.example.shared.domain.entity.Appointment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import com.example.shared.data.repository.OtpAppointment.IOtpAppointmentRepository
import com.example.shared.domain.entity.OtpAppointment
import com.example.shared.utils.DateTimeUtils
import com.example.shared.utils.OtpUtils

class AppointmentRepository(
    private val remote: IRemoteAppointmentDataSource,
    private val otpAppointmentRepository: IOtpAppointmentRepository
) : IAppointmentRepository {

    override suspend fun getAppointmentsByClient(clientId: String): Flow<List<Appointment>> =
        remote.getAppointmentsByClient(clientId)
            .catch { e ->
                println("ERROR fetching appointments by client: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getAppointmentsByWorker(workerId: String): Flow<List<Appointment>> =
        remote.getAppointmentsByWorker(workerId)
            .catch { e ->
                println("ERROR fetching appointments by worker: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getAppointmentById(appointmentId: String): Flow<Appointment?> =
        remote.getAppointmentById(appointmentId)
            .catch { e ->
                println("ERROR fetching appointment: ${e.message}")
                emit(null)
            }

    override suspend fun createAppointment(appointment: Appointment): String =
        try {
            remote.createAppointment(appointment)
        } catch (e: Exception) {
            println("ERROR createAppointment: ${e.message}")
            ""
        }

    override suspend fun updateAppointment(appointment: Appointment) =
        try {
            remote.updateAppointment(appointment)
        } catch (e: Exception) {
            println("ERROR updateAppointment: ${e.message}")
        }

    override suspend fun deleteAppointment(appointmentId: String) =
        try {
            remote.deleteAppointment(appointmentId)
        } catch (e: Exception) {
            println("ERROR deleteAppointment: ${e.message}")
        }

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
}