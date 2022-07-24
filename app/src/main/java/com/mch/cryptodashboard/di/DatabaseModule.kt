package com.mch.cryptodashboard.di

import android.content.Context
import androidx.room.Room
import com.mch.cryptodashboard.data.source.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideCurrencyDao(database: AppDatabase) = database.currencyDao()

    @Provides
    fun provideTierDao(database: AppDatabase) = database.tierDao()

    @Provides
    fun provideWalletDao(database: AppDatabase) = database.walletDao()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            appContext,
            AppDatabase::class.java
        ).build()
    }
}
