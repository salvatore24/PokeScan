package com.SalvoLab.domain.model

data class InventoryItem(
    val id: String = "",
    val userId: String,
    val card: PokemonCard,
    val condition: CardCondition,
    val language: CardLanguage = CardLanguage.ITALIAN,
    val quantity: Int,
    val purchasePrice: Double?,
    val addedAt: Long,
    val notes: String = "",
    val isFoil: Boolean = false,
) {
    /**
     * Valore stimato basato sul prezzo trend Cardmarket della carta,
     * moltiplicato per il modificatore condizione e quantità.
     * La lingua non altera il prezzo qui perché Cardmarket ha prezzi
     * per lingua separati (gestiti lato API in futuro).
     */
    val estimatedValue: Double
        get() {
            val base = card.cardmarketPrices?.trendPrice ?: return 0.0
            return base * condition.priceMultiplier * quantity
        }

    val gainLoss: Double
        get() = estimatedValue - ((purchasePrice ?: 0.0) * quantity)

    val gainLossPercent: Double
        get() {
            val spent = (purchasePrice ?: 0.0) * quantity
            return if (spent > 0) (gainLoss / spent) * 100 else 0.0
        }
}

/**
 * Lingua dell'edizione fisica della carta.
 * I prezzi Cardmarket sono per lingua — in futuro si può chiamare
 * l'API con il parametro lingua per ottenere il prezzo esatto.
 * Per ora la lingua è informativa e non modifica il calcolo del prezzo.
 */
enum class CardLanguage(
    val label: String,
    val code: String,
    val flag: String,
) {
    ITALIAN    ("Italiano",   "IT", "🇮🇹"),
    ENGLISH    ("Inglese",    "EN", "🇬🇧"),
    JAPANESE   ("Giapponese", "JP", "🇯🇵"),
    GERMAN     ("Tedesco",    "DE", "🇩🇪"),
    FRENCH     ("Francese",   "FR", "🇫🇷"),
    SPANISH    ("Spagnolo",   "ES", "🇪🇸"),
    PORTUGUESE ("Portoghese", "PT", "🇵🇹"),
    KOREAN     ("Coreano",    "KO", "🇰🇷"),
}

enum class CardCondition(
    val label: String,
    val abbreviation: String,
    /** Coefficiente sul prezzo trend Cardmarket per stimare il valore reale */
    val priceMultiplier: Double,
) {
    MINT        ("Mint",         "MT", 1.10),
    NEAR_MINT   ("Near Mint",    "NM", 1.00),
    EXCELLENT   ("Excellent",    "EX", 0.85),
    GOOD        ("Good",         "GD", 0.70),
    LIGHT_PLAYED("Light Played", "LP", 0.55),
    PLAYED      ("Played",       "PL", 0.40),
    POOR        ("Poor",         "PO", 0.25),
}

data class PortfolioSummary(
    val totalCards: Int,
    val uniqueCards: Int,
    val estimatedValue: Double,
    val purchasedValue: Double,
    val gainLoss: Double,
    val gainLossPercent: Double,
)
