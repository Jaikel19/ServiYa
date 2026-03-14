package com.example.shared.data.remote.Address

import com.example.shared.domain.entity.Address
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteAddressDataSource : IRemoteAddressDataSource {

    private val db = Firebase.firestore

    private val usersCollection = "users"
    private val addressesSubcollection = "addresses"

    override suspend fun getAddressesByUser(userId: String): Flow<List<Address>> {
        return db.collection(usersCollection)
            .document(userId)
            .collection(addressesSubcollection)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.data<Address>().copy(id = doc.id)
                    } catch (e: Exception) {
                        println("ERROR parsing address ${doc.id}: ${e.message}")
                        null
                    }
                }
            }
    }

    override suspend fun getAddressById(userId: String, addressId: String): Address? {
        return try {
            val doc = db.collection(usersCollection)
                .document(userId)
                .collection(addressesSubcollection)
                .document(addressId)
                .get()

            if (doc.exists) {
                doc.data<Address>().copy(id = doc.id)
            } else {
                null
            }
        } catch (e: Exception) {
            println("ERROR getAddressById: ${e.message}")
            null
        }
    }

    override suspend fun createAddress(userId: String, address: Address): String {
        return try {
            val ref = db.collection(usersCollection)
                .document(userId)
                .collection(addressesSubcollection)
                .add(address.copy(id = ""))

            ref.set(address.copy(id = ref.id))
            ref.id
        } catch (e: Exception) {
            println("ERROR createAddress: ${e.message}")
            ""
        }
    }

    override suspend fun updateAddress(userId: String, address: Address) {
        try {
            db.collection(usersCollection)
                .document(userId)
                .collection(addressesSubcollection)
                .document(address.id)
                .set(address)
        } catch (e: Exception) {
            println("ERROR updateAddress: ${e.message}")
        }
    }

    override suspend fun deleteAddress(userId: String, addressId: String) {
        try {
            db.collection(usersCollection)
                .document(userId)
                .collection(addressesSubcollection)
                .document(addressId)
                .delete()
        } catch (e: Exception) {
            println("ERROR deleteAddress: ${e.message}")
        }
    }
}