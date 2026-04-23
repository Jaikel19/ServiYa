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
            if (!doc.exists) {
              println("OTP REMOTE DEBUG 1: doc no existe para $appointmentId")
              return@map null
            }

            val code = doc.get<String>("code") ?: ""
            val purpose = doc.get<String>("purpose") ?: "start"
            val codeHash = doc.get<String>("codeHash") ?: ""
            val createdAt = doc.get<String>("createdAt") ?: ""
            val expiresAt = doc.get<String>("expiresAt") ?: ""
            val usedAt = doc.get<String?>("usedAt")
            val status = doc.get<String>("status") ?: "GENERATED"

            println("OTP REMOTE DEBUG 2: raw code = '$code'")
            println("OTP REMOTE DEBUG 3: raw purpose = '$purpose'")
            println("OTP REMOTE DEBUG 4: raw codeHash = '$codeHash'")
            println("OTP REMOTE DEBUG 5: raw createdAt = '$createdAt'")
            println("OTP REMOTE DEBUG 6: raw expiresAt = '$expiresAt'")
            println("OTP REMOTE DEBUG 7: raw usedAt = '$usedAt'")
            println("OTP REMOTE DEBUG 8: raw status = '$status'")

            OtpAppointment(
                id = doc.id,
                purpose = purpose,
                code = code,
                codeHash = codeHash,
                createdAt = createdAt,
                expiresAt = expiresAt,
                usedAt = usedAt,
                status = status,
            )
          } catch (e: Exception) {
            println("OTP REMOTE DEBUG ERROR: ${e.message}")
            e.printStackTrace()
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
          .update("usedAt" to usedAt, "status" to "VERIFIED")
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
