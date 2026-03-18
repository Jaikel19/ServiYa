package com.example.shared.data.remote.OtpAppointment

import com.example.shared.domain.entity.OtpAppointment
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteOtpAppointmentDataSource : IRemoteOtpAppointmentDataSource {

    private val db = Firebase.firestore

    override suspend fun getOtpByAppointment(appointmentId: String): Flow<OtpAppointment?> {
        return db.collection("appointments")
            .document(appointmentId)
            .collection("otp")
            .document("current")
            .snapshots
            .map { doc ->
                try {
                    if (doc.exists) {
                        doc.data<OtpAppointment>().copy(id = doc.id)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    println("ERROR parsing otp: ${e.message}")
                    null
                }
            }
    }

    override suspend fun createOtp(appointmentId: String, otp: OtpAppointment): String {
        return try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("otp")
                .document("current")
                .set(otp.copy(id = "current"))

            "current"
        } catch (e: Exception) {
            println("ERROR createOtp: ${e.message}")
            ""
        }
    }

    override suspend fun markOtpAsUsed(appointmentId: String, otpId: String, usedAt: String) {
        try {
            db.collection("appointments")
                .document(appointmentId)
                .collection("otp")
                .document(otpId)
                .update(
                    "usedAt" to usedAt,
                    "status" to "VERIFIED"
                )
        } catch (e: Exception) {
            println("ERROR markOtpAsUsed: ${e.message}")
        }
    }

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