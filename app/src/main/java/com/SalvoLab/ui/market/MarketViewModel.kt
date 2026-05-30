package com.SalvoLab.ui.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SalvoLab.domain.model.PokemonCard
import com.SalvoLab.domain.model.PokemonSet
import com.SalvoLab.domain.repository.ICardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarketUiState(
    val searchQuery: String = "",
    val cards: List<PokemonCard> = emptyList(),
    val sets: List<PokemonSet> = emptyList(),
    val selectedSetId: String? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class MarketViewModel @Inject constructor(
    private val cardRepository: ICardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadSets()
        searchCards() // carica le carte Pokémon più popolari all'avvio

        // Debounce della ricerca: aspetta 400ms dopo l'ultimo carattere
        _searchQuery
            .drop(1) // skip il valore iniziale ""
            .debounce(400)
            .distinctUntilChanged()
            .onEach { query ->
                _uiState.update { it.copy(searchQuery = query, currentPage = 1, cards = emptyList(), hasMore = true) }
                searchCards()
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSetSelected(setId: String?) {
        _uiState.update { it.copy(selectedSetId = setId, currentPage = 1, cards = emptyList(), hasMore = true) }
        searchCards()
    }

    fun loadNextPage() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        searchCards(append = true)
    }

    fun refresh() {
        _uiState.update { it.copy(currentPage = 1, cards = emptyList(), hasMore = true) }
        searchCards()
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    private fun loadSets() {
        viewModelScope.launch {
            cardRepository.getSets()
                .onSuccess { sets -> _uiState.update { it.copy(sets = sets) } }
        }
    }

    private fun searchCards(append: Boolean = false) {
        val state = _uiState.value
        if (append) {
            _uiState.update { it.copy(isLoadingMore = true) }
        } else {
            _uiState.update { it.copy(isLoading = true, error = null) }
        }

        viewModelScope.launch {
            val result = when {
                state.selectedSetId != null ->
                    cardRepository.getCardsBySet(state.selectedSetId, state.currentPage)
                state.searchQuery.isNotBlank() ->
                    cardRepository.searchCards(state.searchQuery, state.currentPage)
                else ->
                    cardRepository.searchCards("", state.currentPage)
            }

            result
                .onSuccess { newCards ->
                    _uiState.update { it.copy(
                        isLoading     = false,
                        isLoadingMore = false,
                        cards         = if (append) it.cards + newCards else newCards,
                        hasMore       = newCards.size >= 20,
                    )}
                }
                .onFailure { e ->
                    _uiState.update { it.copy(
                        isLoading     = false,
                        isLoadingMore = false,
                        error         = e.message,
                    )}
                }
        }
    }
}
