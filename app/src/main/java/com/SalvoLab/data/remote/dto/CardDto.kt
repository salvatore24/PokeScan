package com.SalvoLab.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Top-level response wrappers ───────────────────────────────

data class CardListResponse(
    val data: List<CardDto>,
    val page: Int,
    val pageSize: Int,
    val count: Int,
    val totalCount: Int,
)

data class CardResponse(
    val data: CardDto,
)

data class SetListResponse(
    val data: List<SetDto>,
)

// ── Card ──────────────────────────────────────────────────────

data class CardDto(
    val id: String,
    val name: String,
    val number: String,
    val rarity: String?,
    val images: CardImagesDto,
    val set: SetDto,
    val types: List<String>?,
    val hp: String?,
    val artist: String?,
    val cardmarket: CardmarketDto?,
)

data class CardImagesDto(
    val small: String,
    val large: String,
)

// ── Set ───────────────────────────────────────────────────────

data class SetDto(
    val id: String,
    val name: String,
    val series: String,
    val releaseDate: String,
    val images: SetImagesDto?,
    val total: Int,
)

data class SetImagesDto(
    val logo: String?,
    val symbol: String?,
)

// ── Cardmarket prices ─────────────────────────────────────────

data class CardmarketDto(
    val url: String?,
    val updatedAt: String?,
    val prices: CardmarketPricesDto?,
)

data class CardmarketPricesDto(
    val averageSellPrice: Double?,
    val lowPrice: Double?,
    val trendPrice: Double?,
    val avg1: Double?,
    val avg7: Double?,
    val avg30: Double?,
    val reverseHoloAvg1: Double?,
    val reverseHoloAvg7: Double?,
    val reverseHoloAvg30: Double?,
)
