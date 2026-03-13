package com.example.shared.data.repository.Appointment

import com.example.shared.data.remote.appointment.IRemoteAppointmentDataSource
import com.example.shared.domain.entity.Appointment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class AppointmentRepository(
    private val remote: IRemoteAppointmentDataSource
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
}