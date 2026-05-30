package com.SalvoLab.di

import com.SalvoLab.data.repository.CardRepository
import com.SalvoLab.data.repository.InventoryRepository
import com.SalvoLab.domain.repository.ICardRepository
import com.SalvoLab.domain.repository.IInventoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindCardRepository(impl: CardRepository): ICardRepository

    @Binds @Singleton
    abstract fun bindInventoryRepository(impl: InventoryRepository): IInventoryRepository
}
