package com.example.seviya.feature.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.shared.presentation.clientMap.ClientMapViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientMapScreen(clientId: String, onWorkerClick: (workerId: String) -> Unit) {
  val viewModel: ClientMapViewModel = koinViewModel()

  LaunchedEffect(clientId) { viewModel.loadMap(clientId) }

  ClientMapPlatformScreen(clientId = clientId, viewModel = viewModel, onWorkerClick = onWorkerClick)
}

@Composable
expect fun ClientMapPlatformScreen(
    clientId: String,
    viewModel: ClientMapViewModel,
    onWorkerClick: (workerId: String) -> Unit,
)
