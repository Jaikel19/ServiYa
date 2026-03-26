package com.example.seviya.feature.worker

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.presentation.workerToClientReview.WorkerToClientReviewViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorkerToClientReviewScreen(
    appointmentId: String,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
) {
  val viewModel: WorkerToClientReviewViewModel = koinViewModel()

  LaunchedEffect(appointmentId) { viewModel.loadAppointment(appointmentId) }

  WorkerToClientReviewContent(
      viewModel = viewModel,
      onBack = onBack,
      onSubmitSuccess = onSubmitSuccess,
  )
}

@Composable
private fun WorkerToClientReviewContent(
    viewModel: WorkerToClientReviewViewModel,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsState()

  if (uiState.submitSuccess) {
    onSubmitSuccess()
  }

  val appointment = uiState.appointment

  Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF6F7F9)) {
    Column(modifier = Modifier.fillMaxSize()) {
      ReviewHeader()

      Column(
          modifier =
              Modifier.weight(1f)
                  .verticalScroll(rememberScrollState())
                  .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        appointment?.let { appt ->
          Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(22.dp),
              colors = CardDefaults.cardColors(containerColor = White),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Box(
                  modifier = Modifier.size(76.dp).clip(CircleShape).background(Color(0xFFE8EDF4)),
                  contentAlignment = Alignment.Center,
              ) {
                Text(
                    text = appt.clientName.take(1).uppercase(),
                    style =
                        MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandBlue,
                        ),
                )
              }

              Spacer(modifier = Modifier.width(16.dp))

              Column {
                Text(
                    text = "SERVICIO COMPLETADO",
                    color = Color(0xFF2D8CFF),
                    style =
                        MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = appt.clientName,
                    style =
                        MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF111827),
                        ),
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text =
                        "${appt.services.firstOrNull()?.name ?: "Servicio"} • ${extractDateOnly(appt.serviceStartAt)}",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF667085)),
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(32.dp))

          Text(
              text = "¿Cómo calificarías al cliente?",
              modifier = Modifier.fillMaxWidth(),
              style =
                  MaterialTheme.typography.headlineSmall.copy(
                      fontWeight = FontWeight.ExtraBold,
                      color = Color(0xFF111827),
                  ),
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
              text = "Tu opinión ayuda a otros trabajadores",
              modifier = Modifier.fillMaxWidth(),
              style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF667085)),
          )

          Spacer(modifier = Modifier.height(28.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            (1..5).forEach { star ->
              Text(
                  text = if (star <= uiState.rating) "★" else "☆",
                  modifier =
                      Modifier.padding(horizontal = 6.dp).clickable {
                        viewModel.onRatingChanged(star)
                      },
                  style =
                      MaterialTheme.typography.displaySmall.copy(
                          color = Color(0xFFF4C542),
                          fontWeight = FontWeight.Bold,
                      ),
              )
            }
          }

          Spacer(modifier = Modifier.height(36.dp))

          Text(
              text = "Comentarios sobre el cliente (opcional)",
              style =
                  MaterialTheme.typography.headlineSmall.copy(
                      fontWeight = FontWeight.ExtraBold,
                      color = Color(0xFF111827),
                  ),
          )

          Spacer(modifier = Modifier.height(14.dp))

          Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = White),
          ) {
            BasicTextField(
                value = uiState.comment,
                onValueChange = viewModel::onCommentChanged,
                modifier = Modifier.fillMaxWidth().height(150.dp).padding(18.dp),
                textStyle = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF111827)),
                decorationBox = { innerTextField ->
                  if (uiState.comment.isBlank()) {
                    Text(
                        text = "Escribe aquí tu experiencia con el cliente...",
                        style =
                            MaterialTheme.typography.titleMedium.copy(color = Color(0xFF98A2B3)),
                    )
                  }
                  innerTextField()
                },
            )
          }

          Spacer(modifier = Modifier.height(32.dp))

          Text(
              text = "Subir fotos (opcional)",
              style =
                  MaterialTheme.typography.headlineSmall.copy(
                      fontWeight = FontWeight.ExtraBold,
                      color = Color(0xFF111827),
                  ),
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row {
            Card(
                modifier = Modifier.size(110.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F6FA)),
            ) {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = "📷", style = MaterialTheme.typography.headlineLarge)
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                      text = "Añadir",
                      style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF98A2B3)),
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(24.dp))

          uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color(0xFFE53935),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(14.dp))
          }

          Button(
              onClick = { viewModel.submitReview() },
              modifier = Modifier.fillMaxWidth().height(62.dp),
              shape = RoundedCornerShape(22.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2286F0)),
              enabled = !uiState.isSubmitting,
          ) {
            Text(
                text = if (uiState.isSubmitting) "Enviando..." else "Enviar Calificación",
                style =
                    MaterialTheme.typography.headlineSmall.copy(
                        color = White,
                        fontWeight = FontWeight.ExtraBold,
                    ),
            )
          }

          Spacer(modifier = Modifier.height(24.dp))
        }
            ?: run {
              Text(
                  text = "Cargando información de la cita...",
                  style =
                      MaterialTheme.typography.titleLarge.copy(
                          color = Color(0xFF667085),
                          fontStyle = FontStyle.Italic,
                      ),
              )
            }
      }
    }
  }
}

@Composable
private fun ReviewHeader() {
  Box(modifier = Modifier.fillMaxWidth().height(170.dp).background(BrandBlue)) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 26.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Servi",
            color = White,
            style =
                MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                ),
        )
        Text(
            text = "Ya",
            color = BrandRed,
            style =
                MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                ),
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier =
                Modifier.width(28.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFFF5A5A))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier =
                Modifier.size(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.45f))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier =
                Modifier.size(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.45f))
        )
      }
    }

    Text(
        text = "CALIFICAR CLIENTE",
        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 34.dp),
        style =
            MaterialTheme.typography.headlineLarge.copy(
                color = White,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
            ),
    )
  }
}

private fun extractDateOnly(dateTime: String): String {
  return when {
    dateTime.contains("T") -> dateTime.substringBefore("T")
    dateTime.contains(" ") -> dateTime.substringBefore(" ")
    else -> dateTime
  }
}
