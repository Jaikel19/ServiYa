package com.example.seviya.UI

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.theme.AppBackgroundAlt
import com.example.seviya.theme.BrandBlue
import com.example.seviya.theme.BrandBlueDeep
import com.example.seviya.theme.BrandRed
import com.example.seviya.theme.TextOnBlueSubtitle
import com.example.seviya.theme.White
import compose.icons.TablerIcons
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Search

@Composable
internal fun CategoriesHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BrandBlue,
                        BrandBlueDeep
                    )
                )
            )
    ) {
        DecorativeHeaderBubbles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandLogo()
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Encuentra el servicio\nideal para ti",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    lineHeight = 36.sp
                ),
                color = White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Explora categorías y conecta con profesionales confiables cerca de ti.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 21.sp
                ),
                color = TextOnBlueSubtitle
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = White.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = TablerIcons.Search,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Selecciona una categoría para comenzar",
                        color = White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        HeaderWave(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(58.dp)
        )
    }
}

@Composable
private fun BrandLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(White.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = TablerIcons.MapPin,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = White,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) { append("Servi") }

                withStyle(
                    SpanStyle(
                        color = BrandRed,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) { append("Ya") }
            },
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
        )
    }
}

@Composable
private fun DecorativeHeaderBubbles() {
    val infinite = rememberInfiniteTransition(label = "headerBubbles")

    val bubbleTopOffset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleTopOffset"
    )

    val bubbleMiddleOffset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleMiddleOffset"
    )

    val bubbleBottomOffset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleBottomOffset"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 24.dp)
                .offset(y = bubbleTopOffset.dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.06f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .offset(y = bubbleMiddleOffset.dp)
                .size(72.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.05f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 50.dp)
                .offset(y = bubbleBottomOffset.dp)
                .size(90.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.05f))
        )
    }
}

@Composable
private fun HeaderWave(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(0f, h * 0.30f)
            quadraticBezierTo(
                w * 0.22f,
                h * 1.05f,
                w * 0.50f,
                h * 0.55f
            )
            quadraticBezierTo(
                w * 0.78f,
                0f,
                w,
                h * 0.35f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = path,
            color = AppBackgroundAlt
        )
    }
}