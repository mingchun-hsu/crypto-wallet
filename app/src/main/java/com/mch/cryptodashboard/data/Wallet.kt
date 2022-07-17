package com.mch.cryptodashboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Wallet(
    @PrimaryKey
    @SerialName("currency") val currency: String,
    @SerialName("amount") val amount: Double
)
