package com.mch.cryptodashboard.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.mch.cryptodashboard.data.Tier
import kotlinx.coroutines.flow.Flow

@Dao
interface TierDao {

    @Query("SELECT * FROM tier")
    fun getTiers(): Flow<List<Tier>>

    @Insert(onConflict = REPLACE)
    fun insertAll(list: List<Tier>)
}