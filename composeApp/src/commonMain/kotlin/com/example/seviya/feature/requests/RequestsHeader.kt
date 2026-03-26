package com.example.seviya.feature.requests

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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.TextPrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft

@Composable
fun RequestsTopHeader(onBack: () -> Unit) {
  val infiniteTransition = rememberInfiniteTransition(label = "requests_header")

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
          text = "SOLICITUDES",
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
fun RequestsSectionHeader(title: String, subtitle: String) {
  Column {
    Text(
        text = title,
        style =
            MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            ),
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(text = subtitle, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
  }
}
