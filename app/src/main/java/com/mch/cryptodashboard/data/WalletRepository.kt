package com.mch.cryptodashboard.data

import com.mch.cryptodashboard.data.source.WalletDao
import com.mch.cryptodashboard.data.source.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WalletRepository @Inject constructor(
    private val service: WebService,
    private val dao: WalletDao,
) {

    fun getWallets() = dao.getWallets()

    suspend fun refresh() = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            val list = service.getWallets().wallet
            dao.insertAll(list)
        }
    }
}