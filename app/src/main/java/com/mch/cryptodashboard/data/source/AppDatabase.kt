package com.mch.cryptodashboard.data.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mch.cryptodashboard.data.*

@Database(entities = [Currency::class, Tier::class, Wallet::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao

    abstract fun tierDao(): TierDao

    abstract fun walletDao(): WalletDao


    companion object {
        @Volatile
        var instance: AppDatabase? = null

        fun getInstance(applicationContext: Context) = instance ?: synchronized(this) {
            instance ?: build(applicationContext).also { instance = it }
        }

        private fun build(applicationContext: Context) = Room.inMemoryDatabaseBuilder(
            applicationContext,
            AppDatabase::class.java
        ).build()
    }
}
