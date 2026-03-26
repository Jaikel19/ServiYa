package com.example.seviya.feature.requests

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seviya.core.designsystem.theme.AppBackground
import com.example.seviya.core.designsystem.theme.BorderUltraSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.CardSurface

@Composable
fun RequestsScreenScaffold(
    headerContent: @Composable () -> Unit,
    filtersContent: @Composable () -> Unit,
    bodyContent: LazyListScope.() -> Unit,
) {
  LazyColumn(modifier = Modifier.fillMaxSize().background(AppBackground)) {
    item { headerContent() }

    item { Spacer(modifier = Modifier.height(22.dp)) }

    item {
      Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) { filtersContent() }
    }

    item { Spacer(modifier = Modifier.height(12.dp)) }

    bodyContent()

    item { Spacer(modifier = Modifier.height(22.dp)) }
  }
}

@Composable
fun RequestLoadingCard() {
  Surface(
      modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).fillMaxWidth(),
      shape = RoundedCornerShape(28.dp),
      color = CardSurface,
      border = BorderStroke(1.dp, BorderUltraSoft),
      shadowElevation = 2.dp,
  ) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 34.dp),
        contentAlignment = Alignment.Center,
    ) {
      CircularProgressIndicator(color = BrandBlue)
    }
  }
}
