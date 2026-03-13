package com.example.shared.data.remote.Report

import com.example.shared.domain.entity.Report
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteReportDataSource : IRemoteReportDataSource {

    private val db = Firebase.firestore

    // GET (realtime) — solo hay un reporte por cita
    override suspend fun getReportByAppointment(appointmentId: String): Flow<Report?> {
        return db.collection("appointments")
            .document(appointmentId)
            .collection("report")
            .snapshots
            .map { snapshot ->
                snapshot.documents.firstOrNull()?.let { doc ->
                    try {
                        doc.data<Report>().copy(id = doc.id)
                    } catch (e: Exception) {
                        println("ERROR parsing report: ${e.message}")
                        null
                    }
                }
            }
    }

    // CREATE
    override suspend fun createReport(appointmentId: String, report: Report): String {
        return try {
            val ref = db.collection("appointments")
                .document(appointmentId)
                .collection("report")
                .add(report)
            ref.id
        } catch (e: Exception) {
            println("ERROR createReport: ${e.message}")
            ""
        }
    }

    // UPDATE VALIDITY
    override suspend fun updateReportValidity(
        appointmentId: String,
        reportId: String,
        isValid: Boolean
    ) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("report")
                .document(reportId)
                .update("isValid" to isValid)
        } catch (e: Exception) {
            println("ERROR updateReportValidity: ${e.message}")
        }
    }

    // DELETE
    override suspend fun deleteReport(appointmentId: String, reportId: String) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("report")
                .document(reportId)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteReport: ${e.message}")
        }
    }
}
