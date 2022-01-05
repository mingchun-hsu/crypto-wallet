package com.mch.cryptodashboard.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Wallet(
    @SerializedName("currency") val currency: String,
    @SerializedName("amount") val amount: BigDecimal
)
