package com.example.seviya.feature.worker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.AppBackground
import com.example.seviya.core.designsystem.theme.AppBackgroundAlt
import com.example.seviya.core.designsystem.theme.BlueGrayText
import com.example.seviya.core.designsystem.theme.BorderSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.InactiveSoft
import com.example.seviya.core.designsystem.theme.SoftBlueSurface
import com.example.seviya.core.designsystem.theme.SoftSurface
import com.example.seviya.core.designsystem.theme.TextBluePrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Category
import com.example.shared.presentation.workerCategories.WorkerCategoriesUiState
import com.example.shared.presentation.workerCategories.WorkerCategoriesViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerCategoriesScreen(workerId: String, onBack: () -> Unit) {
  val viewModel: WorkerCategoriesViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsState()

  LaunchedEffect(workerId) { viewModel.loadData(workerId) }

  WorkerCategoriesContent(
      uiState = uiState,
      onBack = onBack,
      onToggleCategory = { viewModel.toggleCategory(it) },
      onSearchQueryChange = { viewModel.updateSearchQuery(it) },
      onSave = { viewModel.saveCategories(workerId) },
      onClearSaveSuccess = { viewModel.clearSaveSuccess() },
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WorkerCategoriesContent(
    uiState: WorkerCategoriesUiState,
    onBack: () -> Unit,
    onToggleCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSave: () -> Unit,
    onClearSaveSuccess: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.saveSuccess) {
    if (uiState.saveSuccess) {
      snackbarHostState.showSnackbar("Categorías guardadas correctamente")
      onClearSaveSuccess()
    }
  }

  LaunchedEffect(uiState.errorMessage) {
    uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
  }

  val filteredCategories =
      if (uiState.searchQuery.isBlank()) {
        uiState.allCategories
      } else {
        uiState.allCategories.filter { it.name.contains(uiState.searchQuery, ignoreCase = true) }
      }

  val selectedCategories = uiState.allCategories.filter { it.id in uiState.selectedCategoryIds }

  Scaffold(
      containerColor = AppBackgroundAlt,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      snackbarHost = {
        SnackbarHost(snackbarHostState) { data ->
          Snackbar(snackbarData = data, containerColor = BrandBlue, contentColor = White)
        }
      },
      bottomBar = {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AppBackgroundAlt,
            shadowElevation = 8.dp,
        ) {
          Button(
              onClick = onSave,
              enabled = !uiState.isSaving,
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = 20.dp, vertical = 12.dp)
                      .height(54.dp),
              shape = RoundedCornerShape(18.dp),
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = BrandBlue,
                      disabledContainerColor = InactiveSoft.copy(alpha = 0.35f),
                      contentColor = White,
                      disabledContentColor = White.copy(alpha = 0.75f),
                  ),
          ) {
            if (uiState.isSaving) {
              CircularProgressIndicator(
                  color = White,
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(20.dp),
              )
            } else {
              Text(
                  text = "Guardar Categorías",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              )
            }
          }
        }
      },
  ) { innerPadding ->
    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
      WorkerCategoriesHeader(onBack = onBack)

      LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
          verticalArrangement = Arrangement.spacedBy(0.dp),
      ) {
        item {
          SearchBar(query = uiState.searchQuery, onQueryChange = onSearchQueryChange)
          Spacer(modifier = Modifier.height(20.dp))
        }

        if (selectedCategories.isNotEmpty()) {
          item {
            Text(
                text = "SELECCIONADAS",
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                    ),
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              selectedCategories.forEach { category ->
                SelectedChip(name = category.name, onRemove = { onToggleCategory(category.id) })
              }
            }
            Spacer(modifier = Modifier.height(24.dp))
          }
        }

        item {
          Text(
              text = "TODAS LAS CATEGORÍAS",
              style =
                  MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.2.sp,
                  ),
              color = TextSecondary,
          )
          Spacer(modifier = Modifier.height(10.dp))
        }

        when {
          uiState.isLoading -> {
            item {
              Box(
                  modifier = Modifier.fillMaxWidth().height(200.dp),
                  contentAlignment = Alignment.Center,
              ) {
                CircularProgressIndicator(color = BrandBlue, strokeWidth = 3.dp)
              }
            }
          }

          filteredCategories.isEmpty() && uiState.searchQuery.isNotBlank() -> {
            item {
              Box(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                  contentAlignment = Alignment.Center,
              ) {
                Text(
                    text = "Sin resultados para \"${uiState.searchQuery}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )
              }
            }
          }

          else -> {
            items(filteredCategories, key = { it.id }) { category ->
              CategoryRow(
                  category = category,
                  selected = category.id in uiState.selectedCategoryIds,
                  onToggle = { onToggleCategory(category.id) },
              )
              Spacer(modifier = Modifier.height(10.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun WorkerCategoriesHeader(onBack: () -> Unit) {
  val infiniteTransition = rememberInfiniteTransition(label = "worker_categories_header")

  val leftBadgeScale by
      infiniteTransition.animateFloat(
          initialValue = 1f,
          targetValue = 1.035f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(1800, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "left_badge_scale",
      )

  val rightBadgeScale by
      infiniteTransition.animateFloat(
          initialValue = 1f,
          targetValue = 1.03f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(2100, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "right_badge_scale",
      )

  val bubbleOffsetLarge by
      infiniteTransition.animateFloat(
          initialValue = -4f,
          targetValue = 6f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(2600, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "bubble_offset_large",
      )

  val bubbleOffsetSmall by
      infiniteTransition.animateFloat(
          initialValue = 5f,
          targetValue = -5f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(2200, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "bubble_offset_small",
      )

  val bubbleScaleLarge by
      infiniteTransition.animateFloat(
          initialValue = 1f,
          targetValue = 1.08f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(2400, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "bubble_scale_large",
      )

  val bubbleScaleSmall by
      infiniteTransition.animateFloat(
          initialValue = 0.96f,
          targetValue = 1.06f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(2000, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "bubble_scale_small",
      )

  val shimmerOffset by
      infiniteTransition.animateFloat(
          initialValue = -260f,
          targetValue = 620f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(3200, easing = LinearEasing),
                  repeatMode = RepeatMode.Restart,
              ),
          label = "shimmer_offset",
      )

  val arrowFloat by
      infiniteTransition.animateFloat(
          initialValue = 0f,
          targetValue = -2f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(1200, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "arrow_float",
      )

  val entranceVisible = remember { mutableStateOf(false) }

  LaunchedEffect(Unit) { entranceVisible.value = true }

  AnimatedVisibility(
      visible = entranceVisible.value,
      enter =
          fadeIn(animationSpec = tween(500)) +
              slideInVertically(
                  initialOffsetY = { -it / 3 },
                  animationSpec = tween(600, easing = FastOutSlowInEasing),
              ),
  ) {
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                .background(BrandBlue)
    ) {
      Box(
          modifier =
              Modifier.matchParentSize()
                  .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
      ) {
        Box(
            modifier =
                Modifier.fillMaxHeight()
                    .width(140.dp)
                    .offset(x = shimmerOffset.dp)
                    .graphicsLayer {
                      rotationZ = -18f
                      alpha = 0.16f
                    }
                    .background(
                        Brush.linearGradient(
                            colors =
                                listOf(
                                    Color.Transparent,
                                    White.copy(alpha = 0.45f),
                                    Color.Transparent,
                                )
                        )
                    )
        )
      }

      Row(
          modifier =
              Modifier.align(Alignment.TopStart)
                  .padding(start = 20.dp, top = 42.dp)
                  .graphicsLayer {
                    scaleX = leftBadgeScale
                    scaleY = leftBadgeScale
                  }
                  .clip(RoundedCornerShape(999.dp))
                  .background(White.copy(alpha = 0.14f))
                  .border(
                      width = 1.dp,
                      color = White.copy(alpha = 0.16f),
                      shape = RoundedCornerShape(999.dp),
                  )
                  .padding(horizontal = 12.dp, vertical = 9.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Box(
            modifier =
                Modifier.size(28.dp)
                    .graphicsLayer { translationY = arrowFloat }
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.14f))
                    .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
          Icon(
              imageVector = TablerIcons.ArrowLeft,
              contentDescription = "Volver",
              tint = White,
              modifier = Modifier.size(16.dp),
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "Servi", color = White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
          Text(text = "Ya", color = BrandRed, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        }
      }

      Text(
          text = "CONFIGURACIÓN",
          modifier =
              Modifier.align(Alignment.TopEnd)
                  .padding(end = 20.dp, top = 42.dp)
                  .graphicsLayer {
                    scaleX = rightBadgeScale
                    scaleY = rightBadgeScale
                  }
                  .clip(RoundedCornerShape(999.dp))
                  .background(White.copy(alpha = 0.14f))
                  .border(
                      width = 1.dp,
                      color = White.copy(alpha = 0.16f),
                      shape = RoundedCornerShape(999.dp),
                  )
                  .padding(horizontal = 16.dp, vertical = 10.dp),
          color = White,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
      )

      Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)) {
        Box(
            modifier =
                Modifier.size(82.dp)
                    .graphicsLayer {
                      translationY = bubbleOffsetLarge
                      scaleX = bubbleScaleLarge
                      scaleY = bubbleScaleLarge
                    }
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.08f))
        )

        Box(
            modifier =
                Modifier.size(46.dp)
                    .align(Alignment.BottomStart)
                    .graphicsLayer {
                      translationY = bubbleOffsetSmall
                      scaleX = bubbleScaleSmall
                      scaleY = bubbleScaleSmall
                    }
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.10f))
        )
      }
    }
  }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
        text = "BUSCAR CATEGORÍA",
        style =
            MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
            ),
        color = TextSecondary,
    )

    Spacer(modifier = Modifier.height(10.dp))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = White,
        shadowElevation = 6.dp,
        border =
            BorderStroke(
                width = 1.dp,
                color = if (query.isNotBlank()) BrandBlue.copy(alpha = 0.18f) else BorderSoft,
            ),
    ) {
      TextField(
          value = query,
          onValueChange = onQueryChange,
          placeholder = {
            Text(
                text = "Buscar categorías (ej. Plomería)",
                style = MaterialTheme.typography.bodyMedium,
                color = InactiveSoft,
            )
          },
          leadingIcon = {
            Box(
                modifier =
                    Modifier.size(34.dp)
                        .clip(CircleShape)
                        .background(if (query.isNotBlank()) SoftBlueSurface else AppBackground),
                contentAlignment = Alignment.Center,
            ) {
              Icon(
                  imageVector = TablerIcons.Search,
                  contentDescription = null,
                  tint = if (query.isNotBlank()) BrandBlue else InactiveSoft,
                  modifier = Modifier.size(18.dp),
              )
            }
          },
          trailingIcon =
              if (query.isNotBlank()) {
                {
                  Box(
                      modifier =
                          Modifier.size(32.dp).clip(CircleShape).background(SoftSurface).clickable {
                            onQueryChange("")
                          },
                      contentAlignment = Alignment.Center,
                  ) {
                    Icon(
                        imageVector = TablerIcons.X,
                        contentDescription = "Limpiar",
                        tint = BlueGrayText,
                        modifier = Modifier.size(16.dp),
                    )
                  }
                }
              } else null,
          singleLine = true,
          shape = RoundedCornerShape(24.dp),
          colors =
              TextFieldDefaults.colors(
                  focusedContainerColor = Color.Transparent,
                  unfocusedContainerColor = Color.Transparent,
                  disabledContainerColor = Color.Transparent,
                  focusedIndicatorColor = Color.Transparent,
                  unfocusedIndicatorColor = Color.Transparent,
                  disabledIndicatorColor = Color.Transparent,
                  cursorColor = BrandBlue,
                  focusedTextColor = TextBluePrimary,
                  unfocusedTextColor = TextBluePrimary,
              ),
          textStyle =
              MaterialTheme.typography.bodyLarge.copy(
                  color = TextBluePrimary,
                  fontWeight = FontWeight.SemiBold,
              ),
          modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

@Composable
private fun SelectedChip(name: String, onRemove: () -> Unit) {
  Row(
      modifier =
          Modifier.clip(CircleShape)
              .background(BrandBlue)
              .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = name,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    Spacer(modifier = Modifier.width(4.dp))
    Box(
        modifier = Modifier.size(22.dp).clip(CircleShape).clickable(onClick = onRemove),
        contentAlignment = Alignment.Center,
    ) {
      Icon(
          imageVector = TablerIcons.X,
          contentDescription = "Quitar",
          tint = White,
          modifier = Modifier.size(14.dp),
      )
    }
  }
}

@Composable
private fun CategoryRow(category: Category, selected: Boolean, onToggle: () -> Unit) {
  val visual = categoryRowVisual(category.id)

  Surface(
      shape = RoundedCornerShape(20.dp),
      color = White,
      shadowElevation = 1.dp,
      modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle),
  ) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (selected) BrandBlue.copy(alpha = 0.25f) else BorderSoft,
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
          modifier =
              Modifier.size(48.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(visual.iconBackground),
          contentAlignment = Alignment.Center,
      ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = null,
            tint = visual.iconTint,
            modifier = Modifier.size(26.dp),
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Text(
          text = category.name,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = TextBluePrimary,
          modifier = Modifier.weight(1f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )

      Checkbox(
          checked = selected,
          onCheckedChange = { onToggle() },
          colors =
              CheckboxDefaults.colors(
                  checkedColor = BrandBlue,
                  uncheckedColor = InactiveSoft,
                  checkmarkColor = White,
              ),
      )
    }
  }
}

private data class CategoryRowVisual(
    val icon: ImageVector,
    val iconTint: Color,
    val iconBackground: Color,
)

private fun categoryRowVisual(categoryId: String): CategoryRowVisual {
  return when (categoryId.normalizeCatKey()) {
    "automotriz" -> CategoryRowVisual(TablerIcons.Car, Color(0xFFD24166), Color(0xFFFFE0E8))
    "barberia" -> CategoryRowVisual(TablerIcons.Scissors, Color(0xFF5C54C7), Color(0xFFE5E2FF))
    "belleza" -> CategoryRowVisual(TablerIcons.Scissors, Color(0xFFBF4B89), Color(0xFFF9E1EC))
    "construccion-y-remodelacion" ->
        CategoryRowVisual(TablerIcons.Building, Color(0xFFC15739), Color(0xFFFFE5DB))
    "cuidado-y-acompanamiento" ->
        CategoryRowVisual(TablerIcons.User, Color(0xFF7564D6), Color(0xFFEAE5FF))
    "deportes-y-entrenamiento" ->
        CategoryRowVisual(TablerIcons.Apps, Color(0xFF2F8E5A), Color(0xFFDFF5E6))
    "diseno-y-creatividad" ->
        CategoryRowVisual(TablerIcons.Paint, Color(0xFF8347C8), Color(0xFFF0E2FF))
    "educacion" -> CategoryRowVisual(TablerIcons.Briefcase, Color(0xFF3B78BC), Color(0xFFE2F0FF))
    "electricidad" -> CategoryRowVisual(TablerIcons.Plug, Color(0xFFB88900), Color(0xFFFFF4C6))
    "eventos" -> CategoryRowVisual(TablerIcons.CalendarEvent, Color(0xFFC85A4E), Color(0xFFFFE5E0))
    "fotografia-y-video" ->
        CategoryRowVisual(TablerIcons.Camera, Color(0xFFB24A86), Color(0xFFF8E0EC))
    "gastronomia" -> CategoryRowVisual(TablerIcons.Briefcase, Color(0xFFC97528), Color(0xFFFFECD8))
    "hogar" -> CategoryRowVisual(TablerIcons.Building, Color(0xFF4E7691), Color(0xFFE4F1F7))
    "idiomas" -> CategoryRowVisual(TablerIcons.Globe, Color(0xFF248C99), Color(0xFFDFF7FA))
    "jardineria" -> CategoryRowVisual(TablerIcons.Tree, Color(0xFF5F8E2F), Color(0xFFE8F5D4))
    "limpieza" -> CategoryRowVisual(TablerIcons.Brush, Color(0xFF2F855A), Color(0xFFDDF4E6))
    "mascotas" -> CategoryRowVisual(TablerIcons.User, Color(0xFF379A93), Color(0xFFDDF6F3))
    "moda-y-confeccion" ->
        CategoryRowVisual(TablerIcons.Scissors, Color(0xFFBF5686), Color(0xFFF9E2EC))
    "mudanzas-y-transporte" ->
        CategoryRowVisual(TablerIcons.Car, Color(0xFFC9672C), Color(0xFFFFE8D8))
    "musica-y-arte" -> CategoryRowVisual(TablerIcons.Paint, Color(0xFF7852C1), Color(0xFFEAE3FF))
    "salud-y-bienestar" -> CategoryRowVisual(TablerIcons.User, Color(0xFF358C7F), Color(0xFFDDF5F0))
    "tecnologia" -> CategoryRowVisual(TablerIcons.Settings, Color(0xFF4968DE), Color(0xFFE4E9FF))
    "tramites-y-gestiones" ->
        CategoryRowVisual(TablerIcons.Briefcase, Color(0xFF596780), Color(0xFFE8EDF5))
    "plomeria" -> CategoryRowVisual(TablerIcons.Droplet, Color(0xFF138A99), Color(0xFFDDF7FA))
    else -> CategoryRowVisual(TablerIcons.Briefcase, Color(0xFF596780), Color(0xFFE8EDF5))
  }
}

private fun String.normalizeCatKey(): String =
    this.trim()
        .lowercase()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace("ñ", "n")
