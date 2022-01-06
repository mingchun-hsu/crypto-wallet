package com.mch.cryptodashboard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TierDao {

    @Query("SELECT * FROM tier WHERE fromCurrency IN (:supportedList)")
    fun getTiers(supportedList: List<String>): Flow<List<Tier>>

    @Insert(onConflict = REPLACE)
    fun insertAll(list: List<Tier>)
}