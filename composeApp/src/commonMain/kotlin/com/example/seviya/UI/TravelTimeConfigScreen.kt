package com.example.seviya.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private object Brand {
    val Blue = Color(0xFF004AAD)
    val BlueDark = Color(0xFF002D69)
    val Red = Color(0xFFEF4444)
    val Bg = Color(0xFFF8FAFC)
    val TextMuted = Color(0xFF64748B)
    val Border = Color(0xFFEAEAF2)
}

private enum class BottomTab { HOME, SERVICES, LOGIN, REGISTER }

@Composable
fun TravelTimeConfigScreen(
    initialMinutes: Int? = null,
    onBack: () -> Unit,
    onSave: (minutes: Int) -> Unit,
    // para que también se vea como el wireframe de Categorías con bottom bar:
    onGoHome: () -> Unit = {},
    onGoServices: () -> Unit = {},
    onGoRegister: () -> Unit = {}
) {
    var minutesText by rememberSaveable { mutableStateOf(initialMinutes?.toString() ?: "") }
    val minutesValue = minutesText.toIntOrNull()
    val isValid = minutesValue != null && minutesValue in 0..600

    Scaffold(
        containerColor = Brand.Bg,
        bottomBar = {
            ConfigBottomBar(
                active = BottomTab.LOGIN,
                onHome = onGoHome,
                onServices = onGoServices,
                onLogin = { /* ya estás aquí */ },
                onRegister = onGoRegister
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header tipo Categorías (ola)
            HeaderWave(
                title = "Configuración",
                onBack = onBack
            )

            // Contenido
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp, bottom = 22.dp)
            ) {
                // Ícono circular centrado (puedes reemplazar "🚗" por imagen luego)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Brand.Blue.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🚗", fontSize = 38.sp)
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Tiempo de Traslado",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color(0xFF0F172A)
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Este tiempo se utilizará para bloquear automáticamente tu agenda entre servicios, " +
                            "permitiéndote desplazarte sin contratiempos entre un cliente y otro.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Brand.TextMuted,
                        lineHeight = 22.sp
                    )
                )

                Spacer(Modifier.height(18.dp))

                // Card input estilo moderno
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            text = "Minutos de traslado estimados",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF334155)
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = minutesText,
                            onValueChange = { raw ->
                                minutesText = raw.filter { it.isDigit() }.take(3)
                            },
                            placeholder = { Text("Ej: 30") },
                            trailingIcon = {
                                Text("🕒", fontSize = 18.sp, color = Color(0xFF94A3B8))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Brand.Blue,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                cursorColor = Brand.Blue
                            )
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Sugerencia: 20–40 minutos suele ser lo habitual.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Nota roja (alerta)
                WarningNote(
                    text = "Asegúrate de que este tiempo sea realista para evitar retrasos con tus próximos clientes."
                )

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = { minutesValue?.let(onSave) },
                    enabled = isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Brand.Blue,
                        disabledContainerColor = Brand.Blue.copy(alpha = 0.35f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                ) {
                    Text("💾", fontSize = 16.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Guardar Cambios",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }

                if (!isValid && minutesText.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Ingresa un número válido (0–600).",
                        color = Brand.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderWave(
    title: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .drawBehind {
                // Fondo gradient
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Brand.Blue, Brand.BlueDark),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                )

                // Recorte tipo “ellipse/ola” (similar al wireframe Categorías)
                val w = size.width
                val h = size.height
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(w, h * 0.78f)
                    cubicTo(
                        w * 0.75f, h * 1.05f,
                        w * 0.25f, h * 1.05f,
                        0f, h * 0.78f
                    )
                    close()
                }
                // Pintamos una “máscara” encima? No hace falta: ya es fondo completo.
                // La sensación de ola la da el contenido debajo (bg claro).
            }
    ) {
        // Top row: back + title centrado (como config)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back (círculo transparente)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                    Text("‹", color = Color.White, fontSize = 28.sp)
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))

            // placeholder para balance (mismo ancho que el back)
            Spacer(Modifier.width(40.dp))
        }
    }
}

@Composable
private fun WarningNote(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brand.Red.copy(alpha = 0.10f))
            .leftAccent(Brand.Red, 4.dp)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Brand.Red.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text("i", color = Brand.Red, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            color = Brand.Red,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

private fun Modifier.leftAccent(color: Color, width: Dp): Modifier =
    this.then(
        Modifier.drawBehind {
            drawRect(
                color = color,
                topLeft = Offset(0f, 0f),
                size = Size(width.toPx(), size.height)
            )
        }
    )

@Composable
private fun ConfigBottomBar(
    active: BottomTab,
    onHome: () -> Unit,
    onServices: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.92f),
        tonalElevation = 10.dp,
        modifier = Modifier.border(1.dp, Brand.Border)
    ) {
        BottomItem(
            label = "Inicio",
            glyph = "🏠",
            active = active == BottomTab.HOME,
            onClick = onHome
        )
        BottomItem(
            label = "Servicios",
            glyph = "🧭",
            active = active == BottomTab.SERVICES,
            onClick = onServices
        )
        BottomItem(
            label = "Ingresar",
            glyph = "🔑",
            active = active == BottomTab.LOGIN,
            onClick = onLogin
        )
        BottomItem(
            label = "Registrarse",
            glyph = "👤+",
            active = active == BottomTab.REGISTER,
            onClick = onRegister
        )
    }
}

@Composable
private fun RowScope.BottomItem(
    label: String,
    glyph: String,
    active: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = active,
        onClick = onClick,
        icon = {
            Text(
                text = glyph,
                fontSize = 18.sp,
                color = if (active) Brand.Blue else Color(0xFF94A3B8)
            )
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (active) Brand.Blue else Color(0xFF94A3B8)
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}