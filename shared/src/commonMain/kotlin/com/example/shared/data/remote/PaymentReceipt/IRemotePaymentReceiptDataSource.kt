package com.example.shared.data.remote.PaymentReceipt

import com.example.shared.domain.entity.PaymentReceipt
import kotlinx.coroutines.flow.Flow

interface IRemotePaymentReceiptDataSource {
  suspend fun getReceiptByAppointment(appointmentId: String): Flow<PaymentReceipt?>

  suspend fun createReceipt(appointmentId: String, receipt: PaymentReceipt): String

  suspend fun updateReceiptStatus(
      appointmentId: String,
      receiptId: String,
      status: String,
      note: String?,
      reviewedAt: String?,
      reviewedBy: String?,
      rejectionReason: String?,
  )

  suspend fun deleteReceipt(appointmentId: String, receiptId: String)

  suspend fun updateReceiptImageUrl(appointmentId: String, receiptId: String, imageUrl: String)
}
