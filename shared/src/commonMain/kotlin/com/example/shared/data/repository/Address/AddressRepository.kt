package com.example.shared.data.repository.Address

import com.example.shared.data.remote.Address.IRemoteAddressDataSource
import com.example.shared.domain.entity.Address
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class AddressRepository(
    private val remote: IRemoteAddressDataSource
) : IAddressRepository {

    override suspend fun getAddressesByUser(userId: String): Flow<List<Address>> =
        remote.getAddressesByUser(userId)
            .catch { e ->
                println("ERROR fetching addresses: ${e.message}")
                emit(emptyList())
            }

    override suspend fun getAddressById(userId: String, addressId: String): Address? =
        try {
            remote.getAddressById(userId, addressId)
        } catch (e: Exception) {
            println("ERROR getAddressById: ${e.message}")
            null
        }

    override suspend fun createAddress(userId: String, address: Address): String =
        try {
            remote.createAddress(userId, address)
        } catch (e: Exception) {
            println("ERROR createAddress: ${e.message}")
            ""
        }

    override suspend fun updateAddress(userId: String, address: Address) =
        try {
            remote.updateAddress(userId, address)
        } catch (e: Exception) {
            println("ERROR updateAddress: ${e.message}")
        }

    override suspend fun deleteAddress(userId: String, addressId: String) =
        try {
            remote.deleteAddress(userId, addressId)
        } catch (e: Exception) {
            println("ERROR deleteAddress: ${e.message}")
        }
}