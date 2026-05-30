@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.SalvoLab.ui.carddetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.SalvoLab.domain.model.CardCondition
import com.SalvoLab.domain.model.CardLanguage
import com.SalvoLab.domain.model.CardmarketPrices
import com.SalvoLab.domain.model.PokemonCard
import com.SalvoLab.domain.util.toItalianRarity
import com.SalvoLab.domain.util.toItalianSetName
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: CardDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    // Snackbar successo
    LaunchedEffect(uiState.addSuccess) {
        if (uiState.addSuccess) {
            snackbarHostState.showSnackbar("✓ Carta aggiunta all'inventario!")
            viewModel.dismissError()
        }
    }
    // Snackbar errore
    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            snackbarHostState.showSnackbar(msg, withDismissAction = true)
            viewModel.dismissError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title           = { Text(uiState.card?.name ?: "Dettaglio carta") },
                navigationIcon  = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
            )
        },
        floatingActionButton = {
            if (uiState.card != null) {
                FloatingActionButton(onClick = viewModel::onShowAddDialog) {
                    Icon(Icons.Default.Add, contentDescription = "Aggiungi all'inventario")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->

        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            uiState.card != null -> CardDetailContent(
                card    = uiState.card!!,
                padding = padding,
            )
        }
    }

    // Dialog aggiungi a inventario
    if (uiState.showAddDialog) {
        AddToInventoryDialog(
            uiState                = uiState,
            onDismiss              = viewModel::onDismissDialog,
            onConfirm              = viewModel::addToInventory,
            onConditionChanged     = viewModel::onConditionChanged,
            onLanguageChanged      = viewModel::onLanguageChanged,
            onQuantityChanged      = viewModel::onQuantityChanged,
            onPurchasePriceChanged = viewModel::onPurchasePriceChanged,
            onFoilChanged          = viewModel::onFoilChanged,
            onNotesChanged         = viewModel::onNotesChanged,
        )
    }
}

// ── Card Detail Content ───────────────────────────────────────

@Composable
private fun CardDetailContent(card: PokemonCard, padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Immagine grande
        AsyncImage(
            model              = card.imageUrlLarge,
            contentDescription = card.name,
            modifier           = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentScale       = ContentScale.Fit,
        )

        Column(
            modifier            = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Nome + set
            Text(card.name, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
            Text(
                "${card.set.name.toItalianSetName()} · #${card.number} · ${card.rarity.toItalianRarity() ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Info carta
            if (card.hp != null || card.types.isNotEmpty() || card.artist != null) {
                CardInfoChips(card)
            }

            HorizontalDivider()

            // Prezzi Cardmarket
            card.cardmarketPrices?.let { PriceSection(it) }
                ?: Text("Prezzi Cardmarket non disponibili", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(80.dp)) // Spazio per il FAB
        }
    }
}

@Composable
private fun CardInfoChips(card: PokemonCard) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        card.hp?.let {
            SuggestionChip(onClick = {}, label = { Text("HP $it") })
        }
        card.types.forEach { type ->
            SuggestionChip(onClick = {}, label = { Text(type) })
        }
    }
    card.artist?.let {
        Text("Illustrazione: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PriceSection(prices: CardmarketPrices) {
    Text("Prezzi Cardmarket", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

    // Grafico sparkline storico
    val chartPoints = listOfNotNull(
        prices.avg30?.let { "30g" to it },
        prices.avg7?.let  { "7g"  to it },
        prices.avg1?.let  { "1g"  to it },
        prices.trendPrice?.let { "trend" to it },
    )
    if (chartPoints.size >= 2) {
        PriceChart(points = chartPoints)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape  = RoundedCornerShape(12.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PriceRow("Trend",           prices.trendPrice, highlighted = true)
            PriceRow("Minimo (low)",    prices.lowPrice)
            PriceRow("Media 1 giorno",  prices.avg1)
            PriceRow("Media 7 giorni",  prices.avg7)
            PriceRow("Media 30 giorni", prices.avg30)
            if (prices.reverseHoloAvg7 != null) {
                HorizontalDivider()
                Text("Reverse Holo", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                PriceRow("Media 7 giorni",  prices.reverseHoloAvg7)
                PriceRow("Media 30 giorni", prices.reverseHoloAvg30)
            }
        }
    }
}

// ── Trading Chart ─────────────────────────────────────────────

private enum class ChartPeriod(val label: String) {
    DAY_1("1G"), WEEK_1("7G"), MONTH_1("1M")
}

@Composable
private fun PriceChart(points: List<Pair<String, Double>>) {
    var selectedPeriod by remember { mutableStateOf(ChartPeriod.MONTH_1) }

    // Filtra i punti in base al periodo selezionato
    val visiblePoints = when (selectedPeriod) {
        ChartPeriod.DAY_1   -> points.filter { it.first in listOf("1g", "trend") }
        ChartPeriod.WEEK_1  -> points.filter { it.first in listOf("7g", "1g", "trend") }
        ChartPeriod.MONTH_1 -> points
    }.ifEmpty { points }

    val minVal    = visiblePoints.minOf { it.second }
    val maxVal    = visiblePoints.maxOf { it.second }
    val range     = (maxVal - minVal).takeIf { it > 0 } ?: 1.0
    val isUp      = visiblePoints.last().second >= visiblePoints.first().second
    val lineColor = if (isUp) Color(0xFF26A69A) else Color(0xFFEF5350) // verde/rosso trading

    val bgColor       = MaterialTheme.colorScheme.surface
    val gridColor     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f)
    val labelColor    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    val currentPrice  = visiblePoints.last().second
    val openPrice     = visiblePoints.first().second
    val changePct     = if (openPrice > 0) ((currentPrice - openPrice) / openPrice) * 100 else 0.0

    Card(
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.padding(16.dp)) {

            // ── Header: prezzo attuale + variazione ───────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        formatPrice(currentPrice),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${if (isUp) "▲" else "▼"} ${"%.2f".format(abs(changePct))}%",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = lineColor,
                        )
                        Text("vs ${visiblePoints.first().first}", style = MaterialTheme.typography.bodySmall, color = labelColor)
                    }
                }

                // Selector periodo
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ChartPeriod.entries.forEach { period ->
                        val selected = period == selectedPeriod
                        Surface(
                            onClick = { selectedPeriod = period },
                            shape   = RoundedCornerShape(6.dp),
                            color   = if (selected) lineColor.copy(alpha = 0.15f) else Color.Transparent,
                        ) {
                            Text(
                                period.label,
                                style    = MaterialTheme.typography.labelSmall.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
                                color    = if (selected) lineColor else labelColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Canvas grafico ────────────────────────────────────
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val w          = size.width
                val h          = size.height
                val padL       = 0f
                val padR       = 0f
                val padT       = 8.dp.toPx()
                val padB       = 24.dp.toPx()
                val chartW     = w - padL - padR
                val chartH     = h - padT - padB
                val n          = visiblePoints.size
                val stepX      = if (n > 1) chartW / (n - 1) else chartW

                fun yFor(v: Double) = padT + chartH - ((v - minVal) / range * chartH).toFloat()
                fun xFor(i: Int)    = padL + i * stepX

                // ── Linee griglia orizzontali (3 livelli) ─────────
                val gridSteps = 3
                for (i in 0..gridSteps) {
                    val y = padT + chartH * i / gridSteps
                    drawLine(gridColor, androidx.compose.ui.geometry.Offset(padL, y), androidx.compose.ui.geometry.Offset(w - padR, y), strokeWidth = 1.dp.toPx())
                }

                // ── Area gradiente sotto la curva ─────────────────
                val areaPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(xFor(0), h - padB)
                    visiblePoints.forEachIndexed { i, (_, v) -> lineTo(xFor(i), yFor(v)) }
                    lineTo(xFor(n - 1), h - padB)
                    close()
                }
                drawPath(
                    areaPath,
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors     = listOf(lineColor.copy(alpha = 0.25f), lineColor.copy(alpha = 0.0f)),
                        startY     = padT,
                        endY       = h - padB,
                    ),
                )

                // ── Linea principale ──────────────────────────────
                val linePath = androidx.compose.ui.graphics.Path().apply {
                    visiblePoints.forEachIndexed { i, (_, v) ->
                        if (i == 0) moveTo(xFor(i), yFor(v)) else lineTo(xFor(i), yFor(v))
                    }
                }
                drawPath(linePath, color = lineColor, style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2.5.dp.toPx(),
                    cap   = androidx.compose.ui.graphics.StrokeCap.Round,
                    join  = androidx.compose.ui.graphics.StrokeJoin.Round,
                ))

                // ── Punto finale (prezzo corrente) ────────────────
                val lastX = xFor(n - 1)
                val lastY = yFor(visiblePoints.last().second)
                drawCircle(color = lineColor, radius = 5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(lastX, lastY))
                drawCircle(color = bgColor,   radius = 3.dp.toPx(), center = androidx.compose.ui.geometry.Offset(lastX, lastY))

                // ── Linea tratteggiata prezzo corrente ────────────
                val dashLen = 6.dp.toPx()
                val gapLen  = 4.dp.toPx()
                var x = 0f
                while (x < lastX - 10.dp.toPx()) {
                    drawLine(lineColor.copy(alpha = 0.35f), androidx.compose.ui.geometry.Offset(x, lastY), androidx.compose.ui.geometry.Offset(minOf(x + dashLen, lastX), lastY), strokeWidth = 1.dp.toPx())
                    x += dashLen + gapLen
                }

                // ── Label asse X ──────────────────────────────────
                // Disegnate tramite compose sotto il canvas
            }

            // Label X sotto il canvas
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                visiblePoints.forEach { (label, _) ->
                    Text(label, style = MaterialTheme.typography.labelSmall, color = labelColor)
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Banda min/max ─────────────────────────────────────
            HorizontalDivider(color = gridColor)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Minimo periodo", style = MaterialTheme.typography.labelSmall, color = labelColor)
                    Text(formatPrice(minVal), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFFEF5350))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Massimo periodo", style = MaterialTheme.typography.labelSmall, color = labelColor)
                    Text(formatPrice(maxVal), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFF26A69A))
                }
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, value: Double?, highlighted: Boolean = false) {
    if (value == null) return
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = if (highlighted) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    else MaterialTheme.typography.bodyMedium,
        )
        Text(
            formatPrice(value),
            style = if (highlighted) MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary,
            ) else MaterialTheme.typography.bodyMedium,
        )
    }
}

// ── Add to Inventory Dialog ───────────────────────────────────

@Composable
private fun AddToInventoryDialog(
    uiState: CardDetailUiState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onConditionChanged: (CardCondition) -> Unit,
    onLanguageChanged: (CardLanguage) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    onPurchasePriceChanged: (String) -> Unit,
    onFoilChanged: (Boolean) -> Unit,
    onNotesChanged: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title   = { Text("Aggiungi all'inventario") },
        text    = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // ── Condizione ───────────────────────────────────
                var conditionExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded         = conditionExpanded,
                    onExpandedChange = { conditionExpanded = it },
                ) {
                    OutlinedTextField(
                        value         = "${uiState.selectedCondition.label} (${uiState.selectedCondition.abbreviation})",
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Condizione") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(conditionExpanded) },
                        modifier      = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        singleLine    = true,
                    )
                    ExposedDropdownMenu(expanded = conditionExpanded, onDismissRequest = { conditionExpanded = false }) {
                        CardCondition.entries.forEach { c ->
                            DropdownMenuItem(
                                text    = { Text("${c.label} (${c.abbreviation}) — ×${c.priceMultiplier}") },
                                onClick = { onConditionChanged(c); conditionExpanded = false },
                            )
                        }
                    }
                }

                // ── Lingua ───────────────────────────────────────
                var languageExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded         = languageExpanded,
                    onExpandedChange = { languageExpanded = it },
                ) {
                    OutlinedTextField(
                        value         = "${uiState.selectedLanguage.flag} ${uiState.selectedLanguage.label}",
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Lingua carta") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(languageExpanded) },
                        modifier      = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        singleLine    = true,
                    )
                    ExposedDropdownMenu(expanded = languageExpanded, onDismissRequest = { languageExpanded = false }) {
                        CardLanguage.entries.forEach { l ->
                            DropdownMenuItem(
                                text    = { Text("${l.flag} ${l.label}") },
                                onClick = { onLanguageChanged(l); languageExpanded = false },
                            )
                        }
                    }
                }

                // ── Quantità ─────────────────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Quantità", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                    IconButton(onClick = { onQuantityChanged(uiState.quantity - 1) }) { Text("-", style = MaterialTheme.typography.titleMedium) }
                    Text("${uiState.quantity}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = { onQuantityChanged(uiState.quantity + 1) }) { Text("+", style = MaterialTheme.typography.titleMedium) }
                }

                // ── Prezzo acquisto ───────────────────────────────
                OutlinedTextField(
                    value         = uiState.purchasePrice,
                    onValueChange = onPurchasePriceChanged,
                    label         = { Text("Prezzo acquisto (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                )

                // ── Stima valore in tempo reale ───────────────────
                val trendBase  = uiState.card?.cardmarketPrices?.trendPrice
                val estValue   = trendBase?.let {
                    it * uiState.selectedCondition.priceMultiplier * uiState.quantity
                }
                val spent      = uiState.purchasePrice.toDoubleOrNull()?.times(uiState.quantity)
                if (estValue != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape  = RoundedCornerShape(8.dp),
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Valore stimato", style = MaterialTheme.typography.labelMedium)
                                Text(formatPrice(estValue), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                            }
                            if (spent != null && spent > 0) {
                                val gl = estValue - spent
                                val glColor = if (gl >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Gain/Loss vs spesa", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        "${if (gl >= 0) "+" else ""}${formatPrice(gl)}",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = glColor,
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Foil ─────────────────────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = uiState.isFoil, onCheckedChange = onFoilChanged)
                    Text("Reverse Holo / Foil")
                }

                // ── Note ─────────────────────────────────────────
                OutlinedTextField(
                    value         = uiState.notes,
                    onValueChange = onNotesChanged,
                    label         = { Text("Note (opzionale)") },
                    modifier      = Modifier.fillMaxWidth(),
                    maxLines      = 2,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !uiState.isAddingToInventory,
            ) {
                if (uiState.isAddingToInventory) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Aggiungi")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        },
    )
}

private fun formatPrice(value: Double): String =
    String.format(Locale.ITALIAN, "%.2f €", value)
