package com.example.shared.data.remote.OtpCode

import com.example.shared.data.remote.FirestoreSubcollectionCrud
import com.example.shared.domain.entity.OtpCode
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow

class RemoteOtpCodeDataSource : IRemoteOtpCodeDataSource {

    private val crud = FirestoreSubcollectionCrud(Firebase.firestore, "users", "otpCodes")

    override suspend fun getOtpCodesByUser(userId: String): Flow<List<OtpCode>> =
        crud.observeList(userId) { doc -> doc.data<OtpCode>().copy(id = doc.id) }

    override suspend fun getOtpCodeById(userId: String, otpId: String): OtpCode? =
        crud.getDocument(userId, otpId) { doc -> doc.data<OtpCode>().copy(id = doc.id) }

    override suspend fun createOtpCode(userId: String, otp: OtpCode): String =
        crud.addDocument(userId, otp)

    override suspend fun markOtpCodeAsUsed(userId: String, otpId: String, usedAt: Long) =
        crud.updateFields(userId, otpId, "usedAt" to usedAt)

    override suspend fun deleteOtpCode(userId: String, otpId: String) =
        crud.deleteDocument(userId, otpId)
}