package com.mch.cryptodashboard.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CurrencyRepository(
    private val service: WebService,
    private val dao: CurrencyDao
) {

    fun getCurrency() = dao.getCurrencies()

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val list = service.getCurrency().currencies
        dao.insertAll(list)
    }
}