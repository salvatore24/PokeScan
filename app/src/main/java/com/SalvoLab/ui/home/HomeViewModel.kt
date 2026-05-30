package com.SalvoLab.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SalvoLab.domain.model.InventoryItem
import com.SalvoLab.domain.model.PokemonCard
import com.SalvoLab.domain.model.PortfolioSummary
import com.SalvoLab.domain.repository.ICardRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val portfolio: PortfolioSummary? = null,
    val recentItems: List<InventoryItem> = emptyList(),
    val trendingCards: List<PokemonCard> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val inventoryRepository: IInventoryRepository,
    private val cardRepository: ICardRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        val userId = auth.currentUser?.uid ?: return

        // Combina portfolio e inventario recenti in un unico flow
        combine(
            inventoryRepository.getPortfolioSummary(userId),
            inventoryRepository.getInventory(userId),
        ) { portfolio, items ->
            _uiState.update {
                it.copy(
                    isLoading   = false,
                    portfolio   = portfolio,
                    recentItems = items.sortedByDescending { item -> item.addedAt }.take(10),
                )
            }
        }
        .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        .launchIn(viewModelScope)

        // Carte in tendenza (da API)
        viewModelScope.launch {
            cardRepository.getTrendingCards()
                .onSuccess { cards -> _uiState.update { it.copy(trendingCards = cards) } }
                .onFailure { /* silenzioso: le trending sono opzionali */ }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadDashboard()
    }
}
