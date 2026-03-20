package com.example.shared.data.repository.PaymentReceipt

import com.example.shared.data.remote.PaymentReceipt.IRemotePaymentReceiptDataSource
import com.example.shared.domain.entity.PaymentReceipt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class PaymentReceiptRepository(
    private val remote: IRemotePaymentReceiptDataSource
) : IPaymentReceiptRepository {

    override suspend fun getReceiptByAppointment(appointmentId: String): Flow<PaymentReceipt?> =
        remote.getReceiptByAppointment(appointmentId)
            .catch { e ->
                println("ERROR fetching receipt: ${e.message}")
                emit(null)
            }

    override suspend fun createReceipt(appointmentId: String, receipt: PaymentReceipt): String =
        try {
            remote.createReceipt(appointmentId, receipt)
        } catch (e: Exception) {
            println("ERROR createReceipt: ${e.message}")
            ""
        }

    override suspend fun updateReceiptStatus(
        appointmentId: String,
        receiptId: String,
        status: String,
        note: String?,
        reviewedAt: String?,
        reviewedBy: String?,
        rejectionReason: String?
    ) = try {
        remote.updateReceiptStatus(
            appointmentId = appointmentId,
            receiptId = receiptId,
            status = status,
            note = note,
            reviewedAt = reviewedAt,
            reviewedBy = reviewedBy,
            rejectionReason = rejectionReason
        )
    } catch (e: Exception) {
        println("ERROR updateReceiptStatus: ${e.message}")
    }
    override suspend fun updateReceiptImageUrl(
        appointmentId: String,
        receiptId: String,
        imageUrl: String
    ) = try {
        remote.updateReceiptImageUrl(appointmentId, receiptId, imageUrl)
    } catch (e: Exception) {
        println("ERROR updateReceiptImageUrl: ${e.message}")
    }
    override suspend fun deleteReceipt(appointmentId: String, receiptId: String) =
        try {
            remote.deleteReceipt(appointmentId, receiptId)
        } catch (e: Exception) {
            println("ERROR deleteReceipt: ${e.message}")
        }
}