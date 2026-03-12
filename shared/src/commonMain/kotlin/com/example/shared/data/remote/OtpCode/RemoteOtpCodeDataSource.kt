package com.example.shared.data.remote.OtpCode


import com.example.shared.domain.entity.OtpCode
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteOtpCodeDataSource : IRemoteOtpCodeDataSource {

    private val db = Firebase.firestore

    // GET ALL (realtime)
    override suspend fun getOtpCodesByUser(userId: String): Flow<List<OtpCode>> {
        return db.collection("users")
            .document(userId)
            .collection("otpCodes")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    try {
                        doc.data<OtpCode>().copy(id = doc.id)
                    } catch (e: Exception) {
                        OtpCode(id = doc.id)
                    }
                }
            }
    }

    // GET ONE
    override suspend fun getOtpCodeById(userId: String, otpId: String): OtpCode? {
        return try {
            val doc = db.collection("users")
                .document(userId)
                .collection("otpCodes")
                .document(otpId)
                .get()
            if (doc.exists) doc.data<OtpCode>().copy(id = doc.id) else null
        } catch (e: Exception) {
            println("ERROR getOtpCodeById: ${e.message}")
            null
        }
    }

    // CREATE
    override suspend fun createOtpCode(userId: String, otp: OtpCode): String {
        return try {
            val ref = db.collection("users")
                .document(userId)
                .collection("otpCodes")
                .add(otp)
            ref.id
        } catch (e: Exception) {
            println("ERROR createOtpCode: ${e.message}")
            ""
        }
    }

    // MARK AS USED
    override suspend fun markOtpCodeAsUsed(userId: String, otpId: String, usedAt: Long) {
        try {
            db.collection("users")
                .document(userId)
                .collection("otpCodes")
                .document(otpId)
                .update("usedAt" to usedAt)
        } catch (e: Exception) {
            println("ERROR markOtpCodeAsUsed: ${e.message}")
        }
    }

    // DELETE
    override suspend fun deleteOtpCode(userId: String, otpId: String) {
        try {
            db.collection("users")
                .document(userId)
                .collection("otpCodes")
                .document(otpId)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteOtpCode: ${e.message}")
        }
    }
}