package com.mch.cryptodashboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Currency(
    @PrimaryKey
    @SerializedName("coin_id") val coinId: String,
    @SerializedName("name") val name: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("colorful_image_url") val colorfulImageURL: String
)