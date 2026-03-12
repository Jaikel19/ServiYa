package com.example.shared.data.repository

import com.example.shared.data.remote.IRemoteBookingDataSource
import com.example.shared.domain.entity.Booking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class BookingRepository(
    private val remote: IRemoteBookingDataSource
) : IBookingRepository {

    override fun getBookingsByWorker(workerId: String): Flow<List<Booking>> =
        remote.getBookingsByWorker(workerId)
            .catch { e ->
                println("ERROR fetching bookings: ${e.message}")
                emit(emptyList())
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

    override suspend fun cancelAppointmentByWorker(bookingId: String) {
        remote.cancelAppointmentByWorker(bookingId)
    }
}