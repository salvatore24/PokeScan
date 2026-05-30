package com.SalvoLab.data.remote.api

import com.SalvoLab.data.remote.dto.CardListResponse
import com.SalvoLab.data.remote.dto.CardResponse
import com.SalvoLab.data.remote.dto.SetListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonTcgApi {

    @GET("cards")
    suspend fun searchCards(
        @Query("q")        query: String,
        @Query("page")     page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("orderBy")  orderBy: String = "cardmarket.prices.trendPrice",
        @Query("select")   select: String = "id,name,number,rarity,images,set,types,hp,artist,cardmarket",
    ): CardListResponse

    @GET("cards/{id}")
    suspend fun getCard(@Path("id") id: String): CardResponse

    @GET("sets")
    suspend fun getSets(
        @Query("orderBy") orderBy: String = "-releaseDate",
    ): SetListResponse

    companion object {
        const val BASE_URL = "https://api.pokemontcg.io/v2/"
    }
}
