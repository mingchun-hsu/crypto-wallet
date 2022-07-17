package com.mch.cryptodashboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Tier(
    @PrimaryKey
    @SerialName("from_currency") val fromCurrency: String,
    @SerialName("to_currency") val toCurrency: String,
    @SerialName("rates") val rates: List<Rate>
) {

    @Serializable
    data class Rate(
        @SerialName("amount") val amount: String,
        @SerialName("rate") val rate: String
    )
}