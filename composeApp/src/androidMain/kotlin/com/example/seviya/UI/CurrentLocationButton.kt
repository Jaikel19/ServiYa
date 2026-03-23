package com.example.seviya.UI

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.theme.AvatarBlueSoft
import com.example.seviya.theme.BrandBlue
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

@Composable
actual fun CurrentLocationButton(
    onLocationObtained: (latitude: Double, longitude: Double, province: String, canton: String, district: String) -> Unit,
    onError: () -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                @SuppressLint("MissingPermission")
                val task = fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                task.addOnSuccessListener { location ->
                    if (location != null) {
                        val geocoder = Geocoder(context, Locale("es", "CR"))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                                val address = addresses.firstOrNull()
                                onLocationObtained(
                                    location.latitude,
                                    location.longitude,
                                    address?.adminArea ?: "",
                                    address?.subAdminArea ?: "",
                                    address?.locality ?: ""
                                )
                            }
                        } else {
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            val address = addresses?.firstOrNull()
                            onLocationObtained(
                                location.latitude,
                                location.longitude,
                                address?.adminArea ?: "",
                                address?.subAdminArea ?: "",
                                address?.locality ?: ""
                            )
                        }
                    } else {
                        onError()
                    }
                }
                task.addOnFailureListener {
                    onError()
                }
            } catch (e: SecurityException) {
                onError()
            }
        } else {
            onError()
        }
    }

    Button(
        onClick = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AvatarBlueSoft,
            contentColor = BrandBlue
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = "Usar mi ubicación actual",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
