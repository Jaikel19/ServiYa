package com.example.seviya.feature.landing

// ✅ Tabler Icons (Compose Icons)
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.AvatarBlueSoft
import com.example.seviya.core.designsystem.theme.BorderUltraSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandBlueAlt
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.SoftBlueSurface
import com.example.seviya.core.designsystem.theme.SoftBlueSurfaceAlt
import com.example.seviya.core.designsystem.theme.SubtitleOnBlue
import com.example.seviya.core.designsystem.theme.TextPrimary
import com.example.seviya.core.designsystem.theme.TextSecondary
import com.example.seviya.core.designsystem.theme.White
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import kotlinx.coroutines.delay

private enum class RolesTab {
  HOME,
  LOGIN,
  REGISTER,
}

private enum class SelectedRole {
  CLIENT,
  WORKER,
}

/* ----------------------------- HELPERS ANIM ----------------------------- */

private data class RoleEnterAnim(val alpha: Float, val offsetY: Dp, val scale: Float)

@Composable
private fun rememberRoleEnterAnim(delayMs: Int, distance: Dp = 18.dp): RoleEnterAnim {
  var show by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    delay(delayMs.toLong())
    show = true
  }

  val alpha by
      animateFloatAsState(
          targetValue = if (show) 1f else 0f,
          animationSpec = tween(650, easing = FastOutSlowInEasing),
          label = "enter_alpha",
      )

  val offsetY by
      animateDpAsState(
          targetValue = if (show) 0.dp else distance,
          animationSpec = tween(650, easing = FastOutSlowInEasing),
          label = "enter_offset",
      )

  val scale by
      animateFloatAsState(
          targetValue = if (show) 1f else 0.985f,
          animationSpec = tween(650, easing = FastOutSlowInEasing),
          label = "enter_scale",
      )

  return RoleEnterAnim(alpha = alpha, offsetY = offsetY, scale = scale)
}

@Composable
private fun RoleAnimatedSection(
    delayMs: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
  val a = rememberRoleEnterAnim(delayMs)
  Box(
      modifier =
          modifier.offset(y = a.offsetY).alpha(a.alpha).graphicsLayer {
            scaleX = a.scale
            scaleY = a.scale
          }
  ) {
    content()
  }
}

private fun Modifier.shimmerOverlay(alpha: Float = 0.16f, durationMs: Int = 1600): Modifier =
    composed {
      val inf = rememberInfiniteTransition(label = "shimmer")
      val x by
          inf.animateFloat(
              initialValue = -1f,
              targetValue = 2f,
              animationSpec =
                  infiniteRepeatable(
                      animation = tween(durationMs, easing = FastOutSlowInEasing),
                      repeatMode = RepeatMode.Restart,
                  ),
              label = "shimmer_x",
          )

      drawWithContent {
        drawContent()
        val w = size.width
        val h = size.height
        val shimmerW = w * 0.55f
        val startX = x * w

        val brush =
            Brush.linearGradient(
                colors = listOf(Color.Transparent, White.copy(alpha = alpha), Color.Transparent),
                start = Offset(startX, 0f),
                end = Offset(startX + shimmerW, h),
            )
        drawRect(brush = brush)
      }
    }

private fun Modifier.bouncyClick(onClick: () -> Unit): Modifier = composed {
  val interaction = remember { MutableInteractionSource() }
  val pressed by interaction.collectIsPressedAsState()

  val s by
      animateFloatAsState(
          targetValue = if (pressed) 0.965f else 1f,
          animationSpec = tween(140, easing = FastOutSlowInEasing),
          label = "press_scale",
      )

  this.graphicsLayer {
        scaleX = s
        scaleY = s
      }
      .clickable(
          interactionSource = interaction,
          indication = LocalIndication.current,
          onClick = onClick,
      )
}

/* ----------------------------- SCREEN ----------------------------- */

@Composable
fun RoleCatalogScreen(onPickClient: () -> Unit, onPickWorker: () -> Unit) {
  var selected by rememberSaveable { mutableStateOf<SelectedRole?>(null) }

  Scaffold(containerColor = White, contentWindowInsets = WindowInsets(0, 0, 0, 0)) { padding ->
    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
      val topH = maxHeight * 0.56f
      val bottomH = maxHeight - topH

      Column(Modifier.fillMaxSize()) {
        RoleHeaderAnimated(height = topH)

        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .height(bottomH)
                    .background(White)
                    .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          RoleAnimatedSection(240) {
            Text(
                text = "Elige tu rol",
                style =
                    MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
          }

          Spacer(Modifier.height(8.dp))

          RoleAnimatedSection(300) {
            Text(
                text = "Selecciona cómo usarás la plataforma para\ncontinuar",
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary,
                        lineHeight = 22.sp,
                    ),
                textAlign = TextAlign.Center,
            )
          }

          Spacer(Modifier.height(18.dp))

          RoleAnimatedSection(360) {
            Row(
                modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
              RolePickCardAnimated(
                  title = "Cliente",
                  subtitle = "Busco servicios",
                  icon = TablerIcons.User,
                  selected = selected == SelectedRole.CLIENT,
                  modifier = Modifier.weight(1f),
              ) {
                selected = SelectedRole.CLIENT
                onPickClient()
              }

              RolePickCardAnimated(
                  title = "Trabajador",
                  subtitle = "Ofrezco servicios",
                  icon = TablerIcons.Briefcase,
                  selected = selected == SelectedRole.WORKER,
                  modifier = Modifier.weight(1f),
              ) {
                selected = SelectedRole.WORKER
                onPickWorker()
              }
            }
          }
        }
      }
    }
  }
}

/* ----------------------------- HEADER CON MUCHAS ANIMACIONES ----------------------------- */

@Composable
private fun RoleHeaderAnimated(height: Dp) {
  val inf = rememberInfiniteTransition(label = "header_inf")

  val gradT by
      inf.animateFloat(
          initialValue = 0f,
          targetValue = 1f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(9000, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "gradT",
      )

  val glowA by
      inf.animateFloat(
          initialValue = 0.10f,
          targetValue = 0.18f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(2400, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "glowA",
      )

  val glowB by
      inf.animateFloat(
          initialValue = 0.08f,
          targetValue = 0.15f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(2800, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "glowB",
      )

  val floatY by
      inf.animateFloat(
          initialValue = 0f,
          targetValue = 10f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(2600, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "floatY",
      )

  val headerBrush =
      Brush.linearGradient(
          colors = listOf(BrandBlue, BrandBlueAlt),
          start = Offset(0f, 0f),
          end = Offset(1000f + 400f * gradT, 1300f - 250f * gradT),
      )

  Box(modifier = Modifier.fillMaxWidth().height(height).background(headerBrush)) {
    Box(
        modifier =
            Modifier.size(260.dp)
                .offset((-110).dp, (-110).dp)
                .background(BrandRed.copy(alpha = glowA), CircleShape)
    )

    Box(
        modifier =
            Modifier.size(200.dp)
                .offset(x = 260.dp, y = 210.dp)
                .background(SubtitleOnBlue.copy(alpha = glowB), CircleShape)
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 34.dp).padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      RoleAnimatedSection(0) { LogoPillCompact() }

      Spacer(Modifier.height(14.dp))

      RoleAnimatedSection(80) {
        Text(
            text = "Conectamos soluciones con\nnecesidades de forma segura",
            color = White,
            textAlign = TextAlign.Center,
            style =
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp,
                ),
        )
      }

      Spacer(Modifier.height(14.dp))

      Column(
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.widthIn(max = 320.dp),
      ) {
        RoleAnimatedSection(140) {
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FeatureGlassTileSmall(
                icon = TablerIcons.Search,
                label = "BUSCA",
                modifier = Modifier.weight(1f),
                float = floatY * 0.30f,
            )
            FeatureGlassTileSmall(
                icon = TablerIcons.Briefcase,
                label = "OFRECE",
                modifier = Modifier.weight(1f),
                float = floatY * 0.20f,
            )
          }
        }

        RoleAnimatedSection(200) {
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FeatureGlassTileSmall(
                icon = TablerIcons.ShieldCheck,
                label = "CONFÍA",
                modifier = Modifier.weight(1f),
                float = floatY * 0.24f,
            )
            FeatureGlassTileSmall(
                icon = TablerIcons.Coin,
                label = "GANA",
                modifier = Modifier.weight(1f),
                float = floatY * 0.16f,
            )
          }
        }
      }
    }

    Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(92.dp)) {
      PrettyWaveAnimated(modifier = Modifier.fillMaxSize(), baseColor = White)
    }
  }
}

/* ---------------- PIEZAS UI ---------------- */

@Composable
private fun LogoPillCompact() {
  Card(
      shape = RoundedCornerShape(999.dp),
      colors = CardDefaults.cardColors(containerColor = White),
      elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
      modifier =
          Modifier.clip(RoundedCornerShape(999.dp))
              .shimmerOverlay(alpha = 0.10f, durationMs = 2300),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
          modifier =
              Modifier.size(28.dp).clip(CircleShape).background(BrandRed.copy(alpha = 0.14f)),
          contentAlignment = Alignment.Center,
      ) {
        Icon(
            imageVector = TablerIcons.MapPin,
            contentDescription = null,
            tint = BrandRed,
            modifier = Modifier.size(16.dp),
        )
      }

      Spacer(Modifier.width(8.dp))

      Text(
          text =
              buildAnnotatedString {
                withStyle(SpanStyle(color = BrandBlue, fontWeight = FontWeight.ExtraBold)) {
                  append("Servi")
                }
                withStyle(SpanStyle(color = BrandRed, fontWeight = FontWeight.ExtraBold)) {
                  append("Ya")
                }
              },
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
      )
    }
  }
}

@Composable
private fun FeatureGlassTileSmall(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    float: Float = 0f,
) {
  val inf = rememberInfiniteTransition(label = "tile_inf_$label")
  val pulse by
      inf.animateFloat(
          initialValue = 1f,
          targetValue = 1.05f,
          animationSpec =
              infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "pulse_$label",
      )

  Card(
      modifier =
          modifier
              .height(78.dp)
              .offset(y = (-float).dp)
              .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
              }
              .clip(RoundedCornerShape(20.dp))
              .shimmerOverlay(alpha = 0.12f, durationMs = 2000),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.12f)),
      border = BorderStroke(1.dp, White.copy(alpha = 0.18f)),
  ) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Icon(icon, null, tint = White, modifier = Modifier.size(20.dp))
      Spacer(Modifier.height(6.dp))
      Text(
          label,
          color = White.copy(alpha = 0.78f),
          style =
              MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.6.sp,
              ),
      )
    }
  }
}

@Composable
private fun RolePickCardAnimated(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
  val borderColor by
      animateColorAsState(
          targetValue = if (selected) BrandBlueAlt else BorderUltraSoft,
          animationSpec = tween(250, easing = FastOutSlowInEasing),
          label = "border",
      )

  val bgColor by
      animateColorAsState(
          targetValue = if (selected) SoftBlueSurface else White,
          animationSpec = tween(250, easing = FastOutSlowInEasing),
          label = "bg",
      )

  val iconBgColor by
      animateColorAsState(
          targetValue = if (selected) AvatarBlueSoft else SoftBlueSurfaceAlt,
          animationSpec = tween(250, easing = FastOutSlowInEasing),
          label = "icon_bg",
      )

  val inf = rememberInfiniteTransition(label = "role_float_$title")
  val floatY by
      inf.animateFloat(
          initialValue = 0f,
          targetValue = if (selected) 8f else 0f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(1600, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse,
              ),
          label = "float",
      )

  Card(
      modifier =
          modifier
              .height(210.dp)
              .offset(y = (-floatY).dp)
              .clip(RoundedCornerShape(32.dp))
              .bouncyClick(onClick),
      shape = RoundedCornerShape(32.dp),
      colors = CardDefaults.cardColors(containerColor = bgColor),
      elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 10.dp else 6.dp),
      border = BorderStroke(2.dp, borderColor),
  ) {
    Column(
        modifier = Modifier.fillMaxSize().padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      Box(
          modifier =
              Modifier.size(64.dp)
                  .clip(RoundedCornerShape(22.dp))
                  .background(iconBgColor)
                  .shimmerOverlay(alpha = if (selected) 0.14f else 0.08f, durationMs = 2200),
          contentAlignment = Alignment.Center,
      ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = BrandBlue,
            modifier = Modifier.size(28.dp),
        )
      }

      Spacer(Modifier.height(16.dp))

      Text(
          text = title,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
          color = TextPrimary,
      )

      Spacer(Modifier.height(6.dp))

      Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
          textAlign = TextAlign.Center,
      )
    }
  }
}

/* ---------------- WAVE ANIMADA ---------------- */

@Composable
private fun PrettyWaveAnimated(modifier: Modifier, baseColor: Color) {
  val inf = rememberInfiniteTransition(label = "wave_inf")
  val a by
      inf.animateFloat(
          initialValue = 0.22f,
          targetValue = 0.38f,
          animationSpec =
              infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "wave_hi",
      )

  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    val base =
        Path().apply {
          val y0 = h * 0.52f
          moveTo(0f, y0)

          cubicTo(w * 0.18f, h * 0.38f, w * 0.35f, h * 0.78f, w * 0.55f, h * 0.56f)
          cubicTo(w * 0.72f, h * 0.36f, w * 0.88f, h * 0.76f, w, h * 0.50f)

          lineTo(w, h)
          lineTo(0f, h)
          close()
        }

    val highlight =
        Path().apply {
          val y0 = h * 0.56f
          moveTo(0f, y0)

          cubicTo(w * 0.22f, h * 0.44f, w * 0.38f, h * 0.86f, w * 0.58f, h * 0.62f)
          cubicTo(w * 0.75f, h * 0.44f, w * 0.90f, h * 0.78f, w, h * 0.58f)

          lineTo(w, h)
          lineTo(0f, h)
          close()
        }

    drawPath(path = base, color = Color.Black.copy(alpha = 0.06f), style = Stroke(width = 10f))

    drawPath(path = base, color = baseColor)
    drawPath(path = highlight, color = White.copy(alpha = a))
  }
}
