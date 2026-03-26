package com.example.shared.data.repository.Report

import com.example.shared.data.remote.Report.IRemoteReportDataSource
import com.example.shared.data.repository.catchNull
import com.example.shared.data.repository.safeStringCall
import com.example.shared.data.repository.safeUnitCall
import com.example.shared.domain.entity.Report
import kotlinx.coroutines.flow.Flow

class ReportRepository(
    private val remote: IRemoteReportDataSource
) : IReportRepository {

    override suspend fun getReportByAppointment(appointmentId: String): Flow<Report?> =
        remote.getReportByAppointment(appointmentId).catchNull("fetching report")

    override suspend fun createReport(appointmentId: String, report: Report): String =
        safeStringCall("createReport") { remote.createReport(appointmentId, report) }

    override suspend fun updateReportValidity(
        appointmentId: String,
        reportId: String,
        isValid: Boolean
    ) = safeUnitCall("updateReportValidity") {
        remote.updateReportValidity(appointmentId, reportId, isValid)
    }

    override suspend fun deleteReport(appointmentId: String, reportId: String) =
        safeUnitCall("deleteReport") { remote.deleteReport(appointmentId, reportId) }
}