package ar.com.gaston.factx.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(1400.dp, 880.dp)
    )

    Window(onCloseRequest = ::exitApplication, state = windowState, title = "FactX") {
        window.minimumSize = Dimension(1180, 720)
        FactXApplication()
    }
}

private enum class Glyph {
    DASHBOARD,
    RECEIVED,
    ISSUED,
    SUPPLIERS,
    CUSTOMERS,
    PAYMENTS,
    EXPORT,
    DATABASE,
    CALENDAR,
    DOCUMENTS,
    MOON,
    SUN
}

private enum class SemanticTone {
    PRIMARY,
    ACCENT,
    SUCCESS,
    WARNING,
    DANGER
}

private enum class Destination(val label: String, val milestone: String?, val glyph: Glyph) {
    DASHBOARD("Inicio", null, Glyph.DASHBOARD),
    RECEIVED("Recibidos", "Disponible al conectar servicios en v0.2.x", Glyph.RECEIVED),
    ISSUED("Emitidos", "Disponible al conectar servicios en v0.2.x", Glyph.ISSUED),
    SUPPLIERS("Proveedores", "Disponible en v0.1.x", Glyph.SUPPLIERS),
    CUSTOMERS("Clientes", "Disponible después de la primera UI de proveedores", Glyph.CUSTOMERS),
    PAYMENTS("Pagos y cobros", "Disponible en un hito posterior de v1", Glyph.PAYMENTS),
    EXPORT("Exportar", "Disponible en v0.5.x", Glyph.EXPORT)
}

private data class DashboardMetric(
    val label: String,
    val value: String,
    val context: String,
    val tone: SemanticTone,
    val glyph: Glyph
)

private data class LatestDocument(
    val counterparty: String,
    val type: String,
    val number: String,
    val issuedOn: String,
    val dueOn: String,
    val total: String,
    val status: String,
    val statusTone: SemanticTone
)

private val dashboardMetrics = listOf(
    DashboardMetric("Pendiente de pagar", "$ 215.400", "12 comprobantes", SemanticTone.WARNING, Glyph.PAYMENTS),
    DashboardMetric("Pendiente de cobrar", "$ 178.750", "10 comprobantes", SemanticTone.PRIMARY, Glyph.ISSUED),
    DashboardMetric("Vencen esta semana", "7", "Total $ 96.250", SemanticTone.DANGER, Glyph.CALENDAR),
    DashboardMetric("Comprobantes del mes", "24", "Recibidos + emitidos", SemanticTone.SUCCESS, Glyph.DOCUMENTS)
)

private val latestDocuments = listOf(
    LatestDocument("FactX Demo Proveedora Alfa", "FACTURA", "A-0001-00012345", "12/06/2026", "25/06/2026", "$ 125.000", "PENDIENTE", SemanticTone.WARNING),
    LatestDocument("FactX Demo Cliente Norte", "FACTURA", "B-0002-00054321", "11/06/2026", "26/06/2026", "$ 89.000", "PARCIAL", SemanticTone.PRIMARY),
    LatestDocument("FactX Demo Insumos Beta", "TICKET", "T-0000-00007654", "10/06/2026", "10/06/2026", "$ 987,65", "PAGADO", SemanticTone.SUCCESS),
    LatestDocument("FactX Demo Cliente Sur", "TICKET", "B-0003-00001980", "09/06/2026", "24/06/2026", "$ 1.250,50", "COBRADO", SemanticTone.ACCENT),
    LatestDocument("FactX Demo Servicios Gamma", "PRESUPUESTO", "P-0000-00000428", "08/06/2026", "22/06/2026", "$ 45.500", "PENDIENTE", SemanticTone.WARNING)
)

private fun SemanticTone.colorFor(colors: FactXColorPalette): Color = when (this) {
    SemanticTone.PRIMARY -> colors.primary
    SemanticTone.ACCENT -> colors.accent
    SemanticTone.SUCCESS -> colors.success
    SemanticTone.WARNING -> colors.warning
    SemanticTone.DANGER -> colors.danger
}

@Composable
private fun FactXApplication() {
    var destination by remember { mutableStateOf(Destination.DASHBOARD) }
    var themeMode by remember { mutableStateOf(FactXThemeMode.LIGHT) }
    val colors = themeMode.colors()

    Row(Modifier.fillMaxSize().background(colors.background)) {
        Sidebar(destination, colors, onDestinationSelected = { destination = it })
        when (destination) {
            Destination.DASHBOARD -> Dashboard(colors, themeMode) {
                themeMode = if (themeMode == FactXThemeMode.LIGHT) FactXThemeMode.DARK else FactXThemeMode.LIGHT
            }
            else -> Placeholder(destination, colors)
        }
    }
}

@Composable
private fun Sidebar(selected: Destination, colors: FactXColorPalette, onDestinationSelected: (Destination) -> Unit) {
    Row(Modifier.width(FactXTokens.SidebarWidth).fillMaxHeight().background(colors.sidebar)) {
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight().padding(horizontal = 18.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            FactXLogo(colors)
            Spacer(Modifier.height(34.dp))
            Destination.entries.forEach { destination ->
                NavigationItem(destination, destination == selected, colors) { onDestinationSelected(destination) }
            }
            Spacer(Modifier.weight(1f))
            Text("Modo demostración", color = colors.textMuted, fontSize = 11.sp)
        }
        Box(Modifier.width(1.dp).fillMaxHeight().background(colors.border))
    }
}

@Composable
private fun FactXLogo(colors: FactXColorPalette) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(40.dp)) {
            val upperLeftArm = Path().apply {
                moveTo(size.width * .08f, size.height * .07f)
                lineTo(size.width * .32f, size.height * .07f)
                lineTo(size.width * .49f, size.height * .435f)
                lineTo(size.width * .43f, size.height * .435f)
                close()
            }
            val upperRightArm = Path().apply {
                moveTo(size.width * .68f, size.height * .07f)
                lineTo(size.width * .92f, size.height * .07f)
                lineTo(size.width * .57f, size.height * .435f)
                lineTo(size.width * .51f, size.height * .435f)
                close()
            }
            val lowerLeftArm = Path().apply {
                moveTo(size.width * .08f, size.height * .93f)
                lineTo(size.width * .32f, size.height * .93f)
                lineTo(size.width * .49f, size.height * .565f)
                lineTo(size.width * .43f, size.height * .565f)
                close()
            }
            val lowerRightArm = Path().apply {
                moveTo(size.width * .68f, size.height * .93f)
                lineTo(size.width * .92f, size.height * .93f)
                lineTo(size.width * .57f, size.height * .565f)
                lineTo(size.width * .51f, size.height * .565f)
                close()
            }
            drawPath(upperLeftArm, colors.logoCyan)
            drawPath(upperRightArm, colors.logoCyanBlue)
            drawPath(lowerLeftArm, colors.logoDarkBlue)
            drawPath(lowerRightArm, colors.logoMediumBlue)
        }
        Spacer(Modifier.width(11.dp))
        Text("FactX", color = colors.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 23.sp)
    }
}

@Composable
private fun NavigationItem(destination: Destination, selected: Boolean, colors: FactXColorPalette, onClick: () -> Unit) {
    val background = if (selected) colors.activeNavigation else Color.Transparent
    val contentColor = if (selected) colors.activeNavigationContent else colors.textSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(FactXTokens.SmallRadius))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FactXGlyph(destination.glyph, contentColor, Modifier.size(21.dp))
        Spacer(Modifier.width(11.dp))
        Text(destination.label, color = contentColor, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium, fontSize = FactXTokens.BodySize)
    }
}

@Composable
private fun Dashboard(colors: FactXColorPalette, themeMode: FactXThemeMode, onThemeToggle: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val useVerticalScroll = maxHeight < FactXTokens.ScrollThreshold
        val contentHeightModifier = if (useVerticalScroll) {
            Modifier.verticalScroll(rememberScrollState())
        } else {
            Modifier.fillMaxHeight()
        }

        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = FactXTokens.ContentMaxWidth)
                    .fillMaxWidth()
                    .then(contentHeightModifier)
                    .padding(horizontal = FactXTokens.PagePadding, vertical = FactXTokens.PageVerticalPadding)
            ) {
                DashboardHeader(colors, themeMode, onThemeToggle)
                Spacer(Modifier.height(24.dp))
                KpiRow(colors)
                Spacer(Modifier.height(FactXTokens.SectionGap))
                ChartRow(colors)
                Spacer(Modifier.height(FactXTokens.SectionGap))
                LatestDocumentsPanel(
                    colors,
                    Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(colors: FactXColorPalette, themeMode: FactXThemeMode, onThemeToggle: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text("Dashboard", color = colors.textPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text("Resumen operativo", color = colors.textSecondary, fontSize = FactXTokens.BodySize)
        }
        Spacer(Modifier.weight(1f))
        ThemeToggle(colors, themeMode, onThemeToggle)
        Spacer(Modifier.width(10.dp))
        DemoBadge(colors)
    }
}

@Composable
private fun DemoBadge(colors: FactXColorPalette) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(colors.primarySoft)
            .border(1.dp, colors.primary.copy(alpha = .24f), RoundedCornerShape(50))
            .padding(horizontal = 11.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FactXGlyph(Glyph.DATABASE, colors.primary, Modifier.size(15.dp))
        Spacer(Modifier.width(7.dp))
        Text("Datos demo", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ThemeToggle(colors: FactXColorPalette, themeMode: FactXThemeMode, onThemeToggle: () -> Unit) {
    val isLight = themeMode == FactXThemeMode.LIGHT
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(50))
            .background(colors.surfaceSecondary)
            .border(1.dp, colors.border, RoundedCornerShape(50))
            .clickable(onClick = onThemeToggle),
        contentAlignment = Alignment.Center
    ) {
        FactXGlyph(if (isLight) Glyph.MOON else Glyph.SUN, colors.textSecondary, Modifier.size(17.dp))
    }
}

@Composable
private fun KpiRow(colors: FactXColorPalette) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(FactXTokens.SectionGap),
        modifier = Modifier.fillMaxWidth().height(FactXTokens.KpiHeight)
    ) {
        dashboardMetrics.forEach { metric ->
            MetricCard(metric, colors, Modifier.weight(1f).fillMaxHeight())
        }
    }
}

@Composable
private fun MetricCard(metric: DashboardMetric, colors: FactXColorPalette, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(FactXTokens.CardRadius)
    val accent = metric.tone.colorFor(colors)
    Column(
        modifier = Modifier
            .shadow(2.dp, shape)
            .clip(shape)
            .background(colors.surface)
            .border(1.dp, colors.border, shape)
            .then(modifier)
            .padding(horizontal = 18.dp, vertical = 15.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent.copy(alpha = .16f)),
                contentAlignment = Alignment.Center
            ) {
                FactXGlyph(metric.glyph, accent, Modifier.size(20.dp))
            }
            Spacer(Modifier.width(11.dp))
            Text(metric.label, color = colors.textSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(12.dp))
        Text(metric.value, color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 29.sp)
        Spacer(Modifier.height(2.dp))
        Text(metric.context, color = colors.textSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun ChartRow(colors: FactXColorPalette) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(FactXTokens.SectionGap),
        modifier = Modifier.fillMaxWidth().height(FactXTokens.ChartHeight)
    ) {
        SectionCard("Documentos por estado", colors, Modifier.weight(1f).fillMaxHeight()) {
            StatusBarChart(colors)
        }
        SectionCard("Distribución comercial", colors, Modifier.weight(1f).fillMaxHeight()) {
            CommercialDistribution(colors)
        }
    }
}

@Composable
private fun SectionCard(title: String, colors: FactXColorPalette, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(FactXTokens.CardRadius)
    Column(
        modifier = Modifier
            .shadow(2.dp, shape)
            .clip(shape)
            .background(colors.surface)
            .border(1.dp, colors.border, shape)
            .then(modifier)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = {
            Text(title, color = colors.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            content()
        }
    )
}

@Composable
private fun StatusBarChart(colors: FactXColorPalette) {
    val values = listOf(
        Triple("Pendientes", 12, SemanticTone.WARNING),
        Triple("Parciales", 5, SemanticTone.PRIMARY),
        Triple("Cobrados /\npagados", 19, SemanticTone.SUCCESS)
    )
    val maxValue = values.maxOf { it.second }.toFloat()

    Box(Modifier.fillMaxWidth().height(162.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val gridBottom = size.height - 39.dp.toPx()
            listOf(.16f, .39f, .62f, .85f).forEach { fraction ->
                drawLine(
                    color = colors.border.copy(alpha = .78f),
                    start = Offset(0f, gridBottom * fraction),
                    end = Offset(size.width, gridBottom * fraction),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxSize().padding(top = 2.dp, bottom = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            values.forEach { (label, value, tone) ->
                val color = tone.colorFor(colors)
                val barHeight = (value / maxValue * 92f).dp
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.weight(1f))
                    Text(value.toString(), color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(5.dp))
                    Box(
                        Modifier
                            .width(56.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(topStart = 7.dp, topEnd = 7.dp))
                            .background(color)
                    )
                    Spacer(Modifier.height(9.dp))
                    Text(
                        label,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        lineHeight = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommercialDistribution(colors: FactXColorPalette) {
    Row(Modifier.fillMaxWidth().height(162.dp), verticalAlignment = Alignment.CenterVertically) {
        DonutChart(colors)
        Spacer(Modifier.width(22.dp))
        Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
            DistributionLegend("Recibidos", 14, "58%", colors.accent, colors)
            DistributionLegend("Emitidos", 10, "42%", colors.primary, colors)
        }
    }
}

@Composable
private fun DonutChart(colors: FactXColorPalette) {
    Box(Modifier.size(154.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 18.dp.toPx()
            val inset = stroke / 2f
            val chartSize = Size(size.width - stroke, size.height - stroke)
            val receivedSweep = 205f
            val issuedSweep = 143f
            drawArc(
                color = colors.accent,
                startAngle = -90f,
                sweepAngle = receivedSweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = chartSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = colors.primary,
                startAngle = -90f + receivedSweep + 12f,
                sweepAngle = issuedSweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = chartSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total", color = colors.textSecondary, fontSize = 12.sp)
            Text("24", color = colors.textPrimary, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DistributionLegend(label: String, quantity: Int, percentage: String, color: Color, colors: FactXColorPalette) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(9.dp).clip(RoundedCornerShape(50)).background(color))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, color = colors.textSecondary, fontSize = 12.sp)
            Text("$quantity comprobantes · $percentage", color = colors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun LatestDocumentsPanel(colors: FactXColorPalette, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(FactXTokens.CardRadius)
    Column(
        modifier = Modifier
            .shadow(2.dp, shape)
            .clip(shape)
            .background(colors.surface)
            .border(1.dp, colors.border, shape)
            .then(modifier)
            .padding(18.dp)
    ) {
        Text("Últimos comprobantes", color = colors.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(10.dp))
        DocumentTableHeader(colors)
        latestDocuments.forEachIndexed { index, document ->
            DocumentTableRow(document, colors)
            if (index < latestDocuments.lastIndex) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.divider))
            }
        }
        Spacer(Modifier.height(2.dp))
        Text("Ver todos los comprobantes", color = colors.primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DocumentTableHeader(colors: FactXColorPalette) {
    Row(Modifier.fillMaxWidth().padding(bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        TableCell("Proveedor / Cliente", 2.35f, colors.textSecondary, FontWeight.SemiBold)
        TableCell("Tipo", 1.0f, colors.textSecondary, FontWeight.SemiBold)
        TableCell("Número", 1.55f, colors.textSecondary, FontWeight.SemiBold)
        TableCell("Emisión", 1.2f, colors.textSecondary, FontWeight.SemiBold)
        TableCell("Vencimiento", 1.25f, colors.textSecondary, FontWeight.SemiBold)
        TableCell("Total", 1.05f, colors.textSecondary, FontWeight.SemiBold, TextAlign.End)
        Spacer(Modifier.width(12.dp))
        Text("Estado", color = colors.textSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.width(88.dp))
        Spacer(Modifier.width(8.dp))
        Text("⋯", color = colors.textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.width(16.dp))
    }
}

@Composable
private fun DocumentTableRow(document: LatestDocument, colors: FactXColorPalette) {
    Row(
        Modifier.fillMaxWidth().height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(document.counterparty, 2.35f, colors.textPrimary, FontWeight.Medium)
        TableCell(document.type, 1.0f, colors.textSecondary, FontWeight.Medium)
        TableCell(document.number, 1.55f, colors.textSecondary, FontWeight.Normal)
        TableCell(document.issuedOn, 1.2f, colors.textSecondary, FontWeight.Normal)
        TableCell(document.dueOn, 1.25f, colors.textSecondary, FontWeight.Normal)
        TableCell(document.total, 1.05f, colors.textPrimary, FontWeight.SemiBold, TextAlign.End)
        Spacer(Modifier.width(12.dp))
        StatusBadge(document.status, document.statusTone.colorFor(colors))
        Spacer(Modifier.width(8.dp))
        MoreIndicator(colors)
    }
}

@Composable
private fun RowScope.TableCell(
    value: String,
    weight: Float,
    color: Color,
    fontWeight: FontWeight,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = value,
        color = color,
        fontSize = 11.sp,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(weight).padding(end = 8.dp)
    )
}

@Composable
private fun StatusBadge(label: String, color: Color) {
    Box(
        Modifier
            .width(88.dp)
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = .12f))
            .padding(horizontal = 7.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun MoreIndicator(colors: FactXColorPalette) {
    Canvas(Modifier.size(16.dp)) {
        val centerY = size.height / 2f
        listOf(size.width * .24f, size.width * .5f, size.width * .76f).forEach { x ->
            drawCircle(colors.textMuted, radius = 1.5.dp.toPx(), center = Offset(x, centerY))
        }
    }
}

@Composable
private fun Placeholder(destination: Destination, colors: FactXColorPalette) {
    Box(Modifier.fillMaxSize().padding(FactXTokens.PagePadding), contentAlignment = Alignment.Center) {
        SectionCard("${destination.label} · base preparada", colors, Modifier.widthIn(max = 520.dp)) {
            Text(destination.milestone.orEmpty(), color = colors.textSecondary, fontSize = FactXTokens.BodySize)
            Text("La navegación continúa siendo local mientras esta sección se incorpora en su hito correspondiente.", color = colors.textMuted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun FactXGlyph(glyph: Glyph, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 1.7.dp.toPx()
        val inset = size.width * .16f
        val innerWidth = size.width - inset * 2
        val innerHeight = size.height - inset * 2
        val rounded = CornerRadius(3.dp.toPx(), 3.dp.toPx())

        when (glyph) {
            Glyph.DASHBOARD -> {
                val cell = size.width * .27f
                listOf(Offset(inset, inset), Offset(size.width - inset - cell, inset), Offset(inset, size.height - inset - cell), Offset(size.width - inset - cell, size.height - inset - cell)).forEach { topLeft ->
                    drawRoundRect(color, topLeft, Size(cell, cell), rounded)
                }
            }

            Glyph.RECEIVED -> {
                drawRoundRect(color, Offset(inset, inset), Size(innerWidth, innerHeight), rounded, style = Stroke(stroke))
                drawLine(color, Offset(size.width * .31f, size.height * .43f), Offset(size.width * .7f, size.height * .43f), stroke)
                drawLine(color, Offset(size.width * .31f, size.height * .63f), Offset(size.width * .61f, size.height * .63f), stroke)
            }

            Glyph.ISSUED -> {
                drawRoundRect(color, Offset(inset, size.height * .3f), Size(innerWidth, size.height * .42f), rounded, style = Stroke(stroke))
                drawLine(color, Offset(size.width * .25f, size.height * .5f), Offset(size.width * .73f, size.height * .5f), stroke)
                drawLine(color, Offset(size.width * .59f, size.height * .34f), Offset(size.width * .75f, size.height * .5f), stroke)
                drawLine(color, Offset(size.width * .59f, size.height * .66f), Offset(size.width * .75f, size.height * .5f), stroke)
            }

            Glyph.SUPPLIERS -> {
                drawRoundRect(color, Offset(size.width * .25f, size.height * .18f), Size(size.width * .5f, size.height * .66f), rounded, style = Stroke(stroke))
                listOf(.37f, .5f, .63f).forEach { x ->
                    drawLine(color, Offset(size.width * x, size.height * .35f), Offset(size.width * x, size.height * .48f), stroke)
                    drawLine(color, Offset(size.width * x, size.height * .6f), Offset(size.width * x, size.height * .73f), stroke)
                }
            }

            Glyph.CUSTOMERS -> {
                drawCircle(color, radius = size.width * .18f, center = Offset(size.width / 2f, size.height * .34f), style = Stroke(stroke))
                drawArc(color, 200f, 140f, false, Offset(size.width * .22f, size.height * .44f), Size(size.width * .56f, size.height * .44f), style = Stroke(stroke, cap = StrokeCap.Round))
            }

            Glyph.PAYMENTS -> {
                drawRoundRect(color, Offset(inset, size.height * .28f), Size(innerWidth, size.height * .46f), rounded, style = Stroke(stroke))
                drawLine(color, Offset(inset, size.height * .44f), Offset(size.width - inset, size.height * .44f), stroke)
                drawCircle(color, radius = size.width * .055f, center = Offset(size.width * .68f, size.height * .6f))
            }

            Glyph.EXPORT -> {
                drawRoundRect(color, Offset(size.width * .2f, size.height * .58f), Size(size.width * .6f, size.height * .22f), rounded, style = Stroke(stroke))
                drawLine(color, Offset(size.width * .5f, size.height * .15f), Offset(size.width * .5f, size.height * .64f), stroke)
                drawLine(color, Offset(size.width * .33f, size.height * .35f), Offset(size.width * .5f, size.height * .16f), stroke)
                drawLine(color, Offset(size.width * .67f, size.height * .35f), Offset(size.width * .5f, size.height * .16f), stroke)
            }

            Glyph.DATABASE -> {
                drawOval(color, Offset(size.width * .2f, size.height * .17f), Size(size.width * .6f, size.height * .24f), style = Stroke(stroke))
                drawLine(color, Offset(size.width * .2f, size.height * .29f), Offset(size.width * .2f, size.height * .71f), stroke)
                drawLine(color, Offset(size.width * .8f, size.height * .29f), Offset(size.width * .8f, size.height * .71f), stroke)
                drawArc(color, 0f, 180f, false, Offset(size.width * .2f, size.height * .58f), Size(size.width * .6f, size.height * .25f), style = Stroke(stroke))
            }

            Glyph.CALENDAR -> {
                drawRoundRect(color, Offset(inset, size.height * .22f), Size(innerWidth, size.height * .62f), rounded, style = Stroke(stroke))
                drawLine(color, Offset(inset, size.height * .42f), Offset(size.width - inset, size.height * .42f), stroke)
                drawLine(color, Offset(size.width * .35f, size.height * .12f), Offset(size.width * .35f, size.height * .3f), stroke)
                drawLine(color, Offset(size.width * .65f, size.height * .12f), Offset(size.width * .65f, size.height * .3f), stroke)
            }

            Glyph.DOCUMENTS -> {
                drawRoundRect(color.copy(alpha = .55f), Offset(size.width * .18f, size.height * .25f), Size(size.width * .48f, size.height * .58f), rounded, style = Stroke(stroke))
                drawRoundRect(color, Offset(size.width * .34f, size.height * .14f), Size(size.width * .48f, size.height * .58f), rounded, style = Stroke(stroke))
                drawLine(color, Offset(size.width * .45f, size.height * .38f), Offset(size.width * .7f, size.height * .38f), stroke)
                drawLine(color, Offset(size.width * .45f, size.height * .53f), Offset(size.width * .66f, size.height * .53f), stroke)
            }

            Glyph.MOON -> {
                val crescent = Path().apply {
                    moveTo(size.width * .66f, size.height * .12f)
                    cubicTo(size.width * .31f, size.height * .16f, size.width * .14f, size.height * .48f, size.width * .29f, size.height * .74f)
                    cubicTo(size.width * .45f, size.height * .99f, size.width * .78f, size.height * .88f, size.width * .89f, size.height * .63f)
                    cubicTo(size.width * .70f, size.height * .76f, size.width * .51f, size.height * .67f, size.width * .46f, size.height * .50f)
                    cubicTo(size.width * .41f, size.height * .32f, size.width * .51f, size.height * .19f, size.width * .66f, size.height * .12f)
                    close()
                }
                drawPath(crescent, color)
            }

            Glyph.SUN -> {
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(color, radius = size.width * .19f, center = center, style = Stroke(stroke))
                listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f).forEach { degrees ->
                    val radians = Math.toRadians(degrees.toDouble())
                    val start = Offset(
                        center.x + kotlin.math.cos(radians).toFloat() * size.width * .31f,
                        center.y + kotlin.math.sin(radians).toFloat() * size.height * .31f
                    )
                    val end = Offset(
                        center.x + kotlin.math.cos(radians).toFloat() * size.width * .43f,
                        center.y + kotlin.math.sin(radians).toFloat() * size.height * .43f
                    )
                    drawLine(color, start, end, stroke, StrokeCap.Round)
                }
            }
        }
    }
}
