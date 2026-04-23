package com.example.shared.data.remote.Schedule

import com.example.shared.domain.entity.Schedule
import kotlinx.coroutines.flow.Flow

interface IRemoteScheduleDataSource {
  suspend fun getScheduleByUser(userId: String): Flow<List<Schedule>>

  suspend fun getScheduleByDay(userId: String, dayKey: String): Schedule?

  suspend fun createOrUpdateSchedule(userId: String, schedule: Schedule)

  suspend fun deleteSchedule(userId: String, dayKey: String)
}
