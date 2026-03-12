package com.example.shared.data.repository.PaymentReceipt

import com.example.shared.domain.entity.PaymentReceipt
import kotlinx.coroutines.flow.Flow

interface IPaymentReceiptRepository {
    suspend fun getReceiptByAppointment(appointmentId: String): Flow<PaymentReceipt?>
    suspend fun createReceipt(appointmentId: String, receipt: PaymentReceipt): String
    suspend fun updateReceiptStatus(appointmentId: String, receiptId: String, status: String, note: String?)
    suspend fun deleteReceipt(appointmentId: String, receiptId: String)
}