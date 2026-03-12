package com.example.shared.data.remote.cancellationPolicy

import com.example.shared.domain.entity.CancellationPolicy

interface IRemoteCancellationPolicyDataSource {
    suspend fun getCancellationPolicy(workerId: String): CancellationPolicy?
}