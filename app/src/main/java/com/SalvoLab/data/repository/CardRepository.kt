package com.SalvoLab.data.repository

import com.SalvoLab.data.remote.api.PokemonTcgApi
import com.SalvoLab.data.remote.mapper.toDomain
import com.SalvoLab.domain.model.PokemonCard
import com.SalvoLab.domain.model.PokemonSet
import com.SalvoLab.domain.repository.ICardRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepository @Inject constructor(
    private val api: PokemonTcgApi,
) : ICardRepository {

    override suspend fun searchCards(query: String, page: Int, pageSize: Int): Result<List<PokemonCard>> =
        runCatching {
            // Ricerca per nome + tipo Pokémon (esclude carte Trainer/Energy)
            val q = if (query.isBlank()) "supertype:Pokémon" else "name:\"$query*\" supertype:Pokémon"
            api.searchCards(query = q, page = page, pageSize = pageSize).data.map { it.toDomain() }
        }

    override suspend fun getCardById(id: String): Result<PokemonCard> =
        runCatching { api.getCard(id).data.toDomain() }

    override suspend fun getCardsBySet(setId: String, page: Int, pageSize: Int): Result<List<PokemonCard>> =
        runCatching {
            api.searchCards(
                query    = "set.id:$setId supertype:Pokémon",
                page     = page,
                pageSize = pageSize,
                orderBy  = "number",
            ).data.map { it.toDomain() }
        }

    override suspend fun getSets(): Result<List<PokemonSet>> =
        runCatching { api.getSets().data.map { it.toDomain() } }

    override suspend fun getTrendingCards(): Result<List<PokemonCard>> =
        runCatching {
            // Carte Pokémon con prezzo Cardmarket, ordinate per trend decrescente
            api.searchCards(
                query    = "supertype:Pokémon -cardmarket.prices.trendPrice:[* TO 0]",
                page     = 1,
                pageSize = 20,
                orderBy  = "-cardmarket.prices.trendPrice",
            ).data.map { it.toDomain() }
        }
}
