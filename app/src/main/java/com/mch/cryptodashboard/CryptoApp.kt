package com.mch.cryptodashboard

import android.app.Application
import com.mch.cryptodashboard.data.*

class CryptoApp : Application() {

    private fun createWebService() = FakeService()

    private fun getDatabase() = AppDatabase.getInstance(this)

    fun getCurrencyRepository() = CurrencyRepository(createWebService(), getDatabase().currencyDao())

    fun getTierRepository() = TierRepository(createWebService(), getDatabase().tierDao())

    fun getWalletRepository() = WalletRepository(createWebService(), getDatabase().walletDao())
}