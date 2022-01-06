package com.mch.cryptodashboard.ui

import java.math.BigDecimal

data class CurrencyItem(
    val coinId: String,
    val imageUrl: String,
    val name: String,
    val symbol: String,
    val amount: BigDecimal?,
    val balance: BigDecimal?
)
