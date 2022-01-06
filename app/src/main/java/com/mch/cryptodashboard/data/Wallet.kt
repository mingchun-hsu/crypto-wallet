package com.mch.cryptodashboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

@Entity
data class Wallet(
    @PrimaryKey
    @SerializedName("currency") val currency: String,
    @SerializedName("amount") val amount: BigDecimal
)
