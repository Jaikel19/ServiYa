package com.example.seviya.feature.categories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.seviya.core.designsystem.theme.AppBackgroundAlt
import com.example.seviya.core.designsystem.theme.BackgroundTopBlue
import com.example.seviya.core.designsystem.theme.BorderSoftAlt
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.TextBluePrimary
import com.example.seviya.core.designsystem.theme.White
import com.example.seviya.feature.categories.components.CategoriesHeader
import com.example.seviya.feature.categories.components.CategoriesMenuOverlay
import com.example.seviya.feature.categories.components.CategoryCard
import com.example.seviya.feature.categories.components.ContinueButtonBar
import com.example.shared.domain.entity.Category
import com.example.shared.presentation.categories.CategoriesUiState
import com.example.shared.presentation.categories.CategoriesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoriesCatalogScreen(
    selectedCategoryId: String?,
    onGoServices: () -> Unit,
    onGoMap: () -> Unit,
    onGoSearch: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoAgenda: () -> Unit,
    onGoProfile: () -> Unit,
    onGoConfiguration: () -> Unit,
    onGoMessages: () -> Unit,
    onGoDashboard: () -> Unit,
    onGoSettings: () -> Unit,
    onCategoryClick: (Category) -> Unit = {},
    onContinueWithSelectedCategory: () -> Unit,
) {
  val viewModel: CategoriesViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsState()

  CategoriesCatalogContent(
      uiState = uiState,
      selectedCategoryId = selectedCategoryId,
      onGoServices = onGoServices,
      onGoMap = onGoMap,
      onGoSearch = onGoSearch,
      onGoAlerts = onGoAlerts,
      onGoAgenda = onGoAgenda,
      onGoProfile = onGoProfile,
      onGoConfiguration = onGoConfiguration,
      onGoMessages = onGoMessages,
      onGoDashboard = onGoDashboard,
      onGoSettings = onGoSettings,
      onCategoryClick = onCategoryClick,
      onContinueWithSelectedCategory = onContinueWithSelectedCategory,
  )
}

@Composable
private fun CategoriesCatalogContent(
    uiState: CategoriesUiState,
    selectedCategoryId: String?,
    onGoServices: () -> Unit,
    onGoMap: () -> Unit,
    onGoSearch: () -> Unit,
    onGoAlerts: () -> Unit,
    onGoAgenda: () -> Unit,
    onGoProfile: () -> Unit,
    onGoConfiguration: () -> Unit,
    onGoMessages: () -> Unit,
    onGoDashboard: () -> Unit,
    onGoSettings: () -> Unit,
    onCategoryClick: (Category) -> Unit = {},
    onContinueWithSelectedCategory: () -> Unit,
) {
  val menuExpanded = remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(containerColor = AppBackgroundAlt, contentWindowInsets = WindowInsets(0, 0, 0, 0)) {
        padding ->
      Box(
          modifier =
              Modifier.fillMaxSize()
                  .background(
                      Brush.verticalGradient(colors = listOf(BackgroundTopBlue, AppBackgroundAlt))
                  )
                  .padding(padding)
      ) {
        CategoriesCatalogBody(
            uiState = uiState,
            selectedCategoryId = selectedCategoryId,
            onCategoryClick = onCategoryClick,
            onContinueWithSelectedCategory = onContinueWithSelectedCategory,
        )
      }
    }

    CategoriesMenuOverlay(
        visible = menuExpanded.value,
        onDismiss = { menuExpanded.value = false },
        onGoAgenda = {
          menuExpanded.value = false
          onGoAgenda()
        },
        onGoProfile = {
          menuExpanded.value = false
          onGoProfile()
        },
        onGoConfiguration = {
          menuExpanded.value = false
          onGoConfiguration()
        },
        onGoMessages = {
          menuExpanded.value = false
          onGoMessages()
        },
        onGoDashboard = {
          menuExpanded.value = false
          onGoDashboard()
        },
        onGoSettings = {
          menuExpanded.value = false
          onGoSettings()
        },
    )
  }
}

@Composable
private fun CategoriesCatalogBody(
    uiState: CategoriesUiState,
    selectedCategoryId: String?,
    onCategoryClick: (Category) -> Unit,
    onContinueWithSelectedCategory: () -> Unit,
) {
  val errorMessage = uiState.errorMessage

  Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
      CategoriesHeader()

      when {
        uiState.isLoading -> LoadingState()
        errorMessage != null && uiState.categories.isEmpty() -> ErrorState(message = errorMessage)
        else ->
            CategoriesGrid(
                categories = uiState.categories,
                selectedCategoryId = selectedCategoryId,
                onCategoryClick = onCategoryClick,
            )
      }
    }

    if (!uiState.isLoading && uiState.categories.isNotEmpty()) {
      ContinueButtonBar(
          enabled = !selectedCategoryId.isNullOrBlank(),
          onClick = onContinueWithSelectedCategory,
          modifier =
              Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 16.dp),
      )
    }
  }
}

@Composable
private fun LoadingState() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator(color = BrandBlue, strokeWidth = 3.5.dp)
  }
}

@Composable
private fun ErrorState(message: String) {
  Box(
      modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
      contentAlignment = Alignment.Center,
  ) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = White,
        border = BorderStroke(1.dp, BorderSoftAlt),
        shadowElevation = 6.dp,
    ) {
      Text(
          text = message,
          modifier = Modifier.padding(20.dp),
          style = MaterialTheme.typography.bodyLarge,
          color = TextBluePrimary,
          textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
private fun CategoriesGrid(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategoryClick: (Category) -> Unit,
) {
  LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 180.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
      horizontalArrangement = Arrangement.spacedBy(18.dp),
  ) {
    itemsIndexed(items = categories, key = { _, item -> item.id }) { index, category ->
      CategoryCard(
          category = category,
          index = index,
          selected = category.id == selectedCategoryId,
          onClick = { onCategoryClick(category) },
      )
    }
  }
}
