package com.mch.cryptodashboard.data.source

import com.mch.cryptodashboard.data.Currency
import com.mch.cryptodashboard.data.Tier
import com.mch.cryptodashboard.data.Wallet


interface WebService {

    suspend fun getCurrencies(): CurrencyResponse

    suspend fun getTires(): RateResponse

    suspend fun getWallets(): WalletResponse


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