package com.example.shared.data.repository.Schedule

import com.example.shared.data.remote.Schedule.IRemoteScheduleDataSource
import com.example.shared.data.repository.catchEmpty
import com.example.shared.data.repository.safeNullableCall
import com.example.shared.data.repository.safeUnitCall
import com.example.shared.domain.entity.Schedule
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val remote: IRemoteScheduleDataSource) : IScheduleRepository {

  override suspend fun getScheduleByUser(userId: String): Flow<List<Schedule>> =
      remote.getScheduleByUser(userId).catchEmpty("fetching schedule")

  override suspend fun getScheduleByDay(userId: String, dayKey: String): Schedule? =
      safeNullableCall("getScheduleByDay") { remote.getScheduleByDay(userId, dayKey) }

  override suspend fun createOrUpdateSchedule(userId: String, schedule: Schedule) =
      safeUnitCall("createOrUpdateSchedule") { remote.createOrUpdateSchedule(userId, schedule) }

  override suspend fun deleteSchedule(userId: String, dayKey: String) =
      safeUnitCall("deleteSchedule") { remote.deleteSchedule(userId, dayKey) }
}
