@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.SalvoLab.ui.market

import com.SalvoLab.domain.util.toItalianRarity
import com.SalvoLab.domain.util.toItalianSetName

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.SalvoLab.domain.model.CardmarketPrices
import com.SalvoLab.domain.model.PokemonCard
import com.SalvoLab.domain.model.PokemonSet
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    onCardClick: (cardId: String) -> Unit,
    viewModel: MarketViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Infinite scroll: carica più carte avvicinandosi alla fine
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total       = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 5 && total > 0
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Mercato Pokémon", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                )
                // Search bar
                OutlinedTextField(
                    value         = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder   = { Text("Cerca una carta…") },
                    leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape         = RoundedCornerShape(28.dp),
                    singleLine    = true,
                )
                // Set filter chips — nascosti durante la ricerca testuale
                if (uiState.sets.isNotEmpty() && uiState.searchQuery.isBlank()) {
                    SetFilterRow(
                        sets          = uiState.sets,
                        selectedSetId = uiState.selectedSetId,
                        onSetSelected = viewModel::onSetSelected,
                    )
                }
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
            state           = listState,
            modifier        = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding  = PaddingValues(bottom = 16.dp),
        ) {
            items(uiState.cards, key = { it.id }) { card ->
                MarketCardRow(card = card, onClick = { onCardClick(card.id) })
            }

            if (uiState.isLoadingMore) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    }
                }
            }

            if (!uiState.hasMore && uiState.cards.isNotEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Tutte le carte caricate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (uiState.cards.isEmpty() && !uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Text("Nessuna carta trovata", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

// ── Set Filter Chips ──────────────────────────────────────────

@Composable
private fun SetFilterRow(
    sets: List<PokemonSet>,
    selectedSetId: String?,
    onSetSelected: (String?) -> Unit,
) {
    Row(
        modifier          = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selectedSetId == null,
            onClick  = { onSetSelected(null) },
            label    = { Text("Tutti") },
        )
        sets.take(20).forEach { set ->
            FilterChip(
                selected = selectedSetId == set.id,
                onClick  = { onSetSelected(if (selectedSetId == set.id) null else set.id) },
                label    = { Text(set.name) },
            )
        }
    }
}

// ── Market Card Row ───────────────────────────────────────────

@Composable
private fun MarketCardRow(card: PokemonCard, onClick: () -> Unit) {
    ListItem(
        modifier          = Modifier.clickable(onClick = onClick),
        headlineContent   = {
            Text(card.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
        },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "${card.set.name.toItalianSetName()} · #${card.number}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                card.rarity.toItalianRarity()?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
        },
        leadingContent    = {
            AsyncImage(
                model              = card.imageUrlSmall,
                contentDescription = card.name,
                modifier           = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale       = ContentScale.Fit,
            )
        },
        trailingContent   = {
            card.cardmarketPrices?.let { MarketPriceColumn(it) }
                ?: Text("N/D", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun MarketPriceColumn(prices: CardmarketPrices) {
    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        prices.trendPrice?.let {
            Text(
                text  = formatPrice(it),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
            Text("trend", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

private fun formatPrice(value: Double): String =
    String.format(Locale.ITALIAN, "%.2f €", value)
