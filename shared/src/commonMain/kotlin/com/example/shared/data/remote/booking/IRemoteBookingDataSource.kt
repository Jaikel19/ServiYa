package com.example.shared.data.remote

import com.example.shared.domain.entity.Booking
import com.example.shared.domain.entity.CancellationPolicy
import kotlinx.coroutines.flow.Flow

interface IRemoteBookingDataSource {
    fun getBookingsByWorker(workerId: String): Flow<List<Booking>>
    fun getBookingsByClient(clientId: String): Flow<List<Booking>>

    suspend fun getBookingById(bookingId: String): Booking?
    suspend fun getCancellationPolicyByWorkerId(workerId: String): CancellationPolicy?

    suspend fun confirmPayment(bookingId: String)
    suspend fun startAppointment(bookingId: String)
    suspend fun completeAppointment(bookingId: String)
    suspend fun cancelAppointmentByWorker(bookingId: String)
    suspend fun cancelAppointmentByClient(bookingId: String)
}