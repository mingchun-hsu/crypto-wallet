package com.mch.cryptodashboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

@Entity
data class Tier(
    @PrimaryKey
    @SerializedName("from_currency") val fromCurrency: String,
    @SerializedName("to_currency") val toCurrency: String,
    @SerializedName("rates") val rates: List<Rate>
) {

    data class Rate(
        @SerializedName("amount") val amount: String,
        @SerializedName("rate") val rate: BigDecimal
    )
}