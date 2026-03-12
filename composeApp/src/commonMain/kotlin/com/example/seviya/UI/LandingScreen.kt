package com.example.seviya.UI

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

// ✅ Tabler Icons (compose-icons)
import compose.icons.TablerIcons
import compose.icons.tablericons.*

private object LandingColors {
    val Primary = Color(0xFF004AAD)
    val PrimaryDark = Color(0xFF033B8A)
    val Secondary = Color(0xFFEF4444)
    val Accent = Color(0xFFFBBF24)

    val TextOnBlue = Color(0xFFE6F0FF)
    val SurfaceSoft = Color(0xFFF6F7FB)
    val CardBorder = Color(0xFFEAEAF2)
    val Muted = Color(0xFF6B7280)
}

private val HERO_WAVE_H = 92.dp
private val FOOTER_WAVE_H = 92.dp
private val HERO_EXTRA_BOTTOM = 16.dp      // ✅ header más largo para que no se corten cards
private val FOOTER_EXTRA_BOTTOM = 16.dp     // ✅ footer más largo

@Immutable
private data class MissionCategory(
    val title: String,
    val icon: ImageVector,
    val tint: Color,
    val bg: Color,
)

@Composable
fun LandingScreen(
    onGoToServices: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    val scroll = rememberScrollState()
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        bottomBar = {
            LandingBottomBar(
                onHome = { /* ya estás en Inicio */ },
                onLogin = onLogin,
                onRegister = onRegister
            )
        },
        containerColor = Color.White
    ) { padding ->
        val bottomSafe = padding.calculateBottomPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    start = padding.calculateStartPadding(layoutDirection),
                    end = padding.calculateEndPadding(layoutDirection),
                    bottom = 0.dp
                )
                .verticalScroll(scroll)
        ) {
            AnimatedSection(delayMs = 0) {
                HeaderHero(onPrimary = onRegister, onSecondary = onLogin)
            }

            SectionGap(20.dp)

            AnimatedSection(delayMs = 140) {
                MissionsSection(onSeeMap = onGoToServices, onCategory = { })
            }

            SectionGap(20.dp)

            AnimatedSection(delayMs = 260) {
                HowItWorksSection()
            }

            SectionGap(20.dp)

            AnimatedSection(delayMs = 380) {
                AboutSection()
            }

            AnimatedSection(delayMs = 520) {
                FooterCTA(bottomSafeSpace = bottomSafe)
            }
        }
    }
}

/* ----------------------------- SEPARADORES + ENTRADAS ----------------------------- */

@Composable
private fun SectionGap(h: Dp) = Spacer(Modifier.height(h))

private data class EnterAnim(val alpha: Float, val offsetY: Dp, val scale: Float)

@Composable
private fun rememberEnterAnim(delayMs: Int, distance: Dp = 18.dp): EnterAnim {
    var show by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        show = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (show) 1f else 0f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    val t by animateFloatAsState(
        targetValue = if (show) 0f else 1f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "t"
    )
    val scale by animateFloatAsState(
        targetValue = if (show) 1f else 0.985f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "scale"
    )

    return EnterAnim(alpha = alpha, offsetY = distance * t, scale = scale)
}

@Composable
private fun AnimatedSection(
    delayMs: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val anim = rememberEnterAnim(delayMs)
    Box(
        modifier = modifier
            .offset(y = anim.offsetY)
            .alpha(anim.alpha)
            .scale(anim.scale)
    ) { content() }
}

/* ----------------------------- EFECTOS (SHIMMER / BOUNCE / GLOW) ----------------------------- */

private fun Modifier.shimmerOverlay(alpha: Float = 0.18f, durationMs: Int = 1600): Modifier = composed {
    val inf = rememberInfiniteTransition(label = "shimmer")
    val x by inf.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )

    this.drawWithContent {
        drawContent()
        val w = size.width
        val h = size.height
        val shimmerW = w * 0.55f
        val startX = x * w
        val brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = alpha),
                Color.Transparent
            ),
            start = Offset(startX, 0f),
            end = Offset(startX + shimmerW, h)
        )
        drawRect(brush = brush)
    }
}

private fun Modifier.bouncyClick(onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.965f else 1f,
        animationSpec = tween(140, easing = FastOutSlowInEasing),
        label = "press_scale"
    )

    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(
            interactionSource = interaction,
            indication = LocalIndication.current,
            onClick = onClick
        )
}

/* ----------------------------- HEADER ----------------------------- */

@Composable
private fun HeaderHero(
    onPrimary: () -> Unit,
    onSecondary: () -> Unit
) {
    val inf = rememberInfiniteTransition(label = "hero_inf")

    // ✅ Gradiente animado (se mueve)
    val gradT by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradT"
    )

    // ✅ Glows pulsantes
    val glowA by inf.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowA"
    )
    val glowB by inf.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowB"
    )

    // ✅ Float/parallax para el contenido (muy suave)
    val floatY by inf.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "floatY"
    )

    val headerBrush = Brush.linearGradient(
        colors = listOf(LandingColors.Primary, LandingColors.PrimaryDark),
        start = Offset(0f, 0f),
        end = Offset(900f + 400f * gradT, 1300f - 250f * gradT)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerBrush)
    ) {
        // ✅ Contenido con padding; bottom amplio para que NO se corte nada
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp, bottom = HERO_WAVE_H + HERO_EXTRA_BOTTOM)
        ) {
            AnimatedSection(0) { TopBrandRow() }

            Spacer(Modifier.height(18.dp))

            // ✅ pill con micro “breathing”
            val pillPulse by inf.animateFloat(
                initialValue = 0.10f,
                targetValue = 0.16f,
                animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "pillPulse"
            )
            AnimatedSection(70) { MissionPill(text = "ENCONTRÁ AL EXPERTO IDEAL", bgAlpha = pillPulse) }

            Spacer(Modifier.height(14.dp))

            AnimatedSection(140) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) { append("¡Encontrá al ") }
                        withStyle(SpanStyle(color = LandingColors.Secondary, fontWeight = FontWeight.Black)) { append("experto ideal") }
                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) { append(" para cualquier tarea!") }
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black)
                )
            }

            Spacer(Modifier.height(12.dp))

            AnimatedSection(200) {
                Text(
                    text = "Desde belleza hasta tecnología, conectamos profesionales contigo en segundos.",
                    color = LandingColors.TextOnBlue.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
            }

            Spacer(Modifier.height(18.dp))

            AnimatedSection(270) {
                PrimaryCTAButton(
                    text = "REGISTRARSE AHORA",
                    onClick = onPrimary
                )
            }

            Spacer(Modifier.height(12.dp))

            AnimatedSection(330) {
                SecondaryCTAButton(
                    text = "INICIAR SESIÓN",
                    onClick = onSecondary
                )
            }

            Spacer(Modifier.height(18.dp))

            // ✅ cards flotando (más alto)
            AnimatedSection(400) { FloatingCardsMock(extraFloat = floatY) }
        }

        // ✅ OLA full width + más alta + highlight suave
        WaveDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(HERO_WAVE_H),
            color = Color.White,
            invert = false
        )

        // ✅ glows animados
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 170.dp, y = (-46).dp)
                .background(LandingColors.Secondary.copy(alpha = glowA), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-170).dp, y = 240.dp)
                .background(Color(0xFF67A8FF).copy(alpha = glowB), CircleShape)
        )
    }
}

@Composable
private fun TopBrandRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) { append("Servi") }
                withStyle(SpanStyle(color = LandingColors.Secondary, fontWeight = FontWeight.Black)) { append("Ya") }
            },
            style = MaterialTheme.typography.headlineSmall
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(28.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(LandingColors.Secondary)
            )
            Spacer(Modifier.width(6.dp))
            DotPill()
            Spacer(Modifier.width(6.dp))
            DotPill()
        }
    }
}

@Composable
private fun DotPill() {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.25f))
    )
}

@Composable
private fun MissionPill(text: String, bgAlpha: Float = 0.12f) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = bgAlpha))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(LandingColors.Accent)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold)
        )
    }
}

/* ----------------------------- BUTTONS (shimmer + bounce) ----------------------------- */

@Composable
private fun PrimaryCTAButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LandingColors.Secondary),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 2.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(22.dp))
            .shimmerOverlay(alpha = 0.22f, durationMs = 1500)
            .bouncyClick(onClick) // ✅ bounce al presionar
    ) {
        // ✅ Texto centrado y chevron a la derecha
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = text,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.6f.sp
                )
            )
            Icon(
                imageVector = TablerIcons.ChevronRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun SecondaryCTAButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White.copy(alpha = 0.06f),
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .bouncyClick(onClick)
    ) {
        Text(text, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
    }
}

/* ----------------------------- MOCK CARDS (FLOAT + SHINE) ----------------------------- */

@Composable
private fun FloatingCardsMock(extraFloat: Float = 0f) {
    val inf = rememberInfiniteTransition(label = "cards_inf")

    val a by inf.animateFloat(0f, 10f, infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "a")
    val b by inf.animateFloat(8f, 0f, infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b")
    val c by inf.animateFloat(0f, 8f, infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "c")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .size(width = 150.dp, height = 190.dp)
                .offset(x = (-90).dp, y = (-24).dp - (a + extraFloat * 0.35f).dp),
            icon = TablerIcons.ShieldLock,
            iconTint = Color(0xFFBBD7FF)
        )

        GlassCard(
            modifier = Modifier
                .size(width = 150.dp, height = 190.dp)
                .offset(x = 90.dp, y = (2).dp + (b + extraFloat * 0.25f).dp),
            icon = TablerIcons.Star,
            iconTint = LandingColors.Accent
        )

        GlassCardMain(
            modifier = Modifier
                .size(width = 170.dp, height = 220.dp)
                .offset(y = (-6).dp - (c + extraFloat * 0.30f).dp)
        )
    }
}

@Composable
private fun GlassCard(modifier: Modifier, icon: ImageVector, iconTint: Color) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(26.dp)).shimmerOverlay(alpha = 0.10f, durationMs = 2200),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.12f))
            )
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .height(5.dp)
                    .fillMaxWidth(0.75f)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            )
        }
    }
}

@Composable
private fun GlassCardMain(modifier: Modifier) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(28.dp)).shimmerOverlay(alpha = 0.10f, durationMs = 2400),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.20f))
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LandingColors.Secondary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(TablerIcons.Check, null, tint = LandingColors.Secondary, modifier = Modifier.size(24.dp))
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.65f)
                        .clip(RoundedCornerShape(99.dp))
                        .background(LandingColors.Secondary)
                )
            }

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .height(6.dp)
                    .fillMaxWidth(0.70f)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.10f))
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .fillMaxWidth(0.50f)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            )

            Spacer(Modifier.weight(1f))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6EA8FF).copy(alpha = 0.35f))
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .height(4.dp)
                            .width(50.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color.White.copy(alpha = 0.20f))
                    )
                }

                Icon(TablerIcons.Star, null, tint = LandingColors.Accent, modifier = Modifier.size(18.dp))
            }
        }
    }
}

/* ----------------------------- SERVICES (bounce cards + underline anim) ----------------------------- */

@Composable
private fun MissionsSection(
    onSeeMap: () -> Unit,
    onCategory: (MissionCategory) -> Unit
) {
    val categories = listOf(
        MissionCategory("Belleza", TablerIcons.Scissors, Color(0xFFE91E63), Color(0xFFFFE4EE)),
        MissionCategory("Hogar", TablerIcons.Home2, Color(0xFFFF9800), Color(0xFFFFE9D6)),
        MissionCategory("Tecnología", TablerIcons.Command, Color(0xFF3B82F6), Color(0xFFDDEBFF)),
        MissionCategory("Cocina", TablerIcons.Tools, Color(0xFF22C55E), Color(0xFFDFF7E8)),
    )

    val underline = rememberEnterAnim(delayMs = 0, distance = 0.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(TablerIcons.Apps, null, tint = LandingColors.Secondary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Nuestros\nservicios",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width((54f * underline.alpha).coerceAtLeast(4f).dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(LandingColors.Secondary)
                )
            }

            AssistChip(
                onClick = onSeeMap,
                label = {
                    Text(
                        "VER\nTODOS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        textAlign = TextAlign.Center
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = LandingColors.Secondary.copy(alpha = 0.10f),
                    labelColor = LandingColors.Secondary
                ),
                border = null
            )
        }

        Spacer(Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                CategoryCard(categories[0]) { onCategory(categories[0]) }
                CategoryCard(categories[1]) { onCategory(categories[1]) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                CategoryCard(categories[2]) { onCategory(categories[2]) }
                CategoryCard(categories[3]) { onCategory(categories[3]) }
            }
        }
    }
}

@Composable
private fun RowScope.CategoryCard(category: MissionCategory, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(120.dp)
            .bouncyClick(onClick)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, LandingColors.CardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                Modifier.fillMaxSize().padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(category.bg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(category.icon, null, tint = category.tint, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = category.title,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color(0xFF3B3B46)
                )
            }
        }
    }
}

/* ----------------------------- HOW IT WORKS (tiles colores Amarillo/Rojo/Azul + anim) ----------------------------- */

@Composable
private fun HowItWorksSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LandingColors.SurfaceSoft)
            .padding(horizontal = 20.dp)
            .padding(top = 26.dp, bottom = 26.dp)
    ) {
        Text(
            text = "¿CÓMO FUNCIONA?",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = LandingColors.Secondary
            )
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "¿Cómo funciona?",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic
            )
        )

        Spacer(Modifier.height(18.dp))

        Box {
            Box(
                modifier = Modifier
                    .padding(start = 26.dp, top = 10.dp, bottom = 10.dp)
                    .width(4.dp)
                    .fillMaxHeight()
                    .dashedLine(color = Color(0xFFE1E4EF), strokeWidth = 4.dp, onLeft = true)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StepCard(
                    icon = TablerIcons.Search,
                    iconBg = LandingColors.Accent, // ✅ Amarillo
                    title = "Busca tu servicio",
                    desc = "Navega entre cientos de categorías y encuentra lo que necesitas."
                )
                StepCard(
                    icon = TablerIcons.Message,
                    iconBg = LandingColors.Secondary, // ✅ Rojo
                    title = "Elegí al experto",
                    desc = "Mira calificaciones, trabajos previos y chatea con el profesional."
                )
                StepCard(
                    icon = TablerIcons.CalendarEvent,
                    iconBg = LandingColors.Primary, // ✅ Azul
                    title = "¡Listo!",
                    desc = "Agenda la cita y pagá de forma segura a través de nuestra App."
                )
            }
        }
    }
}

@Composable
private fun StepCard(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    desc: String
) {
    val inf = rememberInfiniteTransition(label = "stepPulse")
    val pulse by inf.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClick { },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(iconBg)
                    .graphicsLayer { scaleX = pulse; scaleY = pulse },
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                Spacer(Modifier.height(2.dp))
                Text(
                    desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LandingColors.Muted,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

/* ----------------------------- ABOUT ----------------------------- */

@Composable
private fun AboutSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .padding(top = 22.dp, bottom = 26.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(LandingColors.Secondary.copy(alpha = 0.10f))
                .border(1.dp, LandingColors.Secondary.copy(alpha = 0.20f), RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Sobre nosotros",
                color = LandingColors.Secondary,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "ServiYa nació con el propósito de conectar personas con talento con quienes necesitan soluciones prácticas en su día a día. Somos una plataforma moderna que une la oferta y la demanda local.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF475569),
                lineHeight = 24.sp
            )
        )

        Spacer(Modifier.height(16.dp))

        MissionCard()
    }
}

@Composable
private fun MissionCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .shimmerOverlay(alpha = 0.10f, durationMs = 2600),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = LandingColors.Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(TablerIcons.Rocket, null, tint = LandingColors.Secondary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    "Nuestra misión",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Hacer tu vida más fácil, apoyando al mismo tiempo a emprendedores locales a crecer y destacar en su comunidad.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFD7E7FF),
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

/* ----------------------------- FOOTER (más largo + shimmer) ----------------------------- */

@Composable
private fun FooterCTA(bottomSafeSpace: Dp) {
    val inf = rememberInfiniteTransition(label = "footer_inf")
    val footerGlow by inf.animateFloat(
        0.08f, 0.14f,
        infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "footerGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LandingColors.Primary)
    ) {
        // ola full width
        WaveDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(FOOTER_WAVE_H),
            color = Color.White,
            invert = true
        )

        // glow suave detrás
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-150).dp, y = 120.dp)
                .background(Color.White.copy(alpha = footerGlow), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = FOOTER_WAVE_H + 14.dp)
        ) {
            Text(
                text = "¿Listo para empezar?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Únete a miles de personas que ya están confiando en los expertos de ServiYa.",
                color = Color(0xFFCFE2FF).copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FooterIconButton(TablerIcons.Globe)
                Spacer(Modifier.width(12.dp))
                FooterIconButton(TablerIcons.Camera)
                Spacer(Modifier.width(12.dp))
                FooterIconButton(TablerIcons.Mail)
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) { append("Servi") }
                    withStyle(SpanStyle(color = LandingColors.Secondary, fontWeight = FontWeight.Black)) { append("Ya") }
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "© 2024 ServiYa App. Made for professionals.",
                color = Color(0xFFCFE2FF).copy(alpha = 0.35f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(bottomSafeSpace + FOOTER_EXTRA_BOTTOM))
        }
    }
}

@Composable
private fun FooterIconButton(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(18.dp))
            .shimmerOverlay(alpha = 0.14f, durationMs = 2200),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

/* ----------------------------- BOTTOM BAR ----------------------------- */

@Composable
private fun LandingBottomBar(
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.92f),
        tonalElevation = 10.dp,
        modifier = Modifier.border(1.dp, LandingColors.CardBorder)
    ) {
        BottomItem("INICIO", TablerIcons.Home, true, LandingColors.Primary, onHome)
        BottomItem("INGRESAR", TablerIcons.Login, false, LandingColors.Primary, onLogin)
        BottomItem("REGISTRAR", TablerIcons.UserPlus, false, LandingColors.Secondary, onRegister)
    }
}

@Composable
private fun RowScope.BottomItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val color = if (active) activeColor else Color(0xFF94A3B8)
    NavigationBarItem(
        selected = active,
        onClick = onClick,
        icon = { Icon(icon, null, tint = color) },
        label = { Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color) },
        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
    )
}

/* ----------------------------- WAVES + HELPERS ----------------------------- */

@Composable
private fun WaveDivider(
    modifier: Modifier,
    color: Color,
    invert: Boolean
) {
    Box(
        modifier = modifier.drawBehind {
            val w = size.width
            val h = size.height

            val path = Path().apply {
                if (!invert) {
                    moveTo(0f, h * 0.62f)
                    cubicTo(w * 0.22f, h * 0.16f, w * 0.62f, h * 0.95f, w, h * 0.52f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                } else {
                    moveTo(0f, h * 0.38f)
                    cubicTo(w * 0.22f, h * 0.94f, w * 0.62f, h * 0.06f, w, h * 0.48f)
                    lineTo(w, 0f)
                    lineTo(0f, 0f)
                    close()
                }
            }
            drawPath(path, color = color)

            // ✅ highlight suave (efecto más pro)
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.20f),
                style = Stroke(width = 6f)
            )
        }
    )
}

private fun Modifier.dashedLine(
    color: Color,
    strokeWidth: Dp,
    onLeft: Boolean = true
): Modifier =
    this.then(
        Modifier.drawBehind {
            val x = if (onLeft) 0f else size.width
            val path = Path().apply {
                moveTo(x, 0f)
                lineTo(x, size.height)
            }
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), 0f)
                )
            )
        }
    )