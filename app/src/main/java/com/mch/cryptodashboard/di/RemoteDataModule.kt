package com.mch.cryptodashboard.di

import com.mch.cryptodashboard.data.source.FakeService
import com.mch.cryptodashboard.data.source.WebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RemoteDataModule {

    @Provides
    fun provideWebService(): WebService = FakeService()
}
