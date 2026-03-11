package com.example.shared.data.remote

import com.example.shared.domain.entity.Booking
import kotlinx.coroutines.flow.Flow

interface IRemoteBookingDataSource {
    fun getBookingsByWorker(workerId: String): Flow<List<Booking>>
}