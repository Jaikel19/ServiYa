package com.example.shared.data.repository

import com.example.shared.domain.entity.Booking
import kotlinx.coroutines.flow.Flow

interface IBookingRepository {
    suspend fun getBookingsByWorker(workerId: String): Flow<List<Booking>>
}