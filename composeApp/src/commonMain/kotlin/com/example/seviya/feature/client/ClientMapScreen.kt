package com.example.seviya.feature.client

import androidx.compose.runtime.Composable
import com.example.shared.presentation.clientMap.ClientMapViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientMapRoute(
    clientId: String,
    onWorkerClick: (workerId: String) -> Unit
) {
    val viewModel: ClientMapViewModel = koinViewModel()
    ClientMapScreen(
        clientId = clientId,
        viewModel = viewModel,
        onWorkerClick = onWorkerClick
    )
}

@Composable
expect fun ClientMapScreen(
    clientId: String,
    viewModel: ClientMapViewModel,
    onWorkerClick: (workerId: String) -> Unit
)