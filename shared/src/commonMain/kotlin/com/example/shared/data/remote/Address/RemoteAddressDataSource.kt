package com.example.shared.data.remote.Address

import com.example.shared.data.remote.FirestoreSubcollectionCrud
import com.example.shared.domain.entity.Address
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow

class RemoteAddressDataSource : IRemoteAddressDataSource {

  private val crud = FirestoreSubcollectionCrud(Firebase.firestore, "users", "addresses")

  override suspend fun getAddressesByUser(userId: String): Flow<List<Address>> =
      crud.observeList(userId) { doc -> doc.data<Address>().copy(id = doc.id) }

  override suspend fun getAddressById(userId: String, addressId: String): Address? =
      crud.getDocument(userId, addressId) { doc -> doc.data<Address>().copy(id = doc.id) }

  override suspend fun createAddress(userId: String, address: Address): String {
    val id = crud.addDocument(userId, address.copy(id = ""))
    if (id.isNotEmpty()) {
      crud.setDocument(userId, id, address.copy(id = id))
    }
    return id
  }

  override suspend fun updateAddress(userId: String, address: Address) =
      crud.setDocument(userId, address.id, address)

  override suspend fun deleteAddress(userId: String, addressId: String) =
      crud.deleteDocument(userId, addressId)
}
