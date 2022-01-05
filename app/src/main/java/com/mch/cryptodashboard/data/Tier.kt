package com.mch.cryptodashboard.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Tier(
    @SerializedName("from_currency") val fromCurrency: String,
    @SerializedName("to_currency") val toCurrency: String,
    @SerializedName("rates") val rates: List<Rate>,
    @SerializedName("time_stamp") val timeStamp: Long
) {

    data class Rate(
        @SerializedName("amount") val amount: String,
        @SerializedName("rate") val rate: BigDecimal
    )
}