package com.example.seviya.UI

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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import compose.icons.TablerIcons
import compose.icons.tablericons.Briefcase
import compose.icons.tablericons.Coin
import compose.icons.tablericons.Home
import compose.icons.tablericons.Login
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Search
import compose.icons.tablericons.ShieldCheck
import compose.icons.tablericons.User
import compose.icons.tablericons.UserPlus
import kotlinx.coroutines.delay

private object RoleAdmissionUI {
    val Blue = Color(0xFF004AAD)
    val Blue2 = Color(0xFF3B82F6)
    val Red = Color(0xFFEF4444)

    val SoftBlue = Color(0xFFEBF4FF)
    val Border = Color(0xFFF1F5F9)
    val Muted = Color(0xFF64748B)
    val Muted2 = Color(0xFF94A3B8)
}

private enum class RolesAdmissionTab { HOME, LOGIN, REGISTER }
private enum class SelectedRoleAdmission { CLIENT, WORKER }

/* ----------------------------- HELPERS ANIM ----------------------------- */

private data class RoleAdmissionEnterAnim(
    val alpha: Float,
    val offsetY: Dp,
    val scale: Float
)

@Composable
private fun rememberRoleAdmissionEnterAnim(
    delayMs: Int,
    distance: Dp = 18.dp
): RoleAdmissionEnterAnim {
    var show by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        show = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (show) 1f else 0f,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "enter_alpha"
    )

    val offsetY by animateDpAsState(
        targetValue = if (show) 0.dp else distance,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "enter_offset"
    )

    val scale by animateFloatAsState(
        targetValue = if (show) 1f else 0.985f,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "enter_scale"
    )

    return RoleAdmissionEnterAnim(alpha = alpha, offsetY = offsetY, scale = scale)
}

@Composable
private fun RoleAdmissionAnimatedSection(
    delayMs: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val a = rememberRoleAdmissionEnterAnim(delayMs)

    Box(
        modifier = modifier
            .offset(y = a.offsetY)
            .alpha(a.alpha)
            .graphicsLayer {
                scaleX = a.scale
                scaleY = a.scale
            }
    ) {
        content()
    }
}

private fun Modifier.shimmerOverlay(
    alpha: Float = 0.16f,
    durationMs: Int = 1600
): Modifier = composed {
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

    drawWithContent {
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

    val s by animateFloatAsState(
        targetValue = if (pressed) 0.965f else 1f,
        animationSpec = tween(140, easing = FastOutSlowInEasing),
        label = "press_scale"
    )

    this
        .graphicsLayer {
            scaleX = s
            scaleY = s
        }
        .clickable(
            interactionSource = interaction,
            indication = LocalIndication.current,
            onClick = onClick
        )
}

/* ----------------------------- SCREEN ----------------------------- */

@Composable
fun RoleAdmissionCatalogScreen(
    onGoHome: () -> Unit,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onPickClient: () -> Unit,
    onPickWorker: () -> Unit
) {
    var selected by rememberSaveable { mutableStateOf<SelectedRoleAdmission?>(null) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            RolesAdmissionBottomBar(
                active = RolesAdmissionTab.LOGIN,
                onHome = onGoHome,
                onLogin = onGoLogin,
                onRegister = onGoRegister
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val topH = maxHeight * 0.56f
            val bottomH = maxHeight - topH

            Column(Modifier.fillMaxSize()) {

                RoleAdmissionHeaderAnimated(height = topH)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bottomH)
                        .background(Color.White)
                        .padding(horizontal = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    RoleAdmissionAnimatedSection(240) {
                        Text(
                            text = "¿Cómo deseas ingresar?",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    RoleAdmissionAnimatedSection(300) {
                        Text(
                            text = "Selecciona el tipo de cuenta con el que\nquieres continuar",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = RoleAdmissionUI.Muted,
                                lineHeight = 22.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    RoleAdmissionAnimatedSection(360) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 420.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            RoleAdmissionPickCardAnimated(
                                title = "Cliente",
                                subtitle = "Ingresar al dashboard cliente",
                                icon = TablerIcons.User,
                                selected = selected == SelectedRoleAdmission.CLIENT,
                                modifier = Modifier.weight(1f)
                            ) {
                                selected = SelectedRoleAdmission.CLIENT
                                onPickClient()
                            }

                            RoleAdmissionPickCardAnimated(
                                title = "Trabajador",
                                subtitle = "Ingresar al dashboard trabajador",
                                icon = TablerIcons.Briefcase,
                                selected = selected == SelectedRoleAdmission.WORKER,
                                modifier = Modifier.weight(1f)
                            ) {
                                selected = SelectedRoleAdmission.WORKER
                                onPickWorker()
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ----------------------------- HEADER ----------------------------- */

@Composable
private fun RoleAdmissionHeaderAnimated(height: Dp) {
    val inf = rememberInfiniteTransition(label = "header_inf")

    val gradT by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradT"
    )

    val glowA by inf.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowA"
    )

    val glowB by inf.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowB"
    )

    val floatY by inf.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    val headerBrush = Brush.linearGradient(
        colors = listOf(RoleAdmissionUI.Blue, RoleAdmissionUI.Blue2),
        start = Offset(0f, 0f),
        end = Offset(1000f + 400f * gradT, 1300f - 250f * gradT)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(headerBrush)
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-110).dp, y = (-110).dp)
                .background(RoleAdmissionUI.Red.copy(alpha = glowA), CircleShape)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 260.dp, y = 210.dp)
                .background(Color.White.copy(alpha = glowB), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 34.dp)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoleAdmissionLogoPillCompact()

            Spacer(Modifier.height(14.dp))

            RoleAdmissionAnimatedSection(80) {
                Text(
                    text = "Conectamos soluciones con\nnecesidades de forma segura",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 28.sp
                    )
                )
            }

            Spacer(Modifier.height(14.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.widthIn(max = 320.dp)
            ) {
                RoleAdmissionAnimatedSection(140) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RoleAdmissionFeatureGlassTileSmall(
                            icon = TablerIcons.Search,
                            label = "BUSCA",
                            modifier = Modifier.weight(1f),
                            float = floatY * 0.30f
                        )
                        RoleAdmissionFeatureGlassTileSmall(
                            icon = TablerIcons.Briefcase,
                            label = "OFRECE",
                            modifier = Modifier.weight(1f),
                            float = floatY * 0.20f
                        )
                    }
                }

                RoleAdmissionAnimatedSection(200) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RoleAdmissionFeatureGlassTileSmall(
                            icon = TablerIcons.ShieldCheck,
                            label = "CONFÍA",
                            modifier = Modifier.weight(1f),
                            float = floatY * 0.24f
                        )
                        RoleAdmissionFeatureGlassTileSmall(
                            icon = TablerIcons.Coin,
                            label = "GANA",
                            modifier = Modifier.weight(1f),
                            float = floatY * 0.16f
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(92.dp)
        ) {
            RoleAdmissionPrettyWaveAnimated(
                modifier = Modifier.fillMaxSize(),
                baseColor = Color.White
            )
        }
    }
}

/* ---------------- PIEZAS UI ---------------- */

@Composable
private fun RoleAdmissionLogoPillCompact() {
    Card(
        shape = RoundedCornerShape(999.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .shimmerOverlay(alpha = 0.10f, durationMs = 2300)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(RoleAdmissionUI.Red.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = TablerIcons.MapPin,
                    contentDescription = null,
                    tint = RoleAdmissionUI.Red,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = RoleAdmissionUI.Blue,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) { append("Servi") }

                    withStyle(
                        SpanStyle(
                            color = RoleAdmissionUI.Red,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) { append("Ya") }
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}

@Composable
private fun RoleAdmissionFeatureGlassTileSmall(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    float: Float = 0f
) {
    val inf = rememberInfiniteTransition(label = "tile_inf_$label")
    val pulse by inf.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_$label"
    )

    Card(
        modifier = modifier
            .height(78.dp)
            .offset(y = (-float).dp)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
            .clip(RoundedCornerShape(20.dp))
            .shimmerOverlay(alpha = 0.12f, durationMs = 2000),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.12f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = label,
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.6.sp
                )
            )
        }
    }
}

@Composable
private fun RoleAdmissionPickCardAnimated(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) RoleAdmissionUI.Blue else RoleAdmissionUI.Border,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "border"
    )

    val bgColor by animateColorAsState(
        targetValue = if (selected) RoleAdmissionUI.SoftBlue.copy(alpha = 0.34f) else Color.White,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "bg"
    )

    val inf = rememberInfiniteTransition(label = "roleAdmission_float_$title")
    val floatY by inf.animateFloat(
        initialValue = 0f,
        targetValue = if (selected) 8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Card(
        modifier = modifier
            .height(210.dp)
            .offset(y = (-floatY).dp)
            .clip(RoundedCornerShape(32.dp))
            .bouncyClick(onClick),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 10.dp else 6.dp
        ),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(RoleAdmissionUI.SoftBlue)
                    .shimmerOverlay(
                        alpha = if (selected) 0.14f else 0.08f,
                        durationMs = 2200
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoleAdmissionUI.Blue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color(0xFF0F172A)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = RoleAdmissionUI.Muted
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

/* ---------------- WAVE ---------------- */

@Composable
private fun RoleAdmissionPrettyWaveAnimated(
    modifier: Modifier,
    baseColor: Color
) {
    val inf = rememberInfiniteTransition(label = "wave_inf")
    val a by inf.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_hi"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val base = Path().apply {
            val y0 = h * 0.52f
            moveTo(0f, y0)

            cubicTo(
                w * 0.18f, h * 0.38f,
                w * 0.35f, h * 0.78f,
                w * 0.55f, h * 0.56f
            )
            cubicTo(
                w * 0.72f, h * 0.36f,
                w * 0.88f, h * 0.76f,
                w, h * 0.50f
            )

            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        val highlight = Path().apply {
            val y0 = h * 0.56f
            moveTo(0f, y0)

            cubicTo(
                w * 0.22f, h * 0.44f,
                w * 0.38f, h * 0.86f,
                w * 0.58f, h * 0.62f
            )
            cubicTo(
                w * 0.75f, h * 0.44f,
                w * 0.90f, h * 0.78f,
                w, h * 0.58f
            )

            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = base,
            color = Color.Black.copy(alpha = 0.06f),
            style = Stroke(width = 10f)
        )

        drawPath(path = base, color = baseColor)
        drawPath(path = highlight, color = Color.White.copy(alpha = a))
    }
}

/* ---------------- BOTTOM BAR ---------------- */

@Composable
private fun RolesAdmissionBottomBar(
    active: RolesAdmissionTab,
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, RoleAdmissionUI.Border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp)
                .padding(horizontal = 34.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoleAdmissionBottomBtn(
                label = "INICIO",
                icon = TablerIcons.Home,
                active = active == RolesAdmissionTab.HOME,
                activeColor = RoleAdmissionUI.Blue,
                onClick = onHome
            )

            RoleAdmissionBottomBtn(
                label = "INGRESAR",
                icon = TablerIcons.Login,
                active = active == RolesAdmissionTab.LOGIN,
                activeColor = RoleAdmissionUI.Blue,
                onClick = onLogin
            )

            RoleAdmissionBottomBtn(
                label = "REGISTRAR",
                icon = TablerIcons.UserPlus,
                active = active == RolesAdmissionTab.REGISTER,
                activeColor = RoleAdmissionUI.Red,
                onClick = onRegister
            )
        }
    }
}

@Composable
private fun RoleAdmissionBottomBtn(
    label: String,
    icon: ImageVector,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (active) activeColor else RoleAdmissionUI.Muted2,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "bottom_color"
    )

    Column(
        modifier = Modifier
            .widthIn(min = 78.dp)
            .clip(RoundedCornerShape(16.dp))
            .bouncyClick(onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Bold,
                letterSpacing = 1.4.sp
            )
        )
    }
}