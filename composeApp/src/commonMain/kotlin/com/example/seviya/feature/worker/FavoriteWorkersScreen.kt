package com.example.seviya.feature.worker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.seviya.core.designsystem.theme.AppBackgroundAlt
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.ImageBlueSoftAlt
import com.example.seviya.core.designsystem.theme.Inactive
import com.example.seviya.core.designsystem.theme.TextPrimaryAlt
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.WorkerListItemData
import com.example.shared.presentation.favoriteWorkers.FavoriteWorkersUiState
import com.example.shared.presentation.favoriteWorkers.FavoriteWorkersViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.math.round
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoriteWorkersScreen(
    clientId: String,
    selectedCategoryId: String? = null,
    selectedCategoryName: String? = null,
    avatarPainter: Painter? = null,
    onWorkerClick: (String) -> Unit = {},
    onCategoriesClick: () -> Unit = {},
    onBottomServices: () -> Unit = {},
    onBottomMap: () -> Unit = {},
    onBottomSearch: () -> Unit = {},
    onBottomNotifications: () -> Unit = {},
    onBottomMenu: () -> Unit = {},
) {
  val viewModel: FavoriteWorkersViewModel = koinViewModel()
  val state by viewModel.uiState.collectAsState()

  LaunchedEffect(clientId) { viewModel.loadFavoriteWorkers(clientId) }

  FavoriteWorkersContent(
      state = state,
      selectedCategoryId = selectedCategoryId,
      selectedCategoryName = selectedCategoryName,
      avatarPainter = avatarPainter,
      onWorkerClick = onWorkerClick,
      onConfirmRemoveFavorite = { workerId ->
        viewModel.removeFavoriteByWorkerId(clientId = clientId, workerId = workerId)
      },
      onCategoriesClick = onCategoriesClick,
      onBottomServices = onBottomServices,
      onBottomMap = onBottomMap,
      onBottomSearch = onBottomSearch,
      onBottomNotifications = onBottomNotifications,
      onBottomMenu = onBottomMenu,
  )
}

@Composable
private fun FavoriteWorkersContent(
    state: FavoriteWorkersUiState,
    selectedCategoryId: String? = null,
    selectedCategoryName: String? = null,
    avatarPainter: Painter? = null,
    onWorkerClick: (String) -> Unit = {},
    onConfirmRemoveFavorite: (String) -> Unit = {},
    onCategoriesClick: () -> Unit = {},
    onBottomServices: () -> Unit = {},
    onBottomMap: () -> Unit = {},
    onBottomSearch: () -> Unit = {},
    onBottomNotifications: () -> Unit = {},
    onBottomMenu: () -> Unit = {},
) {
  var searchQuery by remember { mutableStateOf("") }
  var workerPendingDelete by remember { mutableStateOf<WorkerListItemData?>(null) }

  val visibleWorkers =
      remember(state.workers, selectedCategoryId, selectedCategoryName, searchQuery) {
        state.workers
            .filter { worker ->
              val matchCategoryId = selectedCategoryId?.let { it in worker.categoryIds } ?: true
              val matchCategoryName =
                  selectedCategoryName?.let { categoryName ->
                    worker.categoryNames.any { it.equals(categoryName, ignoreCase = true) }
                  } ?: true
              val matchSearch = matchesSearch(worker, searchQuery)

              matchCategoryId && matchCategoryName && matchSearch
            }
            .sortedByDescending { it.stars }
      }

  workerPendingDelete?.let { worker ->
    FavoriteDeleteDialog(
        workerName = worker.name.ifBlank { "este trabajador" },
        onDismiss = { workerPendingDelete = null },
        onConfirm = {
          onConfirmRemoveFavorite(worker.workerId)
          workerPendingDelete = null
        },
    )
  }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = BrandBlue,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
  ) { innerPadding ->
    Column(modifier = Modifier.fillMaxSize().background(BrandBlue).padding(innerPadding)) {
      FavoriteWorkersHeader(
          selectedCategoryName = selectedCategoryName,
          onCategoriesClick = onCategoriesClick,
      )

      Surface(
          modifier = Modifier.fillMaxSize().offset(y = (-18).dp),
          color = AppBackgroundAlt,
          shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          FavoriteSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

          when {
            state.isLoading -> {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Cargando favoritos...",
                    color = TextSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                )
              }
            }

            state.errorMessage != null -> {
              Box(
                  modifier = Modifier.fillMaxSize().padding(24.dp),
                  contentAlignment = Alignment.Center,
              ) {
                Text(
                    text = state.errorMessage ?: "Ocurrió un error.",
                    color = BrandRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
              }
            }

            visibleWorkers.isEmpty() -> {
              Box(
                  modifier = Modifier.fillMaxSize().padding(24.dp),
                  contentAlignment = Alignment.Center,
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                      text =
                          if (searchQuery.isNotBlank() || !selectedCategoryName.isNullOrBlank()) {
                            "No se encontraron trabajadores favoritos con esos criterios."
                          } else {
                            "Aún no tienes trabajadores favoritos."
                          },
                      color = TextSecondary,
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Medium,
                      textAlign = TextAlign.Center,
                  )
                }
              }
            }

            else -> {
              LazyVerticalGrid(
                  columns = GridCells.Fixed(2),
                  modifier = Modifier.fillMaxSize(),
                  contentPadding =
                      PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 110.dp),
                  verticalArrangement = Arrangement.spacedBy(16.dp),
                  horizontalArrangement = Arrangement.spacedBy(16.dp),
              ) {
                items(items = visibleWorkers, key = { it.workerId }) { worker ->
                  FavoriteWorkerGridCard(
                      worker = worker,
                      avatarPainter = avatarPainter,
                      onClick = { onWorkerClick(worker.workerId) },
                      onRemoveClick = { workerPendingDelete = worker },
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

private fun matchesSearch(worker: WorkerListItemData, query: String): Boolean {
  if (query.isBlank()) return true
  val normalized = query.trim()

  return worker.name.contains(normalized, ignoreCase = true) ||
      worker.categoryNames.any { it.contains(normalized, ignoreCase = true) }
}

@Composable
private fun FavoriteSearchBar(query: String, onQueryChange: (String) -> Unit) {
  OutlinedTextField(
      value = query,
      onValueChange = onQueryChange,
      modifier =
          Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 14.dp),
      singleLine = true,
      shape = RoundedCornerShape(18.dp),
      placeholder = { Text(text = "Buscar trabajador o categoría", color = TextSecondary) },
  )
}

@Composable
private fun FavoriteDeleteDialog(workerName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = White,
        shadowElevation = 10.dp,
    ) {
      Column(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Box(
            modifier =
                Modifier.size(58.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFBEAEA))
                    .border(width = 1.dp, color = Color(0xFFF6D4D4), shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
          Icon(
              imageVector = TablerIcons.Trash,
              contentDescription = "Eliminar favorito",
              tint = BrandRed,
              modifier = Modifier.size(24.dp),
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Quitar de favoritos",
            color = TextPrimaryAlt,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Se eliminará a $workerName de tu lista de favoritos.",
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Surface(
              modifier =
                  Modifier.weight(1f)
                      .clip(RoundedCornerShape(18.dp))
                      .clickable(onClick = onDismiss),
              color = Color(0xFFF7F8FA),
              border = BorderStroke(1.dp, Color(0xFFE6EAF0)),
          ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
              Text(
                  text = "Cancelar",
                  color = TextPrimaryAlt,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
              )
            }
          }

          Surface(
              modifier =
                  Modifier.weight(1f)
                      .clip(RoundedCornerShape(18.dp))
                      .clickable(onClick = onConfirm),
              color = BrandRed,
          ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
              Text(
                  text = "Eliminar",
                  color = White,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun FavoriteWorkersHeader(selectedCategoryName: String?, onCategoriesClick: () -> Unit) {
  val subtitle =
      selectedCategoryName?.takeIf { it.isNotBlank() }?.let { "Tus profesionales favoritos en $it" }
          ?: "Revisa los trabajadores que guardaste como favoritos"

  Box(
      modifier =
          Modifier.fillMaxWidth()
              .background(BrandBlue)
              .systemBarsPadding()
              .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 4.dp)) {
      Surface(
          modifier = Modifier.size(72.dp),
          shape = CircleShape,
          color = White.copy(alpha = 0.08f),
      ) {}

      Surface(
          modifier = Modifier.size(38.dp).align(Alignment.BottomStart),
          shape = CircleShape,
          color = White.copy(alpha = 0.10f),
      ) {}
    }

    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        FavoriteServiYaHeaderBadge()

        Surface(
            modifier = Modifier.size(52.dp).clickable(onClick = onCategoriesClick),
            shape = RoundedCornerShape(18.dp),
            color = White.copy(alpha = 0.13f),
            border = BorderStroke(1.dp, White.copy(alpha = 0.18f)),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = TablerIcons.Briefcase,
                contentDescription = "Categorías de servicios",
                tint = White,
                modifier = Modifier.size(22.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      Text(
          text = "Trabajadores Favoritos",
          color = White,
          fontSize = 24.sp,
          lineHeight = 30.sp,
          fontWeight = FontWeight.ExtraBold,
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
          text = subtitle,
          color = White.copy(alpha = 0.82f),
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium,
      )
    }
  }
}

@Composable
private fun FavoriteServiYaHeaderBadge() {
  Surface(
      shape = RoundedCornerShape(999.dp),
      color = White.copy(alpha = 0.13f),
      border = BorderStroke(1.dp, White.copy(alpha = 0.18f)),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = "Servi", color = White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)

      Text(text = "Ya", color = BrandRed, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
    }
  }
}

@Composable
private fun FavoriteWorkerGridCard(
    worker: WorkerListItemData,
    avatarPainter: Painter?,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
  val provinceText = worker.province.ifBlank { "Ubicación no disponible" }

  Surface(
      modifier =
          Modifier.fillMaxWidth()
              .height(260.dp)
              .shadow(4.dp, RoundedCornerShape(18.dp), clip = false)
              .clickable(onClick = onClick),
      shape = RoundedCornerShape(18.dp),
      color = White,
      border = BorderStroke(1.dp, Color(0xFFE4ECF7)),
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Box(
          modifier =
              Modifier.align(Alignment.TopEnd)
                  .padding(top = 10.dp, end = 10.dp)
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(BrandRed.copy(alpha = 0.12f))
                  .border(width = 1.dp, color = BrandRed.copy(alpha = 0.22f), shape = CircleShape)
                  .clickable(onClick = onRemoveClick),
          contentAlignment = Alignment.Center,
      ) {
        Icon(
            imageVector = TablerIcons.Trash,
            contentDescription = "Eliminar favorito",
            tint = BrandRed,
            modifier = Modifier.size(18.dp),
        )
      }

      Column(
          modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
      ) {
        FavoriteWorkerCircularPhoto(
            imageUrl = worker.profilePictureLink,
            avatarPainter = avatarPainter,
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = worker.name.ifBlank { "Profesional" },
            color = TextPrimaryAlt,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(6.dp))

        FavoriteWorkerProvinceInline(province = provinceText)

        Spacer(modifier = Modifier.height(8.dp))

        FavoriteWorkerRatingInline(rating = worker.stars)

        Spacer(modifier = Modifier.height(10.dp))

        FavoriteWorkerPriceText(price = worker.startingPrice)
      }
    }
  }
}

@Composable
private fun FavoriteWorkerProvinceInline(province: String) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
    Icon(
        imageVector = TablerIcons.MapPin,
        contentDescription = null,
        tint = Inactive,
        modifier = Modifier.size(14.dp),
    )

    Spacer(modifier = Modifier.width(4.dp))

    Text(
        text = province,
        color = TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun FavoriteWorkerRatingInline(rating: Double) {
  val roundedRating = remember(rating) { round(rating * 10.0) / 10.0 }

  Text(
      text = "⭐ $roundedRating",
      color = TextPrimaryAlt,
      fontSize = 13.sp,
      fontWeight = FontWeight.SemiBold,
  )
}

@Composable
private fun FavoriteWorkerPriceText(price: Double) {
  val text =
      if (price > 0.0) {
        "Desde ₡${price.toInt()}"
      } else {
        "Precio a consultar"
      }

  Text(
      text = text,
      color = BrandBlue,
      fontSize = 13.sp,
      fontWeight = FontWeight.ExtraBold,
      textAlign = TextAlign.Center,
  )
}

@Composable
private fun FavoriteWorkerCircularPhoto(imageUrl: String?, avatarPainter: Painter?) {
  Surface(
      modifier = Modifier.size(78.dp),
      shape = CircleShape,
      color = ImageBlueSoftAlt,
      border = BorderStroke(1.dp, Color(0xFFDCE8F8)),
  ) {
    Box(contentAlignment = Alignment.Center) {
      when {
        !imageUrl.isNullOrBlank() -> {
          KamelImage(
              resource = asyncPainterResource(imageUrl),
              contentDescription = "Foto del trabajador",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop,
          )
        }

        avatarPainter != null -> {
          Image(
              painter = avatarPainter,
              contentDescription = "Avatar",
              modifier = Modifier.fillMaxSize().padding(14.dp),
              contentScale = ContentScale.Fit,
          )
        }

        else -> {
          Text(text = "SV", color = BrandBlue, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
      }
    }
  }
}
