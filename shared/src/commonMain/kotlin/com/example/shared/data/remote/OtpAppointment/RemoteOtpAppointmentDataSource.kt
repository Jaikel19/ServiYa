package com.example.shared.data.remote.OtpAppointment

import com.example.shared.domain.entity.OtpAppointment
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteOtpAppointmentDataSource : IRemoteOtpAppointmentDataSource {

    private val db = Firebase.firestore

    // GET (realtime) — solo hay un OTP por cita
    override suspend fun getOtpByAppointment(appointmentId: String): Flow<OtpAppointment?> {
        return db.collection("appointments")
            .document(appointmentId)
            .collection("otp")
            .snapshots
            .map { snapshot ->
                snapshot.documents.firstOrNull()?.let { doc ->
                    try {
                        doc.data<OtpAppointment>().copy(id = doc.id)
                    } catch (e: Exception) {
                        println("ERROR parsing otp: ${e.message}")
                        null
                    }
                }
            }
    }

    // CREATE
    override suspend fun createOtp(appointmentId: String, otp: OtpAppointment): String {
        return try {
            val ref = db.collection("appointments")
                .document(appointmentId)
                .collection("otp")
                .add(otp)
            ref.id
        } catch (e: Exception) {
            println("ERROR createOtp: ${e.message}")
            ""
        }
    }

    // MARK AS USED
    override suspend fun markOtpAsUsed(appointmentId: String, otpId: String, usedAt: Long) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("otp")
                .document(otpId)
                .update("usedAt" to usedAt)
        } catch (e: Exception) {
            println("ERROR markOtpAsUsed: ${e.message}")
        }
    }

    // DELETE
    override suspend fun deleteOtp(appointmentId: String, otpId: String) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("otp")
                .document(otpId)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteOtp: ${e.message}")
        }
    }
}