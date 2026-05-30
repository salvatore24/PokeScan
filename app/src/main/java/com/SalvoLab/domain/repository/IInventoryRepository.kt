package com.SalvoLab.domain.repository

import com.SalvoLab.domain.model.InventoryItem
import com.SalvoLab.domain.model.PortfolioSummary
import kotlinx.coroutines.flow.Flow

interface IInventoryRepository {
    fun getInventory(userId: String): Flow<List<InventoryItem>>
    fun getPortfolioSummary(userId: String): Flow<PortfolioSummary>
    suspend fun addCard(item: InventoryItem): Result<String>
    suspend fun updateCard(item: InventoryItem): Result<Unit>
    suspend fun removeCard(itemId: String): Result<Unit>
    suspend fun getItem(itemId: String): Result<InventoryItem>
}
