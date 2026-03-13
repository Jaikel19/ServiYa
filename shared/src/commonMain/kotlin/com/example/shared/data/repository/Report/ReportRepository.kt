package com.example.shared.data.repository.Report

import com.example.shared.data.remote.Report.IRemoteReportDataSource

import com.example.shared.domain.entity.Report
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class ReportRepository(
    private val remote: IRemoteReportDataSource
) : IReportRepository {

    override suspend fun getReportByAppointment(appointmentId: String): Flow<Report?> =
        remote.getReportByAppointment(appointmentId)
            .catch { e ->
                println("ERROR fetching report: ${e.message}")
                emit(null)
            }

    override suspend fun createReport(appointmentId: String, report: Report): String =
        try {
            remote.createReport(appointmentId, report)
        } catch (e: Exception) {
            println("ERROR createReport: ${e.message}")
            ""
        }

    override suspend fun updateReportValidity(
        appointmentId: String,
        reportId: String,
        isValid: Boolean
    ) = try {
        remote.updateReportValidity(appointmentId, reportId, isValid)
    } catch (e: Exception) {
        println("ERROR updateReportValidity: ${e.message}")
    }

    override suspend fun deleteReport(appointmentId: String, reportId: String) =
        try {
            remote.deleteReport(appointmentId, reportId)
        } catch (e: Exception) {
            println("ERROR deleteReport: ${e.message}")
        }
}