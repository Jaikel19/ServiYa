package com.example.seviya.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronLeft

@Composable
fun FullScreenMenu(
    title: String = "Menú",
    options: List<MenuOption>,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF7F7F8)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7F7F8))
                    .padding(start = 10.dp, end = 18.dp, top = 10.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = TablerIcons.ChevronLeft,
                        contentDescription = "Cerrar menú",
                        tint = Color(0xFF1F1F1F)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp
                    ),
                    color = Color(0xFF1F1F1F)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 6.dp, bottom = 20.dp)
            ) {
                options.forEachIndexed { index, option ->
                    MenuItem(option = option)

                    if (index < options.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 72.dp, end = 18.dp)
                                .height(1.dp)
                                .background(Color(0xFFE9E9EC))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItem(option: MenuOption) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = option.onClick)
            .padding(horizontal = 18.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(54.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                tint = option.iconColor,
                modifier = Modifier.size(26.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.title,
                color = Color(0xFF202124),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = option.subtitle,
                color = Color(0xFF8A8F98),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
            )
        }
    }
}