package com.example.shared.data.repository.Schedule


import com.example.shared.data.remote.Schedule.IRemoteScheduleDataSource
import com.example.shared.domain.entity.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class ScheduleRepository(
    private val remote: IRemoteScheduleDataSource
) : IScheduleRepository {

    override suspend fun getScheduleByUser(userId: String): Flow<List<Schedule>> =
        remote.getScheduleByUser(userId)
            .catch { e ->
                println("ERROR fetching schedule: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getScheduleByDay(userId: String, dayKey: String): Schedule? =
        try {
            remote.getScheduleByDay(userId, dayKey)
        } catch (e: Exception) {
            println("ERROR getScheduleByDay: ${e.message}")
            null
        }

    override suspend fun createOrUpdateSchedule(userId: String, schedule: Schedule) =
        try {
            remote.createOrUpdateSchedule(userId, schedule)
        } catch (e: Exception) {
            println("ERROR createOrUpdateSchedule: ${e.message}")
        }

    override suspend fun deleteSchedule(userId: String, dayKey: String) =
        try {
            remote.deleteSchedule(userId, dayKey)
        } catch (e: Exception) {
            println("ERROR deleteSchedule: ${e.message}")
        }
}