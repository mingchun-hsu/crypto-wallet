package com.mch.cryptodashboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Currency(
    @PrimaryKey
    @SerialName("coin_id") val coinId: String,
    @SerialName("name") val name: String,
    @SerialName("symbol") val symbol: String,
    @SerialName("colorful_image_url") val colorfulImageURL: String
)