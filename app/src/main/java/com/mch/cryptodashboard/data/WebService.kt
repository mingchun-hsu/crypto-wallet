package com.mch.cryptodashboard.data


interface WebService {

    suspend fun getCurrency(): CurrencyResponse

    suspend fun getRate(): RateResponse

    suspend fun getWallet(): WalletResponse


    data class CurrencyResponse (
        val currencies: List<Currency>
    )

    data class RateResponse (
        val tiers: List<Tier>
    )

    data class WalletResponse (
        val wallet: List<Wallet>
    )
}