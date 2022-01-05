package com.mch.cryptodashboard.data

class CurrencyRepository(
    private val service: WebService
) {

    suspend fun getCurrency() = service.getCurrency().currencies

    suspend fun getRate() = service.getRate().tiers

    suspend fun getWallet() = service.getWallet().wallet
}