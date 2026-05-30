@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.SalvoLab.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.SalvoLab.domain.model.CardmarketPrices
import com.SalvoLab.domain.model.InventoryItem
import com.SalvoLab.domain.model.PokemonCard
import com.SalvoLab.domain.model.PortfolioSummary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCardClick: (cardId: String) -> Unit,
    onInventoryItemClick: (itemId: String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PokèScan",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            // ── Portfolio Summary Card ─────────────────────────────
            item {
                uiState.portfolio?.let { PortfolioCard(it) }
            }

            // ── Carte recenti ─────────────────────────────────────
            if (uiState.recentItems.isNotEmpty()) {
                item {
                    SectionHeader("La mia collezione")
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.recentItems, key = { it.id }) { item ->
                            InventoryCardItem(item = item, onClick = { onInventoryItemClick(item.id) })
                        }
                    }
                }
            }

            // ── Trending sul mercato ──────────────────────────────
            if (uiState.trendingCards.isNotEmpty()) {
                item {
                    SectionHeader("Trending sul mercato")
                }
                items(uiState.trendingCards.take(10), key = { it.id }) { card ->
                    TrendingCardRow(card = card, onClick = { onCardClick(card.id) })
                }
            }

            // ── Empty state ───────────────────────────────────────
            if (uiState.recentItems.isEmpty() && uiState.trendingCards.isEmpty()) {
                item {
                    EmptyInventoryState()
                }
            }
        }
    }

    // Error snackbar
    uiState.error?.let { msg ->
        LaunchedEffect(msg) {
            // TODO: Snackbar host
            viewModel.dismissError()
        }
    }
}

// ── Portfolio Summary ─────────────────────────────────────────

@Composable
private fun PortfolioCard(summary: PortfolioSummary) {
    val gainColor = if (summary.gainLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
    val gainIcon  = if (summary.gainLoss >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Il mio portfolio", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        text  = formatPrice(summary.estimatedValue),
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text  = "Valore stimato (trend Cardmarket)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(gainIcon, contentDescription = null, tint = gainColor, modifier = Modifier.size(20.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text  = "${if (summary.gainLoss >= 0) "+" else ""}${formatPrice(summary.gainLoss)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = gainColor,
                        )
                        Text(
                            text  = "${if (summary.gainLossPercent >= 0) "+" else ""}${"%.1f".format(summary.gainLossPercent)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = gainColor,
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatItem(label = "Carte totali", value = "${summary.totalCards}")
                StatItem(label = "Carte uniche", value = "${summary.uniqueCards}")
                StatItem(label = "Acquistato", value = formatPrice(summary.purchasedValue))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
    }
}

// ── Inventory Card (orizzontale scroll) ───────────────────────

@Composable
private fun InventoryCardItem(item: InventoryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        shape    = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            AsyncImage(
                model             = item.card.imageUrlSmall,
                contentDescription = item.card.name,
                modifier          = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale      = ContentScale.Crop,
            )
            Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text     = item.card.name,
                    style    = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                )
                item.card.cardmarketPrices?.trendPrice?.let { price ->
                    Text(
                        text  = formatPrice(price),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text  = item.condition.abbreviation,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Trending Row ──────────────────────────────────────────────

@Composable
private fun TrendingCardRow(card: PokemonCard, onClick: () -> Unit) {
    ListItem(
        modifier      = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(card.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
        },
        supportingContent = {
            Text(
                "${card.set.name.toItalianSetName()} · ${card.rarity.toItalianRarity() ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingContent = {
            AsyncImage(
                model              = card.imageUrlSmall,
                contentDescription = card.name,
                modifier           = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale       = ContentScale.Crop,
            )
        },
        trailingContent = {
            card.cardmarketPrices?.let { PriceColumn(it) }
        },
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun PriceColumn(prices: CardmarketPrices) {
    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        prices.trendPrice?.let {
            Text(
                text  = formatPrice(it),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
        prices.lowPrice?.let {
            Text(
                text  = "min ${formatPrice(it)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text     = title,
        style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun EmptyInventoryState() {
    Box(
        modifier           = Modifier.fillMaxWidth().padding(48.dp),
        contentAlignment   = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Nessuna carta ancora!", style = MaterialTheme.typography.titleMedium)
            Text(
                "Vai su Scansiona o Mercato per aggiungere le tue prime carte.",
                style   = MaterialTheme.typography.bodyMedium,
                color   = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatPrice(value: Double): String =
    String.format(Locale.ITALIAN, "%.2f €", value)
