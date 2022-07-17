package com.mch.cryptodashboard.data.source

import androidx.room.TypeConverter
import com.mch.cryptodashboard.data.Tier
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromRateList(value: List<Tier.Rate>?): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun stringToRateList(value: String): List<Tier.Rate>? {
        return Json.decodeFromString(value)
    }
}