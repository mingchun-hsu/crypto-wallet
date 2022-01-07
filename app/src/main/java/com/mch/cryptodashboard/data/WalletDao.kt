package com.mch.cryptodashboard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet")
    fun getWallets(): Flow<List<Wallet>>

    @Insert(onConflict = REPLACE)
    fun insertAll(list: List<Wallet>)
}