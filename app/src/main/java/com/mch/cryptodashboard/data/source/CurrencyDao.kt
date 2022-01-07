package com.mch.cryptodashboard.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.mch.cryptodashboard.data.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currency")
    fun getCurrencies(): Flow<List<Currency>>

    @Insert(onConflict = REPLACE)
    fun insertAll(list: List<Currency>)
}