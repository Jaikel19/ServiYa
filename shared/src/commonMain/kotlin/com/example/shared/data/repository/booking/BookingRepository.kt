package com.example.shared.data.repository

import com.example.shared.data.remote.IRemoteBookingDataSource
import com.example.shared.domain.entity.Booking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class BookingRepository(
    private val remote: IRemoteBookingDataSource
) : IBookingRepository {

    override suspend fun getBookingsByWorker(workerId: String): Flow<List<Booking>> =
        remote.getBookingsByWorker(workerId)
            .catch { e ->
                println("ERROR fetching bookings: ${e.message}")
                emit(emptyList())
            }
}