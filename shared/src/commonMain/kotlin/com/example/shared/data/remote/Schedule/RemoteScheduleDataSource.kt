package com.example.shared.data.remote.Schedule

import com.example.shared.data.remote.FirestoreSubcollectionCrud
import com.example.shared.domain.entity.Schedule
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow

class RemoteScheduleDataSource : IRemoteScheduleDataSource {

  private val crud = FirestoreSubcollectionCrud(Firebase.firestore, "users", "schedule")

  override suspend fun getScheduleByUser(userId: String): Flow<List<Schedule>> =
      crud.observeList(userId) { doc -> doc.data<Schedule>().copy(dayKey = doc.id) }

  override suspend fun getScheduleByDay(userId: String, dayKey: String): Schedule? =
      crud.getDocument(userId, dayKey) { doc -> doc.data<Schedule>().copy(dayKey = doc.id) }

  override suspend fun createOrUpdateSchedule(userId: String, schedule: Schedule) =
      crud.setDocument(userId, schedule.dayKey, schedule)

  override suspend fun deleteSchedule(userId: String, dayKey: String) =
      crud.deleteDocument(userId, dayKey)
}
