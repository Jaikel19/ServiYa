package com.example.shared.data.repository.PaymentReceipt

import com.example.shared.data.remote.PaymentReceipt.IRemotePaymentReceiptDataSource
import com.example.shared.data.repository.catchNull
import com.example.shared.data.repository.safeStringCall
import com.example.shared.data.repository.safeUnitCall
import com.example.shared.domain.entity.PaymentReceipt
import kotlinx.coroutines.flow.Flow

class PaymentReceiptRepository(private val remote: IRemotePaymentReceiptDataSource) :
    IPaymentReceiptRepository {

  override suspend fun getReceiptByAppointment(appointmentId: String): Flow<PaymentReceipt?> =
      remote.getReceiptByAppointment(appointmentId).catchNull("fetching receipt")

  override suspend fun createReceipt(appointmentId: String, receipt: PaymentReceipt): String =
      safeStringCall("createReceipt") { remote.createReceipt(appointmentId, receipt) }

  override suspend fun updateReceiptStatus(
      appointmentId: String,
      receiptId: String,
      status: String,
      note: String?,
      reviewedAt: String?,
      reviewedBy: String?,
      rejectionReason: String?,
  ) =
      safeUnitCall("updateReceiptStatus") {
        remote.updateReceiptStatus(
            appointmentId = appointmentId,
            receiptId = receiptId,
            status = status,
            note = note,
            reviewedAt = reviewedAt,
            reviewedBy = reviewedBy,
            rejectionReason = rejectionReason,
        )
      }

  override suspend fun updateReceiptImageUrl(
      appointmentId: String,
      receiptId: String,
      imageUrl: String,
  ) =
      safeUnitCall("updateReceiptImageUrl") {
        remote.updateReceiptImageUrl(appointmentId, receiptId, imageUrl)
      }

  override suspend fun deleteReceipt(appointmentId: String, receiptId: String) =
      safeUnitCall("deleteReceipt") { remote.deleteReceipt(appointmentId, receiptId) }
}
