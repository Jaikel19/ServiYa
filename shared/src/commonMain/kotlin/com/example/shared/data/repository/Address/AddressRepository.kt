package com.example.shared.data.repository.Address

import com.example.shared.data.remote.Address.IRemoteAddressDataSource
import com.example.shared.data.repository.catchEmpty
import com.example.shared.data.repository.safeNullableCall
import com.example.shared.data.repository.safeStringCall
import com.example.shared.data.repository.safeUnitCall
import com.example.shared.domain.entity.Address
import kotlinx.coroutines.flow.Flow

class AddressRepository(private val remote: IRemoteAddressDataSource) : IAddressRepository {

  override suspend fun getAddressesByUser(userId: String): Flow<List<Address>> =
      remote.getAddressesByUser(userId).catchEmpty("fetching addresses")

  override suspend fun getAddressById(userId: String, addressId: String): Address? =
      safeNullableCall("getAddressById") { remote.getAddressById(userId, addressId) }

  override suspend fun createAddress(userId: String, address: Address): String =
      safeStringCall("createAddress") { remote.createAddress(userId, address) }

  override suspend fun updateAddress(userId: String, address: Address) =
      safeUnitCall("updateAddress") { remote.updateAddress(userId, address) }

  override suspend fun deleteAddress(userId: String, addressId: String) =
      safeUnitCall("deleteAddress") { remote.deleteAddress(userId, addressId) }
}
