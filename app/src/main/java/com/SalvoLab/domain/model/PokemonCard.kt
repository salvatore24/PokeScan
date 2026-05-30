package com.SalvoLab.domain.model

data class PokemonCard(
    val id: String,
    val name: String,
    val number: String,
    val rarity: String?,
    val imageUrlSmall: String,
    val imageUrlLarge: String,
    val set: PokemonSet,
    val types: List<String>,
    val hp: String?,
    val artist: String?,
    val cardmarketPrices: CardmarketPrices?,
)

data class PokemonSet(
    val id: String,
    val name: String,
    val series: String,
    val releaseDate: String,
    val logoUrl: String?,
    val symbolUrl: String?,
    val total: Int,
)

data class CardmarketPrices(
    val averageSellPrice: Double?,
    val lowPrice: Double?,
    val trendPrice: Double?,
    val avg1: Double?,   // media 1 giorno
    val avg7: Double?,   // media 7 giorni
    val avg30: Double?,  // media 30 giorni
    val reverseHoloAvg1: Double?,
    val reverseHoloAvg7: Double?,
    val reverseHoloAvg30: Double?,
)
