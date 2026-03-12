package com.example.shared.data.repository

import com.example.shared.domain.entity.Booking
import kotlinx.coroutines.flow.Flow

interface IBookingRepository {
    fun getBookingsByWorker(workerId: String): Flow<List<Booking>>

    suspend fun confirmPayment(bookingId: String)
    suspend fun startAppointment(bookingId: String)
    suspend fun completeAppointment(bookingId: String)
    suspend fun cancelAppointmentByWorker(bookingId: String)
}