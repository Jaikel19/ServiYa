package com.example.shared.data.remote.Schedule

import com.example.shared.domain.entity.Schedule
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteScheduleDataSource : IRemoteScheduleDataSource {

    private val db = Firebase.firestore

    // GET ALL (realtime)
    override suspend fun getScheduleByUser(userId: String): Flow<List<Schedule>> {
        return db.collection("users")
            .document(userId)
            .collection("schedule")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    try {
                        doc.data<Schedule>().copy(dayKey = doc.id)
                    } catch (e: Exception) {
                        Schedule(dayKey = doc.id)
                    }
                }
            }
    }

    // GET ONE
    override suspend fun getScheduleByDay(userId: String, dayKey: String): Schedule? {
        return try {
            val doc = db.collection("users")
                .document(userId)
                .collection("schedule")
                .document(dayKey)
                .get()
            if (doc.exists) doc.data<Schedule>().copy(dayKey = doc.id) else null
        } catch (e: Exception) {
            println("ERROR getScheduleByDay: ${e.message}")
            null
        }
    }

    // CREATE OR UPDATE (usa dayKey como ID del documento)
    override suspend fun createOrUpdateSchedule(userId: String, schedule: Schedule) {
        try {
            db.collection("users")
                .document(userId)
                .collection("schedule")
                .document(schedule.dayKey)
                .set(schedule)
        } catch (e: Exception) {
            println("ERROR createOrUpdateSchedule: ${e.message}")
        }
    }

    // DELETE
    override suspend fun deleteSchedule(userId: String, dayKey: String) {
        try {
            db.collection("users")
                .document(userId)
                .collection("schedule")
                .document(dayKey)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteSchedule: ${e.message}")
        }
    }
}