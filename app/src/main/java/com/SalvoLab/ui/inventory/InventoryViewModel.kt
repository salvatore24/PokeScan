@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.SalvoLab.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SalvoLab.domain.model.CardCondition
import com.SalvoLab.domain.model.CardLanguage
import com.SalvoLab.domain.model.InventoryItem
import com.SalvoLab.domain.model.PortfolioSummary
import com.SalvoLab.domain.repository.IInventoryRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class InventorySortOrder(val label: String) {
    DATE_DESC("Più recenti"),
    DATE_ASC("Più vecchie"),
    VALUE_DESC("Valore ↓"),
    VALUE_ASC("Valore ↑"),
    NAME_ASC("Nome A→Z"),
    GAIN_DESC("Guadagno ↓"),
}

data class InventoryUiState(
    val isLoading: Boolean = true,
    val items: List<InventoryItem> = emptyList(),
    val filteredItems: List<InventoryItem> = emptyList(),
    val portfolio: PortfolioSummary? = null,
    val searchQuery: String = "",
    val sortOrder: InventorySortOrder = InventorySortOrder.DATE_DESC,
    val filterCondition: CardCondition? = null,
    val filterLanguage: CardLanguage? = null,
    val error: String? = null,
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: IInventoryRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadInventory()
    }

    private fun loadInventory() {
        val userId = auth.currentUser?.uid ?: return
        combine(
            inventoryRepository.getInventory(userId),
            inventoryRepository.getPortfolioSummary(userId),
        ) { items, portfolio ->
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    items     = items,
                    portfolio = portfolio,
                    filteredItems = applyFilters(items, state),
                )
            }
        }
        .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        .launchIn(viewModelScope)
    }

    fun onSearchChanged(q: String) {
        _uiState.update { state ->
            val updated = state.copy(searchQuery = q)
            updated.copy(filteredItems = applyFilters(state.items, updated))
        }
    }

    fun onSortChanged(sort: InventorySortOrder) {
        _uiState.update { state ->
            val updated = state.copy(sortOrder = sort)
            updated.copy(filteredItems = applyFilters(state.items, updated))
        }
    }

    fun onConditionFilterChanged(c: CardCondition?) {
        _uiState.update { state ->
            val updated = state.copy(filterCondition = c)
            updated.copy(filteredItems = applyFilters(state.items, updated))
        }
    }

    fun onLanguageFilterChanged(l: CardLanguage?) {
        _uiState.update { state ->
            val updated = state.copy(filterLanguage = l)
            updated.copy(filteredItems = applyFilters(state.items, updated))
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    suspend fun removeItem(itemId: String) {
        inventoryRepository.removeCard(itemId)
    }

    private fun applyFilters(items: List<InventoryItem>, state: InventoryUiState): List<InventoryItem> {
        return items
            .filter { item ->
                (state.searchQuery.isBlank() || item.card.name.contains(state.searchQuery, ignoreCase = true)) &&
                (state.filterCondition == null || item.condition == state.filterCondition) &&
                (state.filterLanguage == null || item.language == state.filterLanguage)
            }
            .sortedWith(when (state.sortOrder) {
                InventorySortOrder.DATE_DESC  -> compareByDescending { it.addedAt }
                InventorySortOrder.DATE_ASC   -> compareBy { it.addedAt }
                InventorySortOrder.VALUE_DESC -> compareByDescending { it.estimatedValue }
                InventorySortOrder.VALUE_ASC  -> compareBy { it.estimatedValue }
                InventorySortOrder.NAME_ASC   -> compareBy { it.card.name }
                InventorySortOrder.GAIN_DESC  -> compareByDescending { it.gainLoss }
            })
    }
}
