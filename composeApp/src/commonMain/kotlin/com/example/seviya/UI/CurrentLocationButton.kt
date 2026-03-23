package com.example.seviya.UI

import androidx.compose.runtime.Composable

@Composable
expect fun CurrentLocationButton(
    onLocationObtained: (latitude: Double, longitude: Double, province: String, canton: String, district: String) -> Unit,
    onError: () -> Unit
)
