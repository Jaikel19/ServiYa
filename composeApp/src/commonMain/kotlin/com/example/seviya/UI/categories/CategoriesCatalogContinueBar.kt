package com.example.seviya.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seviya.theme.BorderSoftAlt
import com.example.seviya.theme.BrandBlue
import com.example.seviya.theme.InactiveSoft
import com.example.seviya.theme.White
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
internal fun ContinueButtonBar(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            ),
        shape = RoundedCornerShape(24.dp),
        color = White,
        border = BorderStroke(1.dp, BorderSoftAlt)
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandBlue,
                disabledContainerColor = InactiveSoft.copy(alpha = 0.35f),
                contentColor = White,
                disabledContentColor = White.copy(alpha = 0.75f)
            )
        ) {
            Text(
                text = "Ver trabajadores",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}