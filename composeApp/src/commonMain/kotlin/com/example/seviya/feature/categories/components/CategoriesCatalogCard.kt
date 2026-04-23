package com.example.seviya.feature.categories.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.BorderSoftAlt
import com.example.seviya.core.designsystem.theme.TextBluePrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Category
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronRight
import kotlinx.coroutines.delay

@Composable
internal fun CategoryCard(category: Category, index: Int, selected: Boolean, onClick: () -> Unit) {
  val visuals = categoryVisual(category.id)
  val entered = remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    delay(index * 55L)
    entered.value = true
  }

  val cardAlpha by
      animateFloatAsState(
          targetValue = if (entered.value) 1f else 0f,
          animationSpec = tween(450),
          label = "cardAlpha",
      )
  val cardTranslationY by
      animateFloatAsState(
          targetValue = if (entered.value) 0f else 42f,
          animationSpec = tween(500),
          label = "cardTranslationY",
      )
  val cardScale by
      animateFloatAsState(
          targetValue = if (entered.value) 1f else 0.95f,
          animationSpec = tween(500),
          label = "cardScale",
      )

  val infinite = rememberInfiniteTransition(label = "categoryCard")
  val bubbleScale by
      infinite.animateFloat(
          initialValue = 0.96f,
          targetValue = 1.08f,
          animationSpec =
              infiniteRepeatable(animation = tween(2200), repeatMode = RepeatMode.Reverse),
          label = "bubbleScale",
      )
  val iconFloat by
      infinite.animateFloat(
          initialValue = 0f,
          targetValue = -9f,
          animationSpec =
              infiniteRepeatable(animation = tween(2000), repeatMode = RepeatMode.Reverse),
          label = "iconFloat",
      )

  Card(
      modifier =
          Modifier.fillMaxWidth()
              .height(214.dp)
              .graphicsLayer {
                alpha = cardAlpha
                translationY = cardTranslationY
                scaleX = cardScale
                scaleY = cardScale
              }
              .shadow(elevation = 10.dp, shape = RoundedCornerShape(30.dp), clip = false)
              .clickable(onClick = onClick),
      shape = RoundedCornerShape(30.dp),
      colors = CardDefaults.cardColors(containerColor = if (selected) visuals.cardTint else White),
      border =
          BorderStroke(
              width = if (selected) 2.dp else 1.dp,
              color = if (selected) visuals.accent else BorderSoftAlt,
          ),
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Box(
        modifier =
            Modifier.fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(visuals.cardTint, White)))
    ) {
      Box(
          modifier =
              Modifier.fillMaxWidth()
                  .height(6.dp)
                  .background(
                      Brush.horizontalGradient(
                          colors = listOf(visuals.accent, visuals.accentSecondary)
                      )
                  )
      )

      Box(
          modifier =
              Modifier.align(Alignment.TopEnd)
                  .padding(top = 16.dp, end = 16.dp)
                  .size(42.dp)
                  .graphicsLayer {
                    scaleX = bubbleScale
                    scaleY = bubbleScale
                  }
                  .clip(CircleShape)
                  .background(visuals.accent.copy(alpha = 0.08f))
      )

      Column(
          modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 18.dp),
          horizontalAlignment = Alignment.Start,
          verticalArrangement = Arrangement.SpaceBetween,
      ) {
        Column {
          Box(
              modifier =
                  Modifier.graphicsLayer { translationY = iconFloat }
                      .size(66.dp)
                      .clip(RoundedCornerShape(30.dp))
                      .background(
                          Brush.linearGradient(
                              colors = listOf(visuals.iconPlateStart, visuals.iconPlateEnd)
                          )
                      )
                      .border(
                          width = 1.dp,
                          color = visuals.accent.copy(alpha = 0.10f),
                          shape = RoundedCornerShape(28.dp),
                      ),
              contentAlignment = Alignment.Center,
          ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(visuals.iconInner),
                contentAlignment = Alignment.Center,
            ) {
              Icon(
                  imageVector = visuals.icon,
                  contentDescription = null,
                  tint = visuals.iconTint,
                  modifier = Modifier.size(30.dp),
              )
            }
          }

          Spacer(modifier = Modifier.height(5.dp))

          Text(
              text = category.name,
              style =
                  MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp,
                      lineHeight = 22.sp,
                  ),
              color = TextBluePrimary,
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
              text = "Explora profesionales",
              style =
                  MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Medium,
                      fontSize = 12.sp,
                      lineHeight = 17.sp,
                  ),
              color = TextSecondary,
          )
        }

        Row(
            modifier =
                Modifier.align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        if (selected) visuals.accent.copy(alpha = 0.16f)
                        else visuals.buttonBackground
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
              text = if (selected) "Seleccionada" else "Ver opciones",
              color = visuals.buttonText,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          )

          Spacer(modifier = Modifier.width(6.dp))

          Icon(
              imageVector = TablerIcons.ChevronRight,
              contentDescription = null,
              tint = visuals.buttonText,
              modifier = Modifier.size(16.dp),
          )
        }
      }
    }
  }
}
