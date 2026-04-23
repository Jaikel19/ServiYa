package com.example.shared.data.remote.Report

import com.example.shared.domain.entity.Report
import kotlinx.coroutines.flow.Flow

interface IRemoteReportDataSource {
  suspend fun getReportByAppointment(appointmentId: String): Flow<Report?>

  suspend fun createReport(appointmentId: String, report: Report): String

  suspend fun updateReportValidity(appointmentId: String, reportId: String, isValid: Boolean)

  suspend fun deleteReport(appointmentId: String, reportId: String)
}
