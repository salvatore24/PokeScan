@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.SalvoLab.ui.inventory

import com.SalvoLab.domain.util.toItalianSetName

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.SalvoLab.domain.model.CardCondition
import com.SalvoLab.domain.model.CardLanguage
import com.SalvoLab.domain.model.InventoryItem
import com.SalvoLab.domain.model.PortfolioSummary
import java.util.Locale

@Composable
fun InventoryScreen(
    onCardClick: (cardId: String) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSortSheet  by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("La mia collezione", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                    actions = {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtri")
                        }
                        IconButton(onClick = { showSortSheet = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Ordina")
                        }
                    },
                )
                OutlinedTextField(
                    value         = uiState.searchQuery,
                    onValueChange = viewModel::onSearchChanged,
                    placeholder   = { Text("Cerca nella collezione…") },
                    leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier      = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    shape         = RoundedCornerShape(24.dp),
                    singleLine    = true,
                )
            }
        }
    ) { padding ->

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            // ── Portfolio summary ─────────────────────────────────
            item {
                uiState.portfolio?.let { PortfolioMiniCard(it) }
            }

            // ── Filtri attivi ─────────────────────────────────────
            val hasActiveFilters = uiState.filterCondition != null || uiState.filterLanguage != null
            if (hasActiveFilters) {
                item {
                    ActiveFilterRow(
                        condition = uiState.filterCondition,
                        language  = uiState.filterLanguage,
                        onClearCondition = { viewModel.onConditionFilterChanged(null) },
                        onClearLanguage  = { viewModel.onLanguageFilterChanged(null) },
                    )
                }
            }

            // ── Conteggio risultati ───────────────────────────────
            item {
                Text(
                    "${uiState.filteredItems.size} carte · ordinate per ${uiState.sortOrder.label}",
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            if (uiState.filteredItems.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Nessuna carta trovata", style = MaterialTheme.typography.titleMedium)
                            Text(
                                if (uiState.items.isEmpty()) "Aggiungi la tua prima carta dal Mercato o tramite Scanner!"
                                else "Prova a cambiare i filtri",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            } else {
                items(uiState.filteredItems, key = { it.id }) { item ->
                    InventoryItemRow(item = item, onCardClick = { onCardClick(item.card.id) })
                }
            }
        }
    }

    // ── Sort bottom sheet ─────────────────────────────────────────
    if (showSortSheet) {
        ModalBottomSheet(onDismissRequest = { showSortSheet = false }) {
            Column(Modifier.padding(bottom = 24.dp)) {
                Text(
                    "Ordina per",
                    style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(16.dp),
                )
                InventorySortOrder.entries.forEach { sort ->
                    ListItem(
                        headlineContent = { Text(sort.label) },
                        trailingContent = {
                            if (sort == uiState.sortOrder) {
                                Icon(Icons.Default.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        modifier = Modifier.clickable {
                            viewModel.onSortChanged(sort)
                            showSortSheet = false
                        },
                    )
                }
            }
        }
    }

    // ── Filter bottom sheet ───────────────────────────────────────
    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(Modifier.padding(bottom = 24.dp)) {
                Text(
                    "Filtra per condizione",
                    style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(16.dp),
                )
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        FilterChip(
                            selected = uiState.filterCondition == null,
                            onClick  = { viewModel.onConditionFilterChanged(null) },
                            label    = { Text("Tutte") },
                        )
                    }
                    items(CardCondition.entries) { c ->
                        FilterChip(
                            selected = uiState.filterCondition == c,
                            onClick  = { viewModel.onConditionFilterChanged(c) },
                            label    = { Text(c.abbreviation) },
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Filtra per lingua",
                    style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        FilterChip(
                            selected = uiState.filterLanguage == null,
                            onClick  = { viewModel.onLanguageFilterChanged(null) },
                            label    = { Text("Tutte") },
                        )
                    }
                    items(CardLanguage.entries) { l ->
                        FilterChip(
                            selected = uiState.filterLanguage == l,
                            onClick  = { viewModel.onLanguageFilterChanged(l) },
                            label    = { Text("${l.flag} ${l.code}") },
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ── Portfolio mini card ───────────────────────────────────────

@Composable
private fun PortfolioMiniCard(summary: PortfolioSummary) {
    val gainColor = if (summary.gainLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column {
                Text("Portfolio", style = MaterialTheme.typography.labelMedium)
                Text(formatPrice(summary.estimatedValue), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                Text("${summary.totalCards} carte · ${summary.uniqueCards} uniche", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Gain/Loss", style = MaterialTheme.typography.labelSmall)
                Text(
                    "${if (summary.gainLoss >= 0) "+" else ""}${formatPrice(summary.gainLoss)}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = gainColor,
                )
                Text(
                    "${if (summary.gainLossPercent >= 0) "+" else ""}${"%.1f".format(summary.gainLossPercent)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = gainColor,
                )
            }
        }
    }
}

// ── Active filter chips ───────────────────────────────────────

@Composable
private fun ActiveFilterRow(
    condition: CardCondition?,
    language: CardLanguage?,
    onClearCondition: () -> Unit,
    onClearLanguage: () -> Unit,
) {
    Row(
        modifier              = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        condition?.let {
            InputChip(
                selected = true,
                onClick  = onClearCondition,
                label    = { Text(it.abbreviation) },
                trailingIcon = { Text("×") },
            )
        }
        language?.let {
            InputChip(
                selected = true,
                onClick  = onClearLanguage,
                label    = { Text("${it.flag} ${it.code}") },
                trailingIcon = { Text("×") },
            )
        }
    }
}

// ── Inventory item row ────────────────────────────────────────

@Composable
private fun InventoryItemRow(item: InventoryItem, onCardClick: () -> Unit) {
    val gainColor = if (item.gainLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)

    ListItem(
        modifier          = Modifier.clickable(onClick = onCardClick),
        headlineContent   = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(item.card.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                Text(item.language.flag, style = MaterialTheme.typography.bodyMedium)
                if (item.isFoil) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(4.dp),
                    ) { Text("HOLO", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)) }
                }
            }
        },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "${item.card.set.name.toItalianSetName()} · #${item.card.number} · ${item.condition.abbreviation} · ×${item.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                item.purchasePrice?.let { pp ->
                    Text(
                        "Acquisto: ${formatPrice(pp * item.quantity)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        leadingContent    = {
            AsyncImage(
                model              = item.card.imageUrlSmall,
                contentDescription = item.card.name,
                modifier           = Modifier.size(56.dp).clip(RoundedCornerShape(6.dp)),
                contentScale       = ContentScale.Fit,
            )
        },
        trailingContent   = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    formatPrice(item.estimatedValue),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
                if (item.purchasePrice != null) {
                    Text(
                        "${if (item.gainLoss >= 0) "+" else ""}${formatPrice(item.gainLoss)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = gainColor,
                    )
                }
            }
        },
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

private fun formatPrice(value: Double): String =
    String.format(Locale.ITALIAN, "%.2f €", value)
