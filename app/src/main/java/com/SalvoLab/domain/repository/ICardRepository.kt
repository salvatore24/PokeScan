package com.SalvoLab.domain.repository

import com.SalvoLab.domain.model.PokemonCard
import com.SalvoLab.domain.model.PokemonSet

interface ICardRepository {
    suspend fun searchCards(query: String, page: Int = 1, pageSize: Int = 20): Result<List<PokemonCard>>
    suspend fun getCardById(id: String): Result<PokemonCard>
    suspend fun getCardsBySet(setId: String, page: Int = 1, pageSize: Int = 20): Result<List<PokemonCard>>
    suspend fun getSets(): Result<List<PokemonSet>>
    suspend fun getTrendingCards(): Result<List<PokemonCard>>
}
