package ar.com.gaston.factx.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class FactXThemeMode {
    LIGHT,
    DARK
}

data class FactXColorPalette(
    val background: Color,
    val surface: Color,
    val surfaceSecondary: Color,
    val border: Color,
    val divider: Color,
    val primary: Color,
    val primarySoft: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val sidebar: Color,
    val activeNavigation: Color,
    val activeNavigationContent: Color,
    val navigationHover: Color,
    val logoCyan: Color,
    val logoCyanBlue: Color,
    val logoMediumBlue: Color,
    val logoDarkBlue: Color
)

val FactXLightColors = FactXColorPalette(
    background = Color(0xFFF6F8FC),
    surface = Color(0xFFFFFFFF),
    surfaceSecondary = Color(0xFFF0F4FA),
    border = Color(0xFFE5EAF1),
    divider = Color(0xFFEEF1F5),
    primary = Color(0xFF2563EB),
    primarySoft = Color(0xFFEFF6FF),
    accent = Color(0xFF14B8A6),
    textPrimary = Color(0xFF172033),
    textSecondary = Color(0xFF58657A),
    textMuted = Color(0xFF8A95A7),
    success = Color(0xFF16A36A),
    warning = Color(0xFFF0A63A),
    danger = Color(0xFFE35D6A),
    sidebar = Color(0xFFFBFCFE),
    activeNavigation = Color(0xFF2563EB),
    activeNavigationContent = Color(0xFFFFFFFF),
    navigationHover = Color(0xFFF1F5F9),
    logoCyan = Color(0xFF53BCE9),
    logoCyanBlue = Color(0xFF3EA3E3),
    logoMediumBlue = Color(0xFF2377D7),
    logoDarkBlue = Color(0xFF0A59BC)
)

val FactXDarkColors = FactXColorPalette(
    background = Color(0xFF0F172A),
    surface = Color(0xFF182235),
    surfaceSecondary = Color(0xFF202C3F),
    border = Color(0xFF334155),
    divider = Color(0xFF2A3A50),
    primary = Color(0xFF3B82F6),
    primarySoft = Color(0xFF172D52),
    accent = Color(0xFF38BDF8),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFFCBD5E1),
    textMuted = Color(0xFF94A3B8),
    success = Color(0xFF34D399),
    warning = Color(0xFFFBBF24),
    danger = Color(0xFFFB7185),
    sidebar = Color(0xFF111827),
    activeNavigation = Color(0xFF2563EB),
    activeNavigationContent = Color(0xFFFFFFFF),
    navigationHover = Color(0xFF1E293B),
    logoCyan = Color(0xFF53BCE9),
    logoCyanBlue = Color(0xFF3EA3E3),
    logoMediumBlue = Color(0xFF2377D7),
    logoDarkBlue = Color(0xFF0A59BC)
)

fun FactXThemeMode.colors(): FactXColorPalette = when (this) {
    FactXThemeMode.LIGHT -> FactXLightColors
    FactXThemeMode.DARK -> FactXDarkColors
}

object FactXTokens {
    val SidebarWidth = 244.dp
    val ContentMaxWidth = 1480.dp
    val PagePadding = 32.dp
    val PageVerticalPadding = 18.dp
    val SectionGap = 18.dp
    val CardRadius = 14.dp
    val SmallRadius = 10.dp
    val KpiHeight = 144.dp
    val ChartHeight = 232.dp
    val ScrollThreshold = 820.dp
    val TitleSize = 31.sp
    val BodySize = 14.sp
}
