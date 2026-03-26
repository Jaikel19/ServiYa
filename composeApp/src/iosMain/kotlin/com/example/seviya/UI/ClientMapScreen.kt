package com.example.seviya.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.shared.presentation.clientMap.ClientMapViewModel

@Composable
actual fun ClientMapPlatformScreen(
    clientId: String,
    viewModel: ClientMapViewModel,
    onWorkerClick: (workerId: String) -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text("Mapa no disponible en iOS por ahora")
  }
}
