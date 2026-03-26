package com.example.shared.data.remote.Report

import com.example.shared.data.remote.FirestoreSubcollectionCrud
import com.example.shared.domain.entity.Report
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow

class RemoteReportDataSource : IRemoteReportDataSource {

  private val crud = FirestoreSubcollectionCrud(Firebase.firestore, "appointments", "report")

  override suspend fun getReportByAppointment(appointmentId: String): Flow<Report?> =
      crud.observeFirst(appointmentId) { doc -> doc.data<Report>().copy(id = doc.id) }

  override suspend fun createReport(appointmentId: String, report: Report): String =
      crud.addDocument(appointmentId, report)

  override suspend fun updateReportValidity(
      appointmentId: String,
      reportId: String,
      isValid: Boolean,
  ) = crud.updateFields(appointmentId, reportId, "isValid" to isValid)

  override suspend fun deleteReport(appointmentId: String, reportId: String) =
      crud.deleteDocument(appointmentId, reportId)
}
