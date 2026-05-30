package com.SalvoLab.data.remote.mapper

import com.SalvoLab.data.remote.dto.CardDto
import com.SalvoLab.data.remote.dto.CardmarketPricesDto
import com.SalvoLab.data.remote.dto.SetDto
import com.SalvoLab.domain.model.CardmarketPrices
import com.SalvoLab.domain.model.PokemonCard
import com.SalvoLab.domain.model.PokemonSet

fun CardDto.toDomain() = PokemonCard(
    id             = id,
    name           = name,
    number         = number,
    rarity         = rarity,
    imageUrlSmall  = images.small,
    imageUrlLarge  = images.large,
    set            = set.toDomain(),
    types          = types ?: emptyList(),
    hp             = hp,
    artist         = artist,
    cardmarketPrices = cardmarket?.prices?.toDomain(),
)

fun SetDto.toDomain() = PokemonSet(
    id          = id,
    name        = name,
    series      = series,
    releaseDate = releaseDate,
    logoUrl     = images?.logo,
    symbolUrl   = images?.symbol,
    total       = total,
)

fun CardmarketPricesDto.toDomain() = CardmarketPrices(
    averageSellPrice  = averageSellPrice,
    lowPrice          = lowPrice,
    trendPrice        = trendPrice,
    avg1              = avg1,
    avg7              = avg7,
    avg30             = avg30,
    reverseHoloAvg1   = reverseHoloAvg1,
    reverseHoloAvg7   = reverseHoloAvg7,
    reverseHoloAvg30  = reverseHoloAvg30,
)
