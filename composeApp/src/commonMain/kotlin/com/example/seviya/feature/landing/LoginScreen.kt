package com.example.seviya.feature.landing

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.entity.User
import com.example.shared.presentation.login.LoginViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowRight
import compose.icons.tablericons.Eye
import compose.icons.tablericons.EyeOff
import compose.icons.tablericons.MapPin
import org.koin.compose.viewmodel.koinViewModel

private val BrandBlueTop = Color(0xFF1565D8)
private val BrandBlueBottom = Color(0xFF004AAD)
private val BrandBlueDark = Color(0xFF0D1B3D)
private val BrandRed = Color(0xFFFF473A)
private val BrandRedDark = Color(0xFFE63E32)
private val FieldBg = Color(0xFFF3F6FA)
private val FieldText = Color(0xFF0F172A)
private val FieldHint = Color(0xFF9AA8BC)
private val SubtitleText = Color(0xFF7A879B)
private val SheetColor = Color(0xFFFDFDFD)

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (User) -> Unit,
) {
    val viewModel: LoginViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(state.loggedUser?.uid) {
        val user = state.loggedUser ?: return@LaunchedEffect
        onLoginSuccess(user)
        viewModel.consumeLoginSuccess()
    }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val heroAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "heroAlpha",
    )

    val sheetOffset by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 28.dp,
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "sheetOffset",
    )

    val sheetAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "sheetAlpha",
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
    ) {
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .background(Color.White)
                    .imePadding(),
        ) {
            LoginHeroSection(
                modifier =
                    Modifier.fillMaxWidth()
                        .fillMaxHeight(0.54f)
                        .graphicsLayer(alpha = heroAlpha),
            )

            LoginFormSheet(
                modifier =
                    Modifier.align(Alignment.BottomCenter)
                        .offset(y = sheetOffset)
                        .graphicsLayer(alpha = sheetAlpha)
                        .fillMaxWidth()
                        .fillMaxHeight(0.50f),
                email = state.email,
                password = state.password,
                isLoading = state.isLoading,
                canSubmit = state.canSubmit,
                errorMessage = state.errorMessage,
                passwordVisible = passwordVisible,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                onLogin = { viewModel.login() },
            )
        }
    }
}

@Composable
private fun LoginHeroSection(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heroAnimation")

    val floatingLogoOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "floatingLogoOffset",
    )

    val floatingChipOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "floatingChipOffset",
    )

    Box(
        modifier =
            modifier
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors = listOf(BrandBlueTop, BrandBlueBottom),
                        ),
                )
                .statusBarsPadding(),
    ) {
        Box(
            modifier =
                Modifier.align(Alignment.TopStart)
                    .offset(x = (-48).dp, y = (-18).dp)
                    .size(176.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape),
        )

        Box(
            modifier =
                Modifier.align(Alignment.TopEnd)
                    .offset(x = 38.dp, y = 24.dp)
                    .size(132.dp)
                    .background(BrandRed.copy(alpha = 0.10f), CircleShape),
        )

        Box(
            modifier =
                Modifier.align(Alignment.BottomStart)
                    .offset(x = (-22).dp, y = 34.dp)
                    .size(116.dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape),
        )

        Box(
            modifier =
                Modifier.align(Alignment.BottomEnd)
                    .offset(x = 22.dp, y = 34.dp)
                    .size(104.dp)
                    .background(BrandRed.copy(alpha = 0.10f), CircleShape),
        )

        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.offset(y = floatingLogoOffset.dp),
            ) {
                ServiYaLogoCard()
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Servicios confiables\ncuando los necesitas",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 31.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Encuentra profesionales cerca de ti\ny agenda en minutos",
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
            )

        }
    }
}

@Composable
private fun ServiYaLogoCard() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        modifier =
            Modifier.shadow(
                elevation = 22.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false,
            ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Servi",
                    color = BrandBlueBottom,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Ya",
                    color = BrandRed,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier =
                    Modifier.width(64.dp)
                        .height(4.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(BrandBlueBottom, BrandRed),
                            ),
                            shape = RoundedCornerShape(999.dp),
                        ),
            )
        }
    }
}

@Composable
private fun HeroInfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.14f),
        modifier =
            Modifier.shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(999.dp),
                clip = false,
            ),
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LoginFormSheet(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    isLoading: Boolean,
    canSubmit: Boolean,
    errorMessage: String?,
    passwordVisible: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLogin: () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = SheetColor,
        shape = RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp),
        shadowElevation = 18.dp,
    ) {
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 20.dp),
        ) {
            Box(
                modifier =
                    Modifier.align(Alignment.CenterHorizontally)
                        .width(54.dp)
                        .height(5.dp)
                        .background(
                            color = Color(0xFFDCE3ED),
                            shape = RoundedCornerShape(100.dp),
                        ),
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Bienvenido de nuevo",
                color = BrandBlueDark,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ingresa con tu correo y contraseña",
                color = SubtitleText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            LoginField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = "Correo electrónico",
                keyboardType = KeyboardType.Email,
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "Contraseña",
                keyboardType = KeyboardType.Password,
                visualTransformation =
                    if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                trailingContent = {
                    Icon(
                        imageVector =
                            if (passwordVisible) {
                                TablerIcons.EyeOff
                            } else {
                                TablerIcons.Eye
                            },
                        contentDescription = null,
                        tint = FieldHint,
                        modifier =
                            Modifier.size(22.dp)
                                .clickable { onTogglePasswordVisibility() },
                    )
                },
            )

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BrandRed.copy(alpha = 0.10f),
                ) {
                    Text(
                        text = errorMessage,
                        color = BrandRedDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogin,
                enabled = canSubmit && !isLoading,
                shape = RoundedCornerShape(22.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = BrandRed,
                        disabledContainerColor = BrandRed.copy(alpha = 0.45f),
                    ),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 0.dp,
                    ),
                modifier =
                    Modifier.fillMaxWidth()
                        .height(62.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.4.dp,
                        color = Color.White,
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Ingresar",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = TablerIcons.ArrowRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = {
            Text(
                text = placeholder,
                color = FieldHint,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
            )
        },
        trailingIcon = trailingContent,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(20.dp),
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = FieldBg,
                unfocusedContainerColor = FieldBg,
                disabledContainerColor = FieldBg,
                errorContainerColor = FieldBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedTextColor = FieldText,
                unfocusedTextColor = FieldText,
                disabledTextColor = FieldText,
                cursorColor = BrandBlueBottom,
            ),
        modifier =
            Modifier.fillMaxWidth()
                .height(68.dp),
    )
}