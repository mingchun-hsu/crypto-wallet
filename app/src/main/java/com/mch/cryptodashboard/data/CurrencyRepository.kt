package com.mch.cryptodashboard.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyRepository(
    private val service: WebService,
    private val dao: CurrencyDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getCurrencies() = dao.getCurrencies()

    suspend fun refresh() = withContext(ioDispatcher) {
        val list = service.getCurrencies().currencies
        dao.insertAll(list)
    }
}