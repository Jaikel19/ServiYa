package com.example.seviya.UI

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.seviya.core.designsystem.theme.White
import compose.icons.TablerIcons
import compose.icons.tablericons.Apps
import compose.icons.tablericons.Briefcase
import compose.icons.tablericons.Brush
import compose.icons.tablericons.Building
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.Camera
import compose.icons.tablericons.Car
import compose.icons.tablericons.Droplet
import compose.icons.tablericons.Globe
import compose.icons.tablericons.Paint
import compose.icons.tablericons.Plug
import compose.icons.tablericons.Scissors
import compose.icons.tablericons.Settings
import compose.icons.tablericons.Tree
import compose.icons.tablericons.User

internal data class CategoryVisuals(
    val icon: ImageVector,
    val accent: Color,
    val accentSecondary: Color,
    val cardTint: Color,
    val iconPlateStart: Color,
    val iconPlateEnd: Color,
    val iconInner: Color,
    val iconTint: Color,
    val buttonBackground: Color,
    val buttonText: Color
)

internal fun categoryVisual(categoryId: String): CategoryVisuals {
    return when (categoryId.normalizedCategoryKey()) {
        "automotriz" -> CategoryVisuals(
            icon = TablerIcons.Car,
            accent = Color(0xFFE25578),
            accentSecondary = Color(0xFFF08CA1),
            cardTint = Color(0xFFFFF6F8),
            iconPlateStart = Color(0xFFFFE0E8),
            iconPlateEnd = Color(0xFFFFCFDA),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFFD24166),
            buttonBackground = Color(0xFFFFEAF0),
            buttonText = Color(0xFFD24166)
        )

        "barberia" -> CategoryVisuals(
            icon = TablerIcons.Scissors,
            accent = Color(0xFF6C63D9),
            accentSecondary = Color(0xFF9A92F2),
            cardTint = Color(0xFFF8F7FF),
            iconPlateStart = Color(0xFFE5E2FF),
            iconPlateEnd = Color(0xFFD8D3FF),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF5C54C7),
            buttonBackground = Color(0xFFEFEDFF),
            buttonText = Color(0xFF5C54C7)
        )

        "belleza" -> CategoryVisuals(
            icon = TablerIcons.Scissors,
            accent = Color(0xFFD85D9E),
            accentSecondary = Color(0xFFF1A5C7),
            cardTint = Color(0xFFFFF7FB),
            iconPlateStart = Color(0xFFF9E1EC),
            iconPlateEnd = Color(0xFFF4CFE0),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFFBF4B89),
            buttonBackground = Color(0xFFFDEAF3),
            buttonText = Color(0xFFBF4B89)
        )

        "construccion-y-remodelacion" -> CategoryVisuals(
            icon = TablerIcons.Building,
            accent = Color(0xFFD66A4A),
            accentSecondary = Color(0xFFF0A07E),
            cardTint = Color(0xFFFFF7F4),
            iconPlateStart = Color(0xFFFFE5DB),
            iconPlateEnd = Color(0xFFFFD3C4),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFFC15739),
            buttonBackground = Color(0xFFFFEEE7),
            buttonText = Color(0xFFC15739)
        )

        "cuidado-y-acompanamiento" -> CategoryVisuals(
            icon = TablerIcons.User,
            accent = Color(0xFF8C7AE6),
            accentSecondary = Color(0xFFB6A7F6),
            cardTint = Color(0xFFF9F7FF),
            iconPlateStart = Color(0xFFEAE5FF),
            iconPlateEnd = Color(0xFFDDD5FF),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF7564D6),
            buttonBackground = Color(0xFFF1EDFF),
            buttonText = Color(0xFF7564D6)
        )

        "deportes-y-entrenamiento" -> CategoryVisuals(
            icon = TablerIcons.Apps,
            accent = Color(0xFF3FAE74),
            accentSecondary = Color(0xFF84D3A1),
            cardTint = Color(0xFFF4FCF7),
            iconPlateStart = Color(0xFFDFF5E6),
            iconPlateEnd = Color(0xFFCDEED9),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF2F8E5A),
            buttonBackground = Color(0xFFEAF7EF),
            buttonText = Color(0xFF2F8E5A)
        )

        "diseno-y-creatividad" -> CategoryVisuals(
            icon = TablerIcons.Paint,
            accent = Color(0xFF9B5DE5),
            accentSecondary = Color(0xFFC89AF4),
            cardTint = Color(0xFFFBF7FF),
            iconPlateStart = Color(0xFFF0E2FF),
            iconPlateEnd = Color(0xFFE4D0FF),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF8347C8),
            buttonBackground = Color(0xFFF4EBFF),
            buttonText = Color(0xFF8347C8)
        )

        "educacion" -> CategoryVisuals(
            icon = TablerIcons.Briefcase,
            accent = Color(0xFF4B8FD8),
            accentSecondary = Color(0xFF8EC0F5),
            cardTint = Color(0xFFF5FAFF),
            iconPlateStart = Color(0xFFE2F0FF),
            iconPlateEnd = Color(0xFFD2E7FF),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF3B78BC),
            buttonBackground = Color(0xFFEBF5FF),
            buttonText = Color(0xFF3B78BC)
        )

        "electricidad" -> CategoryVisuals(
            icon = TablerIcons.Plug,
            accent = Color(0xFFE2B100),
            accentSecondary = Color(0xFFF0C94D),
            cardTint = Color(0xFFFFFCF1),
            iconPlateStart = Color(0xFFFFF4C6),
            iconPlateEnd = Color(0xFFFFEAA2),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFFB88900),
            buttonBackground = Color(0xFFFFF6D8),
            buttonText = Color(0xFFB88900)
        )

        "eventos" -> CategoryVisuals(
            icon = TablerIcons.CalendarEvent,
            accent = Color(0xFFE06C5F),
            accentSecondary = Color(0xFFF3A297),
            cardTint = Color(0xFFFFF7F5),
            iconPlateStart = Color(0xFFFFE5E0),
            iconPlateEnd = Color(0xFFFFD5CE),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFFC85A4E),
            buttonBackground = Color(0xFFFFEEE9),
            buttonText = Color(0xFFC85A4E)
        )

        "fotografia-y-video" -> CategoryVisuals(
            icon = TablerIcons.Camera,
            accent = Color(0xFFC95C9A),
            accentSecondary = Color(0xFFE59BC1),
            cardTint = Color(0xFFFFF7FB),
            iconPlateStart = Color(0xFFF8E0EC),
            iconPlateEnd = Color(0xFFF3D1E2),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFFB24A86),
            buttonBackground = Color(0xFFFBEAF3),
            buttonText = Color(0xFFB24A86)
        )

        "gastronomia" -> CategoryVisuals(
            icon = TablerIcons.Briefcase,
            accent = Color(0xFFE08A3A),
            accentSecondary = Color(0xFFF5B26B),
            cardTint = Color(0xFFFFFAF5),
            iconPlateStart = Color(0xFFFFECD8),
            iconPlateEnd = Color(0xFFFFDFC0),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFFC97528),
            buttonBackground = Color(0xFFFFF1E3),
            buttonText = Color(0xFFC97528)
        )

        "hogar" -> CategoryVisuals(
            icon = TablerIcons.Building,
            accent = Color(0xFF5D8AA8),
            accentSecondary = Color(0xFF9BBFD2),
            cardTint = Color(0xFFF6FBFD),
            iconPlateStart = Color(0xFFE4F1F7),
            iconPlateEnd = Color(0xFFD5E8F2),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF4E7691),
            buttonBackground = Color(0xFFEDF6FA),
            buttonText = Color(0xFF4E7691)
        )

        "idiomas" -> CategoryVisuals(
            icon = TablerIcons.Globe,
            accent = Color(0xFF2FA7B7),
            accentSecondary = Color(0xFF79D1DB),
            cardTint = Color(0xFFF3FCFD),
            iconPlateStart = Color(0xFFDFF7FA),
            iconPlateEnd = Color(0xFFCBEFF4),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF248C99),
            buttonBackground = Color(0xFFE9F8FA),
            buttonText = Color(0xFF248C99)
        )

        "jardineria" -> CategoryVisuals(
            icon = TablerIcons.Tree,
            accent = Color(0xFF72A83B),
            accentSecondary = Color(0xFFA2C96A),
            cardTint = Color(0xFFF8FDF3),
            iconPlateStart = Color(0xFFE8F5D4),
            iconPlateEnd = Color(0xFFD9EFB8),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF5F8E2F),
            buttonBackground = Color(0xFFF0F7E2),
            buttonText = Color(0xFF5F8E2F)
        )

        "limpieza" -> CategoryVisuals(
            icon = TablerIcons.Brush,
            accent = Color(0xFF38A169),
            accentSecondary = Color(0xFF68C08F),
            cardTint = Color(0xFFF4FCF7),
            iconPlateStart = Color(0xFFDDF4E6),
            iconPlateEnd = Color(0xFFCDEDD9),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF2F855A),
            buttonBackground = Color(0xFFEAF7EF),
            buttonText = Color(0xFF2F855A)
        )

        "mascotas" -> CategoryVisuals(
            icon = TablerIcons.User,
            accent = Color(0xFF4CB5AE),
            accentSecondary = Color(0xFF8ADAD1),
            cardTint = Color(0xFFF4FCFB),
            iconPlateStart = Color(0xFFDDF6F3),
            iconPlateEnd = Color(0xFFCDEEE9),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF379A93),
            buttonBackground = Color(0xFFEAF8F6),
            buttonText = Color(0xFF379A93)
        )

        "moda-y-confeccion" -> CategoryVisuals(
            icon = TablerIcons.Scissors,
            accent = Color(0xFFD86A9A),
            accentSecondary = Color(0xFFF0A9C7),
            cardTint = Color(0xFFFFF7FB),
            iconPlateStart = Color(0xFFF9E2EC),
            iconPlateEnd = Color(0xFFF3D1E0),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFFBF5686),
            buttonBackground = Color(0xFFFDEBF3),
            buttonText = Color(0xFFBF5686)
        )

        "mudanzas-y-transporte" -> CategoryVisuals(
            icon = TablerIcons.Car,
            accent = Color(0xFFE07A3A),
            accentSecondary = Color(0xFFF4B178),
            cardTint = Color(0xFFFFF8F3),
            iconPlateStart = Color(0xFFFFE8D8),
            iconPlateEnd = Color(0xFFFFD9C3),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFFC9672C),
            buttonBackground = Color(0xFFFFF0E5),
            buttonText = Color(0xFFC9672C)
        )

        "musica-y-arte" -> CategoryVisuals(
            icon = TablerIcons.Paint,
            accent = Color(0xFF8F67D8),
            accentSecondary = Color(0xFFC0A2F0),
            cardTint = Color(0xFFF9F7FF),
            iconPlateStart = Color(0xFFEAE3FF),
            iconPlateEnd = Color(0xFFDDD2FF),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF7852C1),
            buttonBackground = Color(0xFFF1EDFF),
            buttonText = Color(0xFF7852C1)
        )

        "salud-y-bienestar" -> CategoryVisuals(
            icon = TablerIcons.User,
            accent = Color(0xFF49A99A),
            accentSecondary = Color(0xFF8AD5C8),
            cardTint = Color(0xFFF4FCFA),
            iconPlateStart = Color(0xFFDDF5F0),
            iconPlateEnd = Color(0xFFCAEDE5),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF358C7F),
            buttonBackground = Color(0xFFEAF8F4),
            buttonText = Color(0xFF358C7F)
        )

        "tecnologia" -> CategoryVisuals(
            icon = TablerIcons.Settings,
            accent = Color(0xFF5B7CFA),
            accentSecondary = Color(0xFF93A8FF),
            cardTint = Color(0xFFF6F8FF),
            iconPlateStart = Color(0xFFE4E9FF),
            iconPlateEnd = Color(0xFFD5DDFF),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF4968DE),
            buttonBackground = Color(0xFFEDF1FF),
            buttonText = Color(0xFF4968DE)
        )

        "tramites-y-gestiones" -> CategoryVisuals(
            icon = TablerIcons.Briefcase,
            accent = Color(0xFF6B7A99),
            accentSecondary = Color(0xFFA1AEC8),
            cardTint = Color(0xFFF7F9FC),
            iconPlateStart = Color(0xFFE8EDF5),
            iconPlateEnd = Color(0xFFDCE4F0),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF596780),
            buttonBackground = Color(0xFFEEF2F7),
            buttonText = Color(0xFF596780)
        )

        "plomeria" -> CategoryVisuals(
            icon = TablerIcons.Droplet,
            accent = Color(0xFF16A6B6),
            accentSecondary = Color(0xFF4FC3D9),
            cardTint = Color(0xFFF2FCFD),
            iconPlateStart = Color(0xFFDDF7FA),
            iconPlateEnd = Color(0xFFC8EFF4),
            iconInner = White.copy(alpha = 0.92f),
            iconTint = Color(0xFF138A99),
            buttonBackground = Color(0xFFE8F8FA),
            buttonText = Color(0xFF138A99)
        )

        else -> CategoryVisuals(
            icon = TablerIcons.Briefcase,
            accent = Color(0xFF6B7A99),
            accentSecondary = Color(0xFFA1AEC8),
            cardTint = Color(0xFFF7F9FC),
            iconPlateStart = Color(0xFFE8EDF5),
            iconPlateEnd = Color(0xFFDCE4F0),
            iconInner = White.copy(alpha = 0.94f),
            iconTint = Color(0xFF596780),
            buttonBackground = Color(0xFFEEF2F7),
            buttonText = Color(0xFF596780)
        )
    }
}

private fun String.normalizedCategoryKey(): String {
    return this
        .trim()
        .lowercase()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace("ñ", "n")
}