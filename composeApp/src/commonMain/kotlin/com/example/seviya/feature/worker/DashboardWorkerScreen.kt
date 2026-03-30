package com.example.seviya.feature.worker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.seviya.core.designsystem.theme.AccentYellow
import com.example.seviya.core.designsystem.theme.ActiveChipBackground
import com.example.seviya.core.designsystem.theme.ActiveChipText
import com.example.seviya.core.designsystem.theme.AppBackground
import com.example.seviya.core.designsystem.theme.BlueGrayText
import com.example.seviya.core.designsystem.theme.BlueGrayTextDark
import com.example.seviya.core.designsystem.theme.BlueGrayTextLight
import com.example.seviya.core.designsystem.theme.BorderSoft
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.CardBlueDeep
import com.example.seviya.core.designsystem.theme.CardSurface
import com.example.seviya.core.designsystem.theme.MetricGreen
import com.example.seviya.core.designsystem.theme.OnlineGreen
import com.example.seviya.core.designsystem.theme.RingTrack
import com.example.seviya.core.designsystem.theme.SoftBlueSurface
import com.example.seviya.core.designsystem.theme.SoftRedSurface
import com.example.seviya.core.designsystem.theme.TextPrimary
import com.example.seviya.core.designsystem.theme.VibrantRed
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.domain.entity.Booking
import com.example.shared.presentation.workerDashboard.WorkerDashboardUiState
import com.example.shared.presentation.workerDashboard.WorkerDashboardViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Adjustments
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.ChartBar
import compose.icons.tablericons.Clock
import compose.icons.tablericons.MapPin
import compose.icons.tablericons.Message
import compose.icons.tablericons.Photo
import compose.icons.tablericons.Tool
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.math.roundToInt
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerDashboardScreen(
    workerId: String,
    onOpenCategories: () -> Unit = {},
    onOpenTravelTime: () -> Unit = {},
    onOpenDailyAgenda: () -> Unit = {},
    onOpenSchedule: () -> Unit = {},
    onOpenPortfolio: () -> Unit = {},
    onOpenAppointmentDetail: (Booking) -> Unit = {},
    onStartAppointment: (Booking) -> Unit = {},
    onCompleteAppointment: (Booking) -> Unit = {},
    onOpenReview: (Booking) -> Unit = {},
) {
  val viewModel: WorkerDashboardViewModel = koinViewModel()
  val state by viewModel.uiState.collectAsState()

  LaunchedEffect(workerId) { viewModel.load(workerId) }

  WorkerDashboardContent(
      state = state,
      onOpenCategories = onOpenCategories,
      onOpenTravelTime = onOpenTravelTime,
      onOpenDailyAgenda = onOpenDailyAgenda,
      onOpenSchedule = onOpenSchedule,
      onOpenPortfolio = onOpenPortfolio,
      onOpenAppointmentDetail = onOpenAppointmentDetail,
      onStartAppointment = onStartAppointment,
      onCompleteAppointment = onCompleteAppointment,
      onOpenReview = onOpenReview,
  )
}

@Composable
private fun WorkerDashboardContent(
    state: WorkerDashboardUiState,
    onOpenCategories: () -> Unit = {},
    onOpenTravelTime: () -> Unit = {},
    onOpenDailyAgenda: () -> Unit = {},
    onOpenSchedule: () -> Unit = {},
    onOpenPortfolio: () -> Unit = {},
    onOpenAppointmentDetail: (Booking) -> Unit = {},
    onStartAppointment: (Booking) -> Unit = {},
    onCompleteAppointment: (Booking) -> Unit = {},
    onOpenReview: (Booking) -> Unit = {},
) {
  val profile = state.profile

  val sortedBookings = state.bookings.sortedBy { parseBookingOrderKey(it.date) }

  var currentQueueBookingId by rememberSaveable { mutableStateOf<String?>(null) }

  val currentQueueBooking = sortedBookings.firstOrNull { it.id == currentQueueBookingId }

  val firstConfirmedBooking =
      sortedBookings.firstOrNull { it.status.equals("confirmed", ignoreCase = true) }

  LaunchedEffect(sortedBookings, currentQueueBookingId) {
    val currentStillExists = sortedBookings.any { it.id == currentQueueBookingId }
    if (!currentStillExists) {
      currentQueueBookingId = firstConfirmedBooking?.id
    }
  }

  val currentBooking = currentQueueBooking ?: firstConfirmedBooking

  val upcomingBookings =
      sortedBookings.filter {
        it.status.equals("confirmed", ignoreCase = true) && it.id != currentBooking?.id
      }

  val completedCount = state.bookings.count { it.status.equals("completed", ignoreCase = true) }

  val totalIncome =
      state.bookings
          .filter { it.status.equals("completed", ignoreCase = true) }
          .sumOf { it.totalCost }

  Surface(modifier = Modifier.fillMaxSize(), color = AppBackground) {
    when {
      state.isLoading -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = BrandBlue)
        }
      }

      else -> {
        Column(
            modifier =
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).navigationBarsPadding()
        ) {
          WorkerDashboardHeader(
              workerName = profile?.name ?: "Trabajador",
              avatarUrl = profile?.profilePictureLink.orEmpty(),
          )

          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = 20.dp)
                      .padding(top = 18.dp, bottom = 28.dp),
              verticalArrangement = Arrangement.spacedBy(26.dp),
          ) {
            CurrentAppointmentSection(
                booking = currentBooking,
                onOpenAppointmentDetail = onOpenAppointmentDetail,
                onStartAppointment = onStartAppointment,
                onCompleteAppointment = onCompleteAppointment,
                onOpenReview = onOpenReview,
                onGoNextConfirmed = {
                  val nextConfirmed =
                      sortedBookings.firstOrNull {
                        it.status.equals("confirmed", ignoreCase = true) &&
                            it.id != currentBooking?.id
                      }
                  currentQueueBookingId = nextConfirmed?.id
                },
            )

            QuickAccessSection(
                onOpenCategories = onOpenCategories,
                onOpenTravelTime = onOpenTravelTime,
                onOpenDailyAgenda = onOpenDailyAgenda,
                onOpenSchedule = onOpenSchedule,
                onOpenPortfolio = onOpenPortfolio,
            )

            UpcomingAppointmentsSection(
                bookings = upcomingBookings,
                onOpenAppointmentDetail = onOpenAppointmentDetail,
            )

            MetricsSection(
                servicesCount = completedCount,
                rating = profile?.stars ?: 0.0,
                totalIncome = totalIncome,
            )

            state.errorMessage?.let { error ->
              Text(text = error, color = VibrantRed, style = MaterialTheme.typography.bodyMedium)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun WorkerDashboardHeader(workerName: String, avatarUrl: String) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(118.dp)
              .background(BrandBlue)
              .windowInsetsPadding(WindowInsets(0, 30, 0, 0))
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box {
          WorkerProfileAvatar(imageUrl = avatarUrl, workerName = workerName)

          Box(
              modifier =
                  Modifier.align(Alignment.BottomEnd)
                      .size(16.dp)
                      .clip(CircleShape)
                      .background(OnlineGreen)
                      .border(2.dp, BrandBlue, CircleShape)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(verticalArrangement = Arrangement.Center) {
          Text(
              text = "HOLA DE NUEVO,",
              color = White.copy(alpha = 0.72f),
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
          )

          Text(
              text = workerName,
              color = White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style =
                  MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
          )
        }
      }
    }
  }
}

@Composable
private fun WorkerProfileAvatar(imageUrl: String?, workerName: String) {
  Box(modifier = Modifier.size(74.dp), contentAlignment = Alignment.Center) {
    Box(
        modifier =
            Modifier.size(82.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.22f))
                .border(width = 2.dp, color = White, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
      Box(
          modifier =
              Modifier.size(66.dp)
                  .clip(CircleShape)
                  .background(White.copy(alpha = 0.14f))
                  .border(width = 1.5.dp, color = White.copy(alpha = 0.30f), shape = CircleShape),
          contentAlignment = Alignment.Center,
      ) {
        when {
          !imageUrl.isNullOrBlank() -> {
            val painterResource = asyncPainterResource(data = imageUrl)

            KamelImage(
                resource = painterResource,
                contentDescription = "Foto del trabajador",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                onFailure = { WorkerAvatarFallback(workerInitials = initialsFromName(workerName)) },
            )
          }

          else -> {
            WorkerAvatarFallback(workerInitials = initialsFromName(workerName))
          }
        }
      }
    }
  }
}

@Composable
private fun WorkerAvatarFallback(workerInitials: String) {
  Box(
      modifier = Modifier.fillMaxSize().background(White.copy(alpha = 0.10f)),
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = workerInitials,
        color = White,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
    )
  }
}

@Composable
private fun CurrentAppointmentSection(
    booking: Booking?,
    onOpenAppointmentDetail: (Booking) -> Unit,
    onStartAppointment: (Booking) -> Unit,
    onCompleteAppointment: (Booking) -> Unit,
    onOpenReview: (Booking) -> Unit,
    onGoNextConfirmed: () -> Unit,
) {
  val ringVisual = appointmentRingVisual(booking?.status.orEmpty())

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          text = "Siguiente Cita",
          style =
              MaterialTheme.typography.headlineSmall.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = TextPrimary,
              ),
      )

      StatusPill(booking?.status ?: "")
    }

    if (booking == null) {
      Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(32.dp),
          color = CardSurface,
          border = BorderStroke(1.dp, BorderSoft),
          shadowElevation = 2.dp,
      ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
          Text(
              text = "No tienes citas disponibles en este momento.",
              style = MaterialTheme.typography.bodyLarge.copy(color = BlueGrayTextDark),
          )
        }
      }
      return
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onOpenAppointmentDetail(booking) },
        shape = RoundedCornerShape(34.dp),
        color = CardSurface,
        border = BorderStroke(1.dp, BorderSoft),
        shadowElevation = 6.dp,
    ) {
      Column(
          modifier = Modifier.fillMaxWidth().padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        AppointmentProgressRing(
            progress = ringVisual.progress,
            progressColor = ringVisual.color,
            modifier = Modifier.size(220.dp),
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = booking.services.firstOrNull()?.name ?: "Servicio",
            style =
                MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = "${booking.clientName} • ${extractTimeFromDateTime(booking.date)}",
            style = MaterialTheme.typography.bodyLarge.copy(color = BlueGrayText),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(22.dp))

        when {
          booking.status.equals("confirmed", true) -> {
            Button(
                onClick = { onStartAppointment(booking) },
                modifier = Modifier.fillMaxWidth().height(72.dp),
                shape = RoundedCornerShape(24.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = currentActionButtonColor(booking.status)
                    ),
            ) {
              Text(
                  text = "Iniciar cita",
                  style =
                      MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
              )
            }
          }

          booking.status.equals("in_progress", true) -> {
            Button(
                onClick = { onCompleteAppointment(booking) },
                modifier = Modifier.fillMaxWidth().height(72.dp),
                shape = RoundedCornerShape(24.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = currentActionButtonColor(booking.status)
                    ),
            ) {
              Text(
                  text = "Completar cita",
                  style =
                      MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
              )
            }
          }

          booking.status.equals("completed", true) -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Button(
                  onClick = { onOpenReview(booking) },
                  modifier = Modifier.fillMaxWidth().height(72.dp),
                  shape = RoundedCornerShape(24.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = MetricGreen),
              ) {
                Text(
                    text = "Hacer reseña",
                    style =
                        MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                )
              }

              OutlinedButton(
                  onClick = onGoNextConfirmed,
                  modifier = Modifier.fillMaxWidth().height(64.dp),
                  shape = RoundedCornerShape(24.dp),
              ) {
                Text(
                    text = "Siguiente cita",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                )
              }
            }
          }

          else -> {
            Button(
                onClick = { onOpenAppointmentDetail(booking) },
                modifier = Modifier.fillMaxWidth().height(72.dp),
                shape = RoundedCornerShape(24.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = currentActionButtonColor(booking.status)
                    ),
            ) {
              Text(
                  text = currentActionText(booking.status),
                  style =
                      MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AppointmentProgressRing(
    progress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier,
) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val stroke = 16.dp.toPx()
      val diameter = size.minDimension - stroke
      val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)

      drawArc(
          color = RingTrack,
          startAngle = 0f,
          sweepAngle = 360f,
          useCenter = false,
          topLeft = topLeft,
          size = Size(diameter, diameter),
          style = Stroke(width = stroke, cap = StrokeCap.Round),
      )

      if (progress > 0f) {
        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = 360f * progress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
      }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
          imageVector = TablerIcons.Tool,
          contentDescription = null,
          tint = BrandBlue,
          modifier = Modifier.size(34.dp),
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
          text = "${(progress * 100).toInt()}%",
          style =
              MaterialTheme.typography.displaySmall.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = TextPrimary,
              ),
      )

      Text(
          text = "PROGRESO",
          style =
              MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = BlueGrayTextLight,
              ),
      )
    }
  }
}

@Composable
private fun QuickAccessSection(
    onOpenCategories: () -> Unit,
    onOpenTravelTime: () -> Unit,
    onOpenDailyAgenda: () -> Unit,
    onOpenSchedule: () -> Unit,
    onOpenPortfolio: () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Text(
        text = "Accesos Rápidos",
        style =
            MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
            ),
    )

    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        QuickAccessItem(
            title = "Categoría",
            icon = TablerIcons.Adjustments,
            iconTint = BrandBlue,
            background = SoftBlueSurface,
            onClick = onOpenCategories,
        )

        QuickAccessItem(
            title = "Traslado",
            icon = TablerIcons.MapPin,
            iconTint = BrandBlue,
            background = SoftBlueSurface,
            onClick = onOpenTravelTime,
        )

        QuickAccessItem(
            title = "Agenda Diaria",
            icon = TablerIcons.CalendarEvent,
            iconTint = BrandBlue,
            background = SoftBlueSurface,
            onClick = onOpenDailyAgenda,
        )

        QuickAccessItem(
          title = "Horario",
          icon = TablerIcons.Clock,
          iconTint = BrandBlue,
          background = SoftBlueSurface,
          onClick = onOpenSchedule,
      )

      QuickAccessItem(
          title = "Portafolio",
          icon = TablerIcons.Photo,
          iconTint = BrandBlue,
          background = SoftBlueSurface,
          onClick = onOpenPortfolio,
      )
    }
  }
}

@Composable
private fun QuickAccessItem(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    background: Color,
    onClick: () -> Unit,
) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.clickable { onClick() },
  ) {
    Box(
        modifier = Modifier.size(72.dp).clip(RoundedCornerShape(24.dp)).background(background),
        contentAlignment = Alignment.Center,
    ) {
      Icon(
          imageVector = icon,
          contentDescription = title,
          tint = iconTint,
          modifier = Modifier.size(30.dp),
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = title,
        style =
            MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BlueGrayTextDark,
            ),
    )
  }
}

@Composable
private fun UpcomingAppointmentsSection(
    bookings: List<Booking>,
    onOpenAppointmentDetail: (Booking) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Text(
        text = "Próximas Citas",
        style =
            MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
            ),
    )

    if (bookings.isEmpty()) {
      Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(28.dp),
          color = CardSurface,
          border = BorderStroke(1.dp, BorderSoft),
      ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            contentAlignment = Alignment.Center,
        ) {
          Text(
              text = "No hay próximas citas.",
              style = MaterialTheme.typography.bodyLarge.copy(color = BlueGrayTextDark),
          )
        }
      }
      return
    }

    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      bookings.forEachIndexed { index, booking ->
        UpcomingAppointmentCard(
            booking = booking,
            dark = index % 2 == 0,
            onClick = { onOpenAppointmentDetail(booking) },
        )
      }
    }
  }
}

@Composable
private fun UpcomingAppointmentCard(booking: Booking, dark: Boolean, onClick: () -> Unit) {
  val background = if (dark) CardBlueDeep else SoftBlueSurface
  val titleColor = if (dark) White else TextPrimary
  val subtitleColor = if (dark) White.copy(alpha = 0.70f) else BlueGrayText
  val chipBg = if (dark) White.copy(alpha = 0.14f) else BrandBlue.copy(alpha = 0.10f)
  val chipText = if (dark) White else BrandBlue
  val iconBox = if (dark) White.copy(alpha = 0.12f) else White

  Surface(
      modifier = Modifier.size(width = 240.dp, height = 165.dp).clickable { onClick() },
      shape = RoundedCornerShape(30.dp),
      color = background,
      border = if (dark) null else BorderStroke(1.dp, BorderSoft),
      shadowElevation = 4.dp,
  ) {
    Column(
        modifier = Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top,
      ) {
        Box(
            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(iconBox),
            contentAlignment = Alignment.Center,
        ) {
          Icon(
              imageVector = TablerIcons.Tool,
              contentDescription = null,
              tint = if (dark) White else BrandBlue,
          )
        }

        Surface(shape = RoundedCornerShape(12.dp), color = chipBg) {
          Text(
              text = appointmentDayLabel(booking.date),
              color = chipText,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
          )
        }
      }

      Column {
        Text(
            text = booking.services.firstOrNull()?.name ?: "Servicio",
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = titleColor,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = booking.clientName,
            style = MaterialTheme.typography.bodyMedium.copy(color = subtitleColor),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
      }

      Text(
          text = extractTimeFromDateTime(booking.date),
          style =
              MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = titleColor,
              ),
      )
    }
  }
}

@Composable
private fun MetricsSection(servicesCount: Int, rating: Double, totalIncome: Double) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Text(
        text = "Métricas de Desempeño",
        style =
            MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
            ),
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      MetricCard(
          modifier = Modifier.weight(1f),
          title = "Servicios",
          value = servicesCount.toString(),
          valueColor = BrandBlue,
      )

      MetricCard(
          modifier = Modifier.weight(1f),
          title = "Calif.",
          value = formatRating(rating),
          valueColor = TextPrimary,
          badgeText = "★",
          badgeColor = AccentYellow,
      )

      MetricCard(
          modifier = Modifier.weight(1f),
          title = "Ingresos",
          value = formatMoney(totalIncome),
          valueColor = MetricGreen,
      )
    }
  }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    valueColor: Color,
    badgeText: String? = null,
    badgeColor: Color = valueColor,
) {
  Surface(
      modifier = modifier,
      shape = RoundedCornerShape(26.dp),
      color = CardSurface,
      border = BorderStroke(1.dp, BorderSoft),
      shadowElevation = 2.dp,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
          text = title.uppercase(),
          style =
              MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = BlueGrayTextLight,
              ),
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
      ) {
        badgeText?.let {
          Text(
              text = it,
              color = badgeColor,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
          )

          Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = value,
            style =
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = valueColor,
                ),
            maxLines = 1,
        )
      }
    }
  }
}

@Composable
private fun StatusPill(status: String) {
  val text: String
  val bg: Color
  val fg: Color

  when {
    status.equals("in_progress", true) -> {
      text = "ACTIVO"
      bg = ActiveChipBackground
      fg = ActiveChipText
    }

    status.equals("confirmed", true) -> {
      text = "CONFIRMADA"
      bg = ActiveChipBackground
      fg = ActiveChipText
    }

    status.equals("completed", true) -> {
      text = "FINALIZADA"
      bg = MetricGreen.copy(alpha = 0.14f)
      fg = MetricGreen
    }

    status.equals("payment_pending", true) -> {
      text = "PAGO"
      bg = AccentYellow.copy(alpha = 0.16f)
      fg = AccentYellow
    }

    status.equals("cancelled", true) -> {
      text = "CANCELADA"
      bg = VibrantRed.copy(alpha = 0.14f)
      fg = VibrantRed
    }

    else -> {
      text = "SIN ESTADO"
      bg = BlueGrayTextLight.copy(alpha = 0.16f)
      fg = BlueGrayTextDark
    }
  }

  Surface(shape = RoundedCornerShape(100.dp), color = bg) {
    Text(
        text = text,
        color = fg,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
    )
  }
}

private data class AppointmentRingVisual(val progress: Float, val color: Color)

private fun appointmentRingVisual(status: String): AppointmentRingVisual {
  return when {
    status.equals("confirmed", true) ->
        AppointmentRingVisual(progress = 0f, color = BlueGrayTextLight)

    status.equals("in_progress", true) ->
        AppointmentRingVisual(progress = 0.25f, color = VibrantRed)

    status.equals("completed", true) -> AppointmentRingVisual(progress = 1f, color = MetricGreen)

    status.equals("payment_pending", true) ->
        AppointmentRingVisual(progress = 0f, color = BlueGrayTextLight)

    else -> AppointmentRingVisual(progress = 0f, color = BlueGrayTextLight)
  }
}

private fun currentActionText(status: String): String {
  return when {
    status.equals("confirmed", true) -> "Iniciar cita"
    status.equals("in_progress", true) -> "Completar cita"
    status.equals("completed", true) -> "Hacer reseña"
    status.equals("payment_pending", true) -> "Ver detalle"
    else -> "Abrir cita"
  }
}

private fun currentActionButtonColor(status: String): Color {
  return when {
    status.equals("completed", true) -> MetricGreen
    else -> VibrantRed
  }
}

private fun extractTimeFromDateTime(dateTime: String): String {
  return when {
    dateTime.contains("T") -> dateTime.substringAfter("T").take(5)
    dateTime.contains(" ") -> dateTime.substringAfter(" ").take(5)
    else -> dateTime
  }
}

private fun appointmentDayLabel(dateTime: String): String {
  val dateOnly =
      when {
        dateTime.contains("T") -> dateTime.substringBefore("T")
        dateTime.contains(" ") -> dateTime.substringBefore(" ")
        else -> dateTime
      }

  return if (dateOnly.isBlank()) "Próxima" else dateOnly
}

private fun parseBookingOrderKey(dateTime: String): String {
  return dateTime.trim()
}

private fun formatMoney(amount: Double): String {
  val rounded = amount.toInt()
  return "₡$rounded"
}

private fun formatRating(rating: Double): String {
  val scaled = (rating * 10).roundToInt()
  val integerPart = scaled / 10
  val decimalPart = scaled % 10
  return "$integerPart.$decimalPart"
}

private fun initialsFromName(name: String): String {
  return name
      .trim()
      .split(" ")
      .filter { it.isNotBlank() }
      .take(2)
      .joinToString("") { it.first().uppercase() }
      .ifBlank { "W" }
}
