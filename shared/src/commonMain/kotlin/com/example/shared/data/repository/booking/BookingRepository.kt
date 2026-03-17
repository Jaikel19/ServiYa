package com.example.shared.data.repository

import com.example.shared.data.remote.IRemoteBookingDataSource
import com.example.shared.domain.entity.Booking
import com.example.shared.domain.entity.CancellationPolicy
import com.example.shared.domain.entity.WorkerProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class BookingRepository(
    private val remote: IRemoteBookingDataSource
) : IBookingRepository {

    override fun getBookingsByWorker(workerId: String): Flow<List<Booking>> =
        remote.getBookingsByWorker(workerId)
            .catch { e ->
                println("ERROR fetching bookings by worker: ${e.message}")
                emit(emptyList())
            }

    override fun getBookingsByClient(clientId: String): Flow<List<Booking>> =
        remote.getBookingsByClient(clientId)
            .catch { e ->
                println("ERROR fetching bookings by client: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getBookingById(bookingId: String): Booking? {
        return remote.getBookingById(bookingId)
    }

    override suspend fun getCancellationPolicyByWorkerId(workerId: String): CancellationPolicy? {
        return remote.getCancellationPolicyByWorkerId(workerId)
    }

    override suspend fun getWorkerProfile(workerId: String): WorkerProfile? {
        return remote.getWorkerProfile(workerId)
    }

    override suspend fun approvedAppointment(bookingId: String) {
        remote.approvedAppointment(bookingId)
    }

    override suspend fun confirmPayment(bookingId: String) {
        remote.confirmPayment(bookingId)
    }

    override suspend fun startAppointment(bookingId: String) {
        remote.startAppointment(bookingId)
    }

    override suspend fun completeAppointment(bookingId: String) {
        remote.completeAppointment(bookingId)
    }

    override suspend fun rejectAppointmentByWorker(bookingId: String) {
        remote.rejectAppointmentByWorker(bookingId)
    }

    override suspend fun cancelAppointmentByWorker(bookingId: String) {
        remote.cancelAppointmentByWorker(bookingId)
    }

    override suspend fun cancelAppointmentByClient(bookingId: String) {
        remote.cancelAppointmentByClient(bookingId)
    }
}