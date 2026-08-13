package ar.com.gaston.factx.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "FactX") {
        FactXApplication()
    }
}

private enum class Destination(val label: String, val milestone: String?) {
    DASHBOARD("Inicio", null),
    RECEIVED("Recibidos", "Disponible al conectar servicios en v0.2.x"),
    ISSUED("Emitidos", "Disponible al conectar servicios en v0.2.x"),
    SUPPLIERS("Proveedores", "Disponible en v0.1.x"),
    CUSTOMERS("Clientes", "Disponible después de la primera UI de proveedores"),
    PAYMENTS("Pagos y cobros", "Disponible en un hito posterior de v1"),
    EXPORT("Exportar", "Disponible en v0.5.x")
}

@Composable
private fun FactXApplication() {
    var destination by remember { mutableStateOf(Destination.DASHBOARD) }
    Row(Modifier.fillMaxSize().background(FactXColors.Canvas)) {
        Sidebar(destination, onDestinationSelected = { destination = it })
        when (destination) {
            Destination.DASHBOARD -> Dashboard()
            else -> Placeholder(destination)
        }
    }
}

@Composable
private fun Sidebar(selected: Destination, onDestinationSelected: (Destination) -> Unit) {
    Column(
        modifier = Modifier.width(FactXTokens.SidebarWidth).fillMaxHeight().background(FactXColors.Sidebar).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FactXLogo()
        Spacer(Modifier.height(34.dp))
        Destination.entries.forEach { destination ->
            NavigationItem(destination, destination == selected) { onDestinationSelected(destination) }
        }
        Spacer(Modifier.weight(1f))
        Text("Control comercial interno", color = FactXColors.SidebarMuted, fontSize = 12.sp)
        Text("Datos demo · sin conexión", color = FactXColors.SidebarMuted, fontSize = 12.sp)
    }
}

@Composable
private fun FactXLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(34.dp)) {
            drawLine(FactXColors.Blue, start = androidx.compose.ui.geometry.Offset(size.width * .15f, size.height * .15f), end = androidx.compose.ui.geometry.Offset(size.width * .85f, size.height * .85f), strokeWidth = 8f, cap = StrokeCap.Round)
            drawLine(FactXColors.Teal, start = androidx.compose.ui.geometry.Offset(size.width * .85f, size.height * .15f), end = androidx.compose.ui.geometry.Offset(size.width * .15f, size.height * .85f), strokeWidth = 8f, cap = StrokeCap.Round)
        }
        Spacer(Modifier.width(10.dp))
        Text("FactX", color = FactXColors.Surface, fontWeight = FontWeight.Bold, fontSize = 23.sp)
    }
}

@Composable
private fun NavigationItem(destination: Destination, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) FactXColors.Blue else FactXColors.Sidebar
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(FactXTokens.SmallRadius)).background(background).clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(destination.label, color = FactXColors.Surface, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, fontSize = FactXTokens.BodySize)
    }
}

@Composable
private fun Dashboard() {
    Column(Modifier.fillMaxSize().padding(FactXTokens.PagePadding)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column { Text("Dashboard", color = FactXColors.Text, fontSize = FactXTokens.TitleSize, fontWeight = FontWeight.Bold); Text("Resumen operativo", color = FactXColors.MutedText, fontSize = FactXTokens.BodySize) }
            Spacer(Modifier.weight(1f))
            StatusBadge("Datos demo", FactXColors.Teal)
        }
        Spacer(Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(FactXTokens.SectionGap), modifier = Modifier.fillMaxWidth()) {
            MetricCard("Pendiente de pagar", "$ 215.400", FactXColors.Pending, Modifier.weight(1f))
            MetricCard("Pendiente de cobrar", "$ 178.750", FactXColors.Blue, Modifier.weight(1f))
            MetricCard("Vencen esta semana", "7", FactXColors.Danger, Modifier.weight(1f))
            MetricCard("Comprobantes del mes", "24", FactXColors.Success, Modifier.weight(1f))
        }
        Spacer(Modifier.height(FactXTokens.SectionGap))
        Row(horizontalArrangement = Arrangement.spacedBy(FactXTokens.SectionGap), modifier = Modifier.fillMaxWidth()) {
            SectionCard("Documentos por estado", Modifier.weight(1f)) { StatusRow("Pendientes", "12", FactXColors.Pending); StatusRow("Parciales", "5", FactXColors.Blue); StatusRow("Cobrados / pagados", "19", FactXColors.Success) }
            SectionCard("Distribución comercial", Modifier.weight(1f)) { StatusRow("Recibidos", "14", FactXColors.Teal); StatusRow("Emitidos", "10", FactXColors.Blue); Text("Datos de presentación: no se consulta PostgreSQL.", color = FactXColors.MutedText, fontSize = 12.sp) }
        }
        Spacer(Modifier.height(FactXTokens.SectionGap))
        SectionCard("Últimos comprobantes", Modifier.fillMaxWidth()) {
            StatusRow("Factura recibida · FactX Demo Proveedora Alfa", "Pendiente", FactXColors.Pending)
            StatusRow("Factura emitida · FactX Demo Cliente Norte", "Parcial", FactXColors.Blue)
            StatusRow("Ticket emitido · FactX Demo Cliente Sur", "Cobrado", FactXColors.Success)
        }
    }
}

@Composable
private fun Placeholder(destination: Destination) {
    Column(Modifier.fillMaxSize().padding(FactXTokens.PagePadding), verticalArrangement = Arrangement.Center) {
        Text(destination.label, color = FactXColors.Text, fontSize = FactXTokens.TitleSize, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text(destination.milestone.orEmpty(), color = FactXColors.MutedText, fontSize = FactXTokens.BodySize)
        Spacer(Modifier.height(20.dp))
        SectionCard("Base preparada") { Text("La navegación es local y esta pantalla todavía no conecta services, repositorios ni PostgreSQL.", color = FactXColors.MutedText, fontSize = FactXTokens.BodySize) }
    }
}

@Composable
private fun MetricCard(title: String, value: String, accent: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Box(modifier.shadow(3.dp, RoundedCornerShape(FactXTokens.CardRadius)).clip(RoundedCornerShape(FactXTokens.CardRadius)).background(FactXColors.Surface).then(modifier).padding(20.dp)) {
        Column { Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(accent)); Spacer(Modifier.height(18.dp)); Text(title, color = FactXColors.MutedText, fontSize = 13.sp); Spacer(Modifier.height(6.dp)); Text(value, color = FactXColors.Text, fontWeight = FontWeight.Bold, fontSize = 22.sp) }
    }
}

@Composable
private fun SectionCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.shadow(2.dp, RoundedCornerShape(FactXTokens.CardRadius)).clip(RoundedCornerShape(FactXTokens.CardRadius)).background(FactXColors.Surface).then(modifier).padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(title, color = FactXColors.Text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        content()
    }
}

@Composable
private fun StatusRow(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text(label, color = FactXColors.MutedText, fontSize = 13.sp, modifier = Modifier.weight(1f)); StatusBadge(value, color) }
}

@Composable
private fun StatusBadge(label: String, color: androidx.compose.ui.graphics.Color) {
    Box(Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = .14f)).padding(horizontal = 10.dp, vertical = 5.dp)) { Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
}
