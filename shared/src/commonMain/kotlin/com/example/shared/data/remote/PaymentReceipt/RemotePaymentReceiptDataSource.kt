package com.example.shared.data.remote.PaymentReceipt

import com.example.shared.data.remote.FirestoreSubcollectionCrud
import com.example.shared.domain.entity.PaymentReceipt
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemotePaymentReceiptDataSource : IRemotePaymentReceiptDataSource {

    private val crud = FirestoreSubcollectionCrud(Firebase.firestore, "appointments", "paymentReceipt")

    override suspend fun getReceiptByAppointment(appointmentId: String): Flow<PaymentReceipt?> = flow {
        val receipt: PaymentReceipt? = try {
            val snapshot = crud.subcollectionRef(appointmentId).get()
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

    override suspend fun createReceipt(appointmentId: String, receipt: PaymentReceipt): String =
        crud.addDocument(appointmentId, receipt)

    override suspend fun updateReceiptImageUrl(
        appointmentId: String,
        receiptId: String,
        imageUrl: String
    ) = crud.updateFields(appointmentId, receiptId, "imageUrl" to imageUrl)

    override suspend fun updateReceiptStatus(
        appointmentId: String,
        receiptId: String,
        status: String,
        note: String?,
        reviewedAt: String?,
        reviewedBy: String?,
        rejectionReason: String?
    ) = crud.updateFields(
        appointmentId, receiptId,
        "status" to status,
        "note" to note,
        "reviewedAt" to reviewedAt,
        "reviewedBy" to reviewedBy,
        "rejectionReason" to rejectionReason
    )

    override suspend fun deleteReceipt(appointmentId: String, receiptId: String) =
        crud.deleteDocument(appointmentId, receiptId)
}