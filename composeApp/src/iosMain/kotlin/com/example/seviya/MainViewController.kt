package com.example.seviya

import androidx.compose.ui.window.ComposeUIViewController
import com.example.seviya.app.App
import com.example.shared.di.initKoin

fun MainViewController() = ComposeUIViewController(configure = { initKoin() }) { App() }