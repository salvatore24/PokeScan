package com.SalvoLab.ui.carddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SalvoLab.domain.model.CardCondition
import com.SalvoLab.domain.model.CardLanguage
import com.SalvoLab.domain.model.InventoryItem
import com.SalvoLab.domain.model.PokemonCard
import com.SalvoLab.domain.repository.ICardRepository
import com.SalvoLab.domain.repository.IInventoryRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardDetailUiState(
    val card: PokemonCard? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    // Aggiungi a inventario
    val showAddDialog: Boolean = false,
    val selectedCondition: CardCondition = CardCondition.NEAR_MINT,
    val selectedLanguage: CardLanguage = CardLanguage.ITALIAN,
    val quantity: Int = 1,
    val purchasePrice: String = "",
    val isFoil: Boolean = false,
    val notes: String = "",
    val isAddingToInventory: Boolean = false,
    val addSuccess: Boolean = false,
)

@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val cardRepository: ICardRepository,
    private val inventoryRepository: IInventoryRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val cardId: String = checkNotNull(savedStateHandle["cardId"])

    private val _uiState = MutableStateFlow(CardDetailUiState())
    val uiState: StateFlow<CardDetailUiState> = _uiState.asStateFlow()

    init {
        loadCard()
    }

    private fun loadCard() {
        viewModelScope.launch {
            cardRepository.getCardById(cardId)
                .onSuccess { card -> _uiState.update { it.copy(isLoading = false, card = card) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun onShowAddDialog()  = _uiState.update { it.copy(showAddDialog = true, addSuccess = false) }
    fun onDismissDialog()  = _uiState.update { it.copy(showAddDialog = false) }

    fun onConditionChanged(c: CardCondition) = _uiState.update { it.copy(selectedCondition = c) }
    fun onLanguageChanged(l: CardLanguage)   = _uiState.update { it.copy(selectedLanguage = l) }
    fun onQuantityChanged(q: Int)            = _uiState.update { it.copy(quantity = q.coerceIn(1, 99)) }
    fun onPurchasePriceChanged(p: String)    = _uiState.update { it.copy(purchasePrice = p) }
    fun onFoilChanged(f: Boolean)            = _uiState.update { it.copy(isFoil = f) }
    fun onNotesChanged(n: String)            = _uiState.update { it.copy(notes = n) }
    fun dismissError()                       = _uiState.update { it.copy(error = null) }

    fun addToInventory() {
        val state = _uiState.value
        val card  = state.card
        if (card == null) {
            _uiState.update { it.copy(error = "Carta non ancora caricata, riprova.") }
            return
        }
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(error = "Devi essere autenticato per aggiungere carte all'inventario.") }
            return
        }

        _uiState.update { it.copy(isAddingToInventory = true, error = null) }

        viewModelScope.launch {
            runCatching {
                val item = InventoryItem(
                    userId        = userId,
                    card          = card,
                    condition     = state.selectedCondition,
                    language      = state.selectedLanguage,
                    quantity      = state.quantity,
                    purchasePrice = state.purchasePrice.replace(",", ".").toDoubleOrNull(),
                    addedAt       = System.currentTimeMillis(),
                    notes         = state.notes,
                    isFoil        = state.isFoil,
                )
                inventoryRepository.addCard(item).getOrThrow()
            }
            .onSuccess {
                _uiState.update { it.copy(isAddingToInventory = false, showAddDialog = false, addSuccess = true) }
            }
            .onFailure { e ->
                android.util.Log.e("CardDetailVM", "addToInventory failed", e)
                _uiState.update { it.copy(isAddingToInventory = false, error = "Errore salvataggio: ${e.localizedMessage}") }
            }
        }
    }
}
