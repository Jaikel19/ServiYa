package com.example.seviya.feature.appointmentdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.seviya.core.designsystem.theme.White
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.DotsVertical

data class AppointmentStatusVisuals(
    val text: String,
    val backgroundColor: Color,
    val textColor: Color,
)

@Composable
fun AppointmentDetailTopHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    subtitleColor: Color,
    height: Dp,
    titleTextStyle: TextStyle,
    subtitleTextStyle: TextStyle,
    sideButtonSize: Dp,
    sideButtonBackgroundColor: Color,
    contentHorizontalPadding: Dp,
    contentVerticalPadding: Dp,
    titleTopPadding: Dp,
) {
  Box(
      modifier =
          modifier
              .fillMaxWidth()
              .height(height)
              .background(backgroundColor)
              .windowInsetsPadding(WindowInsets.statusBars)
  ) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = contentHorizontalPadding, vertical = contentVerticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
      AppointmentDetailIconButton(
          icon = TablerIcons.ArrowLeft,
          onClick = onBack,
          size = sideButtonSize,
          backgroundColor = sideButtonBackgroundColor,
      )

      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(top = titleTopPadding),
      ) {
        Text(text = title, color = White, style = titleTextStyle)
        Text(text = subtitle, color = subtitleColor, style = subtitleTextStyle)
      }

      AppointmentDetailIconButton(
          icon = TablerIcons.DotsVertical,
          onClick = {},
          size = sideButtonSize,
          backgroundColor = sideButtonBackgroundColor,
      )
    }
  }
}

@Composable
fun AppointmentDetailIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    size: Dp,
    backgroundColor: Color,
) {
  Box(
      modifier =
          Modifier.size(size).background(color = backgroundColor, shape = CircleShape).clickable {
            onClick()
          },
      contentAlignment = Alignment.Center,
  ) {
    Icon(imageVector = icon, contentDescription = null, tint = White)
  }
}

@Composable
fun AppointmentMessageBanner(
    text: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    shapeRadius: Dp = 22.dp,
    paddingValue: Dp = 16.dp,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(shapeRadius),
      color = backgroundColor,
      border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
  ) {
    Text(
        text = text,
        color = textColor,
        style = textStyle,
        modifier = Modifier.padding(paddingValue),
    )
  }
}

@Composable
fun AppointmentStatusChip(
    visuals: AppointmentStatusVisuals,
    modifier: Modifier = Modifier,
    cornerRadius: Dp,
    textStyle: TextStyle,
    horizontalPadding: Dp,
    verticalPadding: Dp,
) {
  Surface(
      modifier = modifier,
      shape = RoundedCornerShape(cornerRadius),
      color = visuals.backgroundColor,
  ) {
    Text(
        text = visuals.text,
        color = visuals.textColor,
        style = textStyle,
        modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
    )
  }
}

@Composable
fun AppointmentCancellationDialogShell(
    title: String,
    titleColor: Color,
    badgeText: String,
    badgeBackgroundColor: Color,
    badgeTextColor: Color,
    confirmText: String,
    dismissText: String,
    confirmEnabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
  AlertDialog(
      onDismissRequest = onDismiss,
      containerColor = White,
      shape = RoundedCornerShape(30.dp),
      title = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
              text = title,
              style =
                  MaterialTheme.typography.headlineSmall.copy(
                      fontWeight = FontWeight.ExtraBold,
                      color = titleColor,
                  ),
          )

          Surface(shape = RoundedCornerShape(16.dp), color = badgeBackgroundColor) {
            Text(
                text = badgeText,
                color = badgeTextColor,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            )
          }
        }
      },
      text = { Column(verticalArrangement = Arrangement.spacedBy(14.dp), content = content) },
      confirmButton = {
        TextButton(onClick = onConfirm, enabled = confirmEnabled) {
          Text(
              text = confirmText,
              color = if (confirmEnabled) badgeTextColor else Color(0xFFE3A5A5),
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
          )
        }
      },
      dismissButton = {
        TextButton(onClick = onDismiss) {
          Text(
              text = dismissText,
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          )
        }
      },
  )
}

@Composable
fun AppointmentCancellationDetailRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
        text = label,
        style =
            MaterialTheme.typography.labelLarge.copy(
                color = labelColor,
                fontWeight = FontWeight.ExtraBold,
            ),
    )

    Text(
        text = value,
        style =
            MaterialTheme.typography.bodyLarge.copy(
                color = valueColor,
                fontWeight = FontWeight.SemiBold,
            ),
    )
  }
}

@Composable
fun AppointmentCancellationWarningCard(
    message: String,
    title: String,
    backgroundColor: Color,
    borderColor: Color,
    titleColor: Color,
    textColor: Color,
) {
  Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      color = backgroundColor,
      border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Text(
          text = title,
          style =
              MaterialTheme.typography.labelLarge.copy(
                  color = titleColor,
                  fontWeight = FontWeight.ExtraBold,
              ),
      )

      Text(
          text = message,
          style =
              MaterialTheme.typography.bodyMedium.copy(
                  color = textColor,
                  fontWeight = FontWeight.SemiBold,
              ),
      )
    }
  }
}
