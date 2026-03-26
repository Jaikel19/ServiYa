package com.example.shared.data.remote.Address

import com.example.shared.domain.entity.Address
import kotlinx.coroutines.flow.Flow

interface IRemoteAddressDataSource {
  suspend fun getAddressesByUser(userId: String): Flow<List<Address>>

  suspend fun getAddressById(userId: String, addressId: String): Address?

  suspend fun createAddress(userId: String, address: Address): String

  suspend fun updateAddress(userId: String, address: Address)

  suspend fun deleteAddress(userId: String, addressId: String)
}
