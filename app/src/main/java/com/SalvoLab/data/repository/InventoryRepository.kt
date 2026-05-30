package com.SalvoLab.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import com.SalvoLab.domain.model.CardCondition
import com.SalvoLab.domain.model.CardLanguage
import com.SalvoLab.domain.model.CardmarketPrices
import com.SalvoLab.domain.model.InventoryItem
import com.SalvoLab.domain.model.PortfolioSummary
import com.SalvoLab.domain.model.PokemonCard
import com.SalvoLab.domain.model.PokemonSet
import com.SalvoLab.domain.repository.IInventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val COLLECTION_INVENTORY = "inventory"

@Singleton
class InventoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : IInventoryRepository {

    override fun getInventory(userId: String): Flow<List<InventoryItem>> =
        firestore.collection(COLLECTION_INVENTORY)
            .whereEqualTo("userId", userId)
            .dataObjects<InventoryItemDocument>()
            .map { docs -> docs.mapNotNull { it.toDomain() } }

    override fun getPortfolioSummary(userId: String): Flow<PortfolioSummary> =
        getInventory(userId).map { items ->
            val trendTotal    = items.sumOf { it.estimatedValue }
            val purchaseTotal = items.sumOf { (it.purchasePrice ?: 0.0) * it.quantity }
            val gainLoss = trendTotal - purchaseTotal
            val gainLossPct = if (purchaseTotal > 0) (gainLoss / purchaseTotal) * 100 else 0.0
            PortfolioSummary(
                totalCards       = items.sumOf { it.quantity },
                uniqueCards      = items.distinctBy { it.card.id }.size,
                estimatedValue   = trendTotal,
                purchasedValue   = purchaseTotal,
                gainLoss         = gainLoss,
                gainLossPercent  = gainLossPct,
            )
        }

    override suspend fun addCard(item: InventoryItem): Result<String> = runCatching {
        val doc = firestore.collection(COLLECTION_INVENTORY).document()
        doc.set(item.toDocument(doc.id)).await()
        doc.id
    }

    override suspend fun updateCard(item: InventoryItem): Result<Unit> = runCatching {
        firestore.collection(COLLECTION_INVENTORY)
            .document(item.id)
            .set(item.toDocument(item.id))
            .await()
    }

    override suspend fun removeCard(itemId: String): Result<Unit> = runCatching {
        firestore.collection(COLLECTION_INVENTORY).document(itemId).delete().await()
    }

    override suspend fun getItem(itemId: String): Result<InventoryItem> = runCatching {
        val snap = firestore.collection(COLLECTION_INVENTORY).document(itemId).get().await()
        snap.toObject<InventoryItemDocument>()?.toDomain() ?: error("Item non trovato")
    }
}

// ── Firestore document model ──────────────────────────────────

data class InventoryItemDocument(
    val id: String = "",
    val userId: String = "",
    val cardId: String = "",
    val cardName: String = "",
    val cardNumber: String = "",
    val cardRarity: String? = null,
    val cardImageSmall: String = "",
    val cardImageLarge: String = "",
    val setId: String = "",
    val setName: String = "",
    val setSeries: String = "",
    val setReleaseDate: String = "",
    val setLogoUrl: String? = null,
    val setSymbolUrl: String? = null,
    val setTotal: Int = 0,
    val cardTypes: List<String> = emptyList(),
    val cardHp: String? = null,
    val cardArtist: String? = null,
    val trendPrice: Double? = null,
    val lowPrice: Double? = null,
    val avg30: Double? = null,
    val condition: String = CardCondition.NEAR_MINT.name,
    val language: String = CardLanguage.ITALIAN.name,
    val quantity: Int = 1,
    val purchasePrice: Double? = null,
    val addedAt: Long = 0L,
    val notes: String = "",
    val isFoil: Boolean = false,
)

private fun InventoryItemDocument.toDomain(): InventoryItem? = runCatching {
    InventoryItem(
        id            = id,
        userId        = userId,
        card          = PokemonCard(
            id             = cardId,
            name           = cardName,
            number         = cardNumber,
            rarity         = cardRarity,
            imageUrlSmall  = cardImageSmall,
            imageUrlLarge  = cardImageLarge,
            set            = PokemonSet(
                id          = setId,
                name        = setName,
                series      = setSeries,
                releaseDate = setReleaseDate,
                logoUrl     = setLogoUrl,
                symbolUrl   = setSymbolUrl,
                total       = setTotal,
            ),
            types          = cardTypes,
            hp             = cardHp,
            artist         = cardArtist,
            cardmarketPrices = if (trendPrice != null) CardmarketPrices(
                averageSellPrice = null,
                lowPrice         = lowPrice,
                trendPrice       = trendPrice,
                avg1             = null,
                avg7             = null,
                avg30            = avg30,
                reverseHoloAvg1  = null,
                reverseHoloAvg7  = null,
                reverseHoloAvg30 = null,
            ) else null,
        ),
        condition     = CardCondition.valueOf(condition),
        language      = runCatching { CardLanguage.valueOf(language) }.getOrDefault(CardLanguage.ITALIAN),
        quantity      = quantity,
        purchasePrice = purchasePrice,
        addedAt       = addedAt,
        notes         = notes,
        isFoil        = isFoil,
    )
}.getOrNull()

private fun InventoryItem.toDocument(docId: String) = InventoryItemDocument(
    id             = docId,
    userId         = userId,
    cardId         = card.id,
    cardName       = card.name,
    cardNumber     = card.number,
    cardRarity     = card.rarity,
    cardImageSmall = card.imageUrlSmall,
    cardImageLarge = card.imageUrlLarge,
    setId          = card.set.id,
    setName        = card.set.name,
    setSeries      = card.set.series,
    setReleaseDate = card.set.releaseDate,
    setLogoUrl     = card.set.logoUrl,
    setSymbolUrl   = card.set.symbolUrl,
    setTotal       = card.set.total,
    cardTypes      = card.types,
    cardHp         = card.hp,
    cardArtist     = card.artist,
    trendPrice     = card.cardmarketPrices?.trendPrice,
    lowPrice       = card.cardmarketPrices?.lowPrice,
    avg30          = card.cardmarketPrices?.avg30,
    condition      = condition.name,
    language       = language.name,
    quantity       = quantity,
    purchasePrice  = purchasePrice,
    addedAt        = addedAt,
    notes          = notes,
    isFoil         = isFoil,
)
