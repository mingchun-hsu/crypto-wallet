package com.mch.cryptodashboard.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class WalletRepository(
    private val service: WebService,
    private val dao: WalletDao
) {

    fun getWallets() = dao.getWallet()

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val list = service.getWallet().wallet
        delay((100L..2000L).random())
        dao.insertAll(list)
    }
}