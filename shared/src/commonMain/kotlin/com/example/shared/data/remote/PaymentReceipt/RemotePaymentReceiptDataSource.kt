package com.example.shared.data.remote.PaymentReceipt

import com.example.shared.domain.entity.PaymentReceipt
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemotePaymentReceiptDataSource : IRemotePaymentReceiptDataSource {

    private val db = Firebase.firestore

    // GET (realtime) — solo hay un recibo por cita
    override suspend fun getReceiptByAppointment(appointmentId: String): Flow<PaymentReceipt?> {
        return db.collection("appointments")
            .document(appointmentId)
            .collection("paymentReceipt")
            .snapshots
            .map { snapshot ->
                snapshot.documents.firstOrNull()?.let { doc ->
                    try {
                        doc.data<PaymentReceipt>().copy(id = doc.id)
                    } catch (e: Exception) {
                        println("ERROR parsing receipt: ${e.message}")
                        null
                    }
                }
            }
    }

    // CREATE
    override suspend fun createReceipt(appointmentId: String, receipt: PaymentReceipt): String {
        return try {
            val ref = db.collection("appointments")
                .document(appointmentId)
                .collection("paymentReceipt")
                .add(receipt)
            ref.id
        } catch (e: Exception) {
            println("ERROR createReceipt: ${e.message}")
            ""
        }
    }

    // UPDATE STATUS
    override suspend fun updateReceiptStatus(
        appointmentId: String,
        receiptId: String,
        status: String,
        note: String?
    ) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("paymentReceipt")
                .document(receiptId)
                .update(
                    "status" to status,
                    "note" to note
                )
        } catch (e: Exception) {
            println("ERROR updateReceiptStatus: ${e.message}")
        }
    }

    // DELETE
    override suspend fun deleteReceipt(appointmentId: String, receiptId: String) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("paymentReceipt")
                .document(receiptId)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteReceipt: ${e.message}")
        }
    }
}