package com.mch.cryptodashboard.data

import com.mch.cryptodashboard.data.source.WalletDao
import com.mch.cryptodashboard.data.source.WebService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WalletRepository(
    private val service: WebService,
    private val dao: WalletDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getWallets() = dao.getWallets()

    suspend fun refresh() = withContext(ioDispatcher) {
        kotlin.runCatching {
            val list = service.getWallets().wallet
            dao.insertAll(list)
        }
    }
}