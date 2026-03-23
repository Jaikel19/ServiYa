package com.example.seviya.UI

import androidx.compose.runtime.Composable

// TODO: iOS location implementation pending
@Composable
actual fun CurrentLocationButton(
    onLocationObtained: (latitude: Double, longitude: Double, province: String, canton: String, district: String) -> Unit,
    onError: () -> Unit
) {
    // No-op for iOS — implementation will be added in a future sprint
}
