package com.mch.cryptodashboard.data.source

import com.mch.cryptodashboard.data.Currency
import com.mch.cryptodashboard.data.Tier
import com.mch.cryptodashboard.data.Wallet
import kotlinx.serialization.Serializable


interface WebService {

    suspend fun getCurrencies(): CurrencyResponse

    suspend fun getTires(): RateResponse

    suspend fun getWallets(): WalletResponse


    @Serializable
    data class CurrencyResponse (
        val currencies: List<Currency>
    )

    @Serializable
    data class RateResponse (
        val tiers: List<Tier>
    )

    @Serializable
    data class WalletResponse (
        val wallet: List<Wallet>
    )
}