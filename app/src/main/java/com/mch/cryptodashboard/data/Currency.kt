package com.mch.cryptodashboard.data

import com.google.gson.annotations.SerializedName

data class Currency(
    @SerializedName("coin_id") val coinId: String,
    @SerializedName("name") val name: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("colorful_image_url") val colorfulImageURL: String
)