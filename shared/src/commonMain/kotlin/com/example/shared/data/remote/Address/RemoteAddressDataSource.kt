package com.example.shared.data.remote.Address

import com.example.shared.domain.entity.Address
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteAddressDataSource : IRemoteAddressDataSource {

    private val db = Firebase.firestore

    // GET ALL (realtime)
    override suspend fun getAddressesByUser(userId: String): Flow<List<Address>> {
        return db.collection("users")
            .document(userId)
            .collection("addresses")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    try {
                        doc.data<Address>().copy(id = doc.id)
                    } catch (e: Exception) {
                        Address(id = doc.id)
                    }
                }
            }
    }

    // GET ONE
    override suspend fun getAddressById(userId: String, addressId: String): Address? {
        return try {
            val doc = db.collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .get()
            if (doc.exists) doc.data<Address>().copy(id = doc.id) else null
        } catch (e: Exception) {
            println("ERROR getAddressById: ${e.message}")
            null
        }
    }

    // CREATE
    override suspend fun createAddress(userId: String, address: Address): String {
        return try {
            val ref = db.collection("users")
                .document(userId)
                .collection("addresses")
                .add(address)
            ref.id
        } catch (e: Exception) {
            println("ERROR createAddress: ${e.message}")
            ""
        }
    }

    // UPDATE
    override suspend fun updateAddress(userId: String, address: Address) {
        try {
            db.collection("users")
                .document(userId)
                .collection("addresses")
                .document(address.id)
                .set(address)
        } catch (e: Exception) {
            println("ERROR updateAddress: ${e.message}")
        }
    }

    // DELETE
    override suspend fun deleteAddress(userId: String, addressId: String) {
        try {
            db.collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteAddress: ${e.message}")
        }
    }
}