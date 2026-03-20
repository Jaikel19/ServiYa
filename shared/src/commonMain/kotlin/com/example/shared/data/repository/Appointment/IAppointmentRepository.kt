package com.example.shared.data.repository.Appointment

import com.example.shared.domain.entity.Appointment
import kotlinx.coroutines.flow.Flow

interface IAppointmentRepository {
    suspend fun getAppointmentsByClient(clientId: String): Flow<List<Appointment>>
    suspend fun getAppointmentsByWorker(workerId: String): Flow<List<Appointment>>
    suspend fun getAppointmentById(appointmentId: String): Flow<Appointment?>

    suspend fun createAppointment(appointment: Appointment): String
    suspend fun updateAppointment(appointment: Appointment)
    suspend fun deleteAppointment(appointmentId: String)

    suspend fun approveAppointment(appointmentId: String)
    suspend fun confirmPayment(appointmentId: String)
    suspend fun startAppointment(appointmentId: String)
    suspend fun completeAppointment(appointmentId: String)
    suspend fun rejectAppointmentByWorker(appointmentId: String)
    suspend fun cancelAppointmentByWorker(appointmentId: String)
    suspend fun cancelAppointmentByClient(appointmentId: String)
    suspend fun markPaymentPending(appointmentId: String)
}