package com.example.seviya.feature.client

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seviya.core.designsystem.theme.BrandBlue
import com.example.seviya.core.designsystem.theme.BrandRed
import com.example.seviya.core.designsystem.theme.White
import com.example.shared.presentation.ClientPaymentUpload.ClientPaymentUploadViewModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@Composable
actual fun ClientPaymentUploadPlatformScreen(
    appointmentId: String,
    viewModel: ClientPaymentUploadViewModel,
    onBack: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(uiState.uploadSuccess) {
    if (uiState.uploadSuccess) {
      onBack()
    }
  }

  val launcher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
          val bytes = context.contentResolver.openInputStream(it)?.readBytes()
          bytes?.let { imageBytes -> viewModel.onImageSelected(imageBytes) }
        }
      }

  Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
      // Header
      Box(modifier = Modifier.fillMaxWidth().height(126.dp).background(BrandBlue)) {
        Surface(
            modifier =
                Modifier.padding(start = 20.dp, top = 24.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .clickable { onBack() },
            shape = RoundedCornerShape(999.dp),
            color = White.copy(alpha = 0.13f),
            border = BorderStroke(1.dp, White.copy(alpha = 0.18f)),
        ) {
          Box(
              modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp),
              contentAlignment = Alignment.Center,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = "Servi",
                  color = White,
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.ExtraBold
                      ),
              )
              Text(
                  text = "Ya",
                  color = BrandRed,
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.ExtraBold
                      ),
              )
            }
          }
        }

        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 20.dp, top = 28.dp),
            shape = RoundedCornerShape(999.dp),
            color = White.copy(alpha = 0.13f),
            border = BorderStroke(1.dp, White.copy(alpha = 0.18f)),
        ) {
          Box(
              modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
              contentAlignment = Alignment.Center,
          ) {
            Text(
                text = "COMPROBANTE",
                color = White,
                style =
                    MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            )
          }
        }
      }

      // Título
      Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
        Text(
            text = "SUBIR COMPROBANTE",
            style =
                MaterialTheme.typography.labelSmall.copy(
                    color = BrandBlue,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                ),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Adjunta tu comprobante SINPE",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )
      }

      // Info de la cita
      uiState.appointment?.let { appointment ->
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
          Column(
              modifier = Modifier.padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                  text = "Servicio",
                  style =
                      MaterialTheme.typography.bodySmall.copy(
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                      ),
              )
              Text(
                  text = appointment.services.firstOrNull()?.name ?: "",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
              )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                  text = "Total a pagar",
                  style =
                      MaterialTheme.typography.bodySmall.copy(
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                      ),
              )
              Text(
                  text = "₡${appointment.totalCost}",
                  style =
                      MaterialTheme.typography.bodyMedium.copy(
                          fontWeight = FontWeight.Bold,
                          color = BrandBlue,
                      ),
              )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                  text = "Trabajador",
                  style =
                      MaterialTheme.typography.bodySmall.copy(
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                      ),
              )
              Text(
                  text = appointment.workerName,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Zona de imagen
      Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            text = "Comprobante de pago",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.selectedImageBytes != null) {
          val uri =
              remember(uiState.selectedImageBytes) {
                val tempFile = File.createTempFile("receipt", ".jpg", context.cacheDir)
                tempFile.writeBytes(uiState.selectedImageBytes!!)
                Uri.fromFile(tempFile)
              }

          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .aspectRatio(3f / 4f)
                      .clip(RoundedCornerShape(16.dp))
                      .clickable { launcher.launch("image/*") }
          ) {
            KamelImage(
                resource = asyncPainterResource(uri.toString()),
                contentDescription = "Comprobante seleccionado",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            Box(
                modifier =
                    Modifier.align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
              Text(
                  text = "Toca para cambiar",
                  color = White,
                  style = MaterialTheme.typography.labelMedium,
              )
            }
          }
        } else {
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(200.dp)
                      .clip(RoundedCornerShape(16.dp))
                      .border(2.dp, BrandBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                      .background(BrandBlue.copy(alpha = 0.05f))
                      .clickable { launcher.launch("image/*") },
              contentAlignment = Alignment.Center,
          ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Text(text = "📎", fontSize = 40.sp)
              Text(
                  text = "Toca para seleccionar imagen",
                  style =
                      MaterialTheme.typography.bodyMedium.copy(
                          color = BrandBlue,
                          fontWeight = FontWeight.SemiBold,
                      ),
                  textAlign = TextAlign.Center,
              )
              Text(
                  text = "JPG o PNG",
                  style =
                      MaterialTheme.typography.bodySmall.copy(
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                      ),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Asegúrate que el comprobante sea legible",
            style =
                MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
      }

      uiState.errorMessage?.let { error ->
        Text(
            text = error,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
      }

      Spacer(modifier = Modifier.height(140.dp))
    }

    // Botón fijo abajo
    Column(
        modifier =
            Modifier.align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                                MaterialTheme.colorScheme.background,
                            )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
      Button(
          onClick = {
            val imageBytes = uiState.selectedImageBytes ?: return@Button
            coroutineScope.launch {
              try {
                val imageUrl =
                    withContext(Dispatchers.IO) {
                      val url = URL("https://api.cloudinary.com/v1_1/dfk5xx88f/image/upload")
                      val boundary = "Boundary${System.currentTimeMillis()}"

                      val connection = url.openConnection() as HttpURLConnection
                      connection.requestMethod = "POST"
                      connection.doOutput = true
                      connection.setRequestProperty(
                          "Content-Type",
                          "multipart/form-data; boundary=$boundary",
                      )

                      val output = connection.outputStream
                      output.write("--$boundary\r\n".toByteArray())
                      output.write(
                          "Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n"
                              .toByteArray()
                      )
                      output.write("seviya_unsigned\r\n".toByteArray())
                      output.write("--$boundary\r\n".toByteArray())
                      output.write(
                          "Content-Disposition: form-data; name=\"file\"; filename=\"receipt.jpg\"\r\n"
                              .toByteArray()
                      )
                      output.write("Content-Type: image/jpeg\r\n\r\n".toByteArray())
                      output.write(imageBytes)
                      output.write("\r\n--$boundary--\r\n".toByteArray())
                      output.flush()

                      val response = connection.inputStream.bufferedReader().readText()
                      println("DEBUG Cloudinary response: $response")

                      val json = JSONObject(response)
                      json.optString("secure_url", "")
                    }

                if (imageUrl.isNotBlank()) {
                  viewModel.onImageUploaded(imageUrl)
                } else {
                  viewModel.onUploadError("Error al subir la imagen")
                }
              } catch (e: Exception) {
                println("ERROR upload: ${e.message}")
                viewModel.onUploadError(e.message ?: "Error desconocido")
              }
            }
          },
          enabled = uiState.selectedImageBytes != null && !uiState.isUploading,
          modifier = Modifier.fillMaxWidth().height(56.dp),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
      ) {
        if (uiState.isUploading) {
          CircularProgressIndicator(
              color = White,
              modifier = Modifier.size(24.dp),
              strokeWidth = 2.dp,
          )
        } else {
          Text(
              text = "Enviar comprobante",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          )
        }
      }
    }

    if (uiState.isLoading) {
      Box(
          modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
          contentAlignment = Alignment.Center,
      ) {
        CircularProgressIndicator(color = White)
      }
    }
  }
}
