package com.mch.cryptodashboard.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal

class Converters {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun stringToBigDecimal(value: String?): BigDecimal? {
        return BigDecimal(value)
    }

    @TypeConverter
    fun fromRateList(value: List<Tier.Rate>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun stringToRateList(value: String?): List<Tier.Rate>? {
        val type = object : TypeToken<List<Tier.Rate>>() {}.type
        return Gson().fromJson(value, type)
    }
}