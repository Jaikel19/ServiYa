package com.example.seviya.UI

import androidx.compose.runtime.Composable
import com.example.shared.presentation.clientMap.ClientMapViewModel

@Composable
expect fun ClientMapScreen(
    clientId: String,
    viewModel: ClientMapViewModel,
    onWorkerClick: (workerId: String) -> Unit
)