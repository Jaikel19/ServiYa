package com.example.shared.data.remote.PaymentReceipt

import com.example.shared.domain.entity.PaymentReceipt
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemotePaymentReceiptDataSource : IRemotePaymentReceiptDataSource {

    private val db = Firebase.firestore

    override suspend fun getReceiptByAppointment(appointmentId: String): Flow<PaymentReceipt?> = flow {
        val receipt: PaymentReceipt? = try {
            val snapshot = db.collection("appointments")
                .document(appointmentId)
                .collection("paymentReceipt")
                .get()

            if (snapshot.documents.isEmpty()) {
                null
            } else {
                val doc = snapshot.documents.first()
                doc.data<PaymentReceipt>().copy(id = doc.id)
            }
        } catch (e: Exception) {
            null
        }

        emit(receipt)
    }

    override suspend fun createReceipt(appointmentId: String, receipt: PaymentReceipt): String {
        return try {
            val ref = db.collection("appointments")
                .document(appointmentId)
                .collection("paymentReceipt")
                .add(receipt)
            ref.id
        } catch (e: Exception) {
            ""
        }
    }

    override suspend fun updateReceiptStatus(
        appointmentId: String,
        receiptId: String,
        status: String,
        note: String?,
        reviewedAt: String?,
        reviewedBy: String?,
        rejectionReason: String?
    ) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("paymentReceipt")
                .document(receiptId)
                .update(
                    "status" to status,
                    "note" to note,
                    "reviewedAt" to reviewedAt,
                    "reviewedBy" to reviewedBy,
                    "rejectionReason" to rejectionReason
                )
        } catch (_: Exception) {
        }
    }

    override suspend fun deleteReceipt(appointmentId: String, receiptId: String) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("paymentReceipt")
                .document(receiptId)
                .delete()
        } catch (_: Exception) {
        }
    }
}